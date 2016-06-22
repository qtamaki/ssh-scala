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

import com.qtamaki.sshs.common.Buffer;
import com.qtamaki.sshs.xfer.FilePermission;

object FileAttributes {
  val EMPTY: FileAttributes = new FileAttributes();

  sealed abstract class Flag(val flag: Int) {
    def isSet(mask: Int) = (mask & flag) == flag;

    def get(): Int = {
      return flag;
    }
  }

  object Flag {
    object SIZE extends Flag(0x00000001)
    object UIDGID extends Flag(0x00000002)
    object MODE extends Flag(0x00000004)
    object ACMODTIME extends Flag(0x00000008)
    object EXTENDED extends Flag(0x80000000)
  }

  class Builder {

    private var mask: Int = 0
    private var size: Long = 0
    private var atime: Long = 0
    private var mtime: Long = 0
    private var mode: FileMode = new FileMode(0);
    private var uid: Int = 0
    private var gid: Int = 0
    private val ext: scala.collection.mutable.Map[String, String] = Map.empty();

    def withSize(size: Long): Builder = {
      mask |= Flag.SIZE.get();
      this.size = size;
      return this;
    }

    def withAtimeMtime(atime: Long, mtime: Long): Builder = {
      mask |= Flag.ACMODTIME.get();
      this.atime = atime;
      this.mtime = mtime;
      return this;
    }

    def withUIDGID(uid: Int, gid: Int): Builder = {
      mask |= Flag.UIDGID.get();
      this.uid = uid;
      this.gid = gid;
      return this;
    }

    def withPermissions(perms: Set[FilePermission]): Builder = {
      mask |= Flag.MODE.get();
      this.mode = new FileMode((if (mode != null) { mode.typeMask } else { 0 }) | FilePermission.toMask(perms));
      return this;
    }

    def withPermissions(perms: Int): Builder = {
      mask |= Flag.MODE.get();
      this.mode = new FileMode((if (mode != null) { mode.typeMask } else { 0 }) | perms);
      return this;
    }

    def withType(typ: FileType): Builder = {
      mask |= Flag.MODE.get();
      this.mode = new FileMode(typ.toMask | (if (mode != null) { mode.permissionsMask } else { 0 }));
      return this;
    }

    def withExtended(typ: String, data: String): Builder = {
      mask |= Flag.EXTENDED.get();
      ext.put(typ, data);
      return this;
    }

    def withExtended(ext: Map[String, String]): Builder = {
      mask |= Flag.EXTENDED.get();
      this.ext ++= ext
      return this;
    }

    def build(): FileAttributes = {
      return new FileAttributes(mode, mask, size, uid, gid, atime, mtime, ext.toMap);
    }

  }
}

final case class FileAttributes(mode: FileMode, mask: Int, size: Long, uid: Int, gid: Int, atime: Long, mtime: Long, ext: Map[String, String]) {
  import FileAttributes._
  
  def this() = {
    this(new FileMode(0), 0, 0, 0, 0, 0, 0, Map.empty)
  }

  def has(flag: Flag) = flag.isSet(mask)

  def getType = mode.ftype

  def extended(typ: String): String = ext.get(typ).getOrElse(null)

  def toBytes(): Array[Byte] = {
    val buf = new Buffer.PlainBuffer();
    buf.putUInt32(mask);

    if (has(Flag.SIZE))
      buf.putUInt64(size);

    if (has(Flag.UIDGID)) {
      buf.putUInt32(uid);
      buf.putUInt32(gid);
    }

    if (has(Flag.MODE))
      buf.putUInt32(mode.mask);

    if (has(Flag.ACMODTIME)) {
      buf.putUInt32(atime);
      buf.putUInt32(mtime);
    }

    if (has(Flag.EXTENDED)) {
      buf.putUInt32(ext.size);
      ext.foreach {
        case (k, v) =>
          buf.putString(k);
          buf.putString(v);
      }
    }
    return buf.getCompactData();
  }

  override def toString() = {
    val sb = new StringBuilder("[");

    if (has(Flag.SIZE))
      sb.append("size=").append(size).append(";");

    if (has(Flag.UIDGID))
      sb.append("uid=").append(size).append(",gid=").append(gid).append(";");

    if (has(Flag.MODE))
      sb.append("mode=").append(mode.toString()).append(";");

    if (has(Flag.ACMODTIME))
      sb.append("atime=").append(atime).append(",mtime=").append(mtime).append(";");

    if (has(Flag.EXTENDED))
      sb.append("ext=").append(ext);

    sb.append("]");

    sb.toString()
  }

}
