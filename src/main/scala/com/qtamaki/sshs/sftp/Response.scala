package com.qtamaki.sshs.sftp

object Response {
  sealed abstract class StatusCode(val code: Int) {

  }
  object StatusCode {
    object UNKNOWN extends StatusCode(-1)
    object OK extends StatusCode(0)
    object EOF extends StatusCode(1)
    object NO_SUCH_FILE extends StatusCode(2)
    object PERMISSION_DENIED extends StatusCode(3)
    object FAILURE extends StatusCode(4)
    object BAD_MESSAGE extends StatusCode(5)
    object NO_CONNECTION extends StatusCode(6)
    object CONNECITON_LOST extends StatusCode(7)
    object OP_UNSUPPORTED extends StatusCode(8)

    val values = Array(
      UNKNOWN,
      OK,
      EOF,
      NO_SUCH_FILE,
      PERMISSION_DENIED,
      FAILURE,
      BAD_MESSAGE,
      NO_CONNECTION,
      CONNECITON_LOST,
      OP_UNSUPPORTED)
    def fromInt(code: Int): StatusCode = {
      values.find(code == _.code).getOrElse(UNKNOWN)
    }

  }

}