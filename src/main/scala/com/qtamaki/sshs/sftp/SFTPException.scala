package com.qtamaki.sshs.sftp

import com.qtamaki.sshs.common.SSHException
import com.qtamaki.sshs.common.DisconnectReason
import com.qtamaki.concurrent.ExceptionChainer

object SFTPException {

      val chainer = new ExceptionChainer[SFTPException]() {

        override
        def chain(t:Throwable):SFTPException = {
          t match {
            case ex:SFTPException => ex
            case _ => new SFTPException(t)
          }
        }

    };


}

class SFTPException(code: DisconnectReason, message: String, cause: Throwable, val sc: Response.StatusCode) extends SSHException(code, message, cause) {

  def this(code: DisconnectReason, message: String, cause: Throwable) = {
    this(code, message, cause, null)
  }
  
    def this(code: DisconnectReason) = {
    this(code, null, null)
  }

  def this(code: DisconnectReason, message: String) = {
    this(code, message, null);
  }

  def this(code: DisconnectReason, cause: Throwable) = {
    this(code, null, cause);
  }

  def this(message: String) = {
    this(DisconnectReason.UNKNOWN, message, null);
  }

  def this(message: String, cause: Throwable) = {
    this(DisconnectReason.UNKNOWN, message, cause);
  }

  def this(cause: Throwable) = {
    this(DisconnectReason.UNKNOWN, null, cause);
  }
  
  def this(sc: Response.StatusCode, msg: String) = {
    this(DisconnectReason.UNKNOWN, msg, null, sc)
  }


}