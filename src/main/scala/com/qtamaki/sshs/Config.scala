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
package com.qtamaki.sshs

import com.qtamaki.keepalive.KeepAliveProvider
import com.qtamaki.sshs.common.Factory
import com.qtamaki.sshs.signature.Signature
import com.qtamaki.sshs.transport.cipher.Cipher
import com.qtamaki.sshs.transport.compression.Compression
import com.qtamaki.sshs.transport.kex.KeyExchange
import com.qtamaki.sshs.transport.mac.MAC
import com.qtamaki.sshs.transport.random.Random
import com.qtamaki.sshs.userauth.keyprovider.FileKeyProvider

trait Config {
  def getCipherFactories: List[Factory.Named[Cipher]]

  def getCompressionFactories: List[Factory.Named[Compression]]

  def getFileKeyProviderFactories: List[Factory.Named[FileKeyProvider]]

  def getKeyExchangeFactories: List[Factory.Named[KeyExchange]]

  def getMACFactories: List[Factory.Named[MAC]]

  def getRandomFactory: Factory[Random]

  def getSignatureFactories: List[Factory.Named[Signature]]

  def getVersion: String

  def setCipherFactories(cipherFactories: List[Factory.Named[Cipher]])

  def setCompressionFactories(compressionFactories: List[Factory.Named[Compression]])

  def setFileKeyProviderFactories(fileKeyProviderFactories: List[Factory.Named[FileKeyProvider]])

  def setKeyExchangeFactories(kexFactories: List[Factory.Named[KeyExchange]])

  def setMACFactories(macFactories: List[Factory.Named[MAC]])

  def setRandomFactory(randomFactory: Factory[Random])

  def setSignatureFactories(signatureFactories: List[Factory.Named[Signature]])

  def setVersion(version: String)

  def getKeepAliveProvider: KeepAliveProvider

  def setKeepAliveProvider(keepAliveProvider: KeepAliveProvider)

  def isWaitForServerIdentBeforeSendingClientIdent: Boolean

  def setWaitForServerIdentBeforeSendingClientIdent(waitForServerIdentBeforeSendingClientIdent: Boolean)
}