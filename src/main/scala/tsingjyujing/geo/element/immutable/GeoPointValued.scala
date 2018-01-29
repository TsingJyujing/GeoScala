package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.basic.operations.IValue

final case class GeoPointValued[T](longitude: Double, latitude: Double, value: T) extends IGeoPoint with IValue[T] {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    override def getValue: T = value
}
