package tsingjyujing.geo.basic.operations

trait IVector extends Normable with Iterable[Double] {
    /**
      * Get norm of self object
      *
      * @param n order of the normal
      * @return
      */
    override def norm(n: Double): Double = math.pow(iterator.map(x => math.pow(x, n)).sum, 1.0 / n)
}
