package com.github.tsingjyujing.geo.basic.operations

/**
  * Define a monoid which has product operation
  *
  * @author tsingjyujing@163.com
  * @tparam T Type of self extends this
  */
trait Productable[T <: Productable[T]] {
    /**
      * Product
      *
      * @param v value
      * @return
      */
    def *(v: T): T
}

