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

final class NegotiatedAlgorithms private[transport](val kex: String, val sig: String, val c2sCipher: String, val s2cCipher: String, val c2sMAC: String, val s2cMAC: String, val c2sComp: String, val s2cComp: String) {
  def getKeyExchangeAlgorithm: String = {
    return kex
  }

  def getSignatureAlgorithm: String = {
    return sig
  }

  def getClient2ServerCipherAlgorithm: String = {
    return c2sCipher
  }

  def getServer2ClientCipherAlgorithm: String = {
    return s2cCipher
  }

  def getClient2ServerMACAlgorithm: String = {
    return c2sMAC
  }

  def getServer2ClientMACAlgorithm: String = {
    return s2cMAC
  }

  def getClient2ServerCompressionAlgorithm: String = {
    return c2sComp
  }

  def getServer2ClientCompressionAlgorithm: String = {
    return s2cComp
  }

  override def toString: String = {
    return ("[ " + "kex=" + kex + "; " + "sig=" + sig + "; " + "c2sCipher=" + c2sCipher + "; " + "s2cCipher=" + s2cCipher + "; " + "c2sMAC=" + c2sMAC + "; " + "s2cMAC=" + s2cMAC + "; " + "c2sComp=" + c2sComp + "; " + "s2cComp=" + s2cComp + " ]")
  }
}