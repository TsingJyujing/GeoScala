package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.mathematical.Probability

class GeoKMeans[T] extends BaseGeoKMeans[T] {
    override def initializePoints(points: Iterable[T], k: Int): Iterable[IGeoPoint] = (1 to k).map(_ => {
        Probability.sphereUniform
    })
}
