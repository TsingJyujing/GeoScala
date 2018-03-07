package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.operations.IContains

/**
  * Double value range
  *
  * @param min min
  * @param max max
  */
case class DoubleRange(min: Double, max: Double) extends IContains[Double] {
    assert(min < max)

    /**
      * Value in range
      * @param x
      * @return
      */
    override def contains(x: Double): Boolean = x <= max && x >= min

    def length: Double = max - min

    /**
      * is r has common area with this
      * @param r another range
      * @return
      */
    def isIntersect(r: DoubleRange): Boolean = !(r.min > max || r.max < min)

    /**
      * Merge range, if not intersect return none
      * @param r another range
      * @return
      */
    def &(r: DoubleRange): Option[DoubleRange] = if (isIntersect(r)) {
        Option(DoubleRange(math.max(min, r.min), math.min(max, r.max)))
    } else {
        None
    }


}
