package com.github.tsingjyujing.geo.basic.operations

/**
  * This vector is not same in Scala's `scala.collection.immutable.Vector`
  * it means a point in N-dimensional Euclid space
  *
  * @author tsingjyujing@163.com
  */
trait IVector extends Normable with Iterable[Double] {

    /**
      * Get norm of self object
      *
      * @param n order of the normal
      * @return
      */
    override def norm(n: Double): Double = math.pow(iterator.map(x => math.pow(x, n)).sum, 1.0 / n)
}
