package com.github.tsingjyujing.geo.basic

import com.github.tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable}
import com.github.tsingjyujing.geo.util.mathematical.{MatrixUtil, VectorUtil}

import scala.util.parsing.json.JSONObject

/**
  * Geodesic line on 2d-sphere described by two points of `IGeoPoint`
  */
trait IGeoLine extends GeoDistanceMeasurable[IGeoPoint] with GeoJSONable {

    /**
      * The length of line should over 1 meter in current accuracy of GPS sensors
      *
      * @return is this line valid
      */
    protected def dataValid: Boolean = lineLength > 0.001

    /**
      * Get the distance of start point to end point as length of the line
      *
      * @return length of the line in kilometer
      */
    def lineLength: Double = getTerminalPoints._1.geoTo(getTerminalPoints._2)

    /**
      * get two points of the line
      *
      * @return
      */
    def getTerminalPoints: (IGeoPoint, IGeoPoint)

    /**
      * normal vector of two vectors
      * for each vector, is the O->point on sphere in R3
      */
    lazy val n: Array[Double] = {
        val v1 = getTerminalPoints._1
        val v2 = getTerminalPoints._2
        VectorUtil.norm2Vector(v1.toIVector3.outProduct(v2.toIVector3).getVector)
    }

    /**
      * Get inversed matrix of {v1,v2,n} which can do decomposition of any vector
      */
    lazy val iM: Array[Array[Double]] = {
        val v1 = getTerminalPoints._1
        val v2 = getTerminalPoints._2
        val M = Array(v1.toIVector3.getVector, v2.toIVector3.getVector, n)
        MatrixUtil.inverseOrder3Matrix(M)
    }

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def geoTo(point: IGeoPoint): Double = {
        val v3 = point.toIVector3.getVector
        val params = MatrixUtil.matrixProduct(iM, v3)
        if (params(0) > 0 && params(1) > 0) { //返回和n的内积的角度的余角计算出的数值
            math.asin(n(0) * v3(0) + n(1) * v3(1) + n(2) * v3(2)) * IGeoPoint.EARTH_RADIUS
        } else {
            // Select the nearest in two nodes
            math.min(point geoTo getTerminalPoints._1, point geoTo getTerminalPoints._2)
        }
    }

    override def toGeoJSON: JSONObject = GeoJSONable.createLineString(Array(getTerminalPoints._1, getTerminalPoints._2))

}
