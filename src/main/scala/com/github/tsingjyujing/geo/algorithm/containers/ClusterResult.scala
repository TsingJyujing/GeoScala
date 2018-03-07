package com.github.tsingjyujing.geo.algorithm.containers

import com.github.tsingjyujing.geo.basic.IGeoPoint

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Data structure to describe
  *
  * @author tsingjyujing@163.com
  * @tparam K Type of label
  * @tparam V Type of point
  */
class ClusterResult[K, V <: IGeoPoint] {

    /**
      * Data container of result
      */
    val data: mutable.Map[K, mutable.ArrayBuffer[V]] = mutable.Map[K, mutable.ArrayBuffer[V]]()

    /**
      * Add a point to given class
      *
      * @param classId label id
      * @param point   point value
      */
    def append(classId: K, point: V): Unit = {
        if (!(data contains classId)) {
            data(classId) = mutable.ArrayBuffer[V]()
        }
        data(classId).append(point)
    }


    /**
      * Get all point in given class
      *
      * @param classId label id
      * @return
      */
    def apply(classId: K): mutable.ArrayBuffer[V] = if (data contains classId) {
        data(classId)
    } else {
        mutable.ArrayBuffer[V]()
    }

    /**
      * Get points iterable
      *
      * @return
      */
    def toIterable: Iterable[LabeledPoint[K, V]] = data.flatMap(
        kv => {
            kv._2.map(LabeledPoint(kv._1, _))
        }
    )

    def resultMap: mutable.Map[K, ArrayBuffer[V]] = data

    def classes: collection.Set[K] = data.keySet

}


object ClusterResult {
    /**
      * Create result set from labeled points
      *
      * @param data labeled points
      * @tparam K Type of label
      * @tparam V Type of point
      * @return
      */
    def apply[K, V <: IGeoPoint](data: Iterable[LabeledPoint[K, V]]): ClusterResult[K, V] = {
        val cluster = new ClusterResult[K, V]()
        data.groupBy(_.classId).foreach(kv => {
            cluster.data.put(kv._1, mutable.ArrayBuffer[V](kv._2.map(_.value).toSeq: _*))
        })
        cluster
    }
}
