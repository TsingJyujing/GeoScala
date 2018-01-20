package tsingjyujing.geo.element.mutable

import tsingjyujing.geo.basic.timeseries.ITick

class TickImpl(var tick:Double) extends ITick{
    override def getTick: Double = tick

    override def setTick(tick: Double): Unit = {
        this.tick = tick
    }
}
