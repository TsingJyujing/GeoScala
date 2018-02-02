package tsingjyujing.geo.algorithm.cluster

import tsingjyujing.geo.algorithm.containers.{ClusterResult, LabeledPoint}
import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.element.GeoPointTree
import sys.process.stdout
import scala.collection.mutable

/**
  *
  */
class DBScan[V <: IGeoPoint](
                                val searchRadius: Double = 0.5,
                                val isMergeClass: Boolean = false
                            ) {
    val data = new GeoPointTree[LabeledPoint[Int, V]]
    val keySet: mutable.HashSet[Int] = mutable.HashSet[Int]()

    def getNewClassId: Int = if (keySet.isEmpty) {
        0
    } else {
        keySet.max + 1
    }

    def append(point: V): Int = if (isMergeClass) {
        appendWithMerge(point)
    } else {
        appendWithoutMerge(point)
    }

    private def appendWithoutMerge(point: V): Int = {
        val searchResult = data.geoNear(point, searchRadius)
        if (searchResult.isDefined) {
            val classId = searchResult.get.classId
            data.appendPoint(LabeledPoint(classId, point))
            classId
        } else {
            val classId = getNewClassId
            data.appendPoint(LabeledPoint(classId, point))
            keySet.add(classId)
            classId
        }
    }

    private def appendWithMerge(point: V): Int = {
        val searchResult = data.geoWithin(point, -1.0, searchRadius)
        if (searchResult.isEmpty) {
            val classId = getNewClassId
            data.appendPoint(LabeledPoint(classId, point))
            keySet.add(classId)
            classId
        } else {
            val uniqueClassIds = searchResult.map(_.classId).toSet
            val classId = uniqueClassIds.head
            data.appendPoint(LabeledPoint(classId, point))
            if (uniqueClassIds.size > 1) {
                val keySetCopy = keySet.toIndexedSeq
                keySetCopy.foreach(
                    cls => {
                        if (cls != classId && uniqueClassIds.contains(cls)) {
                            keySet.remove(cls)
                        }
                    }
                )
                data.foreach(
                    point => {
                        val cls = point.classId
                        if (cls != classId && uniqueClassIds.contains(cls)) {
                            point.classId = classId
                        }
                    }
                )

            }
            classId
        }
    }

    def toClusterResult: ClusterResult[Int, V] = ClusterResult(data)
}

object DBScan {
    def apply[V <: IGeoPoint](
                                 points: Iterable[V],
                                 searchRadius: Double = 0.5,
                                 isMergeClass: Boolean = false
                             ): ClusterResult[Int, V] = {
        val startTime = System.currentTimeMillis()
        val cr = new DBScan[V](searchRadius, isMergeClass)
        val pointCount = points.size
        val printMargin = math.floor(pointCount / 100.0)
        points.zipWithIndex.foreach(pid => {
            cr.append(pid._1)
            if (pid._2 % printMargin == 0) {
                val pastTime = System.currentTimeMillis() - startTime
                val speed = pid._2 * 1.0 / pastTime
                stdout.print("\rClustering:%3.0f%%  %10.3f kpps".format(pid._2 * 100.0 / pointCount, speed))
                stdout.flush()
            }
        })
        println("\nDone")
        cr.toClusterResult
    }

}