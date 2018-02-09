package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.algorithm.containers.ClusterResult
import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.mutable.GeoPoint
import com.github.tsingjyujing.geo.util.mathematical.Probability

import scala.collection.parallel.immutable.ParSeq

object GeoKMeans {
    def apply[V <: IGeoPoint](
                                 points: Iterable[V],
                                 k: Int,
                                 maxIter: Int = 100
                             ): ClusterResult[Int, V] = {
        val centerPoints = (1 to k).map(_ => {
            Probability.sphereUniform
        }).toParArray
        throw new Exception("Unimplemented method.")
    }
}
