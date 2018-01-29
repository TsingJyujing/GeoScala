package tsingjyujing.geo.basic

import org.scalatest._
import tsingjyujing.geo.element.{GeoHeatMap, GeoHeatMapCommon}
import tsingjyujing.geo.element.immutable.{GeoPoint, Vector2}
import tsingjyujing.geo.element.mutable.DoubleValue

class GeoHeatMapCommonTest extends FlatSpec with Matchers {
    "GeoHeatMapCommon" should "geo heat map common" in {
        val points = IndexedSeq(
            (GeoPoint(120, 30), Vector2(1.01, 2.01)),
            (GeoPoint(120, 30), Vector2(1.01, 2.01)),
            (GeoPoint(120, 30), Vector2(1.01, 2.01)),
            (GeoPoint(120, 30), Vector2(1.01, 2.01)),
            (GeoPoint(121, 30), Vector2(1.01, 2.01)),
            (GeoPoint(120, 31), Vector2(1.01, 2.01)),
            (GeoPoint(120, 31), Vector2(1.01, 2.01))
        )

        val geoHeatMap = GeoHeatMapCommon.buildFromPoints[IVector2](points, Vector2(0, 0), 0x20000)
        geoHeatMap.foreach(
            println
        )
    }
    "GeoHeatMap" should "geo heat map common" in {
        val points = IndexedSeq(
            (GeoPoint(120, 30), 1.01),
            (GeoPoint(120, 30), 1.01),
            (GeoPoint(120, 30), 1.01),
            (GeoPoint(120, 30), 1.01),
            (GeoPoint(121, 30), 1.01),
            (GeoPoint(120, 31), 1.01),
            (GeoPoint(120, 31), 1.01)
        )

        val geoHeatMap = GeoHeatMap.buildFromPoints(points)
        geoHeatMap.foreach(println)
    }

}
