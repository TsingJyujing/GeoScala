package com.github.tsingjyujing.geo.element.mutable

import com.github.tsingjyujing.geo.basic.timeseries.ITick

class TickImpl(var tick: Double) extends ITick {
    override def getTick: Double = tick

    override def setTick(tick: Double): Unit = {
        this.tick = tick
    }
}
