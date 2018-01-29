package tsingjyujing.geo.basic

import org.scalatest._
import tsingjyujing.geo.element.immutable.GeoPoint

class GeoPolygonTest extends FlatSpec with Matchers {
    "Geo point" should "get disance" in {
        val point0 = new GeoPoint(120,30)
        val point1 = new GeoPoint(120,30)
        println("Distance of two points = %f".format(point0 geoTo point1))
    }
}
