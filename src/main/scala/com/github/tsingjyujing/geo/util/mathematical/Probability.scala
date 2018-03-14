package com.github.tsingjyujing.geo.util.mathematical

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.immutable.GeoPoint

import scala.util.Random

/**
  * Probability utility
  *
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

    /**
      *
      * Select a data randomly from a set
      *
      * @param data samples to select
      * @tparam T type of sample
      * @return
      */
    def selectUniformly[T](data: IndexedSeq[T]): T = data(random.nextInt(data.size))

    /**
      *
      * @param data              samples to select
      * @param probabilityWeight probability weight of the samples
      * @tparam T type of sample
      * @return
      */
    def selectByProbability[T](data: Iterable[T], probabilityWeight: Iterable[Double]): T = {

        assert(data.size == probabilityWeight.size)

        val sumProb: Double = probabilityWeight.sum
        var currentProb: Double = 1.0

        val selectedData: Option[(T, Double)] = data.zip(probabilityWeight).find(x => {
            val p = x._2 / sumProb
            val m = p / currentProb
            val isSelect = random.nextDouble() <= (p / m)
            currentProb -= p
            isSelect
        })

        if (selectedData.isDefined) {
            selectedData.get._1
        } else {
            data.last
        }

    }


}
