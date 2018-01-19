package tsingjyujing.geo.util.convertor

import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.basic.operations.GeoTransformable
import tsingjyujing.geo.element.mutable.GeoPoint

object WGS84 extends GeoTransformable {
    /**
      * Encrypt WGS84 location to other format
      *
      * @param x WGS84 position
      * @return
      */
    override def transform(x: IGeoPoint) = new GeoPoint(x.getLongitude, x.getLatitude)

    override def inverseTransform(y: IGeoPoint, eps: Double = 0): IGeoPoint = new GeoPoint(y.getLongitude, y.getLatitude)

    override def inverseTransformFast(y: IGeoPoint): GeoPoint = new GeoPoint(y.getLongitude, y.getLatitude)
}
