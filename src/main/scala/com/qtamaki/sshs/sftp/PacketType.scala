package com.qtamaki.sshs.sftp

sealed abstract class PacketType(i: Int) {
  val b = i.asInstanceOf[Byte]
  
  def toByte() = b
}

object PacketType {
  object UNKNOWN extends PacketType(0)
  object INIT extends PacketType(1)
  object VERSION extends PacketType(2)
  object OPEN extends PacketType(3)
  object CLOSE extends PacketType(4)
  object READ extends PacketType(5)
  object WRITE extends PacketType(6)
  object LSTAT extends PacketType(7)
  object FSTAT extends PacketType(8)
  object SETSTAT extends PacketType(9)
  object FSETSTAT extends PacketType(10)
  object OPENDIR extends PacketType(11)
  object READDIR extends PacketType(12)
  object REMOVE extends PacketType(13)
  object MKDIR extends PacketType(14)
  object RMDIR extends PacketType(15)
  object REALPATH extends PacketType(16)
  object STAT extends PacketType(17)
  object RENAME extends PacketType(18)
  object READLINK extends PacketType(19)
  object SYMLINK extends PacketType(20)
  object STATUS extends PacketType(101)
  object HANDLE extends PacketType(102)
  object DATA extends PacketType(103)
  object NAME extends PacketType(104)
  object ATTRS extends PacketType(105)
  object EXTENDED extends PacketType(200)
  object EXTENDED_REPLY extends PacketType(201)

  val values:Map[Byte, PacketType] = Map(
    UNKNOWN.b -> UNKNOWN ,
    INIT.b -> INIT,
    VERSION.b -> VERSION,
    OPEN.b -> OPEN,
    CLOSE.b -> CLOSE,
    READ.b -> READ,
    WRITE.b -> WRITE,
    LSTAT.b -> LSTAT,
    FSTAT.b -> FSTAT,
    SETSTAT.b -> SETSTAT,
    FSETSTAT.b -> FSETSTAT,
    OPENDIR.b -> OPENDIR,
    READDIR.b -> READDIR,
    REMOVE.b -> REMOVE,
    MKDIR.b -> MKDIR,
    RMDIR.b -> RMDIR,
    REALPATH.b -> REALPATH,
    STAT.b -> STAT,
    RENAME.b -> RENAME,
    READLINK.b -> READLINK,
    SYMLINK.b -> SYMLINK,
    STATUS.b -> STATUS,
    HANDLE.b -> HANDLE,
    DATA.b -> DATA,
    NAME.b -> NAME,
    ATTRS.b -> ATTRS,
    EXTENDED.b -> EXTENDED,
    EXTENDED_REPLY.b -> EXTENDED_REPLY)

  def fromByte(b: Byte) = { values.get(b).getOrElse(UNKNOWN) }

}