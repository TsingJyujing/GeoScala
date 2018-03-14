package com.github.tsingjyujing.geo.model

import java.util.{ArrayList => JavaList}

import com.github.tsingjyujing.geo.element.GeoPolygon
import com.github.tsingjyujing.geo.element.immutable.GeoPoint

import scala.collection.JavaConverters._

/**
  * Entity class to convert GeoJSON(Polygon) to GeoObject
  */
class GeoJsonPolygon {
    var `type`: String = null
    var coordinates: JavaList[JavaList[JavaList[Double]]] = null

    def verify: Boolean = `type` == "Polygon"

    def getPolygon: GeoPolygon = {
        assert(verify, "type mismatch")
        assert(coordinates != null, "")
        assert(coordinates.size() <= 1, "polygon area is greater than 1 (ring polygon will support in feature release)")
        coordinates.asScala.foreach(a => {
            a.asScala.foreach(
                coordinatesList => {
                    assert(coordinatesList.size() == 2, "coordinates size is not 2")
                }
            )
        })
        GeoPolygon(
            coordinates.get(0).asScala.map(
                x => GeoPoint(x.get(0), x.get(1))
            )
        )
    }
}
