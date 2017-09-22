package tsingjyujing.geo.util.mathematical

import tsingjyujing.geo.basic.geounit.{GeodesicLine, GeometryPoint}
import tsingjyujing.geo.basic.timeseries.Tickable
import tsingjyujing.geo.util.mathematical.VectorUtil.outerProduct

import scala.collection.{immutable, mutable}
import scala.util.control.Breaks

/**
  * @author tsingjyujing
  */
object GPSUtil {
    
    def steeringAngleGeodesic[T](
                                  p1: GeometryPoint[T],
                                  p2: GeometryPoint[T],
                                  p3: GeometryPoint[T]
                                ): Double = {
        
        val v1 = p1.get3DPos()
        val v2 = p2.get3DPos()
        val v3 = p3.get3DPos()
        GeometryUtil.angle(
            outerProduct(v1, v2),
            outerProduct(v2, v3)
        )
    }
    
    
    def steeringAngleEuclidean[T](
                                   p1: GeometryPoint[T],
                                   p2: GeometryPoint[T],
                                   p3: GeometryPoint[T]
                                 ): Double = {
        val latRatio = math.cos((p1.latitude + p2.latitude + p3.latitude) * math.Pi / 3.0 / 180.0)
        GeometryUtil.angle(
            Array((p2.longitude - p1.longitude) * latRatio, p2.latitude - p1.latitude),
            Array((p3.longitude - p2.longitude) * latRatio, p3.latitude - p2.latitude)
        )
    }
    
    def GPSMile[T](gpsIter: Iterator[GeometryPoint[T]]): Double = {
        var gpsMile = 0.0
        var lastPoint = gpsIter.next()
        while (gpsIter.hasNext) {
            val currentPoint = gpsIter.next()
            gpsMile += currentPoint.distance(lastPoint)
            lastPoint = currentPoint
        }
        gpsMile
    }
    
    def sparsifyGPS[T <: Tickable](
                                    gpsArray: IndexedSeq[GeometryPoint[T]],
                                    sparsityParam: Double,
                                    sparsitySearchParam: Int
                                  ) = sparsifyGPSIndexed(
        gpsArray, sparsityParam, sparsitySearchParam
    ).map(
        i => gpsArray(i)
    )
    
    def sparsifyGPSIndexed(
                            gpsArray: IndexedSeq[GeometryPoint[_ <: Tickable]],
                            sparsityParam: Double,
                            sparsitySearchParam: Int
                          ): IndexedSeq[Int] = {
        if (gpsArray.size < 10) return gpsArray.indices
        val returnList = new mutable.MutableList[Int]
        returnList += 0
        var nowIndex = 0
        val getDistance = (startIndex: Int, endIndex: Int) => {
            val line = new GeodesicLine(
                gpsArray(startIndex),
                gpsArray(endIndex)
            )
            gpsArray.slice(startIndex + 1, endIndex).map((point) => line.distance(point)).max
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
    
    def cleanGPS[T <: Tickable](
                                 gpsArray: Iterable[GeometryPoint[T]],
                                 velocityLimit: Double
                               ): immutable.IndexedSeq[GeometryPoint[T]] = {
        val pointIter = gpsArray.iterator
        var lastPoint = pointIter.next()
        val returnList = new mutable.MutableList[GeometryPoint[T]]
        returnList += lastPoint
        while (pointIter.hasNext) {
            val thisPoint = pointIter.next()
            val distance = thisPoint.distance(lastPoint)
            val velocity = distance / math.abs(thisPoint.userInfo.getTick - lastPoint.userInfo.getTick)
            if (velocity < velocityLimit) {
                returnList += thisPoint
                lastPoint = thisPoint
            }
        }
        returnList.toIndexedSeq
    }
}
