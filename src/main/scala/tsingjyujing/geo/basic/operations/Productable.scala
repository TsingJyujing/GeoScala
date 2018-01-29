package tsingjyujing.geo.basic.operations

trait Productable[T <: Productable[T]] {
    def *(v: T): T

    def one: T
}
