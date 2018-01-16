package tsingjyujing.geo.scala.basic

import tsingjyujing.geo.scala.basic.operations.IHashedIndex


trait IHashableGeoPoint extends IGeoPoint with IHashedIndex[Long] {

    def getGeoHashAccuracy: Long

    override def indexCode: Long = IHashableGeoPoint.createCodeFromGps(this, getGeoHashAccuracy)


}

object IHashableGeoPoint {
    /**
      * 2^30
      **/
    val POW2E30: Long = 0x40000000L

    /**
      * 2^31
      **/
    val POW2E31: Long = 0x80000000L

    def createCodeFromGps(point: IGeoPoint, accuracy: Long): Long = {
        val lngCode = Math.floor((point.getLongitude + 180.0D) / 180 * accuracy).toLong
        val latCode = Math.floor((point.getLatitude + 90.00D) / 180 * accuracy).toLong
        lngCode * IHashableGeoPoint.POW2E31 + latCode
    }

    // TODO get boundary of current hash block

    def revertFromCode(code: Long): IGeoPoint = {
        val
        new IGeoPoint {
            override def getLongitude: Double = ???
            override def getLatitude: Double = ???
        }
    }
}