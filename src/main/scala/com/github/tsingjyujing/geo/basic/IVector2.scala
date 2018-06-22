package com.github.tsingjyujing.geo.basic


import com.github.tsingjyujing.geo.basic.operations._
import com.github.tsingjyujing.geo.element.immutable.Vector2

/**
  * vector in 2d Euclid space
  */
trait IVector2
    extends IVector
        with InnerProductable[IVector2]
        with Angleable[IVector2]
        with DistanceMeasurable[IVector2]
        with Addable[IVector2] {

    override def +(v: IVector2): Vector2 = Vector2(getX + v.getX, getY + v.getY)

    def -(v: IVector2): Vector2 = Vector2(getX - v.getX, getY - v.getY)

    def *(v: IVector2): Vector2 = Vector2(getX * v.getX, getY * v.getY)

    def /(v: IVector2): Vector2 = Vector2(getX / v.getX, getY / v.getY)

    def +(v: Double): Vector2 = Vector2(getX + v, getY + v)

    def -(v: Double): Vector2 = Vector2(getX - v, getY - v)

    def *(v: Double): Vector2 = Vector2(getX * v, getY * v)

    def /(v: Double): Vector2 = Vector2(getX / v, getY / v)


    def getX: Double

    def getY: Double

    override def norm(n: Double): Double = {
        math.pow(math.pow(getX, n) + math.pow(getY, n), 1.0 / n)
    }

    override def norm2: Double = math.sqrt(IVector2.getPow2Sum(getX, getY))

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def to(point: IVector2): Double = math.sqrt(IVector2.getPow2Sum(getX - point.getX, getY - point.getY))

    /**
      * Get inner product of two vectors
      *
      * @param point
      * @return
      */
    override def innerProduct(point: IVector2): Double = IVector2.innerProduct2(this, point)

    /**
      * Get cosed value of two points
      *
      * @param x compare unit
      * @return
      */
    override def conAngle(x: IVector2): Double = IVector2.cosAngle(this, x)

    /**
      * Get array format value
      * very useful in Matlab
      *
      * @return
      */
    def getVector: Array[Double] = Array(getX, getY)

    override def iterator: Iterator[Double] = getVector.iterator

    /**
      * Get direction angle (unit is rad)
      *
      * @return
      */
    def direction: Double = math.atan2(getY, getX)

    /**
      *
      * Rotate vector a certain angle
      *
      * @param theta       angle (in rad) to rotate
      * @param isClockwise along the clockwise (default is counter clockwise)
      * @return
      */
    def rotate(theta: Double, isClockwise: Boolean = false): IVector2 = {
        val _theta = if (isClockwise) {
            -theta
        } else {
            theta
        }
        val sinTheta = math.sin(_theta)
        val cosTheta = math.cos(_theta)
        IVector2(
            cosTheta * getX + sinTheta * getY,
            -sinTheta * getX + cosTheta * getY
        )
    }
}

object IVector2 {

    /**
      * Get a new vector which immutable
      *
      * @param x x
      * @param y y
      * @return new vector
      */
    def apply(x: Double, y: Double): IVector2 = Vector2(x, y)

    def getPow2Sum(x: Double, y: Double): Double = x * x + y * y

    def innerProduct2(point1: IVector2, point2: IVector2): Double = point1.getX * point2.getX + point1.getY * point2.getY

    def cosAngle(point1: IVector2, point2: IVector2): Double = (point1 innerProduct point2) / (point1.norm2 * point2.norm2)

}
