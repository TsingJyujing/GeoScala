package tsingjyujing.geo.scala.basic.operations

/**
  * Object which can get a norm N
  */
trait Normable{
    def norm(n:Double):Double
    def norm2:Double = norm(2)
}
