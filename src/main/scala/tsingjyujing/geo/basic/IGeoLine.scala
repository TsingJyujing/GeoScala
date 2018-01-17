package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations.GeoDistanceMeasurable

trait IGeoLine extends GeoDistanceMeasurable[IGeoPoint] {

    def getTerminalPoints: (IGeoPoint, IGeoPoint)

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def geoTo(point: IGeoPoint) = {
        throw new Exception("Unimplemented method")
    }
}
