package tsingjyujing.geo.basic.operations

/**
  * Which can get inner product to another object by given type T
  * @tparam T Type of the object to compare
  */
trait InnerProductable[T] {
    def innerProduct(point:T):Double
}
