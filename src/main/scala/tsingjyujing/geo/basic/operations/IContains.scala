package tsingjyujing.geo.basic.operations

trait IContains[T] {
    def contains(x: T): Boolean
}
