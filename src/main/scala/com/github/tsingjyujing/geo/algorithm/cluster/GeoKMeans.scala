package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.algorithm.containers.{ClusterResult, LabeledPoint}
import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.GeoUtil
import com.github.tsingjyujing.geo.util.mathematical.Probability

import scala.collection.mutable.ArrayBuffer
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
        val centerPoints = ArrayBuffer[IGeoPoint]()
        centerPoints.appendAll(
            (1 to k).map(_ => {
                Probability.sphereUniform
            })
        )
        var lossValue = Double.MaxValue
        breakable(
            (0 until maxStepCount).foreach(currentStep => {
                val EMStep1 = points.map(
                    point => {
                        val electedPoint = centerPoints.zipWithIndex.minBy(_._1.geoTo(point))
                        val distance = electedPoint._1.geoTo(point)
                        (point, distance, electedPoint._2)
                    }
                )
                val currentLoss = EMStep1.map(_._2).sum
                println("Step[%d]  Current loss=%f".format(currentStep, currentLoss))
                if (currentLoss >= lossValue) {
                    break()
                } else {
                    EMStep1.groupBy(
                        _._3
                    ).foreach(kvs => {
                        centerPoints(kvs._1) = GeoUtil.mean(kvs._2.map(_._1))
                    })
                }
                lossValue = currentLoss
            })
        )
        ClusterResult(points.map(point => {
            val classId = centerPoints.zipWithIndex.minBy(_._1.geoTo(point))._2
            LabeledPoint(classId, point)
        }))
    }
}
