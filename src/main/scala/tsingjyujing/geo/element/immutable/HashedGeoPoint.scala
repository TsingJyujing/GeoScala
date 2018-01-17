package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.IHashableGeoPoint

final class HashedGeoPoint(longitude: Double, latitude: Double, accuracy: Long = 12000) extends IHashableGeoPoint {
    override def getGeoHashAccuracy: Long = accuracy
    override def getLongitude: Double = longitude
    override def getLatitude: Double = latitude
}
