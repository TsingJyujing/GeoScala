package tsingjyujing.geo.element

import tsingjyujing.geo.basic.operations.IContains

case class DoubleRange(min: Double, max: Double) extends IContains[Double] {
    assert(min < max)
    override def contains(x: Double): Boolean = x <= max && x >= min
}
