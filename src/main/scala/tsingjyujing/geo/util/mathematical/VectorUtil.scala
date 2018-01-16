package tsingjyujing.geo.util.mathematical

object VectorUtil {
  def outerProduct(v1: Array[Double], v2: Array[Double]): Array[Double] = {
    assert(v1.length == 3)
    assert(v2.length == 3)
    Array(
      0.0D + v1(1) * v2(2) - v1(2) * v2(1),
      0.0D - v1(0) * v2(2) + v1(2) * v2(0),
      0.0D + v1(0) * v2(1) - v1(1) * v2(0)
    )
  }

  def norm2Vector(normLen: Double, vec: Array[Double]): Array[Double] = {
    val normFactor = normLen/math.sqrt(vec.map(x=>x*x).sum)
    vec.map(x=>x*normFactor)
  }
}
