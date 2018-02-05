package com.github.tsingjyujing.geo.basic.timeseries

/**
  * Which can get tick and comparable
  */
trait ITick extends Comparable[ITick] {

    def getTick: Double

    def setTick(tick: Double): Unit

    override def compareTo(t: ITick): Int = getTick.compareTo(t.getTick)
}
