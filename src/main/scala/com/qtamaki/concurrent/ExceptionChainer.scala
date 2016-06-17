package com.qtamaki.concurrent

trait ExceptionChainer[Z <: Throwable] {
  def chain(t: Throwable): Z
}