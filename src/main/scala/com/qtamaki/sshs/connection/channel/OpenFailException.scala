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
package com.qtamaki.sshs.connection.channel

import com.qtamaki.sshs.connection.ConnectionException
import com.qtamaki.sshs.common.DisconnectReason

object OpenFailException {
  sealed abstract class Reason(val code: Int) {
  }
  object Reason {
    object UNKNOWN extends Reason(0)
    object ADMINISTRATIVELY_PROHIBITED extends Reason(1)
    object CONNECT_FAILED extends Reason(2)
    object UNKNOWN_CHANNEL_TYPE extends Reason(3)
    object RESOURCE_SHORTAGE extends Reason(4)

    val values = Array(
      UNKNOWN,
      ADMINISTRATIVELY_PROHIBITED,
      CONNECT_FAILED,
      UNKNOWN_CHANNEL_TYPE,
      RESOURCE_SHORTAGE)

    def fromInt(code: Int): Reason = {
      values.find(_.code == code).getOrElse(UNKNOWN)
    }
  }
}

class OpenFailException(channelType: String, reason: OpenFailException.Reason, code: DisconnectReason, message: String, cause: Throwable)
    extends ConnectionException(code, message, cause) {

  def this(channelType: String, reasonCode: Int, message: String) {
    this(channelType, OpenFailException.Reason.fromInt(reasonCode), DisconnectReason.UNKNOWN, message, null)
  }

  def this(channelType: String, reason: OpenFailException.Reason, message: String) {
    this(channelType, reason, DisconnectReason.UNKNOWN, message, null)
  }

  override def getMessage = message

  override def toString() = "Opening `" + channelType + "` channel failed: " + getMessage()

}
