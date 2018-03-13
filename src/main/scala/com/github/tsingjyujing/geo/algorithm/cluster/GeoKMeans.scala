package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.mathematical.Probability

/**
  * Standard Geo K-Means with uniformed initialize on sphere
  *
  * @tparam T type of point
  */
class GeoKMeans[T <: IGeoPoint] extends BaseGeoKMeans[T] {
    override def initializePoints(points: Iterable[T], k: Int): Iterable[IGeoPoint] = (1 to k).map(_ => {
        Probability.sphereUniform
    })
}
