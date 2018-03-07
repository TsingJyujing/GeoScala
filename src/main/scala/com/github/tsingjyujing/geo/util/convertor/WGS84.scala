package com.github.tsingjyujing.geo.util.convertor

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.GeoTransformable
import com.github.tsingjyujing.geo.element.mutable.GeoPoint

/**
  * Return a new object which extends IGeoPoint and has same longitude and latitude
  * @author tsingjyujing@163.com
  */
object WGS84 extends GeoTransformable {
    /**
      * Encrypt WGS84 location to other format
      *
      * @param x WGS84 position
      * @return
      */
    override def transform(x: IGeoPoint) = GeoPoint(x.getLongitude, x.getLatitude)

    override def inverseTransform(y: IGeoPoint, eps: Double = 0): IGeoPoint = GeoPoint(y.getLongitude, y.getLatitude)

    override def inverseTransformFast(y: IGeoPoint): GeoPoint = GeoPoint(y.getLongitude, y.getLatitude)
}
