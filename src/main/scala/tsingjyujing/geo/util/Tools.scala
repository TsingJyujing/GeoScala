package tsingjyujing.geo.util

import tsingjyujing.geo.basic.geounit.GeometryPoint
import tsingjyujing.geo.basic.timeseries.ITick

import scala.collection.JavaConverters._

object Tools {
    
    def readGPSData(data: Iterable[Array[Double]]): IndexedSeq[GeometryPoint[_ <: ITick]] = data.map(gpsPointData => new GeometryPoint[TickImpl](gpsPointData(0), gpsPointData(1), new TickImpl(gpsPointData(0)))).toIndexedSeq
    
    def readGPSData(data: java.lang.Iterable[Array[Double]]): IndexedSeq[GeometryPoint[_ <: ITick]] = data.asScala.map(gpsPointData => new GeometryPoint[TickImpl](gpsPointData(0), gpsPointData(1), new TickImpl(gpsPointData(0)))).toIndexedSeq
    
    def writeGPSData(data: IndexedSeq[GeometryPoint[_ <: ITick]]): Array[Array[Double]] = data.map(x => Array(x.getLongitude, x.getLatitude, x.getUserInfo.getTick)).toArray
    
    def iterableToJava[T](data: Iterable[T]) = data.asJava
}
