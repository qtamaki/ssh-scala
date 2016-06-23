package com.qtamaki.sshs.xfer

import com.qtamaki.sshs.common.Oct

abstract class FilePermission(val seq: Int*) {
  val v = Oct(seq:_*)
  
  def isIn(mask: Int) = (mask & v) == v
}

object FilePermission {
  def or(perms: FilePermission*) = {
    perms.foldLeft(0)((a, p) => a | p.v)
  }
  /** read permission, owner */
  object USR_R extends FilePermission(4, 0, 0)
  /** write permission, owner */
  object USR_W extends FilePermission(2, 0, 0)
  /** execute/search permission, owner */
  object USR_X extends FilePermission(1, 0, 0)
  /** read permission, group */
  object GRP_R extends FilePermission(0, 4, 0)
  /** write permission, group */
  object GRP_W extends FilePermission(0, 2, 0)
  /** execute/search permission, group */
  object GRP_X extends FilePermission(0, 1, 0)
  /** read permission, others */
  object OTH_R extends FilePermission(0, 0, 4)
  /** write permission, others */
  object OTH_W extends FilePermission(0, 0, 2)
  /** execute/search permission, group */
  object OTH_X extends FilePermission(0, 0, 1)
  /** set-user-ID on execution */
  object SUID extends FilePermission(4, 0, 0, 0)
  /** set-group-ID on execution */
  object SGID extends FilePermission(2, 0, 0, 0)
  /** on directories, restricted deletion flag */
  object STICKY extends FilePermission(1, 0, 0, 0)
  // Composite:
  /** read, write, execute/search by user */
  object USR_RWX extends FilePermission(or(USR_R, USR_W, USR_X))
  /** read, write, execute/search by group */
  object GRP_RWX extends FilePermission(or(GRP_R, GRP_W, GRP_X))
  /** read, write, execute/search by other */
  object OTH_RWX extends FilePermission(or(OTH_R, OTH_W, OTH_X))

  val values = Array(
    USR_R,
    USR_W,
    USR_X,
    GRP_R,
    GRP_W,
    GRP_X,
    OTH_R,
    OTH_W,
    OTH_X,
    SUID,
    SGID,
    STICKY,
    USR_RWX,
    GRP_RWX,
    OTH_RWX)

  def fromMask(mask: Int): Set[FilePermission] = {
    values.filter(_.isIn(mask)).toSet
  }

  def toMask(perms: Set[FilePermission]): Int = {
    perms.foldLeft(0)((a, p) => a | p.v)
  }

}