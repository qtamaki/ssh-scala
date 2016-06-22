package com.qtamaki.sshs.common

object Oct {
  def apply(seq:Seq[Int]) = seq.foldLeft(0)((a, o) => a * 8 + o)
  def apply(seq:Int*) = seq.foldLeft(0)((a, o) => a * 8 + o)
}