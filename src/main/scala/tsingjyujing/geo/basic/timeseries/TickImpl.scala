package tsingjyujing.geo.basic.timeseries

class TickImpl(var tick:Double) extends ITick{
    override def getTick: Double = tick

    override def setTick(tick: Double): Unit = {
        this.tick = tick
    }
}
