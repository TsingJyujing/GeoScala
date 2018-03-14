package com.github.tsingjyujing.geo.algorithm.containers

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.exceptions.ParameterException
import com.github.tsingjyujing.geo.util.GeoUtil

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

    /**
      * Get indicators by string type
      * @param indicatorType indicator type
      * @return
      */
    def getIndicator(indicatorType: String): Double = indicatorType match {
        case "DB" =>
            ClusterResult.clusterIndicateDaviesBouldin(this)
        case _ =>
            throw new ParameterException("Unsupport indicator")
    }
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

    /**
      * Davies Bouldin indicator to describe the performance of cluster result
      * @param result cluster result
      * @tparam K type of key
      * @tparam V type of point
      * @return
      */
    private def clusterIndicateDaviesBouldin[K, V <: IGeoPoint](result: ClusterResult[K, V]): Double = {
        val classes: collection.Set[K] = result.classes
        val k: Int = classes.size
        assert(k > 1, "DaviesBouldin(1) is not defined")
        val centers: mutable.Map[K, IGeoPoint] = result.data.map(kv => (kv._1, GeoUtil.mean(kv._2)))
        classes.map(
            i => {
                classes.filter(_ != i).map(
                    j => {
                        val Ci = centers(i)
                        val Cj = centers(j)
                        val Cij = Ci geoTo Cj
                        val Wis = result.data(i).map(p => p.geoTo(Ci))
                        val Wjs = result.data(i).map(p => p.geoTo(Cj))
                        (Wis.sum / Wis.size + Wjs.sum / Wjs.size) / Cij
                    }
                ).max
            }
        ).sum / k
    }

}
