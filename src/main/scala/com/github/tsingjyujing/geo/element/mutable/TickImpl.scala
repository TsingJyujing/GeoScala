package com.github.tsingjyujing.geo.element.mutable

import com.github.tsingjyujing.geo.basic.timeseries.ITick

/**
  * An simple implementation of ITick
  * @param tick
  */
class TickImpl(var tick: Double) extends ITick {
    override def getTick: Double = tick

    override def setTick(tick: Double): Unit = {
        this.tick = tick
    }
}
