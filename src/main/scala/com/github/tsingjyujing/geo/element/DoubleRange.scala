package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.operations.IContains

case class DoubleRange(min: Double, max: Double) extends IContains[Double] {
    assert(min < max)

    override def contains(x: Double): Boolean = x <= max && x >= min
}
