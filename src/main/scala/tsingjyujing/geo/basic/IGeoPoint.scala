package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable}
import tsingjyujing.geo.element.immutable.{Vector2, Vector3}

import scala.util.parsing.json.JSONObject

trait IGeoPoint extends GeoDistanceMeasurable[IGeoPoint] with GeoJSONable{

    override def toGeoJSON: JSONObject = GeoJSONable.createPoint(this)

    def getLongitude: Double

    def getLatitude: Double

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override final def geoTo(point: IGeoPoint): Double = IGeoPoint.geodesicDistance(this, point)


    implicit final def toIVector3:IVector3 = new Vector3(
        math.cos(getLongitude * IGeoPoint.DEG2RAD) * math.cos(getLatitude * IGeoPoint.DEG2RAD),
        math.sin(getLongitude * IGeoPoint.DEG2RAD) * math.cos(getLatitude * IGeoPoint.DEG2RAD),
        math.sin(getLatitude * IGeoPoint.DEG2RAD)
    )

    implicit final def toIVector2:IVector2 = new Vector2(
        getLongitude,
        getLatitude
    )

    override def toString: String = "Longitude:%3.6f,Latitude:%3.6f".format(getLongitude, getLatitude)

}


object IGeoPoint {
    val DEG2RAD: Double = math.Pi / 180.0
    val MAX_INNER_PRODUCT_FOR_UNIT_VECTOR: Double = 1.0
    val EARTH_RADIUS: Double = 6378.5

    /**
      * Get inner product of two points
      *
      * @param point1 point1
      * @param point2 point2
      * @return
      */
    def getInnerProduct(point1: IGeoPoint, point2: IGeoPoint): Double = math.sin(point1.getLatitude * DEG2RAD) * math.sin(point2.getLatitude * DEG2RAD) + math.cos(point1.getLatitude * DEG2RAD) * math.cos(point2.getLatitude * DEG2RAD) * math.cos(point1.getLongitude * DEG2RAD - point2.getLongitude * DEG2RAD)

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