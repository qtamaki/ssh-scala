package com.qtamaki.sshs.common

import java.util.Arrays;

final class SSHPacket(size: Int)
    extends Buffer(size) {

  def this() = {
    this(0)
  }

  def this(data: Array[Byte]) = {
    this(0)
    this.data = data
    _rpos = 0;
    _wpos = data.length
  }

  /**
   * Constructs new buffer for the specified SSH packet and reserves the needed space (5 bytes) for the packet
   * header.
   *
   * @param msg the SSH command
   */
  def this(msg: Message) = {
    this(0)
    _rpos = 5
    _wpos = 5
    putMessageID(msg);
  }

  def this(p:SSHPacket) = {
    this(0)
    this.data = Arrays.copyOf(p.data, p.wpos);
    this._rpos = p.rpos;
    this._wpos = p.wpos;
  }

  /**
   * Reads an SSH byte and returns it as {@link Message}
   *
   * @return the message identifier
   */
  def readMessageID():Message = {
    return Message.fromByte(readByte());
  }

  /**
   * Writes a byte indicating the SSH message identifier
   *
   * @param msg the identifier as a {@link Message} type
   *
   * @return this
   */
  def putMessageID(msg:Message):SSHPacket = {
    return putByte(msg.toByte);
  }

}
