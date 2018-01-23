package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.IVector3

class Vector3(x: Double, y: Double, z: Double) extends IVector3 {
    override def getX: Double = x

    override def getY: Double = y

    override def getZ: Double = z
}
