package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.IContains
import com.github.tsingjyujing.geo.element.immutable.Vector3
import com.github.tsingjyujing.geo.util.GeoUtil

/**
  * An circle which on the 2d-sphere in radius of earth
  * each point on the edge to center is radius
  *
  * @param centerPoint center of the circle
  * @param radius      radius of the circle
  */
case class GeoCircleArea(centerPoint: IGeoPoint, radius: Double) extends IContains[IGeoPoint] {


    assert(radius >= 0.0, "Radius should not less than zero.")

    /**
      * Is x contains in self
      *
      * @param x point to get contians
      * @return
      */
    override def contains(x: IGeoPoint): Boolean = (centerPoint geoTo x) < radius

    /**
      * Get polygon of
      *
      * @return
      */
    def toPolygon(pointCount: Int = 17): GeoPolygon = {
        val theta = radius / IGeoPoint.EARTH_RADIUS
        val cosTheta = math.cos(theta)
        val sinTheta = math.sin(theta)
        val V = centerPoint.toIVector3
        val u0 = Vector3(V.getY, -V.getX, 0)
        val u1 = Vector3(V.getZ * V.getX, V.getZ * V.getY, -(V.getX * V.getX + V.getY * V.getY))
        val e0 = u0 / u0.norm2
        val e1 = u1 / u1.norm2
        val points = (0 to pointCount).map(_ / (pointCount + 1.0) * 2.0 * math.Pi).map(t => {
            Vector3(
                V.getX * cosTheta + sinTheta * (e0.getX * math.sin(t) + e1.getX * math.cos(t)),
                V.getY * cosTheta + sinTheta * (e0.getY * math.sin(t) + e1.getY * math.cos(t)),
                V.getZ * cosTheta + sinTheta * (e0.getZ * math.sin(t) + e1.getZ * math.cos(t))
            )
        }).map(
            GeoUtil.vector3ToGeoPoint
        )
        GeoPolygon(points)
    }
}
