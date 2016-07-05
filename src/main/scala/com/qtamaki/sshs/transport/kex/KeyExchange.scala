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
package com.qtamaki.sshs.transport.kex

import com.qtamaki.sshs.common.Message
import com.qtamaki.sshs.common.SSHPacket
import com.qtamaki.sshs.transport.Transport
import com.qtamaki.sshs.transport.digest.Digest
import java.math.BigInteger
import java.security.PublicKey

trait KeyExchange {
  def init(trans: Transport, V_S: String, V_C: String, I_S: Array[Byte], I_C: Array[Byte])

  def getH: Array[Byte]

  def getK: BigInteger

  def getHash: Digest

  def getHostKey: PublicKey

  def next(msg: Message, buffer: SSHPacket): Boolean
}