package tsingjyujing.geo.basic

import org.scalatest._
import tsingjyujing.geo.element.GeoHeatMapCommon
import tsingjyujing.geo.element.immutable.{GeoPoint, Vector2}

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

        println("————————————")

        val newMap = geoHeatMap + geoHeatMap
        newMap.foreach(
            println
        )
    }

}
