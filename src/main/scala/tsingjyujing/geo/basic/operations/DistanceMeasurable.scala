package tsingjyujing.geo.basic.operations

/**
  * Distance between type T and this
  *
  * @tparam T type of the object to compare
  */
trait DistanceMeasurable[T <: DistanceMeasurable[T]] {

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param x object to get distance
      * @return
      */
    def to(x: T): Double
}
