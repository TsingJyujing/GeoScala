package tsingjyujing.geo.basic.operations

trait Productable[T] {
    def *(v: T): T
    def one: T
}
