package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IGeoPoint

/**
  * Get a immutable geo point
  *
  * @param longitude
  * @param latitude
  */
final case class GeoPoint(longitude: Double, latitude: Double) extends IGeoPoint {

    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    verifyValues

}
