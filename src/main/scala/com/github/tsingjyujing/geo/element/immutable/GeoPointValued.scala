package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.IValue

/**
  * Get a geo point with value
  *
  * @param longitude
  * @param latitude
  * @param value
  * @tparam T value type
  */
case class GeoPointValued[T](longitude: Double, latitude: Double, value: T) extends IGeoPoint with IValue[T] {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    override def getValue: T = value
}

object GeoPointValued {
    /**
      * Create a new point with value
      *
      * @param point point
      * @param value value
      * @tparam T Type of value
      * @return
      */
    def apply[T](point: IGeoPoint, value: T): GeoPointValued[T] = new GeoPointValued[T](
        point.getLongitude,
        point.getLatitude,
        value
    )
}