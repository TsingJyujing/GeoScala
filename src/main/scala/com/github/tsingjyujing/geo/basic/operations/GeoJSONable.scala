package com.github.tsingjyujing.geo.basic.operations

import com.github.tsingjyujing.geo.basic.IGeoPoint

import scala.util.parsing.json._

trait GeoJSONable {

    def toGeoJSON: JSONObject

    def toGeoJSONString: String = toGeoJSON.toString()

}

object GeoJSONable {

    private def JSONSeq[T](elems: T*): JSONArray = JSONArray(List(elems: _*))

    private def JSONList[T](elems: TraversableOnce[T]): JSONArray = JSONArray(elems.toList)

    private def JSONMap[V](elems: (String, V)*): JSONObject = JSONObject(elems.toMap)

    def createPoint(point: IGeoPoint): JSONObject = {
        JSONMap(
            "type" -> "Point",
            "coordinates" -> JSONSeq(point.getLongitude, point.getLatitude)
        )
    }

    def createMultiPoints(points: TraversableOnce[IGeoPoint]): JSONObject = {
        JSONMap(
            "type" -> "MultiPoint",
            "coordinates" -> JSONList(points.map(point => JSONSeq(point.getLongitude, point.getLatitude)))
        )
    }

    def createLineString(points: TraversableOnce[IGeoPoint]): JSONObject = {
        JSONMap(
            "type" -> "LineString",
            "coordinates" -> JSONList(points.map(point => JSONSeq(point.getLongitude, point.getLatitude)))
        )
    }

    def createMultiLineString(pointsList: TraversableOnce[TraversableOnce[IGeoPoint]]): JSONObject = {
        JSONMap(
            "type" -> "MultiLineString",
            "coordinates" -> JSONList(pointsList.map(points => JSONList(points.map(point => JSONSeq(point.getLongitude, point.getLatitude)))))
        )
    }

    def createPolygon(pointsList: TraversableOnce[Traversable[IGeoPoint]], autoClose: Boolean = true): JSONObject = {
        JSONMap(
            "type" -> "Polygon",
            "coordinates" -> JSONList(
                pointsList.map(points => {
                    val first = points.head
                    val last = points.last
                    JSONList(
                        if (autoClose && first.geoTo(last) >= 1e-10) {
                            List(points.toSeq: _*) ::: List(first)
                        } else {
                            points
                        }
                    )
                })
            )
        )
    }

    def createRingPolygon(points: Traversable[IGeoPoint], autoClose: Boolean = true): JSONObject = createPolygon(List(points), autoClose)

    def createGeometryCollection(objects: TraversableOnce[JSONObject]): JSONObject = {
        JSONMap(
            "type" -> "GeometryCollection",
            "geometries" -> JSONList(objects)
        )
    }

}
