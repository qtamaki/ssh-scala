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
package com.qtamaki.sshs.common

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Arrays;

object Buffer {
  class BufferException(message: String) extends SSHException(message)

  val DEFAULT_SIZE: Int = 256
  val MAX_SIZE: Int = (1 << 30)

  protected def getNextPowerOf2(i: Int): Int = {
    var j: Int = 1;
    while (j < i) {
      j <<= 1;
      if (j <= 0) throw new IllegalArgumentException("Cannot get next power of 2; " + i + " is too large");
    }
    return j;
  }

  final class PlainBuffer(size: Int) extends Buffer(size) {

    def this() = {
      this(0)
    }

    def this(from: Buffer) = {
      this(from._wpos - from._rpos)
      _wpos = from._wpos - from._rpos
      System.arraycopy(from.data, from._rpos, data, 0, _wpos);
    }

    def this(b: Array[Byte]) = {
      this(0)
      this.data = data
      _rpos = 0;
      _wpos = data.length
    }

  }
}

abstract class Buffer(size: Int) {
  import Buffer._

  protected var data: Array[Byte] = null
  protected var _rpos: Int = 0
  protected var _wpos: Int = 0

  /** @see #DEFAULT_SIZE */
  def this() {
    this(Buffer.DEFAULT_SIZE);
  }

  def this(from: Buffer) {
    this(from._wpos - from._rpos)
    _wpos = from._wpos - from._rpos
    System.arraycopy(from.data, from._rpos, data, 0, _wpos);
  }

  def this(data: Array[Byte]) {
    this(0)
    this.data = data
    _rpos = 0;
    _wpos = data.length
  }

  //    public Buffer(int size) {
  //        this(new byte[getNextPowerOf2(size)], false);
  //    }
  //
  //    private def this(data: Array[Byte], read:Boolean) {
  //        this.data = data;
  //        rpos = 0;
  //        wpos = read ? data.length : 0;
  //    }

  def array(): Array[Byte] = data

  def available(): Int = wpos - rpos

  /** Resets this buffer. The object becomes ready for reuse. */
  def clear() {
    _rpos = 0;
    _wpos = 0;
  }

  def rpos = _rpos

  def rpos(rpos: Int) {
    _rpos = rpos
  }

  def wpos = _wpos

  def wpos(wpos: Int) {
    ensureCapacity(wpos - this.wpos)
    _wpos = wpos;
  }

  protected def ensureAvailable(a: Int) {
    if (available() < a)
      throw new BufferException("Underflow");
  }

  def ensureCapacity(capacity: Int) {
    if (data.length - wpos < capacity) {
      val cw = wpos + capacity;
      val tmp = new Array[Byte](Buffer.getNextPowerOf2(cw))
      System.arraycopy(data, 0, tmp, 0, data.length);
      data = tmp;
    }
  }

  /** Compact this {@link SSHPacket} */
  def compact() {
    System.err.println("COMPACTING");
    if (available() > 0)
      System.arraycopy(data, rpos, data, 0, wpos - rpos)
    _wpos = _wpos - _rpos
    _rpos = 0
  }

  def getCompactData(): Array[Byte] = {
    val len = available()
    if (len > 0) {
      val b = new Array[Byte](len)
      System.arraycopy(data, rpos, b, 0, len);
      return b;
    } else
      return new Array[Byte](0)
  }

  /**
   * Read an SSH boolean byte
   *
   * @return the {@code true} or {@code false} value read
   */
  def readBoolean(): Boolean = readByte() != 0

  /**
   * Puts an SSH boolean value
   *
   * @param b the value
   *
   * @return this
   */
  def putBoolean(b: Boolean): this.type = {
    val x = (if (b) { 1 } else { 0 }).toByte
    putByte(x)
  }

  /**
   * Read a byte from the buffer
   *
   * @return the byte read
   */
  def readByte(): Byte = {
    ensureAvailable(1)
    val d = data(rpos)
    _rpos += 1
    d
  }

  /**
   * Writes a single byte into this buffer
   *
   * @param b
   *
   * @return this
   */
  def putByte(b: Byte): this.type = {
    ensureCapacity(1);
    data(wpos) = b
    _wpos += 1
    return this
  }

  /**
   * Read an SSH byte-array
   *
   * @return the byte-array read
   */
  def readBytes(): Array[Byte] = {
    val len = readUInt32AsInt();
    if (len < 0 || len > 32768)
      throw new BufferException("Bad item length: " + len);
    val b = new Array[Byte](len)
    readRawBytes(b);
    return b;
  }

  /**
   * Writes Java byte-array as an SSH byte-array
   *
   * @param b Java byte-array
   *
   * @return this
   */
  def putBytes(b: Array[Byte]): this.type = putBytes(b, 0, b.length)

  /**
   * Writes Java byte-array as an SSH byte-array
   *
   * @param b   Java byte-array
   * @param off offset
   * @param len length
   *
   * @return this
   */
  def putBytes(b: Array[Byte], off: Int, len: Int): this.type = {
    val x = putUInt32(len - off)
    x.putRawBytes(b, off, len)
  }

  def readRawBytes(buf: Array[Byte]) {
    readRawBytes(buf, 0, buf.length);
  }

  def readRawBytes(buf: Array[Byte], off: Int, len: Int) {
    ensureAvailable(len);
    System.arraycopy(data, rpos, buf, off, len);
    _rpos += len;
  }

  def putRawBytes(d: Array[Byte]): this.type = {
    putRawBytes(d, 0, d.length);
  }

  def putRawBytes(d: Array[Byte], off: Int, len: Int): this.type = {
    ensureCapacity(len);
    System.arraycopy(d, off, data, wpos, len);
    _wpos += len;
    this
  }

  /**
   * Copies the contents of provided buffer into this buffer
   *
   * @param buffer the {@code Buffer} to copy
   *
   * @return this
   */
  def putBuffer(buffer: Buffer): this.type = {
    if (buffer != null) {
      val r = buffer.available();
      ensureCapacity(r);
      System.arraycopy(buffer.data, buffer.rpos, data, wpos, r);
      _wpos += r;
    }
    return this
  }

  def readUInt32AsInt(): Int = {
    readUInt32().toInt
  }

  def readUInt32(): Long = {
    ensureAvailable(4);
    val x = data(rpos) << 24 & 0xff000000L |
      data(rpos + 1) << 16 & 0x00ff0000L |
      data(rpos + 2) << 8 & 0x0000ff00L |
      data(rpos + 3) & 0x000000ffL
    _rpos += 4
    x
  }

  /**
   * Writes a uint32 integer
   *
   * @param uint32
   *
   * @return this
   */
  def putUInt32(uint32: Long): this.type = {
    ensureCapacity(4);
    if (uint32 < 0 || uint32 > 0xffffffffL)
      throw new RuntimeException("Invalid value: " + uint32);
    data(wpos) = (uint32 >> 24).toByte
    data(wpos + 1) = (uint32 >> 16).toByte
    data(wpos + 2) = (uint32 >> 8).toByte
    data(wpos + 3) = uint32.toByte
    _wpos += 4
    this
  }

  /**
   * Read an SSH multiple-precision integer
   *
   * @return the MP integer as a {@code BigInteger}
   */
  def readMPInt(): BigInteger = {
    return new BigInteger(readBytes());
  }

  def putMPInt(bi: BigInteger): this.type = {
    val asBytes = bi.toByteArray()
    putUInt32(asBytes.length)
    putRawBytes(asBytes)
  }

  def readUInt64(): Long = {
    val uint64 = (readUInt32() << 32) + (readUInt32() & 0xffffffffL)
    if (uint64 < 0)
      throw new BufferException("Cannot handle values > Long.MAX_VALUE")
    return uint64
  }

  def putUInt64(uint64: Long): this.type = {
    if (uint64 < 0)
      throw new RuntimeException("Invalid value: " + uint64);
    data(wpos) = (uint64 >> 56).toByte
    data(wpos + 1) = (uint64 >> 48).toByte
    data(wpos + 2) = (uint64 >> 40).toByte
    data(wpos + 3) = (uint64 >> 32).toByte
    data(wpos + 4) = (uint64 >> 24).toByte
    data(wpos + 5) = (uint64 >> 16).toByte
    data(wpos + 6) = (uint64 >> 8).toByte
    data(wpos + 7) = uint64.toByte
    _wpos += 8
    this
  }

  /**
   * Reads an SSH string
   *
   * @return the string as a Java {@code String}
   */
  def readString(): String = {
    val len = readUInt32AsInt()
    if (len < 0 || len > 32768)
      throw new BufferException("Bad item length: " + len)
    ensureAvailable(len)

    val s = try {
      new String(data, rpos, len, "UTF-8")
    } catch {
      case e: UnsupportedEncodingException =>
        throw new SSHRuntimeException(e)
    }
    _rpos += len;
    return s;
  }

  /**
   * Reads an SSH string
   *
   * @return the string as a byte-array
   */
  def readStringAsBytes(): Array[Byte] = {
    return readBytes();
  }

  def putString(str: Array[Byte]): this.type = {
    return putBytes(str);
  }

  def putString(str: Array[Byte], offset: Int, len: Int): this.type = {
    return putBytes(str, offset, len)
  }

  def putString(string: String): this.type = {
    return putString(string.getBytes(IOUtils.UTF8))
  }

  /**
   * Writes a char-array as an SSH string and then blanks it out.
   * <p/>
   * This is useful when a plaintext password needs to be sent. If {@code str} is {@code null}, an empty string is
   * written.
   *
   * @param str (null-ok) the string as a character array
   *
   * @return this
   */
  def putSensitiveString(str: Array[Char]): this.type = {
    if (str == null) return putString("");
    putUInt32(str.length);
    ensureCapacity(str.length);
    str.foreach { c: Char =>
      data(wpos) = c.toByte
      _wpos += 1
    }
    Arrays.fill(str, ' ');
    return this
  }

  def readPublicKey(): PublicKey = {
    try {
      val typestr = readString();
      KeyType.fromString(typestr).readPubKeyFromBuffer(typestr, this);
    } catch {
      case e: GeneralSecurityException =>
        throw new SSHRuntimeException(e);
    }
  }

  def putPublicKey(key: PublicKey): this.type = {
    KeyType.fromKey(key).putPubKeyIntoBuffer(key, this);
    return this
  }

  def putSignature(sigFormat: String, sigData: Array[Byte]): this.type = {
    val sig = new PlainBuffer().putString(sigFormat).putBytes(sigData).getCompactData();
    return putString(sig);
  }

  /**
   * Gives a readable snapshot of the buffer in hex. This is useful for debugging.
   *
   * @return snapshot of the buffer as a hex string with each octet delimited by a space
   */
  def printHex(): String = {
    return ByteArrayUtils.printHex(array(), rpos, available());
  }

  override def toString(): String = {
    return "Buffer [rpos=" + rpos + ", wpos=" + wpos + ", size=" + data.length + "]";
  }

}
