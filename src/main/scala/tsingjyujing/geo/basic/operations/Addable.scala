package tsingjyujing.geo.basic.operations

/**
  * Define a add group
  *
  * @tparam T element type
  */
trait Addable[T <: Addable[T]] {
    def +(v: T): T
    def zero: T
}