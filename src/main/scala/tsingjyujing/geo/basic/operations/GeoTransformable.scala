package tsingjyujing.geo.basic.operations

import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.element.mutable.GeoPoint

trait GeoTransformable {
    /**
      * Encrypt WGS84 location to other format
      * @param x WGS84 position
      * @return
      */
    def transform(x: IGeoPoint): IGeoPoint

    /**
      * @param y transformed location
      * @return
      */
    def inverseTransformFast(y: IGeoPoint): GeoPoint = {
        val ffx = transform(y)
        new GeoPoint(
            y.getLongitude * 2 - ffx.getLongitude,
            y.getLatitude * 2 - ffx.getLatitude
        )
    }

    /**
      * XON
      * eps = 1e-6
      * wgs = rev_transform_rough (bad, worsen)
      * old = bad
      * improvement = (99+99j)
      * while (abs(improvement ) > eps) {
      *     improvement = worsen(wgs) - bad
      *     old = wgs
      *     wgs = wgs - improvement
      * }
      * return wgs
      *
      * @param y transformed location
      * @return
      */
    def inverseTransform(y: IGeoPoint, eps: Double = 0.01): IGeoPoint = {
        var errorValue = Double.MaxValue
        var i = 0
        val returnValue = inverseTransformFast(y)
        do {
            val fcx = transform(returnValue)
            errorValue = fcx geoTo y
            returnValue.setLongitude(returnValue.getLongitude - fcx.getLongitude + y.getLongitude)
            returnValue.setLatitude(returnValue.getLatitude - fcx.getLatitude + y.getLatitude)
            i +=1
        } while (errorValue > eps)
        returnValue
    }
}
