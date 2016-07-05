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
package com.qtamaki.sshs.transport.mac

trait MAC {
  def doFinal: Array[Byte]

  def doFinal(input: Array[Byte]): Array[Byte]

  def doFinal(buf: Array[Byte], offset: Int)

  def getBlockSize: Int

  def init(key: Array[Byte])

  def update(foo: Array[Byte])

  def update(foo: Array[Byte], start: Int, len: Int)

  def update(foo: Long)
}