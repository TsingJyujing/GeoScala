package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.algorithm.containers.{ClusterResult, LabeledPoint}
import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.GeoPointTree

import scala.collection.mutable
import scala.sys.process.stdout

/**
  * DB Scan algorithm on geo points
  *
  */
class DBScan[V <: IGeoPoint](
                                val searchRadius: Double = 0.5,
                                val isMergeClass: Boolean = false
                            ) {

    private val data = new GeoPointTree[LabeledPoint[Int, V]]

    private val keySet: mutable.HashSet[Int] = mutable.HashSet[Int]()

    private def getNewClassId: Int = if (keySet.isEmpty) {
        0
    } else {
        keySet.max + 1
    }

    /**
      * Add a point to clusters
      *
      * @param point
      * @return
      */
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
        val searchResult = data.geoWithinRing(point, -1.0, searchRadius)
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
        val printMargin = math.max(math.min(300, math.floor(pointCount / 100.0)), 10)
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