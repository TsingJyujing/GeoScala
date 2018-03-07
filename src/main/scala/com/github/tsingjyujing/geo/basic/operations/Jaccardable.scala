package com.github.tsingjyujing.geo.basic.operations

/**
  * Object which can get jaccard similarity and distance
  *
  * @author tsingjyujing@163.com
  * @tparam T Type to compare with
  */
trait Jaccardable[T <: Jaccardable[T]] {

    def jaccardSimilarity(x: T): Double

    /**
      * Auto get a fake distance by similarity
      *
      * @param x
      * @return
      */
    def jaccardDistance(x: T): Double = {
        val simVal = jaccardSimilarity(x)
        math.exp(-simVal * simVal)
    }
}

object Jaccardable {
    /**
      * J(A,B) = count(A & B)/count(A | B)*100%
      *
      * @param s1 Set1
      * @param s2 Set2
      * @tparam U similarity
      * @return
      */
    def commonJaccard[U](s1: Set[U], s2: Set[U]): Double = (s1 intersect s2).size * 1.0 / (s1 union s2).size
}
