package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.{IGeoPoint, IVector2}

/**
  * Get a immutable geo point
  *
  * @param longitude
  * @param latitude
  */
case class GeoPoint(longitude: Double, latitude: Double) extends IGeoPoint {

    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    verifyValues

}

object GeoPoint {
    /**
      * Create point from IVector2
      *
      * @param data x=longitude y=latitude
      * @return
      */
    def apply(data: IVector2): GeoPoint = new GeoPoint(data.getX, data.getY)
}
