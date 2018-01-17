package tsingjyujing.geo.scala.basic.operations

/**
  * Distance between type T and this
  *
  * @tparam T type of the object to compare
  */
trait GeoDistanceMeasurable[T <: GeoDistanceMeasurable[T]] {

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    def geoTo(point: T): Double
}
