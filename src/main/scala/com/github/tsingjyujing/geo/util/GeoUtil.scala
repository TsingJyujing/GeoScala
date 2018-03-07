package com.github.tsingjyujing.geo.util

import com.github.tsingjyujing.geo.basic.{IGeoPoint, IVector3}
import com.github.tsingjyujing.geo.element.immutable.{GeoPoint, TimeElement, Vector2}
import com.github.tsingjyujing.geo.util.mathematical.VectorUtil

import scala.collection.parallel.ParIterable

/**
  * @author tsingjyujing@163.com
  * Some geographical utility methods
  */
object GeoUtil {

    /**
      * Get steering angle by GPS info
      *
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
      * Convert a 3-d point into WGS84 format
      *
      * @param v vector input
      * @return
      */
    def vector3ToGeoPoint(v: IVector3): IGeoPoint = {
        val meanVector = v / v.norm2
        val latitude = math.asin(meanVector.getZ)
        val rawAngle = math.asin(meanVector.getY / math.cos(latitude))
        val longitude = if (v.getX > 0) {
            rawAngle
        } else {
            if (rawAngle > 0) {
                math.Pi - rawAngle
            } else {
                -(math.Pi + rawAngle)
            }
        }
        GeoPoint(longitude.toDegrees, latitude.toDegrees)
    }

    /**
      * Get center point of points
      *
      * @param points points
      * @return
      */
    def mean(points: TraversableOnce[IGeoPoint]): IGeoPoint = vector3ToGeoPoint(points.map(_.toIVector3).reduce(_ + _))

    /**
      * Get center point of points in parallel
      *
      * @param points points
      * @return
      */
    def mean(points: ParIterable[IGeoPoint]): IGeoPoint = {
        val sumVector3 = points.map(_.toIVector3).reduce(_ + _)
        val meanVector3 = sumVector3 / sumVector3.norm2
        val latitude = math.asin(meanVector3.getZ).toDegrees
        val longitude = math.asin(meanVector3.getY / math.cos(latitude))
        GeoPoint(longitude, latitude)
    }

    /**
      * "linear" interpolation in two gps points with insert count
      *
      * @param fromPoint Start point
      * @param toPoint
      * @param insertPointCount
      * @tparam T
      * @return
      */
    def interp[T <: IGeoPoint](fromPoint: T, toPoint: T, insertPointCount: Int): TraversableOnce[IGeoPoint] = {
        assert(insertPointCount >= 1, "Parameter insertPointCount invalid.")
        val angleMax = math.acos(fromPoint.toIVector3.innerProduct(toPoint.toIVector3))
        val dAngle = angleMax / (1 + insertPointCount)
        VectorUtil.sphereInterpFast(fromPoint.toIVector3, toPoint.toIVector3, (0 to (insertPointCount + 1)).map(_ * dAngle)).map(vector3ToGeoPoint)
    }

    /**
      * "linear" interpolation in two gps points with insert count with ratio
      *
      * @param fromPoint start point
      * @param toPoint   end point
      * @param ratio     the place ratio on geodesic of start-->end
      * @tparam T Type of point
      * @return
      */
    def interp[T <: IGeoPoint](fromPoint: T, toPoint: T, ratio: Double): IGeoPoint = {
        assert(ratio <= 1 && ratio >= 0, "Parameter ratio invalid.")
        vector3ToGeoPoint(VectorUtil.sphereInterpFast(fromPoint.toIVector3, toPoint.toIVector3, ratio * math.acos(fromPoint.toIVector3.innerProduct(toPoint.toIVector3))))
    }

    /**
      * "linear" interpolation in two gps points with insert count with ratios
      *
      * @param fromPoint start point
      * @param toPoint   end point
      * @param ratios    the places ratios on geodesic of start-->end
      * @tparam T Type of point
      * @return
      */
    def interp[T <: IGeoPoint](fromPoint: T, toPoint: T, ratios: TraversableOnce[Double]): TraversableOnce[IGeoPoint] = {
        assert(ratios.forall(ratio => ratio <= 1 && ratio >= 0), "Parameter ratios invalid.")
        val maxAngle = math.acos(fromPoint.toIVector3.innerProduct(toPoint.toIVector3))
        VectorUtil.sphereInterpFast(fromPoint.toIVector3, toPoint.toIVector3, ratios.map(_ * maxAngle)).map(vector3ToGeoPoint)
    }

    /**
      *
      * @param fromPoint        start point
      * @param toPoint          end point
      * @param insertPointCount points to insert in
      * @tparam T Type of point
      * @return
      */
    def interp[T <: IGeoPoint](fromPoint: TimeElement[T], toPoint: TimeElement[T], insertPointCount: Int): Iterable[TimeElement[IGeoPoint]] = {
        assert(insertPointCount >= 1, "Parameter insertPointCount invalid.")
        val angleMax = math.acos(fromPoint.value.toIVector3.innerProduct(toPoint.value.toIVector3))
        val dAngle = angleMax / (1 + insertPointCount)
        val dt = (toPoint.getTick - fromPoint.getTick) / (1 + insertPointCount)
        VectorUtil.sphereInterpFast(fromPoint.getValue.toIVector3, toPoint.getValue.toIVector3, (0 to (insertPointCount + 1)).map(_ * dAngle)).toIterable.zipWithIndex.map(xi => {
            TimeElement(fromPoint.getTick + dt * xi._2, vector3ToGeoPoint(xi._1))
        })
    }
}
