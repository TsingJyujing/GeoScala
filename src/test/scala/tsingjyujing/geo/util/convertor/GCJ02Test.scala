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

        val testedTransformer = BD09
        geoPoints.foreach(
            point=>{
                val gcjPoint = testedTransformer.transform(point)
                val wgsPointFast = testedTransformer.inverseTransformFast(gcjPoint)
                val wgsPoint = testedTransformer.inverseTransform(gcjPoint)
                println(point)
                println(gcjPoint geoTo point)
                println(wgsPointFast geoTo point)
                println(wgsPoint geoTo point)
            }
        )
    }
}
