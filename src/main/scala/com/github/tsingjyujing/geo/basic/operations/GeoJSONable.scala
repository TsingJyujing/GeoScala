package com.github.tsingjyujing.geo.basic.operations

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.model.{GeoJsonPoint, GeoJsonPolygon}
import com.google.gson.Gson

import scala.util.parsing.json._

/**
  * Object which can convert to GeoJSON, for more details, see:
  * http://geojson.org/
  */
trait GeoJSONable {

    /**
      * Get scala original JSON object,
      * JSON object deprecated in Scala 2.12 but still using in 2.10
      *
      * @return
      */
    def toGeoJSON: JSONObject

    def toGeoJSONString: String = toGeoJSON.toString()

}

/**
  * Common utils of generating GeoJSON object
  */
object GeoJSONable {

    private val gson: Gson = new Gson()

    private def JSONSeq[T](elems: T*): JSONArray = JSONArray(List(elems: _*))

    private def JSONList[T](elems: TraversableOnce[T]): JSONArray = JSONArray(elems.toList)

    private def JSONMap[V](elems: (String, V)*): JSONObject = JSONObject(elems.toMap)

    /**
      * Create Point GeoJSON object
      * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.2">Point</a>
      *
      * @param point which can get longitude and latitude
      * @return
      */
    def createPoint(point: IGeoPoint): JSONObject = {
        JSONMap(
            "type" -> "Point",
            "coordinates" -> JSONSeq(point.getLongitude, point.getLatitude)
        )
    }

    /**
      * Create MultiPoint GeoJSON object
      * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.3">MultiPoint</a>
      *
      * @param points points
      * @return
      */
    def createMultiPoint(points: TraversableOnce[IGeoPoint]): JSONObject = {
        JSONMap(
            "type" -> "MultiPoint",
            "coordinates" -> JSONList(points.map(point => JSONSeq(point.getLongitude, point.getLatitude)))
        )
    }

    /**
      * Create LineString GeoJSON object
      * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.4">LineString</a>
      *
      * @param points
      * @return
      */
    def createLineString(points: TraversableOnce[IGeoPoint]): JSONObject = {
        JSONMap(
            "type" -> "LineString",
            "coordinates" -> JSONList(points.map(point => JSONSeq(point.getLongitude, point.getLatitude)))
        )
    }

    /**
      * MultiLineString
      * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.5">MultiLineString</a>
      *
      * @param pointsList
      * @return
      */
    def createMultiLineString(pointsList: TraversableOnce[TraversableOnce[IGeoPoint]]): JSONObject = {
        JSONMap(
            "type" -> "MultiLineString",
            "coordinates" -> JSONList(pointsList.map(points => JSONList(points.map(point => JSONSeq(point.getLongitude, point.getLatitude)))))
        )
    }

    /**
      * Standard support of polygon GeoJSON
      * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.6">Polygon</a>
      *
      * @param pointsList ring and hole
      * @param autoClose  is auto close the polygon
      * @return
      */
    def createPolygon(pointsList: TraversableOnce[Traversable[IGeoPoint]], autoClose: Boolean = true): JSONObject = {
        JSONMap(
            "type" -> "Polygon",
            "coordinates" -> JSONList(
                pointsList.map(points => {
                    val first = points.head
                    val last = points.last
                    JSONList(
                        {
                            val pointsClosed = if (autoClose && first.geoTo(last) >= 1e-10) {
                                List(points.toSeq: _*) ::: List(first)
                            } else {
                                points
                            }
                            pointsClosed.map(p => {
                                JSONList(Array(p.getLongitude, p.getLatitude))
                            })
                        }
                    )
                })
            )
        )
    }

    /**
      * Simple polygon without hole
      * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.6">Polygon</a>
      *
      * @param points    ring
      * @param autoClose is auto close the polygon
      * @return
      */
    def createRingPolygon(points: Traversable[IGeoPoint], autoClose: Boolean = true): JSONObject = createPolygon(List(points), autoClose)

    /**
      * Create GeometryCollection GsonObject
      * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.8">GeometryCollection</a>
      *
      * @param objects
      * @return
      */
    def createGeometryCollection(objects: TraversableOnce[JSONObject]): JSONObject = {
        JSONMap(
            "type" -> "GeometryCollection",
            "geometries" -> JSONList(objects)
        )
    }


    /**
      * Generate object from json string "{\"type\":\"Point\","coordinates":[100,20]}"
      *
      * @param json
      * @return
      */
    def parseGeoJSON(json: String): GeoJSONable = {
        val mapDecode = gson.fromJson(json, classOf[java.util.HashMap[String, Object]])
        if (!mapDecode.containsKey("type")) {
            throw new RuntimeException("Not GeoJSON object.")
        }
        mapDecode.get("type") match {
            case "Point" =>
                gson.fromJson(json, classOf[GeoJsonPoint]).getPoint
            case "Polygon" =>
                gson.fromJson(json, classOf[GeoJsonPolygon]).getPolygon
            case _ =>
                throw new RuntimeException("Unimplemented/unsupport type.")
        }
    }

}
