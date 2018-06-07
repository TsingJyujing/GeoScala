package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable}

import scala.util.parsing.json.JSONObject

/**
  * Geo point line string
  *
  * @param points points which contribute line string
  */
case class GeoLineString(points: IndexedSeq[IGeoPoint]) extends GeoDistanceMeasurable[IGeoPoint] with GeoJSONable {


    assert(points.size >= 2, "Points count can't less than 2.")

    /**
      * Geo line string data
      */
    val geoLineString: IndexedSeq[GeoLine] = points.sliding(2).map(x => GeoLine(x.head, x.last)).toIndexedSeq

    /**
      * Fetch result of the line string
      *
      * @param offsetStart start point index
      * @param offsetEnd   end point indedx
      * @param distance    distance to line
      */
    case class FetchResult(offsetStart: Int, offsetEnd: Int, distance: Double) {
        /**
          * Get fetched line
          *
          * @return
          */
        def getLine: GeoLine = geoLineString(offsetStart)

        /**
          * Get line start point
          *
          * @return
          */
        def getStartPoint: IGeoPoint = points(offsetStart)

        /**
          * Get line end point
          *
          * @return
          */
        def getEndPoint: IGeoPoint = points(offsetEnd)
    }

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def geoTo(point: IGeoPoint): Double = geoLineString.map(_.geoTo(point)).min

    /**
      * Fetch the nearest line in the
      *
      * @param point
      * @return
      */
    def fetchLineString(point: IGeoPoint): FetchResult = {
        val resTemplate = geoLineString.map(_.geoTo(point)).zipWithIndex.minBy(_._1)
        FetchResult(resTemplate._2, resTemplate._2 + 1, resTemplate._1)
    }

    /**
      * Get scala original JSON object,
      * JSON object deprecated in Scala 2.12 but still using in 2.10
      *
      * @return
      */
    override def toGeoJSON: JSONObject = GeoJSONable.createLineString(points)


}
