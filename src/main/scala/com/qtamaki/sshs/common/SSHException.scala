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
package com.qtamaki.sshs.common

import com.qtamaki.sshs.concurrent.ExceptionChainer
import java.io.IOException

object SSHException {
  val chainer: ExceptionChainer[SSHException] = new ExceptionChainer[SSHException]() {

    override def chain(t: Throwable): SSHException = {
      t match {
        case ex: SSHException => ex
        case _ => new SSHException(t)
      }
    }

  };

}

case class SSHException(code: DisconnectReason, message: String, cause: Throwable) extends IOException(message) {

  val reason: DisconnectReason = code
  if (cause != null) { initCause(cause) }

  def this(code: DisconnectReason) = {
    this(code, null, null)
  }

  def this(code: DisconnectReason, message: String) = {
    this(code, message, null);
  }

  def this(code: DisconnectReason, cause: Throwable) = {
    this(code, null, cause);
  }

  def this(message: String) = {
    this(DisconnectReason.UNKNOWN, message, null);
  }

  def this(message: String, cause: Throwable) = {
    this(DisconnectReason.UNKNOWN, message, cause);
  }

  def this(cause: Throwable) = {
    this(DisconnectReason.UNKNOWN, null, cause);
  }

  def getDisconnectReason(): DisconnectReason = {
    reason;
  }

  override def getMessage(): String = {
    if (super.getMessage() != null)
      return super.getMessage();
    else if (getCause() != null && getCause().getMessage() != null)
      return getCause().getMessage();
    else
      return null;
  }

  override def toString(): String = {
    val cls = getClass().getName();
    val code = if (reason != DisconnectReason.UNKNOWN) { "[" + reason + "] " } else { "" }
    val msg = if (getMessage() != null) { getMessage() } else { "" }
    cls + (if (code.isEmpty() && msg.isEmpty()) { "" } else { ": " }) + code + msg;
  }
}