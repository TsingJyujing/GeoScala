package com.github.tsingjyujing.geo.util

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.immutable.{GeoPoint, Vector2}
import scala.collection.parallel.ParIterable

/**
  * Some geographical utility methods
  */
object GeoUtil {

    /**
      * Get steering angle by GPS info
      * @param p1 point1
      * @param p2 point2
      * @param p3 point3
      * @return
      */
    def steeringAngleGeodesic(
                                 p1: IGeoPoint,
                                 p2: IGeoPoint,
                                 p3: IGeoPoint
                             ): Double = {
        val v1 = p1.toIVector3
        val v2 = p2.toIVector3
        val v3 = p3.toIVector3
        (v1 outProduct v2) angle (v2 outProduct v3)
    }

    @deprecated(message = "Use 2d-sphere's local linearity attribution to solve angle is deprecated")
    def steeringAngleEuclidean[T](
                                     p1: IGeoPoint,
                                     p2: IGeoPoint,
                                     p3: IGeoPoint
                                 ): Double = {
        val latRatio = math.cos((p1.getLatitude + p2.getLatitude + p3.getLatitude) * math.Pi / 3.0 / 180.0)

        Vector2(
            (p2.getLongitude - p1.getLongitude) * latRatio,
            p2.getLatitude - p1.getLatitude
        ) angle Vector2(
            (p3.getLongitude - p2.getLongitude) * latRatio,
            p3.getLatitude - p2.getLatitude
        )
    }

    /**
      * Get center point of points
      * @param points points
      * @return
      */
    def mean(points: TraversableOnce[IGeoPoint]): IGeoPoint = {
        val sumVector3 = points.map(_.toIVector3).reduce(_ + _)
        val meanVector3 = sumVector3 / sumVector3.norm2
        val latitude = math.asin(meanVector3.getZ).toDegrees
        val longitude = math.asin(meanVector3.getY / math.cos(latitude))
        GeoPoint(longitude, latitude)
    }

    /**
      * Get center point of points in parallel
      * @param points points
      * @return
      */
    def mean(points:ParIterable[IGeoPoint]):IGeoPoint = {
        val sumVector3 = points.map(_.toIVector3).reduce(_ + _)
        val meanVector3 = sumVector3 / sumVector3.norm2
        val latitude = math.asin(meanVector3.getZ).toDegrees
        val longitude = math.asin(meanVector3.getY / math.cos(latitude))
        GeoPoint(longitude, latitude)
    }
}
