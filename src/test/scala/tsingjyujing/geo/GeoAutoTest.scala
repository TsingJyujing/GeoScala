package tsingjyujing.geo

import java.io.{File, PrintWriter}

import org.scalatest._
import tsingjyujing.geo.basic.{IGeoPoint, IVector2}
import tsingjyujing.geo.basic.IHashableGeoBlock.{createCodeFromGps, revertFromCode}
import tsingjyujing.geo.element.{GeoHeatMapCommon, GeoPointTree}
import tsingjyujing.geo.element.immutable.{GeoPoint, HashedGeoBlock, Vector2}
import tsingjyujing.geo.util.convertor.{BD09, GCJ02}

import scala.util.Random

class GeoAutoTest extends FlatSpec with Matchers {
    val random = new Random(System.currentTimeMillis())

    def rand(ratio: Double = 1.0): Double = random.nextDouble() * ratio

    "Elements" should "GeoHash" in {
        val block = HashedGeoBlock(120,30,0x10000)
        assert(block.inradius<block.circumradius)

        val accuracy = 0x4000000L
        1 to 100 foreach (_ => {
            val pointX = GeoPoint(121.0 + rand(20), 25.6 + rand(20))
            val code = createCodeFromGps(pointX, accuracy)
            val revertPoint = revertFromCode(code, accuracy)
            val maxDistance = 2 * math.Pi / accuracy * IGeoPoint.EARTH_RADIUS * math.sqrt(2)
            assert((pointX geoTo revertPoint) < maxDistance)
        })


    }

    it should "GeoHeatMapCommon" in {
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
        assert(geoHeatMap.size == 3, "GeoMap size not correct")
    }

    it must "GeoPoint" in {
        val point0 = GeoPoint(121, 30)
        val point1 = GeoPoint(120, 31)
        val d = point0 geoTo point1
        assert(d > 140 && d < 150, "Distance not correct")
    }

    it should "GeoPoint TreeSet" in {
        val points = new GeoPointTree[GeoPoint]()
        // Generate test data set
        val centers = IndexedSeq(
            GeoPoint(121, 31),
            GeoPoint(108, 36)
        )
        val radius: Double = 4.0

        (1 to 10000).foreach(_ => {
            centers.foreach(center => {
                val newPoint = GeoPoint(center.getLongitude + rand(0.5), center.getLatitude + rand(0.5))
                points.appendPoint(newPoint)
            })
        })

        centers.foreach(center => {
            val withInPoints = points.geoWithin(center, 0, radius)
            val count1 = withInPoints.size
            val count2 = points.count(point => point.geoTo(center) <= radius)
            withInPoints.foreach(p => {
                assert(p.geoTo(center) <= radius, "Invalid point")
            })
            assert(count1 == count2, "Assert failed while test geo within")
            if (count2 == 0) {
                println("Warning: no point in radius")
            }
        })

        centers.foreach(center => {
            val count1 = points.geoNear(center, radius * 2).get.geoTo(center)
            val count2 = points.map(_.geoTo(center)).min
            assert(count1 == count2, "Assert failed while test geo within")
        })
    }

    "China encrypt convertor" should "BD09" in {
        val writer = new PrintWriter(new File("bd09_error.csv"))
        val eps = 0.001
        val T = BD09
        70.0D.to(150, 0.1).foreach(lng => {
            15.0D.to(55, 0.1).foreach(lat => {
                val rawPoint = GeoPoint(lng, lat)
                val transformedPoint = T.inverseTransform(T.transform(rawPoint), eps = eps)
                val err = rawPoint geoTo transformedPoint
                assert(err <= eps, "Error too large")
                if (err > eps) {
                    val writeString = "%3.6f,%3.6f,%f\n".format(lng, lat, err)
                    writer.write(writeString)
                    println(writeString)
                }
            })
        })
        writer.close()
    }

    it should "GCJ02" in {
        val writer = new PrintWriter(new File("gcj02_error.csv"))
        val eps = 0.001
        val T = GCJ02
        70.0D.to(150, 0.1).foreach(lng => {
            15.0D.to(55, 0.1).foreach(lat => {
                val rawPoint = GeoPoint(lng, lat)
                val transformedPoint = T.inverseTransform(T.transform(rawPoint), eps = eps)
                val err = rawPoint geoTo transformedPoint
                assert(err <= eps, "Error too large")
                if (err > eps) {
                    val writeString = "%3.6f,%3.6f,%f\n".format(lng, lat, err)
                    writer.write(writeString)
                    println(writeString)
                }
            })
        })
        writer.close()
    }
}
