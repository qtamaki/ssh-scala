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
package com.qtamaki.sshs.transport.compression

import com.qtamaki.sshs.common.Buffer
import com.qtamaki.sshs.transport.TransportException

object Compression {

  object Mode extends Enumeration {
    type Mode = Value
    val INFLATE, DEFLATE = Value
  }

}

trait Compression {
  def init(mode: Compression.Mode)

  def isDelayed: Boolean

  def compress(buffer: Buffer[_ <: Buffer[T]])

  @throws[TransportException]
  def uncompress(from: Buffer[_ <: Buffer[T]], to: Buffer[_ <: Buffer[T]])
}