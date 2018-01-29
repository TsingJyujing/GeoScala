package tsingjyujing.geo.element.mutable

import tsingjyujing.geo.basic.operations.{Addable, Productable}

final case class DoubleValue(var value: Double) extends Addable[DoubleValue] with Productable[DoubleValue] {

    override def +(v: DoubleValue) = DoubleValue(value + v.value)

    override def zero = DoubleValue(0.0D)

    override def *(v: DoubleValue) = DoubleValue(value * v.value)

    override def one = DoubleValue(1.0D)

}
