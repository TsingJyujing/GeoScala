package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.operations.IValue
import com.github.tsingjyujing.geo.basic.timeseries.ITick

final case class TimeElement[T](time: Double, value: T) extends ITick with IValue[T] {
    override def getTick: Double = time

    override def setTick(tick: Double): Unit = throw new Exception("Unsupported method")

    override def getValue: T = value
}
