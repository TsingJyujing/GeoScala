package tsingjyujing.geo.scala.basic

import tsingjyujing.geo.scala.basic.operations.IHashedIndex

/**
  * Which geo point can get an Long index value and get hashed.
  */
trait IHashableGeoPoint extends  IGeoPoint with IHashedIndex[Long] {
    def getGeoHashAccuracy: Long

    override def indexCode: Long = IHashableGeoPoint.createCodeFromGps(this, getGeoHashAccuracy)
}

object IHashableGeoPoint {
    /**
      * 2 pow 30
      **/
    val POW2E30: Long = 0x40000000L

    /**
      * 2 pow 31
      **/
    val POW2E31: Long = 0x80000000L


    def createCodeFromGps(point: IGeoPoint, accuracy: Long): Long = {
        val lngCode = Math.round((point.getLongitude + 180.0D) / 180 * accuracy)
        val latCode = Math.round((point.getLatitude + 90.00D) / 180 * accuracy)
        lngCode * IHashableGeoPoint.POW2E31 + latCode
    }

    // TODO get boundary of current hash block

    def revertFromCode(code: Long, accuracy: Long): IGeoPoint = {
        val latCode = code & (POW2E31 - 1)
        val lngCode = (code - latCode) / POW2E31
        val lng = lngCode * 180.0 / accuracy - 180.0
        val lat = latCode * 180.0 / accuracy - 90.0
        new ImmutableGeoPoint(lng, lat)
    }

}