package com.github.tsingjyujing.geo.basic.operations

/**
  * Commonly used in IVector3
  * V1×V2->V3
  * The common define is
  *
  * See <a href="https://en.wikipedia.org/wiki/Outer_product">Outer Product</a> for more details
  *
  * @tparam TI
  * @tparam TO
  */
trait OuterProductable[TI <: OuterProductable[TI, TO], TO] {
    def outProduct(x: TI): TO
}
