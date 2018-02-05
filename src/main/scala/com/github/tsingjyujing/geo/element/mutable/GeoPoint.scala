package com.github.tsingjyujing.geo.element.mutable

import com.github.tsingjyujing.geo.basic.{IGeoPoint, IVector2}
import com.github.tsingjyujing.geo.element.immutable.Vector2

/**
  * Mutable implementation of IGeoPoint
  * @param longitude
  * @param latitude
  */
final case class GeoPoint(private var longitude: Double, private var latitude: Double) extends IGeoPoint {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    def setLongitude(value: Double): Unit = {
        this.longitude = value
    }

    def setLatitude(value: Double): Unit = {
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
}
