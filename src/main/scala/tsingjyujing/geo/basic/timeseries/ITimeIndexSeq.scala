package tsingjyujing.geo.basic.timeseries

import scala.collection.mutable

trait ITimeIndexSeq[T <: ITick] extends mutable.Buffer[T] {

    /**
      * Sort time series by time
      */
    def sortByTick(): Unit = {
        this.sortBy(_.getTick)
    }

    /**
      * Search in sorted array
      * @param time
      * @return
      */
    def query(time: Double): (Int, Int) = {
        if (head.getTick > time) {
            (-1, 0)
        } else if (last.getTick < time) {
            (size - 1, size)
        } else {
            var startIndex: Int = 0
            var endIndex: Int = size - 1
            (1 until size).foreach(
                _ => {
                    val mid = (startIndex + endIndex) / 2
                    val currentTick = apply(mid).getTick
                    if (currentTick < time) startIndex = mid
                    else if (currentTick > time) endIndex = mid
                    else {
                        return (mid, mid)
                    }
                    if ((endIndex - startIndex) == 1) {
                        return (startIndex, endIndex)
                    }
                }
            )
            (-1,-1)
        }
    }


    /**
      * Append unit to time series
      * @param timeUnit
      */
    def +=(timeUnit: T): Unit = {
        this += timeUnit
        sortByTick()
    }

    /**
      * Append many unit to time series
      * @param timeSeries
      */
    def +=(timeSeries: Iterable[T]): Unit = {
        this appendAll timeSeries
        sortByTick()
    }

}


object ITimeIndexSeq {

}