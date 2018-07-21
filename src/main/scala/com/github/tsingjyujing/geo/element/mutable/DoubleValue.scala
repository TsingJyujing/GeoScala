package com.github.tsingjyujing.geo.element.mutable

import com.github.tsingjyujing.geo.basic.operations.{Addable, Productable}

import scala.language.implicitConversions

/**
  * A pack of double to support common operations
  *
  * @param value
  */
case class DoubleValue(var value: Double) extends Addable[DoubleValue] with Productable[DoubleValue] {

    override def +(v: DoubleValue) = DoubleValue(value + v.value)

    override def *(v: DoubleValue) = DoubleValue(value * v.value)

    def toDouble: Double = value

}

object DoubleValue {
    implicit def convertToDoubleValue(v: Double): DoubleValue = new DoubleValue(v)
}
