package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.{IGeoPoint, IVector2}

final case class GeoPoint(longitude: Double, latitude: Double) extends IGeoPoint {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    def +(v: IVector2): GeoPoint = GeoPoint(longitude + v.getX, latitude + v.getY)
}
