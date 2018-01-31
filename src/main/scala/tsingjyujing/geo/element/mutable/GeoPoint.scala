package tsingjyujing.geo.element.mutable

import tsingjyujing.geo.basic.{IGeoPoint, IVector2}
import tsingjyujing.geo.element.immutable.Vector2

final case class GeoPoint(private var longitude: Double, private var latitude: Double) extends IGeoPoint {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    def setLongitude(value: Double): Unit = {
        this.longitude = value
    }

    def setLatitude(value: Double): Unit = {
        this.latitude = value
    }

    def +(x: IVector2): GeoPoint = GeoPoint(getLongitude + x.getX, getLatitude + x.getY)

    def +=(x: IVector2): Unit = {
        setLongitude(getLongitude + x.getX)
        setLatitude(getLatitude + x.getY)
    }

    def -(x: IVector2): GeoPoint = GeoPoint(getLongitude - x.getX, getLatitude - x.getY)

    def -(x: GeoPoint): Vector2 = Vector2(getLongitude - x.getLongitude, getLatitude - x.getLatitude)

    def -=(x: IVector2): Unit = {
        setLongitude(getLongitude - x.getX)
        setLatitude(getLatitude - x.getY)
    }
}
