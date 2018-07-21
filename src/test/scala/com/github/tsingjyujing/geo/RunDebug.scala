package com.github.tsingjyujing.geo

import com.github.tsingjyujing.geo.algorithm.cluster.DBScan
import com.github.tsingjyujing.geo.algorithm.containers.LabeledPoint
import com.github.tsingjyujing.geo.basic.{IGeoPoint, IVector2}
import com.github.tsingjyujing.geo.element.immutable.{GeoLineString, GeoPoint, TimeElement, Vector2}
import com.github.tsingjyujing.geo.element.{GeoCircleArea, GeoPointTimeSeries, GeoPointTree, GeoPolygon}
import com.github.tsingjyujing.geo.util.mathematical.ConvexHull2
import com.github.tsingjyujing.geo.util.mathematical.Probability.{gaussian => randn, uniform => rand}
import com.github.tsingjyujing.geo.util.{FileIO, GeoUtil}
import com.google.gson.Gson
import org.bson.Document

import scala.collection.JavaConverters._
import scala.io.Source

object RunDebug {

    def main(args: Array[String]): Unit = GeoCompressTest()

    /**
      * 折线匹配
      */
    def GeoLineStringMatch: Unit = {
        val points = IndexedSeq(
            GeoPoint(121.346323, 31.220872), GeoPoint(121.346572, 31.220858), GeoPoint(121.347075, 31.220817),
            GeoPoint(121.347076, 31.220817), GeoPoint(121.347403, 31.220789), GeoPoint(121.347504, 31.220784),
            GeoPoint(121.347566, 31.220784), GeoPoint(121.347611, 31.220786), GeoPoint(121.347645, 31.220793),
            GeoPoint(121.347683, 31.220805), GeoPoint(121.347717, 31.22082), GeoPoint(121.347754, 31.22084),
            GeoPoint(121.347782, 31.220867), GeoPoint(121.347812, 31.220901), GeoPoint(121.347829, 31.220926),
            GeoPoint(121.347846, 31.220957), GeoPoint(121.347862, 31.22098), GeoPoint(121.34788, 31.221002),
            GeoPoint(121.3479, 31.221028), GeoPoint(121.347925, 31.221051), GeoPoint(121.347935, 31.221059),
            GeoPoint(121.347946, 31.221066), GeoPoint(121.347962, 31.221075), GeoPoint(121.34797, 31.221078),
            GeoPoint(121.347981, 31.221082), GeoPoint(121.348002, 31.221087), GeoPoint(121.348018, 31.221091),
            GeoPoint(121.348032, 31.221092), GeoPoint(121.348049, 31.221095), GeoPoint(121.348069, 31.221094),
            GeoPoint(121.348093, 31.221091), GeoPoint(121.348133, 31.221084), GeoPoint(121.348151, 31.221078),
            GeoPoint(121.348167, 31.221073), GeoPoint(121.348178, 31.221066), GeoPoint(121.3482, 31.221049),
            GeoPoint(121.348222, 31.22103), GeoPoint(121.348239, 31.221011), GeoPoint(121.348251, 31.220994),
            GeoPoint(121.34826, 31.22098), GeoPoint(121.348267, 31.220963), GeoPoint(121.348271, 31.220949),
            GeoPoint(121.348273, 31.220932), GeoPoint(121.348274, 31.220916), GeoPoint(121.348274, 31.220902),
            GeoPoint(121.348292, 31.220608)
        )
        val line = GeoLineString(points)

        val pointsResample = IndexedSeq(
            GeoPoint(121.346323, 31.220872),
            GeoPoint(121.347076, 31.220817),
            GeoPoint(121.347566, 31.220784),
            GeoPoint(121.347683, 31.220805),
            GeoPoint(121.347782, 31.220867),
            GeoPoint(121.347846, 31.220957),
            GeoPoint(121.3479, 31.221028),
            GeoPoint(121.347946, 31.221066),
            GeoPoint(121.347981, 31.221082),
            GeoPoint(121.348032, 31.221092),
            GeoPoint(121.348093, 31.221091),
            GeoPoint(121.348167, 31.221073),
            GeoPoint(121.348222, 31.22103),
            GeoPoint(121.34826, 31.22098),
            GeoPoint(121.348273, 31.220932),
            GeoPoint(121.348292, 31.220608)
        )

        var sumDistance = 0.0
        var sumMileage = 0.0
        pointsResample.sliding(2).foreach(
            ps => {
                val p = (ps.head.toIVector2 + ps.last.toIVector2) / 2 + IVector2(0.0001, 0.0001)
                val d = ps.head.geoTo(ps.last)
                val newPoint = IGeoPoint(p.getX, p.getY)
                val fetchedResult = line.fetchLineString(newPoint)
                sumMileage += d
                sumDistance += fetchedResult.distance * d
                println(fetchedResult)
            }
        )
        println(s"Frechet result (average) = ${sumDistance / sumMileage}")

    }

    /**
      * GPS路线数据压缩算法
      */
    def GeoCompressTest(): Unit = {
        val data: Iterable[TimeElement[GeoPoint]] = Document.parse(
            Source.fromFile("dzy_3.json").getLines().mkString("\n")
        ).get("data", classOf[Document]).get("tracks").asInstanceOf[java.util.List[Document]].asScala.map(
            x => TimeElement(
                x.getString("gpsTime").toDouble,
                GeoPoint(
                    x.getString("lng").toDouble,
                    x.getString("lat").toDouble
                )
            )
        )
        GeoPointTimeSeries(data).toSparse(0.01, 1000)
        FileIO.writePoints("visualize/dzy_raw.csv", data.map(_.getValue))
        IndexedSeq(0.01, 0.1, 0.5, 1).foreach(
            compressParam => {
                FileIO.writePoints(
                    s"visualize/dzy_compress_$compressParam.csv",
                    GeoPointTimeSeries(data).toSparse(compressParam, 1000).map(_.getValue))
            }
        )
    }

    def GeoPointTreeDebug(): Unit = {
        val points = new GeoPointTree[GeoPoint]()
        // Generate test data set
        val centers = IndexedSeq(
            GeoPoint(121, 31),
            GeoPoint(108, 36)
        )
        val radius: Double = 4.0

        (1 to 10000).foreach(_ => {
            centers.foreach(center => {
                val newPoint = GeoPoint(
                    center.getLongitude + randn(0, 0.5),
                    center.getLatitude + randn(0, 0.5)
                )
                points.appendPoint(newPoint)
            })
        })

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

    def DBScanDebug(): Unit = {
        // 测试用例：根据一些中心随机生成一些点，随后应用DBScan，将聚类后的每一类分开存储，最后放到地图上看（或者生成好几个MultiPoint对象的Collection）
        val createCenters = IndexedSeq(
            GeoPoint(120, 20),
            GeoPoint(121, 21),
            GeoPoint(122, 22),
            GeoPoint(123, 23),
            GeoPoint(124, 24)
        )


        // 每两个中心点之间大约距离为150km
        val points = (1 to 3000).flatMap(_ => {
            createCenters.map(center => {
                GeoPoint(center.getLongitude + randn(std = 0.1), center.getLatitude + randn(std = 0.1))
            })
        })

        println("Clustering started.")
        val result = DBScan(points = points, searchRadius = 10.0, isMergeClass = true)
        println("Done, %d class found, writing...".format(result.classes.size))

        FileIO.writeLabeledPoints("dbscan_result.csv", result.toIterable)
    }

    def createPolygonSamples(): Unit = {
        val offsets = Array(
            Vector2(1, 0),
            Vector2(2, 1),
            Vector2(2, 2),
            Vector2(1, 3),
            Vector2(0, 1)
        )
        val pointO = GeoPoint(108, 36)
        val polygonInfo = new GeoPolygon(offsets.map(pointO + _))
        val randomPoins = (1 to 3000).map(_ => {
            val point = pointO + Vector2(rand(1.5, 5), rand(1.5, 5))
            val isInPolygon = if (polygonInfo.contains(point)) {
                1
            } else {
                0
            }
            LabeledPoint(isInPolygon, point)
        })
        FileIO.writePoints("polygon.csv", polygonInfo)
        FileIO.writeLabeledPoints("test_polygon.csv", randomPoins)
    }

    def visualizeFrechetResult(): Unit = {
        val route = (-math.Pi).to(math.Pi, 0.01).map(t => {
            TimeElement(t + math.Pi, GeoPoint(108, 20) + Vector2(0.3 * math.sin(0.2 * t), 0.3 * math.cos(0.3 * t)))
        })

        val fetchRoute = GeoPointTimeSeries(route)
        val pointTree = new GeoPointTree[GeoPoint]()
        pointTree.appendPoints(route.map(
            p => {
                GeoPoint(p.value.getLongitude + 0.002, p.value.getLatitude + 0.003)
            }
        ))
        val fetchValue = pointTree.geoFrechet(fetchRoute.map(_.getValue))
        val result = fetchValue.toIterable.zip(fetchRoute).map(vs => {
            LabeledPoint(vs._1, vs._2.value)
        })
        FileIO.writeLabeledPoints("fetch_result.csv", result)
        FileIO.writePoints("fetch_set.csv", pointTree)
        FileIO.writeString("pointTree.json", new Gson().toJson(pointTree, pointTree.getClass))
    }

    def testInterp(): Unit = {
        FileIO.writeLabeledPoints(
            "geo_interp_test.csv",
            GeoUtil.interp(
                TimeElement(0, GeoPoint(-160, 20)),
                TimeElement(1000, GeoPoint(160, -20)),
                30
            ).map(
                p => LabeledPoint(p.getTick, p.getValue)
            )
        )
    }

    def testConvHull(): Unit = {
        val pointO = GeoPoint(108, 36)
        val randomPoints = (1 to 3000).map(_ => {
            pointO + Vector2(rand(1.5, 5), rand(1.5, 5))
        })
        val hull = ConvexHull2(randomPoints.map(_.toIVector2))
        FileIO.writePoints("visualize/random_hull_points.csv", randomPoints)
        FileIO.writePoints("visualize/calc_hull_points.csv", hull.map(p => {
            GeoPoint(p.getX, p.getY)
        }))
        FileIO.writeString("test_polygon.json", new GeoPolygon(hull.map(p => {
            GeoPoint(p.getX, p.getY)
        })).toGeoJSONString)
    }

    def testGeoCircle(): Unit = {
        val points = GeoCircleArea(GeoPoint(108, 89), 300).toPolygon(20)
        FileIO.writePoints("visualize/circle_area.csv", points)
        FileIO.writePoints3D("visualize/circle_area_3d.csv", points)
    }
}
