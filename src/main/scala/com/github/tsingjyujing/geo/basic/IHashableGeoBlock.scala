package com.github.tsingjyujing.geo.basic

import com.github.tsingjyujing.geo.basic.IHashableGeoBlock.POW2E31
import com.github.tsingjyujing.geo.basic.operations.IHashedIndex
import com.github.tsingjyujing.geo.element.GeoBox
import com.github.tsingjyujing.geo.element.immutable.GeoPoint

import scala.collection.mutable


/**
  * Squared hash of geo
  * Which geo point can get an Long index value and get hashed.
  */
trait IHashableGeoBlock extends IGeoPoint with IHashedIndex[Long] {

    /**
      * Accuracy to devide earth into N*N parts
      *
      * @return
      */
    def getGeoHashAccuracy: Long

    /**
      * Get center point of the hash block
      *
      * @return
      */
    def getCenterPoint: IGeoPoint = IHashableGeoBlock.revertFromCode(indexCode, getGeoHashAccuracy)

    override def getLongitude: Double = getCenterPoint.getLongitude

    override def getLatitude: Double = getCenterPoint.getLatitude

    /**
      * Get a spherical crown to hash block
      */
    lazy val circumradius: Double = geoBox.anglePoints.map(_ geoTo getCenterPoint).max

    /**
      * Get an inner circle in hash block
      *
      * **For why we use this design:**
      * Get an inradius less than closedFormInradius to solve the value error of IEEE Float
      */
    lazy val inradius: Double = {
        val closedFormInradius = {
            //Calculate tow distance to longitude and latitude boundary
            val centerLatitude = getCenterPoint.getLatitude
            val centerLongitude = getCenterPoint.getLongitude
            val latitudeDirectionalDistance = math.min(
                math.abs(centerLatitude - geoBox.minLatitude),
                math.abs(centerLatitude - geoBox.maxLatitude)
            ).toRadians * IGeoPoint.EARTH_RADIUS
            val longitudeDirectionalDistance = math.min(
                math.abs(centerLongitude - geoBox.minLongitude),
                math.abs(centerLongitude - geoBox.maxLongitude)
            ).toRadians * IGeoPoint.EARTH_RADIUS * math.cos(centerLatitude.toRadians)
            math.min(longitudeDirectionalDistance, latitudeDirectionalDistance)
        }
        if (closedFormInradius <= 10) {
            closedFormInradius * 0.95
        } else {
            closedFormInradius - 0.5
        }
    }

    /**
      * Get points on boundary maybe the min/max distance
      *
      * @param point reference points
      * @return
      */
    def getBoundaryPoints(point: IGeoPoint): Iterable[IGeoPoint] = {
        val boxPoints = List(geoBox.anglePoints: _*)
        val boundLongitude = if (geoBox.getLongitudeRange.contains(point.getLongitude)) {
            List(
                GeoPoint(point.getLongitude, geoBox.minLatitude),
                GeoPoint(point.getLongitude, geoBox.minLatitude)
            )
        } else {
            List.empty
        }
        val boundLatitude = if (geoBox.getLatitudeRange.contains(point.getLatitude)) {
            List(
                GeoPoint(geoBox.minLongitude, point.getLatitude),
                GeoPoint(geoBox.maxLongitude, point.getLatitude)
            )
        } else {
            List.empty
        }
        boxPoints ::: boundLatitude ::: boundLongitude
    }

    /**
      * Get min distance from block to this block
      *
      * @param x block info
      * @return
      */
    def getMinDistance(x: IHashableGeoBlock): Double = math.max((getCenterPoint geoTo x.getCenterPoint) - circumradius + x.circumradius, 0.0)

    /**
      * Get max distance from block to this block
      *
      * @param x block info
      * @return
      */
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
            if (distanceToCenter >= circumradius) {
                distanceToCenter - circumradius
            } else if (distanceToCenter >= inradius) {
                getBoundaryPoints(x).map(_.geoTo(x)).min
            } else {
                // Output detail debug info
                val sb = new mutable.StringBuilder()
                sb.append("InternalError: Distance less than inradius but not in block.\n")
                sb.append("\tblock info: HashCode:%d, Accuracy:%d\n".format(indexCode, getGeoHashAccuracy))
                sb.append("\tblock center: (%3.6f, %3.6f)\n".format(getLongitude, getLatitude))
                sb.append("\tblock radius: (%f, %f)\n".format(inradius, circumradius))
                sb.append("\tblock geoBox: %s\n".format(toGeoBox.toString))
                sb.append("\tblock distance center: (%3.6f, %3.6f)\n".format(x.getLongitude, x.getLatitude))
                throw new RuntimeException(sb.toString())
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

    val geoBox: GeoBox = {
        val accuracy = getGeoHashAccuracy
        val code = indexCode
        val latCode = code & (POW2E31 - 1)
        val lngCode = (code - latCode) / POW2E31
        GeoBox(
            (180.0D * (lngCode - 0.5) - 180.0D * accuracy) / accuracy,
            (180.0D * (lngCode + 0.5) - 180.0D * accuracy) / accuracy,
            (180.0D * (latCode - 0.5) - 90.00D * accuracy) / accuracy,
            (180.0D * (latCode + 0.5) - 90.00D * accuracy) / accuracy
        )
    }

    /**
      * Get boundary box of hash block
      *
      * @return
      */
    def toGeoBox: GeoBox = geoBox

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
    @deprecated(message = "Some bugs in this function, will remove in RELEASE version")
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
            returnPoints.append(GeoPoint(centerPoint.getLongitude + math.acos(judgeMaxLatitude).toDegrees, judgeMinLatitude))
        }
        returnPoints
    }

}