package tsingjyujing.geo.element

import tsingjyujing.geo.basic.IGeoPoint

final class ImmutableGeoPoint(longitude: Double, latitude: Double) extends IGeoPoint {
    override def getLongitude: Double = longitude
    override def getLatitude: Double = latitude
}
