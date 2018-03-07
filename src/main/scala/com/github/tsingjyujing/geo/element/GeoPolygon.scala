package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.{GeoJSONable, IContains}
import com.github.tsingjyujing.geo.util.mathematical.PolygonUtil

import scala.collection.mutable.ArrayBuffer
import scala.util.parsing.json.JSONObject

/**
  * Mutable implementation of polygon
  * @author tsingjyujing@163.com
  * @param polygonPoints polygon points with out first point in the last position
  *                      For example: p0,p1,p2 represents polygon of p0->p1->p2->p0
  */
class GeoPolygon(polygonPoints: Iterable[IGeoPoint]) extends Iterable[IGeoPoint] with IContains[IGeoPoint] with GeoJSONable {

    override def iterator: Iterator[IGeoPoint] = polygon.iterator

    private val polygon: ArrayBuffer[IGeoPoint] = {
        val dataObject = ArrayBuffer[IGeoPoint]()
        dataObject.appendAll(polygonPoints)
        dataObject
    }

    private var geoBox: GeoBox = GeoBox(polygon)

    private def updateBoxBoundary(): Unit = {
        geoBox = GeoBox(polygon)
    }

    /**
      * Change polygon
      *
      * @param operate operation to process polygon
      */
    def operatePolygon(operate: ArrayBuffer[IGeoPoint] => Unit): Unit = {
        operate(polygon)
        updateBoxBoundary()
    }

    override def contains(point: IGeoPoint): Boolean = if (geoBox contains point) {
        PolygonUtil.polygonRayCasting(point, polygon)
    } else {
        false
    }

    override def toGeoJSON: JSONObject = GeoJSONable.createRingPolygon(polygon)
}

