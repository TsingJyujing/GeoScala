package tsingjyujing.geo.algorithm.containers

import tsingjyujing.geo.basic.IGeoPoint

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Data struct to describe
  *
  * @tparam K
  * @tparam V
  */
class ClusterResult[K, V <: IGeoPoint] {

    val data: mutable.Map[K, mutable.ArrayBuffer[V]] = mutable.Map[K, mutable.ArrayBuffer[V]]()

    def append(classId: K, point: V): Unit = {
        if (!(data contains classId)) {
            data(classId) = mutable.ArrayBuffer[V]()
        }
        data(classId).append(point)
    }


    def apply(classId: K): mutable.ArrayBuffer[V] = if (data contains classId) {
        data(classId)
    } else {
        mutable.ArrayBuffer[V]()
    }

    implicit def toIterable: Iterable[LabeledPoint[K, V]] = data.flatMap(
        kv => {
            kv._2.map(LabeledPoint(kv._1, _))
        }
    )

    def resultMap: mutable.Map[K, ArrayBuffer[V]] = data

    def classes: collection.Set[K] = data.keySet

}

object ClusterResult {
    def apply[K, V <: IGeoPoint](data: Iterable[LabeledPoint[K, V]]): ClusterResult[K, V] = {
        val cluster = new ClusterResult[K, V]()
        data.groupBy(_.classId).foreach(kv => {
            cluster.data.put(kv._1, mutable.ArrayBuffer[V](kv._2.map(_.value).toSeq: _*))
        })
        cluster
    }
}