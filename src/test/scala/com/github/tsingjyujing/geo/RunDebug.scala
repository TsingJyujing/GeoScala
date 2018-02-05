package com.github.tsingjyujing.geo

import com.github.tsingjyujing.geo.algorithm.cluster.DBScan
import com.github.tsingjyujing.geo.algorithm.containers.LabeledPoint
import com.github.tsingjyujing.geo.element.{GeoPointTree, GeoPolygon}
import com.github.tsingjyujing.geo.element.immutable.{GeoPoint, Vector2}
import com.github.tsingjyujing.geo.util.FileIO
import com.github.tsingjyujing.geo.util.mathematical.Probability.{gaussian => randn, uniform => rand}

object RunDebug {

    def main(args: Array[String]): Unit = createPolygonSamples()

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
            val isInPolygon = if(polygonInfo.contains(point)){1}else{0}
            LabeledPoint(isInPolygon, point)
        })
        FileIO.writePoints("polygon.csv",polygonInfo)
        FileIO.writeLabeledPoints("test_polygon.csv",randomPoins)
    }
}
