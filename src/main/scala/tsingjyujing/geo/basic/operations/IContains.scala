package tsingjyujing.geo.basic.operations

trait IContains[T] {
    def contains(x: T): Boolean

    final def nonContains(x: T): Boolean = !contains(x)
}
