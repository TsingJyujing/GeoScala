package com.github.tsingjyujing.geo.basic

import com.github.tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable}
import com.github.tsingjyujing.geo.element.immutable.{Vector2, Vector3}
import scala.util.parsing.json.JSONObject

/**
  * Any type which can get longitude and latitude (on earth)
  */
trait IGeoPoint extends GeoDistanceMeasurable[IGeoPoint] with GeoJSONable {

    override def toGeoJSON: JSONObject = GeoJSONable.createPoint(this)

    /**
      * Get longitude recommended in WGS84 format
      * @return Longitude in degree
      */
    def getLongitude: Double

    /**
      * Get latitude recommended in WGS84 format
      * @return Latitude in degree
      */
    def getLatitude: Double

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override final def geoTo(point: IGeoPoint): Double = IGeoPoint.geodesicDistance(this, point)

    /**
      * Get vector3 in R3 on 2d sphere
      * @return
      */
    final def toIVector3: IVector3 = Vector3(
        math.cos(getLongitude.toRadians) * math.cos(getLatitude.toRadians),
        math.sin(getLongitude.toRadians) * math.cos(getLatitude.toRadians),
        math.sin(getLatitude.toRadians)
    )

    /**
      * Get Mercator projection potision as vector2
      * @return
      */
    final def toIVector2: IVector2 = Vector2(
        getLongitude,
        getLatitude
    )

    override def toString: String = "Longitude:%3.6f,Latitude:%3.6f".format(getLongitude, getLatitude)

}


object IGeoPoint {
    val MAX_INNER_PRODUCT_FOR_UNIT_VECTOR: Double = 1.0
    val EARTH_RADIUS: Double = 6378.5

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

}