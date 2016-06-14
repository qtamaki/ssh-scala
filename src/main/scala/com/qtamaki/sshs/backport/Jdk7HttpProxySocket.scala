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
package com.qtamaki.sshs.backport

import java.net._
import java.nio.charset.Charset

case class Jdk7HttpProxySocket(httpProxy: Proxy) extends Socket(if (httpProxy.`type` == Proxy.Type.HTTP) { Proxy.NO_PROXY } else { httpProxy }) {

  override def connect(endpoint: SocketAddress, timeout: Int) {
    if (httpProxy != null) {
      connectHttpProxy(endpoint, timeout);
    } else {
      super.connect(endpoint, timeout);
    }
  }

  private def connectHttpProxy(endpoint: SocketAddress, timeout: Int) {
    super.connect(httpProxy.address(), timeout);

    endpoint match {
      case isa: InetSocketAddress =>
        val httpConnect = "CONNECT " + isa.getHostName() + ":" + isa.getPort() + " HTTP/1.0\n\n"
        getOutputStream().write(httpConnect.getBytes(Charset.forName("UTF-8")))
        checkAndFlushProxyResponse()

      case _ =>
        throw new SocketException("Expected an InetSocketAddress to connect to, got: " + endpoint);
    }
  }

  private def checkAndFlushProxyResponse() {
    val socketInput = getInputStream()
    val tmpBuffer: Array[Byte] = new Array[Byte](512)

    val len = socketInput.read(tmpBuffer, 0, tmpBuffer.length)

    if (len == 0) {
      throw new SocketException("Empty response from proxy")
    }

    val proxyResponse = new String(tmpBuffer, 0, len, "UTF-8")

    // Expecting HTTP/1.x 200 OK
    if (proxyResponse.contains("200")) {
      // Flush any outstanding message in buffer
      if (socketInput.available() > 0) {
        socketInput.skip(socketInput.available());
      }
      // Proxy Connect Successful
    } else {
      throw new SocketException("Fail to create Socket\nResponse was:" + proxyResponse);
    }
  }
}