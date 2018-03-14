package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.util.mathematical.Probability.{selectByProbability => sbp, selectUniformly => su}

import scala.collection.mutable

/**
  * Implementation of K-Means++ on geographical points.
  *
  * @see paper: <a href="http://ilpubs.stanford.edu:8090/778/1/2006-13.pdf">k-means++: The Advantages of Careful Seeding</a>
  * @tparam T type of point
  */
class GeoKMeansPlus[T <: IGeoPoint] extends BaseGeoKMeans[T] {
    /**
      * Get initialized k center points
      *
      * @param points sample point
      * @param k      k centers
      * @return
      */
    override def initializePoints(points: Iterable[T], k: Int): Iterable[IGeoPoint] = {
        val indexedPoints: IndexedSeq[T] = points.toIndexedSeq
        assert(k > 1, "k should an integer which greater than 1")
        val initPoints = mutable.ArrayBuffer[IGeoPoint]()
        initPoints += su(indexedPoints)
        (1 until k).foreach(_ => {
            val probWeight = indexedPoints.map(p => {
                val mind = initPoints.map(_.geoTo(p)).min
                mind * mind
            })
            initPoints += sbp(indexedPoints, probWeight)
        })
        initPoints
    }
}
