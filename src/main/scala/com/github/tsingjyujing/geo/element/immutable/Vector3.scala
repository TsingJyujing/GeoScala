package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IVector3

/**
  * Implementation of IVector3
  *
  * @param x
  * @param y
  * @param z
  */
final case class Vector3(x: Double, y: Double, z: Double) extends IVector3 {
    override def getX: Double = x

    override def getY: Double = y

    override def getZ: Double = z

}
