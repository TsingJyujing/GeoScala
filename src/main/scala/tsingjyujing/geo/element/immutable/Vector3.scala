package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.IVector3

class Vector3(x: Double, y: Double, z: Double) extends IVector3 {
    override def getX: Double = x

    override def getY: Double = y

    override def getZ: Double = z

    override def +(v: IVector3): IVector3 = new Vector3(v.getX + getX, v.getY + getY, v.getZ + getZ)

    override def zero: IVector3 = new Vector3(0, 0, 0)
}
