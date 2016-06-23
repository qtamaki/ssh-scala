/*
 * Copyright (C)2009 - SSHJ Contributors
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
package com.qtamaki.sshs.connection

import com.qtamaki.concurrent.ExceptionChainer;
import com.qtamaki.sshs.common.DisconnectReason;
import com.qtamaki.sshs.common.SSHException;

object ConnectionException {
  def chainer = new ExceptionChainer[ConnectionException]() {
    override def chain(t: Throwable): ConnectionException = {
      t match {
        case c: ConnectionException => c
        case _ => new ConnectionException(t)
      }
    }
  };

}

/** Connection-layer exception. */
class ConnectionException(code: DisconnectReason, message: String, cause: Throwable)
    extends SSHException(code, message, cause) {

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

}
