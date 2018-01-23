package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoDistanceRangeable, IHashedIndex}
import tsingjyujing.geo.element.immutable.GeoPoint

import scala.collection.mutable


/**
  * Squared hash of geo
  * Which geo point can get an Long index value and get hashed.
  *
  */
trait IHashableGeoBlock extends IGeoPoint with IHashedIndex[Long] with GeoDistanceRangeable[IHashableGeoBlock] {

    def getGeoHashAccuracy: Long

    // Don't change indexCode value, this implementation for higher speed
    lazy val getCenterPoint: IGeoPoint = IHashableGeoBlock.revertFromCode(indexCode, getGeoHashAccuracy)

    override def getLongitude: Double = getCenterPoint.getLongitude

    override def getLatitude: Double = getCenterPoint.getLatitude

    lazy val circumradius: Double = getBoundaryPoints(getCenterPoint).map(_ geoTo getCenterPoint).max

    lazy val inradius: Double = getBoundaryPoints(getCenterPoint).map(_ geoTo getCenterPoint).min

    def getBoundaryPoints(blockCode: Long): Iterable[IGeoPoint] = IHashableGeoBlock.getGeoHashBlockBoundaryPoints(this, getGeoHashAccuracy, blockCode)

    def getBoundaryPoints(point: IGeoPoint): Iterable[IGeoPoint] = IHashableGeoBlock.getGeoHashBlockBoundaryPoints(point, getGeoHashAccuracy, indexCode)

    override def getMinDistance(x: IHashableGeoBlock): Double = {
        val centerDistance = getCenterPoint geoTo x.getCenterPoint
        val circumDistance = circumradius + x.circumradius
        if (circumDistance > centerDistance) {
            0.0D
        } else {
            centerDistance - circumDistance
        }
    }

    override def getMaxDistance(x: IHashableGeoBlock): Double = (getCenterPoint geoTo x.getCenterPoint) + (circumradius + x.circumradius)

}

object IHashableGeoBlock {
    /**
      * 2 pow 30
      **/
    val POW2E30: Long = 0x40000000L

    /**
      * 2 pow 31
      **/
    val POW2E31: Long = 0x80000000L
    val DEG2RAD: Double = math.Pi / 180.0
    val RAD2DEG: Double = 1.0 / DEG2RAD
    val MAX_INNER_PRODUCT_FOR_UNIT_VECTOR: Double = 1.0
    val EARTH_RADIUS: Double = 6378.5

    def createCodeFromGps(point: IGeoPoint, accuracy: Long): Long = {
        val lngCode = Math.round((point.getLongitude + 180.0D) / 180 * accuracy)
        val latCode = Math.round((point.getLatitude + 90.00D) / 180 * accuracy)
        lngCode * IHashableGeoBlock.POW2E31 + latCode
    }

    // TODO get boundary of current hash block

    def revertFromCode(code: Long, accuracy: Long): IGeoPoint = {
        val latCode = code & (POW2E31 - 1)
        val lngCode = (code - latCode) / POW2E31
        val lng = lngCode * 180.0 / accuracy - 180.0
        val lat = latCode * 180.0 / accuracy - 90.0
        new GeoPoint(lng, lat)
    }


    /**
      * Get the boundary of given Geometry Hash Block (given by Long) and point
      *
      * @param code        given block hash
      * @param accuracy    accuracy of hash block
      * @param centerPoint search point opp to this block
      * @return List of the points seems to nearest or farthest
      */
    def getGeoHashBlockBoundaryPoints(centerPoint: IGeoPoint, accuracy: Long, code: Long): Iterable[IGeoPoint] = {
        val returnPoints = new mutable.ArrayBuffer[IGeoPoint]()
        val latCode = code & (POW2E31 - 1)
        val lngCode = (code - latCode) / POW2E31
        val lngMin = (180.0D * lngCode - 180.0D * accuracy) / accuracy.toDouble
        val latMin = (180.0D * latCode - 90.00D * accuracy) / accuracy.toDouble
        val lngMax = (180.0D * (lngCode + 1) - 180.0D * accuracy) / accuracy.toDouble
        val latMax = (180.0D * (latCode + 1) - 90.00D * accuracy) / accuracy.toDouble

        returnPoints.append(
            new GeoPoint(lngMin, latMin),
            new GeoPoint(lngMin, latMax),
            new GeoPoint(lngMax, latMin),
            new GeoPoint(lngMax, latMax)
        )

        if (lngMin < centerPoint.getLongitude && lngMax > centerPoint.getLatitude) {
            returnPoints.append(
                new GeoPoint(centerPoint.getLongitude, latMin),
                new GeoPoint(centerPoint.getLongitude, latMax)
            )
        }

        val boundary0 = math.cos((lngMin - centerPoint.getLongitude) * DEG2RAD)
        val boundary1 = math.cos((lngMax - centerPoint.getLongitude) * DEG2RAD)

        val downBoundary = math.min(boundary0, boundary1)
        val upBoundary = math.max(boundary0, boundary1)

        val partialDiffVariable = math.tan(centerPoint.getLatitude * DEG2RAD)
        val judgeMinLatitude = partialDiffVariable / math.tan(latMin * DEG2RAD)
        val judgeMaxLatitude = partialDiffVariable / math.tan(latMax * DEG2RAD)
        if (judgeMinLatitude < upBoundary && judgeMinLatitude > downBoundary) {
            returnPoints.append(new GeoPoint(centerPoint.getLongitude + math.acos(judgeMinLatitude) * RAD2DEG, judgeMinLatitude))
        }
        if (judgeMaxLatitude < upBoundary && judgeMaxLatitude > downBoundary) {
            returnPoints.append(new GeoPoint(centerPoint.getLongitude + math.acos(judgeMaxLatitude) * RAD2DEG, judgeMinLatitude))
        }
        returnPoints
    }

}