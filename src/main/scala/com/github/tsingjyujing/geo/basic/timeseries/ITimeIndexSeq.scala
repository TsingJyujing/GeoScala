package com.github.tsingjyujing.geo.basic.timeseries

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * A common time series trait contains some common methods
  * @tparam T
  */
trait ITimeIndexSeq[T <: ITick] extends mutable.Iterable[T] {

    protected val data: ArrayBuffer[T] = mutable.ArrayBuffer[T]()

    /**
      * Sort time series by time
      */
    def sortByTick(): Unit = {
        data.sortBy(_.getTick)
    }

    def getValue(time: Double): T

    /**
      * Search in sorted array
      *
      * @param time query time
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
                    val currentTick = data(mid).getTick
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
            (-1, -1)
        }
    }

    /**
      * Append unit to time series
      *
      * @param timeUnit
      */
    def append(timeUnit: T): Unit = {
        data append timeUnit
        sortByTick()
    }

    /**
      * Append many unit to time series
      *
      * @param timeSeries
      */
    def appendAll(timeSeries: TraversableOnce[T]): Unit = {
        data appendAll timeSeries
        sortByTick()
    }

    def apply(index: Int): T = data(index)

    /**
      * Apply a status machine on timeseries
      *
      * @param statusFunction function to process element[T] and last (or initial) status[S], get result[R] and next status
      * @param initialStatus initial status
      * @tparam S Type of status
      * @tparam R Type of result
      * @return
      */
    def statusMachine[S, R](statusFunction: (T, S) => (S, R), initialStatus: S = null): Iterable[R] = {
        var status: S = initialStatus
        this.map(
            elem => {
                val statusAndResult = statusFunction(elem, status)
                status = statusAndResult._1
                statusAndResult._2
            }
        )
    }

    override def iterator: Iterator[T] = data.iterator
}
