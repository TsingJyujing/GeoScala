package com.github.tsingjyujing.geo.element

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.{GeoJSONable, IContains}

import scala.util.parsing.json.JSONObject

/**
  * Standard GeoJSON completely Polygon which described in :
  * <a href="https://tools.ietf.org/html/rfc7946#section-3.1.6">Polygon</a>
  *
  * @author Tsing Jyujing
  */
case class GeoPolygonWithHoles(polygonInfo: GeoPolygon, holes: Iterable[GeoPolygon]) extends IContains[IGeoPoint] with GeoJSONable {

    /**
      * Is x contains in self
      *
      * @param x
      * @return
      */
    override def contains(x: IGeoPoint): Boolean = {
        if (!polygonInfo.contains(x)) {
            false
        } else {
            holes.forall(!_.contains(x))
        }
    }

    /**
      * Get scala original JSON object,
      * JSON object deprecated in Scala 2.12 but still using in 2.10
      *
      * @return
      */
    override def toGeoJSON: JSONObject = GeoJSONable.createPolygon(
        holes.toList.::(polygonInfo)
    )
}

object GeoPolygonWithHoles {
    /**
      * Create standard points with
      *
      * @param polygon
      * @param holes
      * @return
      */
    def apply(polygon: Iterable[IGeoPoint], holes: Iterable[Iterable[IGeoPoint]]): GeoPolygonWithHoles = new GeoPolygonWithHoles(
        GeoPolygon(polygon),
        holes.map(GeoPolygon.apply)
    )

    /**
      * Initialize by default GeoJSON described coordinates
      *
      * @param polygon coordinates data
      * @return
      */
    def apply(polygon: Iterable[Iterable[IGeoPoint]]): GeoPolygonWithHoles = new GeoPolygonWithHoles(
        GeoPolygon(polygon.head),
        polygon.tail.map(GeoPolygon.apply)
    )
}