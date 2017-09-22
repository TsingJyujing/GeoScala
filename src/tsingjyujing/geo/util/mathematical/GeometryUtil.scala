package tsingjyujing.geo.util.mathematical

/**
  * @author tsingjyujing
  */
object GeometryUtil {
    
    def angle(v1: Array[Double], v2: Array[Double]): Double = {
        assert(v1.length == v2.length, "Try to get degree of two different dimention vectors.")
        var norm1 = 0.0
        var norm2 = 0.0
        val innerProduct = (v1 zip v2).map(
            x => {
                norm1 += x._1 * x._1
                norm2 += x._2 * x._2
                x._1 * x._2
            }
        ).sum
        math.acos(innerProduct / math.sqrt(norm1 * norm2)) / math.Pi * 180.00
    }
}
