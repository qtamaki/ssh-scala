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
package com.qtamaki.sshs.transport.cipher

object Cipher {

  object Mode extends Enumeration {
    type Mode = Value
    val Encrypt, Decrypt = Value
  }

}

trait Cipher {
  def getBlockSize: Int

  def getIVSize: Int

  def init(mode: Cipher.Mode, key: Array[Byte], iv: Array[Byte])

  def update(input: Array[Byte], inputOffset: Int, inputLen: Int)
}