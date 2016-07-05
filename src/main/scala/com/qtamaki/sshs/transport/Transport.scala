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
package com.qtamaki.sshs.transport

import com.qtamaki.sshs.Config
import com.qtamaki.sshs.Service
import com.qtamaki.sshs.common.DisconnectReason
import com.qtamaki.sshs.common.SSHPacket
import com.qtamaki.sshs.common.SSHPacketHandler
import com.qtamaki.sshs.transport.verification.AlgorithmsVerifier
import com.qtamaki.sshs.transport.verification.HostKeyVerifier
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

trait Transport extends SSHPacketHandler {
  def init(host: String, port: Int, in: InputStream, out: OutputStream)

  def addHostKeyVerifier(hkv: HostKeyVerifier)

  def addAlgorithmsVerifier(verifier: AlgorithmsVerifier)

  def doKex

  def getClientVersion: String

  def getConfig: Config

  def getTimeoutMs: Int

  def setTimeoutMs(timeout: Int)

  @deprecated def getHeartbeatInterval: Int

  @deprecated def setHeartbeatInterval(interval: Int)

  def getRemoteHost: String

  def getRemotePort: Int

  def getServerVersion: String

  def getSessionID: Array[Byte]

  def getService: Service

  def reqService(service: Service)

  def setService(service: Service)

  def isAuthenticated: Boolean

  def setAuthenticated

  def sendUnimplemented: Long

  def isRunning: Boolean

  def join

  def join(timeout: Int, unit: TimeUnit)

  def disconnect

  def disconnect(reason: DisconnectReason)

  def disconnect(reason: DisconnectReason, message: String)

  def write(payload: SSHPacket): Long

  def setDisconnectListener(listener: DisconnectListener)

  def getDisconnectListener: DisconnectListener

  def die(e: Exception)
}