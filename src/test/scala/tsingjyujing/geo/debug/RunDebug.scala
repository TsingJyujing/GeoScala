package tsingjyujing.geo.debug

import java.io.{File, PrintWriter}

import tsingjyujing.geo.algorithm.cluster.DBScan
import tsingjyujing.geo.basic.operations.GeoJSONable
import tsingjyujing.geo.element.GeoPointTree
import tsingjyujing.geo.element.immutable.GeoPoint

import scala.util.Random

object RunDebug {
    val random = new Random(System.currentTimeMillis())

    def rand(ratio: Double = 1.0): Double = random.nextDouble() * ratio

    def randn(mean: Double = 0.0, std: Double = 1.0): Double = math.sqrt(-2 * math.log(rand())) * math.cos(2 * math.Pi * rand()) * std + mean


    def main(args: Array[String]): Unit = GeoPointTreeDebug()

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
                val newPoint = GeoPoint(center.getLongitude + randn(0.5), center.getLatitude + randn(0.5))
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
        println("Start to cluster")
        val result = DBScan(points = points, searchRadius = 10.0, isMergeClass = true)
        println("Done, %d class found, writing...".format(result.classes.size))
        val writer = new PrintWriter(new File("dbscan_result.json"))
        writer.write(GeoJSONable.createGeometryCollection(result.resultMap.map(
            lp => {
                GeoJSONable.createMultiPoints(lp._2)
            }
        )).toString())
        writer.close()

        val writerCSV = new PrintWriter(new File("dbscan_result.csv"))
        result.resultMap.flatMap(kv => {
            kv._2.map(p => {
                "%d,%3.6f,%3.6f\n".format(kv._1, p.getLongitude, p.getLatitude)
            })
        }).foreach(
            writerCSV.write
        )
        writerCSV.close()
    }
}
