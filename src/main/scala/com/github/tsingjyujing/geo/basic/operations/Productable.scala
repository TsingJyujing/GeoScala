package com.github.tsingjyujing.geo.basic.operations

/**
  * Define a monoid which has product operation
  *
  * @tparam T Type of self extends this
  */
trait Productable[T <: Productable[T]] {
    def *(v: T): T
}

