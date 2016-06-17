/*
 * Copyright (C)2016 - SSH-SCALA Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qtamaki.concurrent

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents promised data of the parameterized type {@code V} and allows waiting on it. An exception may also be
 * delivered to a waiter, and will be of the parameterized type {@code T}.
 * <p/>
 * For atomic operations on a promise, e.g. checking if a value is delivered and if it is not then setting it, the
 * associated lock for the promise should be acquired while doing so.
 *
 * Creates this promise with given {@code name}, exception {@code chainer}, and associated {@code lock}.
 *
 * @param name    name of this promise
 * @param chainer {@link ExceptionChainer} that will be used for chaining exceptions
 * @param lock    lock to use
 */
class Promise[V, T <: Throwable](val name: String, val chainer: ExceptionChainer[T], val _lock: ReentrantLock) {

  private val log: Logger = LoggerFactory.getLogger(getClass());

  private val cond: Condition = _lock.newCondition();

  var value: V = null.asInstanceOf[V]
  var pendingEx: T = null.asInstanceOf[T]

  /**
   * Creates this promise with given {@code name} and exception {@code chainer}. Allocates a new {@link
   * java.util.concurrent.locks.Lock lock} object for this promise.
   *
   * @param name    name of this promise
   * @param chainer {@link ExceptionChainer} that will be used for chaining exceptions
   */
  def this(name: String, chainer: ExceptionChainer[T]) = {
    this(name, chainer, null);
  }

  /**
   * Set this promise's value to {@code val}. Any waiters will be delivered this value.
   *
   * @param val the value
   */
  def deliver(value: V) {
    _lock.lock();
    try {
      log.debug(s"Setting <<${name}>> to `${value}`")
      this.value = value
      cond.signalAll();
    } finally {
      _lock.unlock();
    }
  }

  /**
   * Queues error that will be thrown in any waiting thread or any thread that attempts to wait on this promise
   * hereafter.
   *
   * @param e the error
   */
  def deliverError(e: Throwable) {
    _lock.lock();
    try {
      pendingEx = chainer.chain(e)
      cond.signalAll();
    } finally {
      _lock.unlock();
    }
  }

  /** Clears this promise by setting its value and queued exception to {@code null}. */
  def clear() {
    _lock.lock();
    try {
      pendingEx = null.asInstanceOf[T]
      deliver(null.asInstanceOf[V]);
    } finally {
      _lock.unlock();
    }
  }

  /**
   * Wait indefinitely for this promise's value to be deliver.
   *
   * @return the value
   *
   * @throws T in case another thread informs the promise of an error meanwhile
   */
  def retrieve(): V = {
    tryRetrieve(0, TimeUnit.SECONDS);
  }

  /**
   * Wait for {@code timeout} duration for this promise's value to be deliver.
   *
   * @param timeout the timeout
   * @param unit    time unit for the timeout
   *
   * @return the value
   *
   * @throws T in case another thread informs the promise of an error meanwhile, or the timeout expires
   */
  def retrieve(timeout: Long, unit: TimeUnit): V = {
    val value = tryRetrieve(timeout, unit);
    if (value == null)
      throw chainer.chain(new TimeoutException("Timeout expired"));
    else
      return value;
  }

  /**
   * Wait for {@code timeout} duration for this promise's value to be deliver.
   * <p/>
   * If the value is not deliver by the time the timeout expires, returns {@code null}.
   *
   * @param timeout the timeout
   * @param unit    time unit for the timeout
   *
   * @return the value or {@code null}
   *
   * @throws T in case another thread informs the promise of an error meanwhile
   */
  def tryRetrieve(timeout: Long, unit: TimeUnit): V = {
    _lock.lock();
    try {
      if (pendingEx != null)
        throw pendingEx;
      if (value != null)
        return value;
      log.debug(s"Awaiting <<${name}>>");
      if (timeout == 0) {
        while (value == null && pendingEx == null) {
          cond.await();
        }
      } else {
        if (!cond.await(timeout, unit))
          return null.asInstanceOf[V];
      }
      if (pendingEx != null) {
        log.error(s"<<${name}>> woke to: ${pendingEx}");
        throw pendingEx;
      }
      return value;
    } catch {
      case ie: InterruptedException =>
        throw chainer.chain(ie)
    } finally {
      _lock.unlock();
    }
  }

  /** @return whether this promise has a value delivered, and no error waiting to pop. */
  def isDelivered(): Boolean = {
    _lock.lock();
    try {
      return pendingEx == null && value != null;
    } finally {
      _lock.unlock();
    }
  }

  /** @return whether this promise has been delivered an error. */
  def inError(): Boolean = {
    _lock.lock();
    try {
      return pendingEx != null;
    } finally {
      _lock.unlock();
    }
  }

  /** @return whether this promise was fulfilled with either a value or an error. */
  def isFulfilled(): Boolean = {
    _lock.lock();
    try {
      return pendingEx != null || value != null;
    } finally {
      _lock.unlock();
    }
  }

  /** @return whether this promise has threads waiting on it. */
  def hasWaiters(): Boolean = {
    _lock.lock();
    try {
      return _lock.hasWaiters(cond);
    } finally {
      _lock.unlock();
    }
  }

  /** Acquire the lock associated with this promise. */
  def lock() {
    _lock.lock();
  }

  /** Release the lock associated with this promise. */
  def unlock() {
    _lock.unlock();
  }

  override def toString(): String = {
    return name;
  }

}
