package tsingjyujing.geo.element

import tsingjyujing.geo.basic.IHashableGeoPoint

final class ImmutableHashedGeoPoint(longitude: Double, latitude: Double, accuracy: Long = 12000) extends IHashableGeoPoint {
    override def getGeoHashAccuracy: Long = accuracy
    override def getLongitude: Double = longitude
    override def getLatitude: Double = latitude
}
