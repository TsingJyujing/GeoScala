package tsingjyujing.geo.util

import tsingjyujing.geo.basic.geounit.GeometryPoint
import tsingjyujing.geo.basic.timeseries.Tickable

import scala.collection.JavaConverters._

object Tools {
    
    def readGPSData(data: Iterable[Array[Double]]): IndexedSeq[GeometryPoint[_ <: Tickable]] = data.map(gpsPointData => new GeometryPoint[TickImpl](gpsPointData(0), gpsPointData(1), new TickImpl(gpsPointData(0)))).toIndexedSeq
    
    def readGPSData(data: java.lang.Iterable[Array[Double]]): IndexedSeq[GeometryPoint[_ <: Tickable]] = data.asScala.map(gpsPointData => new GeometryPoint[TickImpl](gpsPointData(0), gpsPointData(1), new TickImpl(gpsPointData(0)))).toIndexedSeq
    
    def writeGPSData(data: IndexedSeq[GeometryPoint[_ <: Tickable]]): Array[Array[Double]] = data.map(x => Array(x.longitude, x.latitude, x.userInfo.getTick)).toArray
    
    def toJava[T](data: Iterable[T]) = data.asJava
}
