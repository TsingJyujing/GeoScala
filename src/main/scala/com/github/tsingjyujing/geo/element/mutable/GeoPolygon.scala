package com.github.tsingjyujing.geo.element.mutable

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.{GeoJSONable, IContains}
import com.github.tsingjyujing.geo.element.GeoBox
import com.github.tsingjyujing.geo.util.mathematical.PolygonUtil

import scala.collection.mutable.ArrayBuffer
import scala.util.parsing.json.JSONObject

class GeoPolygon(x: Iterable[IGeoPoint]) extends Iterable[IGeoPoint] with IContains[IGeoPoint] with GeoJSONable {

    override def iterator: Iterator[IGeoPoint] = polygon.iterator

    private val polygon: ArrayBuffer[IGeoPoint] = {
        val dataObject = ArrayBuffer[IGeoPoint]()
        dataObject.appendAll(x)
        dataObject
    }

    private var geoBox: GeoBox = GeoBox(polygon)

    private def updateBoxBoundary(): Unit = {
        geoBox = GeoBox(polygon)
    }


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

