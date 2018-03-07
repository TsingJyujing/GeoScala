package com.github.tsingjyujing.geo.basic.operations

/**
  * Define a add group
  *
  * @author tsingjyujing@163.com
  * @tparam T element type
  */
trait Addable[T <: Addable[T]] {
    /**
      * Add self to a same type value
      *
      * @param v
      * @return
      */
    def +(v: T): T
}


