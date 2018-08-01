package com.github.tsingjyujing.geo.basic.operations

/**
  * Which can get angle between this to T (use rad as unit)
  * Commonly, we use classOf[this] as T for symmetry
  *
  * @author tsingjyujing@163.com
  * @tparam T Type of the object to compare
  */
trait Angleable[T] {
    /**
      * Get cosed angle value of this and x
      *
      * @param x compare unit
      * @return
      */
    def cosAngle(x: T): Double

    /**
      * Get angle (in unit of rad) value of this and x
      *
      * @param x compare unit
      * @return
      */
    def angle(x: T): Double = math.acos(cosAngle(x))
}
