package com.github.tsingjyujing.geo.element.mutable

import com.github.tsingjyujing.geo.basic.operations.{Addable, Productable}

/**
  * A pack of double to support common operations
  * @param value
  */
final case class DoubleValue(var value: Double) extends Addable[DoubleValue] with Productable[DoubleValue] {

    override def +(v: DoubleValue) = DoubleValue(value + v.value)

    override def *(v: DoubleValue) = DoubleValue(value * v.value)

    implicit def toDouble: Double = value


}
