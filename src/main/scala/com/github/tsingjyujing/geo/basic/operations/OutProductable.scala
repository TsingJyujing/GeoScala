package com.github.tsingjyujing.geo.basic.operations

trait OutProductable[TI, TO] {
    def outProduct(x: TI): TO
}
