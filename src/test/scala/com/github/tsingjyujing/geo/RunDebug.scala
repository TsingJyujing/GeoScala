package com.github.tsingjyujing.geo

import com.github.tsingjyujing.geo.algorithm.cluster.DBScan
import com.github.tsingjyujing.geo.algorithm.containers.LabeledPoint
import com.github.tsingjyujing.geo.element.{GeoPointTimeSeries, GeoPointTree, GeoPolygon}
import com.github.tsingjyujing.geo.element.immutable.{GeoPoint, TimeElement, Vector2}
import com.github.tsingjyujing.geo.util.mathematical.ConvexHull2
import com.github.tsingjyujing.geo.util.{FileIO, GeoUtil}
import com.github.tsingjyujing.geo.util.mathematical.Probability.{gaussian => randn, uniform => rand}
import com.google.gson.Gson

object RunDebug {

    def main(args: Array[String]): Unit = testConvHull()

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

}
