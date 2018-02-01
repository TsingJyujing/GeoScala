package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations.IHashedIndex
import tsingjyujing.geo.element.immutable.GeoPoint

import scala.collection.mutable


/**
  * Squared hash of geo
  * Which geo point can get an Long index value and get hashed.
  *
  */
trait IHashableGeoBlock extends IGeoPoint with IHashedIndex[Long] {

    def getGeoHashAccuracy: Long

    def getCenterPoint: IGeoPoint

    override def getLongitude: Double = getCenterPoint.getLongitude

    override def getLatitude: Double = getCenterPoint.getLatitude

    lazy val circumradius: Double = getBoundaryPoints(getCenterPoint).map(_ geoTo getCenterPoint).max

    lazy val inradius: Double = getBoundaryPoints(getCenterPoint).map(_ geoTo getCenterPoint).min

    def getBoundaryPoints(blockCode: Long): Iterable[IGeoPoint] = IHashableGeoBlock.getGeoHashBlockBoundaryPoints(this, getGeoHashAccuracy, blockCode)

    def getBoundaryPoints(point: IGeoPoint): Iterable[IGeoPoint] = IHashableGeoBlock.getGeoHashBlockBoundaryPoints(point, getGeoHashAccuracy, indexCode)

    def getMinDistance(x: IHashableGeoBlock): Double = math.max((getCenterPoint geoTo x.getCenterPoint) - circumradius + x.circumradius, 0.0)

    def getMaxDistance(x: IHashableGeoBlock): Double = (getCenterPoint geoTo x.getCenterPoint) + (circumradius + x.circumradius)

    /**
      * get min distance from block to point
      *
      * if point in block: --> 0
      * else:
      * if point out of circumradius --> distanceToCenter - circumradius
      * else --> calculate for each boundary point
      *
      * @param x
      * @return
      */
    def getMinDistance(x: IGeoPoint): Double = {
        val currentPointHash = IHashableGeoBlock.createCodeFromGps(x, getGeoHashAccuracy)
        if (currentPointHash == indexCode) {
            0
        } else {
            val distanceToCenter = x.geoTo(getCenterPoint)
            if (distanceToCenter > circumradius) {
                distanceToCenter - circumradius
            } else if (distanceToCenter > inradius) {
                getBoundaryPoints(x).map(_.geoTo(x)).min
            } else {
                throw new RuntimeException("InnerError: Distance less than inradius but not in block.")
            }
        }
    }

    /**
      * get max distance from block to point
      *
      * @param x
      * @return
      */
    def getMaxDistance(x: IGeoPoint): Double = (getCenterPoint geoTo x) + circumradius

    override def equals(o: Any): Boolean = o match {
        case ob: IHashableGeoBlock =>
            if (ob.getGeoHashAccuracy == getGeoHashAccuracy) {
                ob.indexCode == indexCode
            } else {
                false
            }
        case _: Any =>
            false
    }

    override def hashCode(): Int = indexCode.hashCode()
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
    val MAX_INNER_PRODUCT_FOR_UNIT_VECTOR: Double = 1.0
    val EARTH_RADIUS: Double = 6378.5

    def createCodeFromGps(point: IGeoPoint, accuracy: Long): Long = {
        val lngCode = math.round((point.getLongitude + 180.0d) / 180.0d * accuracy)
        val latCode = math.round((point.getLatitude + 90.00d) / 180.0d * accuracy)
        lngCode * IHashableGeoBlock.POW2E31 + latCode
    }


    def revertFromCode(code: Long, accuracy: Long): IGeoPoint = {
        val latCode = code & (POW2E31 - 1)
        val lngCode = (code - latCode) / POW2E31
        val lng = lngCode * 180.0 / accuracy - 180.0
        val lat = latCode * 180.0 / accuracy - 90.0
        GeoPoint(lng, lat)
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
        val latCode: Long = code & (POW2E31 - 1)
        val lngCode: Long = (code - latCode) / POW2E31
        val lngMin: Double = (180.0D * (lngCode - 0.5) - 180.0D * accuracy) / accuracy
        val latMin: Double = (180.0D * (latCode - 0.5) - 90.00D * accuracy) / accuracy
        val lngMax: Double = (180.0D * (lngCode + 0.5) - 180.0D * accuracy) / accuracy
        val latMax: Double = (180.0D * (latCode + 0.5) - 90.00D * accuracy) / accuracy

        returnPoints.append(
            GeoPoint(lngMin, latMin),
            GeoPoint(lngMin, latMax),
            GeoPoint(lngMax, latMin),
            GeoPoint(lngMax, latMax)
        )

        if (lngMin < centerPoint.getLongitude && lngMax > centerPoint.getLatitude) {
            returnPoints.append(
                GeoPoint(centerPoint.getLongitude, latMin),
                GeoPoint(centerPoint.getLongitude, latMax)
            )
        }

        val boundary0: Double = math.cos((lngMin - centerPoint.getLongitude).toRadians)
        val boundary1: Double = math.cos((lngMax - centerPoint.getLongitude).toRadians)

        val downBoundary: Double = math.min(boundary0, boundary1)
        val upBoundary: Double = math.max(boundary0, boundary1)

        val partialDiffVariable: Double = math.tan(centerPoint.getLatitude.toRadians)
        val judgeMinLatitude: Double = partialDiffVariable / math.tan(latMin.toRadians)
        val judgeMaxLatitude: Double = partialDiffVariable / math.tan(latMax.toRadians)
        if (judgeMinLatitude < upBoundary && judgeMinLatitude > downBoundary) {
            returnPoints.append(GeoPoint(centerPoint.getLongitude + math.acos(judgeMinLatitude).toDegrees, judgeMinLatitude))
        }
        if (judgeMaxLatitude < upBoundary && judgeMaxLatitude > downBoundary) {
            returnPoints.append(GeoPoint(centerPoint.getLongitude + math.acos(judgeMaxLatitude).toDegrees , judgeMinLatitude))
        }
        returnPoints
    }

}