package com.github.tsingjyujing.geo.basic.operations

/**
  * @author tsingjyujing@163.com
  *         for `Set` using, have a method to judge is an element contains in this
  * @tparam T element type
  */
trait IContains[T] {

    /**
      * Is x contains in self
      *
      * @param x
      * @return
      */
    def contains(x: T): Boolean

    /**
      * NOT contains
      *
      * @param x
      * @return
      */
    final def nonContains(x: T): Boolean = !contains(x)
}
