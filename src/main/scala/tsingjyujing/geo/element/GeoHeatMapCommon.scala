package tsingjyujing.geo.element

import java.util

import tsingjyujing.geo.basic.{IGeoPoint, IHashableGeoBlock}
import tsingjyujing.geo.basic.operations.{Addable, _}
import tsingjyujing.geo.element.immutable.GeoPointValued
import tsingjyujing.geo.util.convertor.ConvertorFactory

import scala.collection.JavaConverters._


/**
  * Heatmap of geo points
  */
class GeoHeatMapCommon[T <: Addable[T]](
                                           val baseValue: T,
                                           val accuracy: Long = 0x10000
                                       )
    extends Jaccardable[GeoHeatMapCommon[T]]
        with Iterable[(Long, T)] {

    protected val data: scala.collection.mutable.Map[Long, T] = scala.collection.mutable.Map[Long, T]()

    def append(k: IHashableGeoBlock, v: T): Unit = if (k.getGeoHashAccuracy == accuracy) {
        this appendByCode(k.indexCode, v)
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def append(k: Long, v: T): Unit = appendByCode(k, v)

    protected def appendByCode(k: Long, v: T): Unit = {
        if (data contains k) {
            data.put(k, v + data(k))
        } else {
            data.put(k, v + baseValue)
        }
    }

    def remove(key: IHashableGeoBlock): Unit = if (key.getGeoHashAccuracy == accuracy) {
        this removeByCode key.indexCode
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def remove(key: Long): Unit = removeByCode(key)

    protected def removeByCode(key: Long): Unit = data remove key

    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def apply(key: Long): T = applyByCode(key)

    protected def applyByCode(key: Long): T = if (data contains key) {
        data(key)
    } else {
        baseValue
    }

    def apply(key: IHashableGeoBlock): T = if (key.getGeoHashAccuracy == accuracy) {
        this (key.indexCode)
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    def +(heatMap: GeoHeatMapCommon[T]): GeoHeatMapCommon[T] = if (heatMap.accuracy == accuracy) {
        val mapReturn = new GeoHeatMapCommon[T](baseValue, accuracy)
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

    def +=(heatMap: GeoHeatMapCommon[T]): Unit = if (heatMap.accuracy == accuracy) {
        heatMap.data.foreach(kv => appendByCode(kv._1, kv._2))
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    override def jaccardSimilarity(x: GeoHeatMapCommon[T]): Double = if (accuracy == x.accuracy) {
        (x.data.keySet intersect data.keySet).size * 1.0 / (x.data.keySet union data.keySet).size
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    def valueFix(f: T => T): Unit = data.foreach(kv => {
        data(kv._1) = f(kv._2)
    })

    override def iterator: Iterator[(Long, T)] = data.iterator


    def getGeoPoints(coordinateType: String = "wgs84"): Iterable[GeoPointValued[T]] = {
        val convertor = ConvertorFactory(coordinateType)
        this.map(kv => {
            val geoInfo = convertor.transform(IHashableGeoBlock.revertFromCode(kv._1, accuracy))
            new GeoPointValued[T](geoInfo.getLongitude, geoInfo.getLatitude, kv._2)
        })
    }

    def getGeoPointsJava(coordinateType: String = "wgs84"): util.List[GeoPointValued[T]] = getGeoPoints(coordinateType).toIndexedSeq.asJava

}

object GeoHeatMapCommon {

    def buildFromPoints[T <: Addable[T]](values: Iterable[(IGeoPoint, T)], baseValue: T, accuracy: Long = 0x10000): GeoHeatMapCommon[T] = {
        val newMap = new GeoHeatMapCommon[T](baseValue, accuracy)
        values.groupBy(
            pointValue=>{
                IHashableGeoBlock.createCodeFromGps(pointValue._1,accuracy)
            }
        ).map(kv => {
            newMap.data.put(kv._1, kv._2.map(_._2).reduce((a, b) => a + b))
        })
        newMap
    }

    def buildFromMap[T <: Addable[T]](value: GeoHeatMapCommon[T], baseValue: T, accuracy: Long = 0x10000): GeoHeatMapCommon[T] = {
        val newMap = new GeoHeatMapCommon(baseValue, accuracy)
        if (value.accuracy == newMap.accuracy) {
            value.foreach(kv => newMap.data.put(kv._1, kv._2))
        } else {
            value.getGeoPoints().foreach(
                x => newMap appendByCode(IHashableGeoBlock.createCodeFromGps(x, accuracy), x.getValue)
            )
        }
        newMap
    }
}
