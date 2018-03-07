package com.github.tsingjyujing.geo.algorithm.filter

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.GeoUtil

/**
  * Mean value filter
  *
  * @param meanStepSize moving window size
  * @author tsingjyujing@163.com
  * @version 1.0
  * @since 2.5
  */
class MeanFilter(val meanStepSize: Int) extends CommonFilter {
    assert(meanStepSize > 0, "Invalid parameter")

    override def filter(points: TraversableOnce[IGeoPoint]): TraversableOnce[IGeoPoint] = points.toIterable.sliding(meanStepSize).map(GeoUtil.mean)
}
