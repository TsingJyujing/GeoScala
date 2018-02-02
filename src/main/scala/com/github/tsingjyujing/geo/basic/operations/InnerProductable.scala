package com.github.tsingjyujing.geo.basic.operations

/**
  * Which can get inner product to another object by given type T
  *
  * @tparam T Type of the object to compare
  */
trait InnerProductable[T] {
    /**
      * Return a value which get inner product of self and point
      *
      * @param point
      * @return
      */
    def innerProduct(point: T): Double
}
