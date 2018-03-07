package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.algorithm.containers.ClusterResult
import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.GeoUtil
import com.github.tsingjyujing.geo.util.mathematical.Probability


/**
  * Geo-K-Means++ will coming soon
  *
  * @author tsingjyujing@163.com
  * @version 1.0
  * @since 2.7
  */
object GeoKMeans {
    def apply[V <: IGeoPoint](
                                 points: Iterable[V],
                                 k: Int,
                                 maxStepCount: Int = 100
                             ): ClusterResult[Int, V] = {
        val centerPoints = (1 to k).map(_ => {
            Probability.sphereUniform
        })
        val lossValue = Double.MaxValue

        (0 until maxStepCount).foreach(_ => {
            points.groupBy(
                point => {
                    centerPoints.zipWithIndex.minBy(_._1.geoTo(point))._2
                }
            ).foreach(kvs => {
                //centerPoints(kvs._1) = GeoUtil.mean(kvs._2)
            })
        })
        throw new Exception("Unimpl")
    }
}
