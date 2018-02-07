package com.github.tsingjyujing.geo.algorithm.filter

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.GeoUtil


class MeanFilter(val meanStepSize: Int) extends CommonFilter {
    assert(meanStepSize > 0, "Invalid parameter")
    override def filter(points: TraversableOnce[IGeoPoint]): TraversableOnce[IGeoPoint] = points.toIterable.sliding(meanStepSize).map(GeoUtil.mean)
}
