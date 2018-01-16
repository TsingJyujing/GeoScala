package tsingjyujing.geo.algorithm

import tsingjyujing.geo.basic.geounit.GeometryPoint
import tsingjyujing.geo.basic.timeseries.ITick
import tsingjyujing.geo.util.mathematical.GPSUtil

/**
  * @author tsingjyujing
  */
class SparseGPS(val cleaningParam: Double, val sparsityParam: Double) {
    def compress[T <: ITick](gpsSequence: IndexedSeq[GeometryPoint[T]]) = GPSUtil.sparsifyGPS(
        GPSUtil.cleanGPS(gpsSequence, cleaningParam * 24.0),
        sparsityParam,
        math.round(math.max(sparsityParam * 100, 32)).toInt
    )
    
    //先使用线性复原吧，测地线的版本还没搞出来
    def uncompressByDistance[T](gpsSequence: IndexedSeq[GeometryPoint[T]], interpDistance: Double) = {
        /*
        val earthRadius = GeometryPoint.EARTH_RADIUS
        val buffedGPS = new mutable.MutableList[GeometryPoint[T]]
        buffedGPS += gpsSequence(0)
        SeqUtil.movingMap[GeometryPoint[T], GeometryPoint[T]](gpsSequence, 2, points => {
            val point1 = points(0)
            val point2 = points(1)
            val distance = point1.distance(point2)
            if (distance <= interpDistance) {
                buffedGPS += point2
            }else{
            
            }
            Unit
        })*/
        throw new Exception("Unimplemented method.")
    }
}
