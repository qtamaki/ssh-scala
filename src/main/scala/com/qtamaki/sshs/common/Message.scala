package com.qtamaki.sshs.common

sealed class Message(val b: Int) {
  def geq(num: Int): Boolean = {
    return b >= num;
  }

  def gt(num: Int): Boolean = {
    return b > num;
  }

  def in(x: Int, y: Int): Boolean = {
    return b >= x && b <= y;
  }

  def leq(num: Int): Boolean = {
    return b <= num;
  }

  def lt(num: Int): Boolean = {
    return b < num;
  }

  def toByte: Byte = {
    return b.toByte;
  }
}

object Message {

  val UNKNOWN = new Message(0);
  val DISCONNECT = new Message(1);
  val IGNORE = new Message(2);
  val UNIMPLEMENTED = new Message(3);
  val DEBUG = new Message(4);
  val SERVICE_REQUEST = new Message(5);
  val SERVICE_ACCEPT = new Message(6);
  val KEXINIT = new Message(20);
  val NEWKEYS = new Message(21);

  val KEXDH_INIT = new Message(30);

  /** { KEXDH_REPLY, KEXDH_GEX_GROUP, SSH_MSG_KEX_ECDH_REPLY } */
  val KEXDH_31 = new Message(31);

  val KEX_DH_GEX_INIT = new Message(32);
  val KEX_DH_GEX_REPLY = new Message(33);
  val KEX_DH_GEX_REQUEST = new Message(34);

  val USERAUTH_REQUEST = new Message(50);
  val USERAUTH_FAILURE = new Message(51);
  val USERAUTH_SUCCESS = new Message(52);
  val USERAUTH_BANNER = new Message(53);

  /** { USERAUTH_PASSWD_CHANGREQ, USERAUTH_PK_OK, USERAUTH_INFO_REQUEST } */
  val USERAUTH_60 = new Message(60);
  val USERAUTH_INFO_RESPONSE = new Message(61);

  val USERAUTH_GSSAPI_EXCHANGE_COMPLETE = new Message(63);
  val USERAUTH_GSSAPI_MIC = new Message(66);

  val GLOBAL_REQUEST = new Message(80);
  val REQUEST_SUCCESS = new Message(81);
  val REQUEST_FAILURE = new Message(82);

  val CHANNEL_OPEN = new Message(90);
  val CHANNEL_OPEN_CONFIRMATION = new Message(91);
  val CHANNEL_OPEN_FAILURE = new Message(92);
  val CHANNEL_WINDOW_ADJUST = new Message(93);
  val CHANNEL_DATA = new Message(94);
  val CHANNEL_EXTENDED_DATA = new Message(95);
  val CHANNEL_EOF = new Message(96);
  val CHANNEL_CLOSE = new Message(97);
  val CHANNEL_REQUEST = new Message(98);
  val CHANNEL_SUCCESS = new Message(99);
  val CHANNEL_FAILURE = new Message(100);

  val values = Array(UNKNOWN,
    DISCONNECT,
    IGNORE,
    UNIMPLEMENTED,
    DEBUG,
    SERVICE_REQUEST,
    SERVICE_ACCEPT,
    KEXINIT,
    NEWKEYS,
    KEXDH_INIT,
    KEXDH_31,
    KEX_DH_GEX_INIT,
    KEX_DH_GEX_REPLY,
    KEX_DH_GEX_REQUEST,
    USERAUTH_REQUEST,
    USERAUTH_FAILURE,
    USERAUTH_SUCCESS,
    USERAUTH_BANNER,
    USERAUTH_60,
    USERAUTH_INFO_RESPONSE,
    USERAUTH_GSSAPI_EXCHANGE_COMPLETE,
    USERAUTH_GSSAPI_MIC,
    GLOBAL_REQUEST,
    REQUEST_SUCCESS,
    REQUEST_FAILURE,
    CHANNEL_OPEN,
    CHANNEL_OPEN_CONFIRMATION,
    CHANNEL_OPEN_FAILURE,
    CHANNEL_WINDOW_ADJUST,
    CHANNEL_DATA,
    CHANNEL_EXTENDED_DATA,
    CHANNEL_EOF,
    CHANNEL_CLOSE,
    CHANNEL_REQUEST,
    CHANNEL_SUCCESS,
    CHANNEL_FAILURE)

  private val cache = new Array[Message](256);

  for (c <- Message.values)
    cache(c.toByte) = c;
  
  (0 until 256).foreach { i =>
    if (cache(i) == null)
      cache(i) = UNKNOWN;
  }

  def fromByte(b: Byte): Message = {
    return cache(b);
  }

}