package com.qtamaki.concurrent

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An event can be set, cleared, or awaited, similar to Python's {@code threading.event}. The key difference is that a
 * waiter may be delivered an exception of parameterized type {@code T}.
 * <p/>
 * Uses {@link Promise} under the hood.
 */
object Event {
  private val SOME = new Object() {
    override def toString(): String = {
      return "SOME";
    }
  };
}

/**
 * Creates this event with given {@code name}, exception {@code chainer}, and associated {@code lock}.
 *
 * @param name    name of this event
 * @param chainer {@link ExceptionChainer} that will be used for chaining exceptions
 * @param lock    lock to use
 */
class Event[T <: Throwable](name: String, chainer: ExceptionChainer[T], _lock: ReentrantLock) {
  import Event._

  private val promise: Promise[Any, T] = new Promise(name, chainer, _lock)

  /**
   * Creates this event with given {@code name} and exception {@code chainer}. Allocates a new {@link
   * java.util.concurrent.locks.Lock Lock} object for this event.
   *
   * @param name    name of this event
   * @param chainer {@link ExceptionChainer} that will be used for chaining exceptions
   */
  def this(name: String, chainer: ExceptionChainer[T]) = {
    this(name, chainer, null);
  }

  /** Sets this event to be {@code true}. Short for {@code set(true)}. */
  def set() {
    promise.deliver(SOME);
  }

  /** Clear this event. A cleared event {@code !isSet()}. */
  def clear() {
    promise.clear();
  }

  /** Deliver the error {@code t} (after chaining) to any present or future waiters. */
  def deliverError(t: Throwable) {
    promise.deliverError(t);
  }

  /**
   * @return whether this event is in a 'set' state. An event is set by a call to {@link #set} or {@link
   *         #deliverError}
   */
  def isSet() = promise.isDelivered();

  /**
   * Await this event to have a definite {@code true} or {@code false} value.
   *
   * @throws T if another thread meanwhile informs this event of an error
   */
  def await() {
    promise.retrieve();
  }

  /**
   * Await this event to have a definite {@code true} or {@code false} value, for {@code timeout} duration.
   *
   * @param timeout timeout
   * @param unit    the time unit for the timeout
   *
   * @throws T if another thread meanwhile informs this event of an error, or timeout expires
   */
  def await(timeout: Long, unit: TimeUnit) {
    promise.retrieve(timeout, unit);
  }

  /**
   * Await this event to have a definite {@code true} or {@code false} value, for {@code timeout} duration.
   * <p/>
   * If the definite value is not available when the timeout expires, returns {@code false}.
   *
   * @param timeout timeout
   * @param unit    the time unit for the timeout
   *
   * @throws T if another thread meanwhile informs this event of an error
   */
  def tryAwait(timeout: Long, unit: TimeUnit): Boolean = {
    promise.tryRetrieve(timeout, unit) != null;
  }

  /** @return whether there are any threads waiting on this event to be set. */
  def hasWaiters() = promise.hasWaiters()

  /** @return whether this event is in an error state i.e. has been delivered an error. */
  def inError() = promise.inError();

  /** Acquire the lock associated with this event. */
  def lock() {
    promise.lock();
  }

  /** Release the lock associated with this event. */
  def unlock() {
    promise.unlock();
  }

  override def toString() = promise.toString();

}
