package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.IContains
import com.github.tsingjyujing.geo.element.immutable.GeoPoint

/**
  * A boundary box with min/max longitude/latitude
  * @param minLongitude
  * @param maxLongitude
  * @param minLatitude
  * @param maxLatitude
  */
case class GeoBox(
                     minLongitude: Double,
                     maxLongitude: Double,
                     minLatitude: Double,
                     maxLatitude: Double
                 ) extends IContains[IGeoPoint] {
    override def contains(x: IGeoPoint): Boolean = (x.getLongitude > minLongitude && x.getLongitude < maxLongitude) && (x.getLatitude > minLatitude && x.getLatitude < maxLatitude)

    def anglePoints: Array[GeoPoint] = Array(
        GeoPoint(minLongitude, minLatitude),
        GeoPoint(minLongitude, maxLatitude),
        GeoPoint(maxLongitude, maxLatitude),
        GeoPoint(maxLongitude, minLatitude)
    )

    def getLongitudeRange = DoubleRange(minLongitude, maxLongitude)

    def getLatitudeRange = DoubleRange(minLatitude, maxLatitude)
}

object GeoBox {
    def apply(points: Iterable[IGeoPoint]): GeoBox = GeoBox(
        points.map(_.getLongitude).min,
        points.map(_.getLongitude).max,
        points.map(_.getLatitude).min,
        points.map(_.getLatitude).max
    )
}
