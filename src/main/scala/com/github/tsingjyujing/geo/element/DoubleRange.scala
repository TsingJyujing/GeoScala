package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.operations.IContains

/**
  * Double value range
  *
  * @param min
  * @param max
  */
case class DoubleRange(min: Double, max: Double) extends IContains[Double] {
    assert(min < max)

    override def contains(x: Double): Boolean = x <= max && x >= min

    def length: Double = max - min

    def isIntersect(r: DoubleRange): Boolean = !(r.min > max || r.max < min)

    def &(r: DoubleRange): Option[DoubleRange] = if (isIntersect(r)) {
        Option(DoubleRange(math.max(min, r.min), math.min(max, r.max)))
    } else {
        None
    }


}
