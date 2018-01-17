package tsingjyujing.geo.basic.operations

/**
  * Object which can get jaccard similarity and distance
  *
  * @tparam T
  */
trait Jaccardable[T <: Jaccardable[T]] {

    def jaccardSimilarity(x: T): Double

    def jaccardDistance(x: T): Double = {
        val simVal = jaccardSimilarity(x)
        math.exp(-simVal * simVal)
    }
}
