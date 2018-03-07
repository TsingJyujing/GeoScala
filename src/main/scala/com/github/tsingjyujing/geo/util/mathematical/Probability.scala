package com.github.tsingjyujing.geo.util.mathematical

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.immutable.GeoPoint

import scala.util.Random

/**
  * Probability utility
  * @author tsingjyujing@163.com
  */
object Probability {
    val random = new Random(
        System.currentTimeMillis()
    )

    /**
      * Return uniformed value, x~U(mean-range/2,mean+range/2)
      *
      * @param mean
      * @param range
      * @return
      */
    def uniform(mean: Double = 0.5, range: Double = 1.0): Double = (random.nextDouble() - 0.5) * range + mean

    /**
      * Common x~U(a,b)
      *
      * @param start
      * @param end
      * @return
      */
    def U(start: Double = 0, end: Double = 1): Double = uniform((start + end) / 2.0, end - start)

    /**
      * Box-Muller algorithm to get X~N(mean,var)
      *
      * @param mean
      * @param std
      * @return
      */
    def gaussian(mean: Double = 0.0, std: Double = 1.0): Double = math.sqrt(-2 * math.log(uniform())) * math.cos(2 * math.Pi * uniform()) * std + mean

    /**
      * Get point distribute on 2d-sphere uniformly
      *
      * @return
      */
    def sphereUniform: IGeoPoint = GeoPoint(U(-180, 180), {
        val randVal = U(0, 1)
        if (randVal >= 0) {
            math.acos(math.abs(randVal))
        } else {
            -math.acos(math.abs(randVal))
        }
    }.toDegrees)

}
