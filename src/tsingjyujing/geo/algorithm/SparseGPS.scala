package tsingjyujing.geo.algorithm


import tsingjyujing.geo.basic.geounit.GeometryPoint
import tsingjyujing.geo.basic.timeseries.Tickable
import tsingjyujing.geo.util.mathematical.GPSUtil

/**
  * @author tsingjyujing
  */
class SparseGPS(val cleaningParam: Double, val sparsityParam: Double) {
    def compress[T <: Tickable](gpsSequence: IndexedSeq[GeometryPoint[T]]) = GPSUtil.sparsifyGPS(
        GPSUtil.cleanGPS(gpsSequence, cleaningParam * 24.0),
        sparsityParam,
        math.round(math.max(sparsityParam * 100, 32)).toInt
    )
    
    //先使用线性复原吧，测地线的版本还没搞出来
    def uncompress[T <: Tickable](gpsSequence: IndexedSeq[GeometryPoint[T]], reverseTime: IndexedSeq[Double]) = {
    
    }
}
