package com.github.tsingjyujing.geo.util.mathematical

import scala.util.Random

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
      * Box-Muller algorithm to get X~N(mean,var)
      *
      * @param mean
      * @param std
      * @return
      */
    def gaussian(mean: Double = 0.0, std: Double = 1.0): Double = math.sqrt(-2 * math.log(uniform())) * math.cos(2 * math.Pi * uniform()) * std + mean

}
