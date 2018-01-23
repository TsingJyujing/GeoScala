package tsingjyujing.geo.element

import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.basic.operations.IContains

class GeoBox(
                val minLongitude: Double,
                val maxLongitude: Double,
                val minLatitude: Double,
                val maxLatitude: Double
            ) extends IContains[IGeoPoint] {
    override def contains(x: IGeoPoint) = (x.getLongitude > minLongitude && x.getLongitude < maxLongitude) && (x.getLatitude > minLatitude && x.getLatitude < maxLatitude)
}

object GeoBox {
    def apply(points: Iterable[IGeoPoint]): GeoBox = new GeoBox(
        points.map(_.getLongitude).min,
        points.map(_.getLongitude).max,
        points.map(_.getLatitude).min,
        points.map(_.getLatitude).max
    )

    def apply(
                 minLongitude: Double,
                 maxLongitude: Double,
                 minLatitude: Double,
                 maxLatitude: Double
             ): GeoBox = new GeoBox(
        minLongitude,
        maxLongitude,
        minLatitude,
        maxLatitude
    )

}
