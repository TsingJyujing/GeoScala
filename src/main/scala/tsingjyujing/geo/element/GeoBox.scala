package tsingjyujing.geo.element

import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.basic.operations.IContains
import tsingjyujing.geo.element.immutable.GeoPoint

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
