package tsingjyujing.geo.util.convertor


import org.scalatest._
import tsingjyujing.geo.element.mutable.GeoPoint

class GCJ02Test extends FlatSpec with Matchers {
    "GCJ02" should "Test" in {
        val geoPoints = IndexedSeq(
            new GeoPoint(120,30),
            new GeoPoint(108,35),
            new GeoPoint(105,25)
        )

        geoPoints.foreach(
            point=>{
                val gcjPoint = GCJ02.transform(point)
                val wgsPointFast = GCJ02.inverseTransformFast(gcjPoint)
                val wgsPoint = GCJ02.inverseTransform(gcjPoint)
                println(point)
                println(gcjPoint geoTo point)
                println(wgsPointFast geoTo point)
                println(wgsPoint geoTo point)
            }
        )
    }
}
