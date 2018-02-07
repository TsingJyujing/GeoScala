package com.github.tsingjyujing.geo.algorithm.filter

import com.github.tsingjyujing.geo.basic.IGeoPoint

/**
  *
  * To extends this trait to implement a filter
  *
  * TO IMPLEMENT:
  *
  * Kalman Filter
  * Partical Filter
  *
  */
trait CommonFilter {
    def filter(points:TraversableOnce[IGeoPoint]):TraversableOnce[IGeoPoint]
}
