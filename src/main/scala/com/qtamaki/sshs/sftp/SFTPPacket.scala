package com.qtamaki.sshs.sftp

import com.qtamaki.sshs.common.Buffer

class SFTPPacket extends Buffer(0) {

    def this(buf:Buffer) = {
      this(from._wpos - from._rpos)
      _wpos = from._wpos - from._rpos
      System.arraycopy(from.data, from._rpos, data, 0, _wpos);
    }

    public SFTPPacket(PacketType pt) {
        super();
        putByte(pt.toByte());
    }

    public FileAttributes readFileAttributes()
            throws SFTPException {
        final FileAttributes.Builder builder = new FileAttributes.Builder();
        try {
            final int mask = readUInt32AsInt();
            if (FileAttributes.Flag.SIZE.isSet(mask))
                builder.withSize(readUInt64());
            if (FileAttributes.Flag.UIDGID.isSet(mask))
                builder.withUIDGID(readUInt32AsInt(), readUInt32AsInt());
            if (FileAttributes.Flag.MODE.isSet(mask))
                builder.withPermissions(readUInt32AsInt());
            if (FileAttributes.Flag.ACMODTIME.isSet(mask))
                builder.withAtimeMtime(readUInt32AsInt(), readUInt32AsInt());
            if (FileAttributes.Flag.EXTENDED.isSet(mask)) {
                final int extCount = readUInt32AsInt();
                for (int i = 0; i < extCount; i++)
                    builder.withExtended(readString(), readString());
            }
        } catch (BufferException be) {
            throw new SFTPException(be);
        }
        return builder.build();
    }

    public PacketType readType()
            throws SFTPException {
        try {
            return PacketType.fromByte(readByte());
        } catch (BufferException be) {
            throw new SFTPException(be);
        }
    }

    public T putFileAttributes(FileAttributes fa) {
        return putRawBytes(fa.toBytes());
    }

    public T putType(PacketType type) {
        return putByte(type.toByte());
    }

}
