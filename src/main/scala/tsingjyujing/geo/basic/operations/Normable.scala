package tsingjyujing.geo.basic.operations

/**
  * Object which can get a norm N
  */
trait Normable {
    /**
      * Get norm of self object
      *
      * @param n order of the normal
      * @return
      */
    def norm(n: Double): Double

    /**
      * Which can implement it for faster calculation
      *
      * @return
      */
    def norm2: Double = norm(2)
}

object Normable {
    /**
      *
      * Common implementation of Norm^2
      * Faster than getNorm(_,2)
      * @param vector vector
      * @return
      */
    def getNorm2(vector: TraversableOnce[Double]): Double = math.sqrt(vector.map(x => x * x).sum)

    /**
      *
      * Common implementation of Norm^n
      * @param vector vector
      * @param n      default in order 2
      * @return
      */
    def getNorm(vector: TraversableOnce[Double], n: Int = 2): Double = math.pow(vector.map(math.pow(_, n)).sum, 1.0D / n)
}