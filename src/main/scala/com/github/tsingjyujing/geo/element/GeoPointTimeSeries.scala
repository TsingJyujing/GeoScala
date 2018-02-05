package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.timeseries.ITimeIndexSeq
import com.github.tsingjyujing.geo.element.immutable.{GeoLine, TimeElement}
import com.github.tsingjyujing.geo.element.mutable.GeoPoint
import com.github.tsingjyujing.geo.util.mathematical.SeqUtil

import scala.util.control.Breaks

/**
  * A TimeSeries with GeoPoint as value
  */
class GeoPointTimeSeries extends ITimeIndexSeq[TimeElement[GeoPoint]] {

    override def getValue(time: Double): TimeElement[GeoPoint] = {
        val indexInfo = query(time)
        if (indexInfo._1 == (-1) && indexInfo._2 == (-1)) {
            throw new RuntimeException("Error while querying value")
        } else if (indexInfo._1 == (-1)) {
            apply(indexInfo._2)
        } else if (indexInfo._2 == (-1)) {
            apply(indexInfo._1)
        } else {
            val dv = apply(indexInfo._2).getValue - apply(indexInfo._1).getValue
            val dt = apply(indexInfo._2).getTick - apply(indexInfo._1).getTick
            new TimeElement[GeoPoint](
                time,
                apply(indexInfo._1).getValue + dv * (time - apply(indexInfo._1).getTick) / dt
            )
        }
    }

    def indices: Range = data.indices

    def mileage: Double = this.sliding(2).map(p2 => p2.head.getValue.geoTo(p2.last.getValue)).sum

    def getSpeed(ratio: Double = 1.0D): DoubleTimeSeries = DoubleTimeSeries(this.sliding(2).map(p2 => {
        val ds = p2.last.getValue.geoTo(p2.head.getValue)
        val dt = p2.last.getTick - p2.head.getTick
        new TimeElement[Double]((p2.head.getTick + p2.last.getTick) / 2.0, ds * ratio / dt)
    }))

    private def cleanSliding(cleanFunc: (TimeElement[GeoPoint], TimeElement[GeoPoint]) => Boolean): GeoPointTimeSeries = GeoPointTimeSeries(sliding(2).filter(p2 => cleanFunc(p2.head, p2.last)).map(_.head))

    def cleanOverSpeed(speedLimit: Double): GeoPointTimeSeries = this.cleanSliding(
        (p1, p2) => {
            val ds = p1.getValue geoTo p2.getValue
            val dt = p2.getTick - p1.getTick
            (ds / dt) > speedLimit
        }
    )


    def toSparse(
                    sparsityParam: Double,
                    sparsitySearchParam: Int
                ): GeoPointTimeSeries = GeoPointTimeSeries(
        toSparseIndex(
            sparsityParam,
            sparsitySearchParam
        ).map(
            i => this (i)
        )
    )

    def toSparseIndex(
                         sparsityParam: Double,
                         sparsitySearchParam: Int
                     ): IndexedSeq[Int] = GeoPointTimeSeries.sparsifyGPSIndexed(
        this, sparsityParam, sparsitySearchParam
    )
}

object GeoPointTimeSeries {

    def apply(data: TraversableOnce[TimeElement[GeoPoint]]): GeoPointTimeSeries = {
        val result = new GeoPointTimeSeries()
        result.append(data)
        result
    }

    def sparsifyGPS(
                       gpsArray: GeoPointTimeSeries,
                       sparsityParam: Double,
                       sparsitySearchParam: Int
                   ): GeoPointTimeSeries = GeoPointTimeSeries(
        sparsifyGPSIndexed(
            gpsArray, sparsityParam, sparsitySearchParam
        ).map(
            i => gpsArray(i)
        )
    )

    def sparsifyGPSIndexed(
                              gpsArray: GeoPointTimeSeries,
                              sparsityParam: Double,
                              sparsitySearchParam: Int
                          ): IndexedSeq[Int] = {
        if (gpsArray.size < 10) return gpsArray.indices
        val returnList = new scala.collection.mutable.MutableList[Int]
        returnList += 0
        var nowIndex = 0
        val getDistance = (startIndex: Int, endIndex: Int) => {
            val line = GeoLine(
                gpsArray(startIndex).value,
                gpsArray(endIndex).value
            )
            gpsArray.slice(startIndex + 1, endIndex).map((point) => line.geoTo(point.value)).max
        }

        val loop = Breaks
        loop.breakable(
            while (true) {
                val indexFound = SeqUtil.searchInSorted(
                    (i) => getDistance(nowIndex, i),
                    sparsityParam,
                    nowIndex + 2,
                    math.min(
                        nowIndex + sparsitySearchParam,
                        gpsArray.size - 2
                    )
                )._1
                if (indexFound >= gpsArray.size - 4) {
                    returnList += gpsArray.size - 1
                    loop.break
                } else {
                    returnList += indexFound
                    nowIndex = indexFound
                }
            }
        )
        returnList.toIndexedSeq
    }
}
