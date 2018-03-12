package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.algorithm.containers.{ClusterResult, LabeledPoint}
import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.GeoUtil

import scala.sys.process.stdout
import scala.util.control.Breaks._

/**
  *
  * @author tsingjyujing@163.com
  * @version 1.0
  * @since 2.7
  */
trait BaseGeoKMeans[V <: IGeoPoint] {

    def initializePoints(points: Iterable[V], k: Int): Iterable[IGeoPoint]

    def lossOutput(currentStep: Int, lossValue: Double, pointCount: Int): Unit = {
        stdout.print("\rLoss[%d] := %f\t\tMean(loss) = %f km".format(currentStep, lossValue, lossValue / pointCount))
        stdout.flush()
    }

    def apply(
                 points: Iterable[V],
                 k: Int,
                 maxStepCount: Int = 100
             ): ClusterResult[Int, V] = {
        // TODO Create new algorithm k-means++ to initialize points with geo-optimized k-means++ algorithm

        var centerPoints: Iterable[IGeoPoint] = initializePoints(points, k)
        var lossValue = Double.MaxValue
        val pointCount = points.size

        breakable(

            (0 until maxStepCount).foreach(currentStep => {
                // For each step while decreasing

                val expectationStep = points.map(
                    point => {
                        val electedPoint = centerPoints.zipWithIndex.minBy(_._1.geoTo(point))
                        val distance = electedPoint._1.geoTo(point)
                        (point, distance, electedPoint._2)
                    }
                )

                // Calculate the loss value
                val currentLoss = expectationStep.map(_._2).sum

                lossOutput(currentStep, currentLoss, pointCount)

                if (currentLoss >= lossValue) {
                    // If loss stops decrease
                    break()
                } else {
                    // Get new centers
                    // EM step 2： Maximization step
                    centerPoints = expectationStep.groupBy(
                        _._3
                    ).map(kvs => {
                        // val classId = kvs._1
                        val pointsInClass = kvs._2.map(_._1)
                        GeoUtil.mean(pointsInClass)
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
