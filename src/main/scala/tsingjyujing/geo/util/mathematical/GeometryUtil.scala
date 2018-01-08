package tsingjyujing.geo.util.mathematical

/**
  * @author tsingjyujing
  */
object GeometryUtil {
    
    def angle(v1: Array[Double], v2: Array[Double]): Double = math.acos(innerProduct(v1, v2) / math.sqrt(norm2(v1) * norm2(v2))) / math.Pi * 180.00
    
    def norm2(v: Iterable[Double]): Double = v.map(x => x * x).sum
    
    def innerProduct(v1: Array[Double], v2: Array[Double]): Double = {
        assert(v1.length == v2.length, "Try to get degree of two different dimension vectors.")
        (v1 zip v2).map(x => {
            x._2 * x._1
        }).sum
    }
}
