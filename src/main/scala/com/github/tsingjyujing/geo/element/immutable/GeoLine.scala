package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable}
import com.github.tsingjyujing.geo.util.mathematical.VectorUtil
import org.apache.commons.math3.linear.{Array2DRowRealMatrix, LUDecomposition, RealMatrix}

import scala.util.parsing.json.JSONObject

/**
  * Create a GeoLine object
  *
  * @param pointStart
  * @param pointEnd
  */
case class GeoLine(pointStart: IGeoPoint, pointEnd: IGeoPoint) extends GeoDistanceMeasurable[IGeoPoint] with GeoJSONable {

    val pointTuple: (IGeoPoint, IGeoPoint) = (pointStart, pointEnd)

    /**
      * get two points of the line
      *
      * @return
      */
    def getTerminalPoints: (IGeoPoint, IGeoPoint) = pointTuple

    /**
      * Get the distance of start point to end point as length of the line
      *
      * @return length of the line in kilometer
      */
    def lineLength: Double = getTerminalPoints._1.geoTo(getTerminalPoints._2)

    /**
      * The length of line should over 1 meter in current accuracy of GPS sensors
      *
      * @return is this line valid
      */
    protected def dataValid: Boolean = lineLength > 0.0

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def geoTo(point: IGeoPoint): Double = {
        val v3 = point.toIVector3.getVector
        val params: Array[Double] = invM.multiply(new Array2DRowRealMatrix(v3)).getColumn(0)
        if (params(0) > 0 && params(1) > 0) { //返回和n的内积的角度的余角计算出的数值
            math.abs(
                math.asin(n.zip(v3).map(x => x._1 * x._2).sum) * IGeoPoint.EARTH_RADIUS
            )
        } else {
            // Select the nearest in two nodes
            math.min(point geoTo getTerminalPoints._1, point geoTo getTerminalPoints._2)
        }
    }

    override def toGeoJSON: JSONObject = GeoJSONable.createLineString(Array(getTerminalPoints._1, getTerminalPoints._2))

    assert(dataValid, "Data not valid")

    /**
      * normal vector of two vectors
      * for each vector, is the O->point on sphere in R3
      */
    val n: Array[Double] = {
        val v1 = getTerminalPoints._1
        val v2 = getTerminalPoints._2
        VectorUtil.norm2Vector(v1.toIVector3.outProduct(v2.toIVector3).getVector)
    }

    /**
      * Get inversed matrix of {v1,v2,n} which can do decomposition of any vector
      */
    val invM: RealMatrix = {
        val v1: Array[Double] = getTerminalPoints._1.toIVector3.toArray
        val v2: Array[Double] = getTerminalPoints._2.toIVector3.toArray
        val M = new Array2DRowRealMatrix(Array(
            v1, v2, n
        )).transpose()
        new LUDecomposition(M).getSolver.getInverse
    }

}
