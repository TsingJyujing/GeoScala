package com.github.tsingjyujing.geo.basic.operations

/**
  * to pack a value with some type
  * you can make a collection with type _<:IValue[T] and getValue as T foreach
  *
  * @author tsingjyujing@163.com
  * @tparam T value type
  */
trait IValue[+T] {

    /**
      * Get value
      *
      * @return
      */
    def getValue: T
}
