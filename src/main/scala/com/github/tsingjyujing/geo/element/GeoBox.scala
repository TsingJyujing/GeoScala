package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.IContains
import com.github.tsingjyujing.geo.element.immutable.GeoPoint

/**
  * A boundary box with min/max longitude/latitude
  *
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

    assert(maxLongitude > minLongitude, "max longitude is less than min longitude: " + toString)
    assert(maxLatitude > minLatitude, "max latitude is less than min latitude: " + toString)

    override def contains(x: IGeoPoint): Boolean = (x.getLongitude > minLongitude && x.getLongitude < maxLongitude) && (x.getLatitude > minLatitude && x.getLatitude < maxLatitude)

    def anglePoints: Array[GeoPoint] = Array(
        GeoPoint(minLongitude, minLatitude),
        GeoPoint(minLongitude, maxLatitude),
        GeoPoint(maxLongitude, maxLatitude),
        GeoPoint(maxLongitude, minLatitude)
    )

    def getLongitudeRange = DoubleRange(minLongitude, maxLongitude)

    def getLatitudeRange = DoubleRange(minLatitude, maxLatitude)

    def isIntersect(b: GeoBox): Boolean = getLongitudeRange.isIntersect(b.getLongitudeRange) && getLatitudeRange.isIntersect(b.getLatitudeRange)

    /**
      * Get intersect zone of GeoBlock
      *
      * @param b geo-block
      * @return
      */
    def &(b: GeoBox): Option[GeoBox] = {
        val longitudeRange = getLongitudeRange & b.getLongitudeRange
        val latitudeRange = getLatitudeRange & b.getLatitudeRange
        if (longitudeRange.isDefined && latitudeRange.isDefined) {
            Option(GeoBox(longitudeRange.get, latitudeRange.get))
        } else {
            None
        }
    }

    /**
      * {{{
      *     assert x as longitude and y as latitude
      *     \delta_{x} = max longitude - min longitude
      *     y1 = max latitude
      *     y0 = min latitude
      *     S = R \times \Delta{x} \times \int_{y_0}^{y_1}{ cos(y) dy \times R}
      * }}}
      *
      * @return
      */
    def area: Double = IGeoPoint.EARTH_RADIUS * IGeoPoint.EARTH_RADIUS * (maxLongitude - minLongitude).toRadians * (math.sin(maxLatitude.toRadians) - math.sin(minLatitude.toRadians))

    /**
      * Get polygon of this block
      * @return
      */
    def toPolygon:GeoPolygon = GeoPolygon(anglePoints)
}

object GeoBox {

    def apply(points: Iterable[IGeoPoint]): GeoBox = GeoBox(
        points.map(_.getLongitude).min,
        points.map(_.getLongitude).max,
        points.map(_.getLatitude).min,
        points.map(_.getLatitude).max
    )

    def apply(
                 longitudeRange: DoubleRange,
                 latitudeRange: DoubleRange
             ): GeoBox = GeoBox(
        longitudeRange.min,
        longitudeRange.max,
        latitudeRange.min,
        latitudeRange.max
    )
}
