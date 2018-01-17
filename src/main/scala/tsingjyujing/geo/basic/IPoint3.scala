package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations.{Angleable, DistanceMeasurable, InnerProductable, Normable}

trait IPoint3 extends InnerProductable[IPoint3] with Normable with Angleable[IPoint3] with DistanceMeasurable[IPoint3] {
    def getX: Double

    def getY: Double

    def getZ: Double

    override def norm(n: Double): Double = {
        math.pow(math.pow(getX, n) + math.pow(getY, n) + math.pow(getZ, n), 1.0 / n)
    }

    override def norm2: Double = math.sqrt(IPoint3.getPow2Sum(getX, getY, getZ))

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def to(point: IPoint3): Double = math.sqrt(IPoint3.getPow2Sum(getX - point.getX, getY - point.getY, getZ - point.getZ))

    override def innerProduct(point: IPoint3): Double = IPoint3.innerProduct3(this, point)

    override def conAngle(x: IPoint3): Double = IPoint3.cosAngle(this, x)
}

object IPoint3 {
    def getPow2Sum(x: Double, y: Double, z: Double): Double = x * x + y * y + z * z

    def innerProduct3(point1: IPoint3, point2: IPoint3): Double = point1.getX * point2.getX + point1.getY * point2.getY + point1.getZ * point2.getZ

    def cosAngle(point1: IPoint3, point2: IPoint3): Double = (point1 innerProduct point2) / (point1.norm2 * point2.norm2)
}
