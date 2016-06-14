package com.qtamaki.sshs.common

sealed abstract class DisconnectReason {
  def toInt() = DisconnectReason.values.indexOf(this)
}

object DisconnectReason {
  case object UNKNOWN extends DisconnectReason
  case object HOST_NOT_ALLOWED_TO_CONNECT extends DisconnectReason
  case object PROTOCOL_ERROR extends DisconnectReason
  case object KEY_EXCHANGE_FAILED extends DisconnectReason
  case object RESERVED extends DisconnectReason
  case object MAC_ERROR extends DisconnectReason
  case object COMPRESSION_ERROR extends DisconnectReason
  case object SERVICE_NOT_AVAILABLE extends DisconnectReason
  case object PROTOCOL_VERSION_NOT_SUPPORTED extends DisconnectReason
  case object HOST_KEY_NOT_VERIFIABLE extends DisconnectReason
  case object CONNECTION_LOST extends DisconnectReason
  case object BY_APPLICATION extends DisconnectReason
  case object TOO_MANY_CONNECTIONS extends DisconnectReason
  case object AUTH_CANCELLED_BY_USER extends DisconnectReason
  case object NO_MORE_AUTH_METHODS_AVAILABLE extends DisconnectReason
  case object ILLEGAL_USER_NAME extends DisconnectReason

  val values = Array(
    UNKNOWN,
    HOST_NOT_ALLOWED_TO_CONNECT,
    PROTOCOL_ERROR,
    KEY_EXCHANGE_FAILED,
    RESERVED,
    MAC_ERROR,
    COMPRESSION_ERROR,
    SERVICE_NOT_AVAILABLE,
    PROTOCOL_VERSION_NOT_SUPPORTED,
    HOST_KEY_NOT_VERIFIABLE,
    CONNECTION_LOST,
    BY_APPLICATION,
    TOO_MANY_CONNECTIONS,
    AUTH_CANCELLED_BY_USER,
    NO_MORE_AUTH_METHODS_AVAILABLE,
    ILLEGAL_USER_NAME)
    
    def fromInt(i: Int) = if(i < 0 || i > values.length){ UNKNOWN}else{values(i)}
  
}