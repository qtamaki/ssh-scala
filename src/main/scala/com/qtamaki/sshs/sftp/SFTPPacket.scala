package com.qtamaki.sshs.sftp

import com.qtamaki.sshs.common.Buffer

class SFTPPacket(size:Int) extends Buffer(size) {

    def this(from:Buffer) = {
      this(from.wpos - from.rpos)
      _wpos = from.wpos - from.rpos
      System.arraycopy(from.data, from.rpos, data, 0, _wpos);
    }

    def this(pt:PacketType) = {
        this(Buffer.DEFAULT_SIZE)
        putByte(pt.toByte());
    }

    def readFileAttributes():FileAttributes = {
        val builder = new FileAttributes.Builder();
        try {
            val mask = readUInt32AsInt();
            if (FileAttributes.Flag.SIZE.isSet(mask))
                builder.withSize(readUInt64());
            if (FileAttributes.Flag.UIDGID.isSet(mask))
                builder.withUIDGID(readUInt32AsInt(), readUInt32AsInt());
            if (FileAttributes.Flag.MODE.isSet(mask))
                builder.withPermissions(readUInt32AsInt());
            if (FileAttributes.Flag.ACMODTIME.isSet(mask))
                builder.withAtimeMtime(readUInt32AsInt(), readUInt32AsInt());
            if (FileAttributes.Flag.EXTENDED.isSet(mask)) {
                val extCount = readUInt32AsInt();
                (0 until extCount).foreach { i =>
                  builder.withExtended(readString(), readString())
                }
            }
        } catch {
          case be:Buffer.BufferException =>
            throw new SFTPException(be);
        }
        return builder.build();
    }

    def readType():PacketType = {
        try {
            return PacketType.fromByte(readByte());
        } catch {
          case be:Buffer.BufferException =>
            throw new SFTPException(be);
        }
    }

    def putFileAttributes(fa:FileAttributes):this.type = {
        return putRawBytes(fa.toBytes());
    }

    def putType(ptype:PacketType):this.type = {
        return putByte(ptype.toByte());
    }
}
