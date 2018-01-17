package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations._
import tsingjyujing.geo.util.mathematical.VectorUtil

trait Vector3
    extends InnerProductable[Vector3]
        with Normable
        with Angleable[Vector3]
        with DistanceMeasurable[Vector3]
        with Iterable[Double]
        with OutProductable[Vector3, Vector3] {

    def getX: Double

    def getY: Double

    def getZ: Double

    override def norm(n: Double): Double = {
        math.pow(math.pow(getX, n) + math.pow(getY, n) + math.pow(getZ, n), 1.0 / n)
    }

    override def norm2: Double = math.sqrt(Vector3.getPow2Sum(getX, getY, getZ))

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def to(point: Vector3): Double = math.sqrt(Vector3.getPow2Sum(getX - point.getX, getY - point.getY, getZ - point.getZ))

    override def innerProduct(point: Vector3): Double = Vector3.innerProduct3(this, point)

    override def conAngle(x: Vector3): Double = Vector3.cosAngle(this, x)

    def getVector:Array[Double] = Array(getX, getY, getZ)

    override def iterator: Iterator[Double] = getVector.iterator

    override def outProduct(x: Vector3): Vector3 = {

        val vec = VectorUtil.outerProduct(getVector,x.getVector)

        new Vector3 {

            override def getX: Double = vec(0)

            override def getY: Double = vec(1)

            override def getZ: Double = vec(2)

        }

    }
}

object Vector3 {
    def getPow2Sum(x: Double, y: Double, z: Double): Double = x * x + y * y + z * z

    def innerProduct3(point1: Vector3, point2: Vector3): Double = point1.getX * point2.getX + point1.getY * point2.getY + point1.getZ * point2.getZ

    def cosAngle(point1: Vector3, point2: Vector3): Double = (point1 innerProduct point2) / (point1.norm2 * point2.norm2)
}
