package tsingjyujing.geo.element.mutable

import tsingjyujing.geo.basic.operations.{Addable, Productable}

class DoubleValue extends Addable[DoubleValue] with Productable[DoubleValue] {

    def this(v: Double) = {
        this()
        value = v
    }

    override def +(v: DoubleValue) = new DoubleValue(value + v.value)

    override def zero = new DoubleValue(0.0D)

    override def *(v: DoubleValue) = new DoubleValue(value * v.value)

    override def one = new DoubleValue(1.0D)

    var value: Double = Double.NaN
}
