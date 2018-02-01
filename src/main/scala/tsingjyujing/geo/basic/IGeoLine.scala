package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable}
import tsingjyujing.geo.util.mathematical.{MatrixUtil, VectorUtil}

import scala.util.parsing.json.JSONObject

trait IGeoLine extends GeoDistanceMeasurable[IGeoPoint] with GeoJSONable {

    def getTerminalPoints: (IGeoPoint, IGeoPoint)

    val n: Array[Double] = {
        val v1 = getTerminalPoints._1
        val v2 = getTerminalPoints._2
        VectorUtil.norm2Vector(v1.toIVector3.outProduct(v2.toIVector3).getVector)
    }

    val iM: Array[Array[Double]] = {
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
            //两个端点中选一个近的
            math.min(point geoTo getTerminalPoints._1, point geoTo getTerminalPoints._2)
        }
    }

    override def toGeoJSON: JSONObject = GeoJSONable.createLineString(Array(getTerminalPoints._1, getTerminalPoints._2))

}

object IGeoLine {

}
