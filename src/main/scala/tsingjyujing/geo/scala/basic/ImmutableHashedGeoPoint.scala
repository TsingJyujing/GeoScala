package tsingjyujing.geo.scala.basic

final class ImmutableHashedGeoPoint(longitude: Double, latitude: Double, accuracy: Long = 12000) extends IHashableGeoPoint {
    override def getGeoHashAccuracy: Long = accuracy
    override def getLongitude: Double = longitude
    override def getLatitude: Double = latitude
}
