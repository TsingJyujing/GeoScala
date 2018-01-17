package tsingjyujing.geo.basic.timeseries

/**
  *
  */
trait ITick extends Comparable[ITick] {

    def getTick: Double

    def setTick(tick: Double): Unit

    override def compareTo(t: ITick): Int = getTick.compareTo(t.getTick)
}
