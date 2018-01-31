package tsingjyujing.geo.algorithm.cluster

import tsingjyujing.geo.algorithm.GeoCluster
import tsingjyujing.geo.algorithm.containers.{ClusterResult, LabeledPoint}
import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.element.GeoPointTree

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

    def append(point: V): Int = if (isMergeClass) {
        val searchResult = data.geoWithin(point, searchRadius)
        if (searchResult.isEmpty) {
            val classId = keySet.max + 1
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
                data.foreach(point => {
                    val cls = point.classId
                    if (cls != classId && uniqueClassIds.contains(cls)) {
                        point.classId = classId
                    }
                })

            }
            classId
        }
    } else {
        val searchResult = data.geoNear(point, searchRadius)
        if (searchResult.isDefined) {
            val classId = searchResult.get.classId
            data.appendPoint(LabeledPoint(classId, point))
            classId
        } else {
            val classId = keySet.max + 1
            data.appendPoint(LabeledPoint(classId, point))
            keySet.add(classId)
            classId
        }
    }

    implicit def toClusterResult: ClusterResult[Int, V] = ClusterResult(data)
}

object DBScan {
    def apply[V <: IGeoPoint](
                                 points: TraversableOnce[V],
                                 searchRadius: Double = 0.5,
                                 isMergeClass: Boolean = false
                             ): ClusterResult[Int, V] = {
        val c = new DBScan[V](searchRadius, isMergeClass)
        points.foreach(point => {
            c.append(point)
        })
        c.toClusterResult
    }

}