package com.github.tsingjyujing.geo.basic.timeseries

/**
  * @author tsingjyujing@163.com
  *         Which can get tick and comparable
  */
trait ITick extends Comparable[ITick] {

    /**
      * Get tick in type of double
      *
      * @return
      */
    def getTick: Double

    /**
      * Set tick
      *
      * @param tick tick
      */
    def setTick(tick: Double): Unit

    override def compareTo(t: ITick): Int = getTick.compareTo(t.getTick)
}
