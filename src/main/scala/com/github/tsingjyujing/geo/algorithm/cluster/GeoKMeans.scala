package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.algorithm.containers.{ClusterResult, LabeledPoint}
import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.GeoUtil
import com.github.tsingjyujing.geo.util.mathematical.Probability

import scala.util.control.Breaks._

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
        // TODO Create new algorithm k-means++ to initialize points with geo-optimized k-means++ algorithm

        var centerPoints: Iterable[IGeoPoint] = (1 to k).map(_ => {
            Probability.sphereUniform
        })
        var lossValue = Double.MaxValue
        breakable(

            (0 until maxStepCount).foreach(currentStep => {
                // For each step while decreasing

                val ExpectationStep = points.map(
                    point => {
                        val electedPoint = centerPoints.zipWithIndex.minBy(_._1.geoTo(point))
                        val distance = electedPoint._1.geoTo(point)
                        (point, distance, electedPoint._2)
                    }
                )

                // Calculate the loss value
                val currentLoss = ExpectationStep.map(_._2).sum

                // TODO Can use stdout to rewrite current training status in a line
                println("Step[%d]  Current loss=%f".format(currentStep, currentLoss))

                if (currentLoss >= lossValue) {
                    // If loss stops decrease
                    break()
                } else {
                    // Get new centers
                    // EM step 2： Maximization step
                    centerPoints = ExpectationStep.groupBy(
                        _._3
                    ).map(kvs => {
                        GeoUtil.mean(kvs._2.map(_._1))
                    })
                }

                lossValue = currentLoss
            })
        )

        // Generate result from last center points which generated
        ClusterResult(points.map(point => {
            val classId = centerPoints.zipWithIndex.minBy(_._1.geoTo(point))._2
            LabeledPoint(classId, point)
        }))
    }
}
