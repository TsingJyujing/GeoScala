package tsingjyujing.geo.element.mutable

import tsingjyujing.geo.basic.IGeoPoint

final class GeoPoint(private var longitude: Double, private var latitude: Double) extends IGeoPoint {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    def setLongitude(value: Double): Unit = {
        this.longitude = value
    }

    def setLatitude(value: Double): Unit = {
        this.latitude = value
    }
}
