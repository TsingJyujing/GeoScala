package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.timeseries.ITimeIndexSeq
import com.github.tsingjyujing.geo.element.immutable.{GeoLine, TimeElement}
import com.github.tsingjyujing.geo.util.GeoUtil
import com.github.tsingjyujing.geo.util.mathematical.SeqUtil

import scala.util.control.Breaks

/**
  * A TimeSeries with GeoPoint as value
  *
  * @author tsingjyujing@163.com
  */
class GeoPointTimeSeries extends ITimeIndexSeq[TimeElement[IGeoPoint]] {

    /**
      * Query value by time
      *
      * @param time time to query
      * @return
      */
    override def getValue(time: Double): TimeElement[IGeoPoint] = {
        val indexInfo = query(time)
        if (indexInfo._1 == (-1) && indexInfo._2 == (-1)) {
            throw new RuntimeException("Error while querying value")
        } else if (indexInfo._1 == (-1)) {
            apply(indexInfo._2)
        } else if (indexInfo._2 == (-1)) {
            apply(indexInfo._1)
        } else {
            val dt = apply(indexInfo._2).getTick - apply(indexInfo._1).getTick
            val tickRatio = (time - apply(indexInfo._1).getTick) / dt
            TimeElement[IGeoPoint](
                time,
                GeoUtil.interp(apply(indexInfo._1).getValue, apply(indexInfo._2).getValue, tickRatio)
            )
        }
    }

    /**
      * Get segmentation by start and end time
      *
      * @param startTime
      * @param endTime
      * @return
      */
    def sliceByTime(startTime: Double, endTime: Double): GeoPointTimeSeries = GeoPointTimeSeries(
        slice(query(startTime)._1, query(endTime)._2)
    )

    /**
      * Get indexes
      *
      * @return
      */
    def indices: Range = data.indices

    /**
      * Get sum mileage of this route
      *
      * @return
      */
    def mileage: Double = this.sliding(2).map(p2 => p2.head.getValue.geoTo(p2.last.getValue)).sum

    /**
      * Get speed by differential points
      *
      * @param ratio scale ratio of the speed
      * @return
      */
    def getSpeed(ratio: Double = 1.0D): DoubleTimeSeries = DoubleTimeSeries(this.sliding(2).map(p2 => {
        val ds = p2.last.getValue.geoTo(p2.head.getValue)
        val dt = p2.last.getTick - p2.head.getTick
        new TimeElement[Double]((p2.head.getTick + p2.last.getTick) / 2.0, ds * ratio / dt)
    }))

    /**
      * Clean data by sliding data
      *
      * @param cleanFunc
      * @return
      */
    private def cleanSliding(cleanFunc: (TimeElement[IGeoPoint], TimeElement[IGeoPoint]) => Boolean): GeoPointTimeSeries = GeoPointTimeSeries(sliding(2).filter(p2 => cleanFunc(p2.head, p2.last)).map(_.head))

    /**
      * Clean overspeed data
      *
      * @param speedLimit max speed allowed to appear
      * @return
      */
    def cleanOverSpeed(speedLimit: Double): GeoPointTimeSeries = {
        var lastValidPoint = this.head
        val dataStack = scala.collection.mutable.ArrayBuffer.empty[TimeElement[IGeoPoint]]
        dataStack.append(lastValidPoint)
        dataStack.appendAll(this.tail.flatMap(
            x => {
                val ds = lastValidPoint.getValue.geoTo(x.getValue)
                val dt = x.getTick - lastValidPoint.getTick
                val speed = ds / dt
                if (speed >= speedLimit) {
                    None
                } else {
                    lastValidPoint = x
                    Some(x)
                }
            }
        ))
        GeoPointTimeSeries(dataStack)
    }

    /**
      * ResultType: Iterable[TimeElement[IGeoPoint]]
      * StatusType: (lastValidPoint:TimeElement[IGeoPoint])
      *
      * @param marginDistance standard margin distance
      * @param maxTolerance   do resample if d>maxTolerance*marginDistance
      * @return
      */
    def isometricallyResample(marginDistance: Double, maxTolerance: Double = 3.0): GeoPointTimeSeries = GeoPointTimeSeries(statusMachine[TimeElement[IGeoPoint], Iterable[TimeElement[IGeoPoint]]](
        (currentPoint, status) => {
            val d = currentPoint.value geoTo status.value
            if (d < marginDistance) {
                (Array.empty[TimeElement[IGeoPoint]], status)
            } else if (d <= marginDistance * maxTolerance) {
                (Array(currentPoint), currentPoint)
            } else {
                (GeoUtil.interp(status, currentPoint, math.ceil(d / marginDistance).toInt), currentPoint)
            }
        }, apply(0)
    ).flatten)


    /**
      * Sparse route
      *
      * @param sparsityParam       sparsity max distance to line
      * @param sparsitySearchParam how many points to search in range
      * @return
      */
    def toSparse(
                    sparsityParam: Double,
                    sparsitySearchParam: Int
                ): GeoPointTimeSeries
    = GeoPointTimeSeries(
        toSparseIndex(
            sparsityParam,
            sparsitySearchParam
        ).map(apply)
    )

    /**
      * Sparse route and get it's indexes
      *
      * @param sparsityParam       sparsity max distance to line
      * @param sparsitySearchParam how many points to search in range
      * @return
      */
    def toSparseIndex(
                         sparsityParam: Double,
                         sparsitySearchParam: Int
                     ): IndexedSeq[Int] = {
        val remainIndexes = GeoPointTimeSeries.removeStayPoints(this)
        val newGeoTimeSeries = GeoPointTimeSeries(remainIndexes.map(this.data))
        val sparsedIndex = GeoPointTimeSeries.sparsifyGPSIndexed(
            newGeoTimeSeries, sparsityParam, sparsitySearchParam
        )
        sparsedIndex map remainIndexes
    }
}

/**
  * Apply and Utils
  */
object GeoPointTimeSeries {

    /**
      * Create TimeSeries by time-elements
      *
      * @param data
      * @return
      */
    def apply(data: TraversableOnce[TimeElement[IGeoPoint]]): GeoPointTimeSeries = {
        val result = new GeoPointTimeSeries()
        result.appendAll(data)
        result
    }

    /**
      * Get sparse points'
      *
      * @param gpsArray            gps points
      * @param sparsityParam       sparsity max distance to line
      * @param sparsitySearchParam how many points to search in range
      * @return
      */
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

    /**
      * Get sparse points' index in seq
      *
      * @param gpsArray            gps points
      * @param sparsityParam       sparsity max distance to line
      * @param sparsitySearchParam how many points to search in range
      * @return
      */
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

    /**
      * Remove stay points and get remain indexes
      *
      * @param gpsArray         gps time series
      * @param allowMinDistance min distance allow to margin
      * @return which points should remain after cleaning
      */
    def removeStayPoints(
                            gpsArray: GeoPointTimeSeries,
                            allowMinDistance: Double = 0.005
                        ): IndexedSeq[Int] = {
        var currentPoint = gpsArray(0)
        val returnList = new scala.collection.mutable.MutableList[Int]
        returnList += 0
        gpsArray.tail.zipWithIndex.foreach(
            pi => {
                val distance = pi._1.getValue.geoTo(currentPoint.getValue)
                if (distance >= allowMinDistance) {
                    currentPoint = pi._1
                    returnList += pi._2
                }
            }
        )
        returnList.toIndexedSeq
    }
}
