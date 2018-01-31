package tsingjyujing.geo.basic.operations

/**
  * Define a monoid which has
  * @tparam T
  */
trait Productable[T <: Productable[T]] {
    def *(v: T): T
}

