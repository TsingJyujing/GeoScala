package com.github.tsingjyujing.geo.element.mutable

import com.github.tsingjyujing.geo.basic.{IGeoPoint, IVector2}

/**
  * Mutable implementation of IGeoPoint
  *
  * **Attention**: Avoid using mutable point in algorithms because it may break precomputed data
  *
  * @param longitude
  * @param latitude
  */
final case class GeoPoint(private var longitude: Double, private var latitude: Double) extends IGeoPoint {

    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    def setLongitude(value: Double): Unit = {
        verifyLongitude
        this.longitude = value
    }

    def setLatitude(value: Double): Unit = {
        verifyLatitude
        this.latitude = value
    }

    def +=(x: IVector2): Unit = {
        setLongitude(getLongitude + x.getX)
        setLatitude(getLatitude + x.getY)
    }

    def -=(x: IVector2): Unit = {
        setLongitude(getLongitude - x.getX)
        setLatitude(getLatitude - x.getY)
    }

    verifyValues
}
