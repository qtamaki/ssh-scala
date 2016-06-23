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
package com.qtamaki.sshs.sftp

import com.qtamaki.sshs.xfer.FilePermission
import com.qtamaki.sshs.common.Oct

case class FileMode(mask: Int) {
  val ftype: FileType = FileType.fromMask(mask) 
  val perms:Set[FilePermission] = FilePermission.fromMask(permissionsMask)
  
  def typeMask = mask & Oct(1,7,0,0,0,0)
  
  def permissionsMask = mask & Oct(7,7,7,7)
  
  def permissions = perms
  
  override def toString =  "[mask=" + Integer.toOctalString(mask) + "]";
}

sealed abstract class FileType(val perms: Int*) {
  val v = Oct(perms:_*)
  def toMask = v
  
}

object FileType {
  /** block special */
  object BLOCK_SPECIAL extends FileType(6, 0, 0, 0, 0)
  /** character special */
  object CHAR_SPECIAL extends FileType(2, 0, 0, 0, 0)
  /** FIFO special */
  object FIFO_SPECIAL extends FileType(1, 0, 0, 0, 0)
  /** socket special */
  object SOCKET_SPECIAL extends FileType(1, 4, 0, 0, 0, 0)
  /** regular */
  object REGULAR extends FileType(1, 0, 0, 0, 0, 0)
  /** directory */
  object DIRECTORY extends FileType(4, 0, 0, 0, 0)
  /** symbolic link */
  object SYMKLINK extends FileType(1, 2, 0, 0, 0, 0)
  /** unknown */
  object UNKNOWN extends FileType(0)

  val values = Array(
    BLOCK_SPECIAL,
    CHAR_SPECIAL,
    FIFO_SPECIAL,
    SOCKET_SPECIAL,
    REGULAR,
    DIRECTORY,
    SYMKLINK,
    UNKNOWN)

  def fromMask(mask: Int): FileType = {
    values.find(_.v == mask).getOrElse(UNKNOWN)
  }
}
  
