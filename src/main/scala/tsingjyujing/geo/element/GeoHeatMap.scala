package tsingjyujing.geo.element

import java.util

import tsingjyujing.geo.basic.{IGeoPoint, IHashableGeoPoint}
import tsingjyujing.geo.basic.operations.{Angleable, InnerProductable, Jaccardable, Normable}
import tsingjyujing.geo.element.immutable.GeoPointValued
import tsingjyujing.geo.util.convertor.ConvertorFactory

import scala.collection.JavaConverters._

/**
  * Heatmap of geo points
  */
class GeoHeatMap(val accuracy: Long = 0x10000) extends InnerProductable[GeoHeatMap] with Normable with Angleable[GeoHeatMap] with Jaccardable[GeoHeatMap] with Iterable[(Long, Double)] {

    private val data = scala.collection.mutable.Map[Long, Double]()

    def append(k: IHashableGeoPoint, v: Double): Unit = if (k.getGeoHashAccuracy == accuracy) {
        this append(k.indexCode, v)
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def append(k: Long, v: Double): Unit = {
        if (data contains k) {
            data.put(k, v + data(k))
        } else {
            data.put(k, v)
        }
    }

    def remove(key: IHashableGeoPoint): Unit = if (key.getGeoHashAccuracy == accuracy) {
        this remove key.indexCode
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def remove(key: Long): Unit = data remove key

    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def apply(key: Long): Double = if (data contains key) {
        data(key)
    } else {
        0.0D
    }

    def apply(key: IHashableGeoPoint): Double = if (key.getGeoHashAccuracy == accuracy) {
        this (key.indexCode)
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    def +(heatMap: GeoHeatMap): GeoHeatMap = if (heatMap.accuracy == accuracy) {
        val mapReturn = new GeoHeatMap(accuracy)
        val keySet = data.keySet | heatMap.data.keySet
        keySet.foreach(
            key => {
                mapReturn.data.put(key, this (key) + heatMap(key))
            }
        )
        mapReturn
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    def +=(heatMap: GeoHeatMap): Unit = if (heatMap.accuracy == accuracy) {
        heatMap.data.foreach(kv => append(kv._1, kv._2))
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      * Implement inner product by vectorization of sparse-map
      *
      * @param point
      * @return
      */
    override def innerProduct(point: GeoHeatMap): Double = if (accuracy == point.accuracy) {
        (data.keySet & point.data.keySet).map(k => this (k) * point(k)).sum
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      *
      * @param n
      * @return
      */
    override def norm(n: Double): Double = math.pow(data.map(kv => math.pow(kv._2, n)).sum, 1.0 / n)

    override def norm2: Double = math.sqrt(data.map(kv => kv._2 * kv._2).sum)

    /**
      * Get cosed angle value of this and x
      *
      * @param x compare unit
      * @return
      */
    override def conAngle(x: GeoHeatMap): Double = innerProduct(x) / (x.norm2 * norm2)

    override def jaccardSimilarity(x: GeoHeatMap): Double = if (accuracy == x.accuracy) {
        (x.data.keySet intersect data.keySet).size * 1.0 / (x.data.keySet union data.keySet).size
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    def valueFix(f: Double => Double): Unit = data.foreach(kv => {
        data(kv._1) = f(kv._2)
    })

    override def iterator: Iterator[(Long, Double)] = data.iterator


    def getGeoPoints(coordinateType: String = "wgs84"): Iterable[GeoPointValued[Double]] = {
        val convertor = ConvertorFactory(coordinateType)
        this.map(kv => {
            val geoInfo = convertor.transform(IHashableGeoPoint.revertFromCode(kv._1, accuracy))
            new GeoPointValued[Double](geoInfo.getLongitude, geoInfo.getLatitude, kv._2)
        })
    }

    def getGeoPointsJava(coordinateType: String = "wgs84"): util.List[GeoPointValued[Double]] = getGeoPoints(coordinateType).toIndexedSeq.asJava

}

object GeoHeatMap {

    def buildFromPoints(values: Iterable[(IGeoPoint, Double)], accuracy: Long = 0x10000): GeoHeatMap = {
        val newMap = new GeoHeatMap(accuracy)
        values.groupBy(_._1).map(kv => {
            newMap.data.put(IHashableGeoPoint.createCodeFromGps(kv._1, accuracy), kv._2.map(_._2).sum)
        })
        newMap
    }

    def buildFromMap(value: GeoHeatMap, accuracy: Long = 0x10000): GeoHeatMap = {
        val newMap = new GeoHeatMap(accuracy)
        if (value.accuracy == newMap.accuracy) {
            value.foreach(kv => newMap.data.put(kv._1, kv._2))
        } else {
            value.getGeoPoints().foreach(
                x => newMap.append(IHashableGeoPoint.createCodeFromGps(x, accuracy), x.getValue)
            )
        }
        newMap
    }
}
