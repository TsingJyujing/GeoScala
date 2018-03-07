package com.github.tsingjyujing.geo.basic.operations

/**
  * Commonly used in IVector3
  * V1×V2->V3
  * The common define is
  *
  * See <a href="https://en.wikipedia.org/wiki/Outer_product">Outer Product</a> for more details
  *
  * @author tsingjyujing@163.com
  * @tparam TI type to get TO
  * @tparam TO result type
  */
trait OuterProductable[TI <: OuterProductable[TI, TO], TO] {
    /**
      * Get out product to TI and output TO
      *
      * @param x value
      * @return
      */
    def outProduct(x: TI): TO
}
