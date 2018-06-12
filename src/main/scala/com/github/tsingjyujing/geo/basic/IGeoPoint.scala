package com.github.tsingjyujing.geo.basic

import com.github.tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable}
import com.github.tsingjyujing.geo.element.immutable.{Vector2, Vector3}
import com.github.tsingjyujing.geo.element.mutable.GeoPoint

import scala.util.parsing.json.JSONObject

/**
  * Any type which can get longitude and latitude (on earth)
  */
trait IGeoPoint extends GeoDistanceMeasurable[IGeoPoint] with GeoJSONable with Serializable {

    def -(x: IVector2): IGeoPoint = GeoPoint(getLongitude - x.getX, getLatitude - x.getY)

    def -(x: IGeoPoint): IVector2 = Vector2(getLongitude - x.getLongitude, getLatitude - x.getLatitude)

    def +(x: IVector2): IGeoPoint = GeoPoint(getLongitude + x.getX, getLatitude * x.getY)

    override def toGeoJSON: JSONObject = GeoJSONable.createPoint(this)

    /**
      * Get longitude recommended in WGS84 format
      *
      * @return Longitude in degree
      */
    def getLongitude: Double

    /**
      * Get latitude recommended in WGS84 format
      *
      * @return Latitude in degree
      */
    def getLatitude: Double

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override final def geoTo(point: IGeoPoint): Double = {
        val dx = math.abs(point.getLongitude - getLongitude)
        val dy = math.abs(point.getLatitude - getLatitude)

        if (dx < 0.02 && dy < 0.02) {
            // If points are closed, use localEuclidDistance
            // for faster and more accuracy while points are too close (such as less than 1m)
            IGeoPoint.localEuclidDistance(this, point)
        } else {
            IGeoPoint.geodesicDistance(this, point)
        }
    }

    /**
      * Get vector3 in R3 on 2d sphere
      *
      * @return
      */
    final def toIVector3: IVector3 = Vector3(
        math.cos(getLongitude.toRadians) * math.cos(getLatitude.toRadians),
        math.sin(getLongitude.toRadians) * math.cos(getLatitude.toRadians),
        math.sin(getLatitude.toRadians)
    )

    /**
      * Get Mercator projection potision as vector2
      *
      * @return
      */
    final def toIVector2: IVector2 = Vector2(
        getLongitude,
        getLatitude
    )

    override def toString: String = "IGeoPoint(%3.6f,%3.6f)".format(getLongitude, getLatitude)

    /**
      * Verify is longitude value is legal
      *
      * @throws AssertionError verify failed
      */
    @throws[AssertionError]
    protected def verifyLongitude: Unit = {
        assert(
            getLongitude >= (-180.0) && getLongitude <= 180.0,
            "Longitude %3.6f is an illegal value (should in [-180,180]).".format(getLongitude)
        )
    }

    /**
      * Verify is latitude value is legal
      *
      * @throws AssertionError verify failed
      */
    @throws[AssertionError]
    protected def verifyLatitude: Unit = {
        assert(
            getLatitude >= (-180.0) && getLatitude <= 180.0,
            "Latitude %3.6f is an illegal value (should in [-90,90]).".format(getLatitude)
        )
    }

    /**
      * Verify is latitude & longitude values are legal
      *
      * @throws AssertionError verify failed
      */
    @throws[AssertionError]
    protected def verifyValues: Unit = {
        verifyLatitude
        verifyLongitude
    }
}


object IGeoPoint {
    val MAX_INNER_PRODUCT_FOR_UNIT_VECTOR: Double = 1.0000
    val EARTH_RADIUS: Double = 6378.5

    /**
      * Create a mutable data
      *
      * @param longitude
      * @param latitude
      * @return
      */
    def apply(longitude: Double, latitude: Double): IGeoPoint = GeoPoint(longitude, latitude)

    /**
      * Get inner product of two points
      *
      * @param point1 point1
      * @param point2 point2
      * @return
      */
    def getInnerProduct(point1: IGeoPoint, point2: IGeoPoint): Double = math.sin(point1.getLatitude.toRadians) * math.sin(point2.getLatitude.toRadians) + math.cos(point1.getLatitude.toRadians) * math.cos(point2.getLatitude.toRadians) * math.cos(point1.getLongitude.toRadians - point2.getLongitude.toRadians)

    /**
      * Get distance on earth of two points
      *
      * @param point1 point1
      * @param point2 point2
      * @return
      */
    def geodesicDistance(point1: IGeoPoint, point2: IGeoPoint): Double = {
        val alpha = getInnerProduct(point1, point2)
        if (alpha >= MAX_INNER_PRODUCT_FOR_UNIT_VECTOR) {
            0.0D
        } else if (alpha <= -MAX_INNER_PRODUCT_FOR_UNIT_VECTOR) {
            EARTH_RADIUS * math.Pi
        } else {
            math.acos(alpha) * EARTH_RADIUS
        }
    }

    /**
      * Distance use local Euclid for more accuracy while two points are closed enough
      *
      * @param point1 point1
      * @param point2 point2
      * @return
      */
    def localEuclidDistance(point1: IGeoPoint, point2: IGeoPoint): Double = {
        val meanLatitude = (point1.getLatitude + point2.getLatitude) / 2.0
        val deltaLatitude = math.abs(point1.getLatitude - point2.getLatitude)
        val deltaLongitude = math.abs(point1.getLongitude - point2.getLongitude)
        val dx = EARTH_RADIUS * math.cos(meanLatitude.toRadians) * deltaLongitude.toRadians
        val dy = EARTH_RADIUS * deltaLatitude.toRadians
        math.sqrt(dx * dx + dy * dy)
    }

}