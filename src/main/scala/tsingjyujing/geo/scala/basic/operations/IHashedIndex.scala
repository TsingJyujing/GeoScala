package tsingjyujing.geo.scala.basic.operations

/**
  * Which class can get an indexed hash
  * the index code should be unique to avoid duplication in Set or other data structures
  * @tparam T should have a great randomly hashCode, recommend use Long, Int, avoid using String or Double
  */
trait IHashedIndex[T] {

    /**
      * isEqual function
      * @param x
      * @return
      */
    def isEqual(x:IHashedIndex[T]):Boolean = x.indexCode.equals(indexCode)

    /**
      * Get a unique indexCode as type T
      * @return
      */
    def indexCode:T

    /**
      * Avoid duplication in Set or key of the Map
      * @param o object to compare
      * @return
      */
    override def equals(o: scala.Any): Boolean = o match {
        case oFetched:IHashedIndex[T] => isEqual(oFetched)
        case _ => false
    }

    /**
      * Create a hashCode to accelerate operations in HashSet or HashMap ect.
      * @return
      */
    override def hashCode(): Int = indexCode.hashCode()
}
