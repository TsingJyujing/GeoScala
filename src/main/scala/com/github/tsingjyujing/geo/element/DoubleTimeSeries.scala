package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.timeseries.ITimeIndexSeq
import com.github.tsingjyujing.geo.element.immutable.TimeElement

/**
  * Double time series with a continues value and discrete time
  * use linear interpolation to give a estimated value
  */
class DoubleTimeSeries extends ITimeIndexSeq[TimeElement[Double]] {

    override def getValue(time: Double): TimeElement[Double] = {
        val indexInfo = query(time)
        if (indexInfo._1 == (-1) && indexInfo._2 == (-1)) {
            throw new RuntimeException("Error while querying value")
        } else if (indexInfo._1 == (-1)) {
            apply(indexInfo._2)
        } else if (indexInfo._2 == (-1)) {
            apply(indexInfo._1)
        } else if (indexInfo._1 == indexInfo._2) {
            apply(indexInfo._1)
        } else {
            val dv = apply(indexInfo._2).getValue - apply(indexInfo._1).getValue
            val dt = apply(indexInfo._2).getTick - apply(indexInfo._1).getTick
            new TimeElement[Double](
                time,
                apply(indexInfo._1).getValue + (time - apply(indexInfo._1).getTick) * dv / dt
            )
        }
    }

}

object DoubleTimeSeries {
    def apply(data: TraversableOnce[TimeElement[Double]]): DoubleTimeSeries = {
        val result = new DoubleTimeSeries()
        result.appendAll(data)
        result
    }
}