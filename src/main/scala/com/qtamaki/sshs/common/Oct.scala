package com.qtamaki.sshs.common

object Oct {
  //def apply(seq:Seq[Int]):Int = seq.foldLeft(0)((a, o) => a * 8 + o)
  
  def apply(seq:Int*):Int = seq.foldLeft(0)((a, o) => a * 8 + o)
}