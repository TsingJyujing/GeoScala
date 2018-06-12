package com.github.tsingjyujing.geo

import java.io.{File, PrintWriter}

import com.github.tsingjyujing.geo.basic.IHashableGeoBlock.{createCodeFromGps, revertGpsFromCode}
import com.github.tsingjyujing.geo.basic.{IGeoPoint, IVector2}
import com.github.tsingjyujing.geo.element.immutable.{GeoLineString, GeoPoint, HashedGeoBlock, Vector2}
import com.github.tsingjyujing.geo.element.{GeoHeatMap, GeoPointTree}
import com.github.tsingjyujing.geo.util.GeoUtil
import com.github.tsingjyujing.geo.util.convertor.{BD09, GCJ02}
import com.github.tsingjyujing.geo.util.mathematical.Probability.{gaussian => randn, uniform => rand}
import org.scalatest._

class GeoAutoTest extends FlatSpec with Matchers {


    "Elements" should "GeoHash" in {
        val block = HashedGeoBlock(120, 30, 0x10000)
        assert(block.inradius < block.circumradius)

        val accuracy = 0x4000000L
        1 to 100 foreach (_ => {
            val pointX = GeoPoint(121.0 + rand(20), 25.6 + rand(20))
            val code = createCodeFromGps(pointX, accuracy)
            val revertPoint = revertGpsFromCode(code, accuracy)
            val maxDistance = 2 * math.Pi / accuracy * IGeoPoint.EARTH_RADIUS * math.sqrt(2)
            assert((pointX geoTo revertPoint) < maxDistance)
        })
    }

    /**
      * Monte Carlo method to test correction of HashedBlock
      */
    it should "Monte Carlo GeoHashBlock Test" in {
        val initPoint = GeoPoint(120, 30)
        val geoBlock = HashedGeoBlock(initPoint, 0x1000)
        val geoBox = geoBlock.toGeoBox
        val centerPoint = geoBlock.getCenterPoint
        val points = (1 to 100000).map(_ => {
            GeoPoint(centerPoint.getLongitude + randn(0, rand(0, 0.05)), centerPoint.getLatitude + rand(0, 0.05))
        })

        val inRadius = geoBlock.inradius
        val outRadius = geoBlock.circumradius

        val minDistance = points.filter(point => {
            geoBox nonContains point
        }).map(_.geoTo(centerPoint)).min

        val maxDistance = points.filter(point => {
            geoBox contains point
        }).map(_.geoTo(centerPoint)).max

        val minError = math.abs(minDistance - inRadius) / minDistance
        val maxError = math.abs(maxDistance - outRadius) / maxDistance

        assert(maxError < 0.05, "Max distance error too large")
        assert(minError < 0.06, "Min distance error too large")


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

        val geoHeatMap = GeoHeatMap.buildFromPoints[IVector2](points, Vector2(0, 0), 0x20000)
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

        // 随机生成10000个点
        (1 to 10000).foreach(_ => {
            centers.foreach(center => {
                val newPoint = GeoPoint(center.getLongitude + rand(0, 0.5), center.getLatitude + rand(0, 0.5))
                points.appendPoint(newPoint)
            })
        })

        /**
          *
          */
        centers.foreach(center => {
            val withInPoints = points.geoWithinRing(center, 0, radius)
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

    it should "Convert position between vector and GeoPoint" in {
        (-180).to(180, 20).foreach(lng => {
            (-90).to(90, 10).foreach(lat => {
                val point = GeoPoint(lng, lat)
                val ipoint = GeoUtil.vector3ToGeoPoint(point.toIVector3)
                assert(point.geoTo(ipoint) <= 0.1, "Convert failed.")
            })
        })
    }

    it should "Circle object works normally" in {

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

    it should "GCJ02 ensure" in {
        val pointsPairs = IndexedSeq(
            (GeoPoint(116.481499, 39.990475), GeoPoint(116.487585177952, 39.991754014757)),
            (GeoPoint(116.481499, 39.990375), GeoPoint(116.487585177952, 39.991653917101))
        )
        val T = GCJ02
        pointsPairs.foreach(
            ps => {
                assert(T.transform(ps._1).geoTo(ps._2) < 0.01)
            }
        )
    }

    it should "BD09 ensure" in {
        val pointsPairs = IndexedSeq(
            (GeoPoint(114.21892734521, 29.575429778924), GeoPoint(114.2307519546763, 29.57908428837437))
        )
        val T = BD09
        pointsPairs.foreach(
            ps => {
                assert(T.transform(ps._1).geoTo(ps._2) < 0.01)
            }
        )
    }

    "Geo line string" should "created normally" in {
        val line = GeoLineString(IndexedSeq(
            GeoPoint(121.346323, 31.220872), GeoPoint(121.346572, 31.220858), GeoPoint(121.347075, 31.220817), GeoPoint(121.347076, 31.220817), GeoPoint(121.347403, 31.220789), GeoPoint(121.347504, 31.220784), GeoPoint(121.347566, 31.220784), GeoPoint(121.347611, 31.220786), GeoPoint(121.347645, 31.220793), GeoPoint(121.347683, 31.220805), GeoPoint(121.347717, 31.22082), GeoPoint(121.347754, 31.22084), GeoPoint(121.347782, 31.220867), GeoPoint(121.347812, 31.220901), GeoPoint(121.347829, 31.220926), GeoPoint(121.347846, 31.220957), GeoPoint(121.347862, 31.22098), GeoPoint(121.34788, 31.221002), GeoPoint(121.3479, 31.221028), GeoPoint(121.347925, 31.221051), GeoPoint(121.347935, 31.221059), GeoPoint(121.347946, 31.221066), GeoPoint(121.347962, 31.221075), GeoPoint(121.34797, 31.221078), GeoPoint(121.347981, 31.221082), GeoPoint(121.348002, 31.221087), GeoPoint(121.348018, 31.221091), GeoPoint(121.348032, 31.221092), GeoPoint(121.348049, 31.221095), GeoPoint(121.348069, 31.221094), GeoPoint(121.348093, 31.221091), GeoPoint(121.348133, 31.221084), GeoPoint(121.348151, 31.221078), GeoPoint(121.348167, 31.221073), GeoPoint(121.348178, 31.221066), GeoPoint(121.3482, 31.221049), GeoPoint(121.348222, 31.22103), GeoPoint(121.348239, 31.221011), GeoPoint(121.348251, 31.220994), GeoPoint(121.34826, 31.22098), GeoPoint(121.348267, 31.220963), GeoPoint(121.348271, 31.220949), GeoPoint(121.348273, 31.220932), GeoPoint(121.348274, 31.220916), GeoPoint(121.348274, 31.220902), GeoPoint(121.348292, 31.220608)
        ))
        println(line)
    }
}
