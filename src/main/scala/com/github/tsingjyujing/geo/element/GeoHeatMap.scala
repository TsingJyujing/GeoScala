package com.github.tsingjyujing.geo.element

import java.util

import com.github.tsingjyujing.geo.basic.operations.{Addable, _}
import com.github.tsingjyujing.geo.basic.{IGeoPoint, IHashableGeoBlock}
import com.github.tsingjyujing.geo.element.immutable.GeoPointValued
import com.github.tsingjyujing.geo.util.convertor.ConvertorFactory

import scala.collection.JavaConverters._


/**
  * Heatmap of geo points
  */
class GeoHeatMap[T <: Addable[T]](
                                           val baseValue: T,
                                           val accuracy: Long = 0x10000
                                       )
    extends Jaccardable[GeoHeatMap[T]]
        with Iterable[(Long, T)] {

    protected val data: scala.collection.mutable.Map[Long, T] = scala.collection.mutable.Map[Long, T]()

    /**
      * Append a point into heatmap
      * @param k
      * @param v
      */
    def append(k: IHashableGeoBlock, v: T): Unit = if (k.getGeoHashAccuracy == accuracy) {
        this appendByCode(k.indexCode, v)
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      * Append a point into heatmap by hash code
      * Not recommend to using
      * @param k
      * @param v
      */
    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def append(k: Long, v: T): Unit = appendByCode(k, v)

    protected def appendByCode(k: Long, v: T): Unit = {
        if (data contains k) {
            data.put(k, v + data(k))
        } else {
            data.put(k, v + baseValue)
        }
    }

    /**
      * Remove block data in heatmap
      * @param key block info
      */
    def remove(key: IHashableGeoBlock): Option[T] = if (key.getGeoHashAccuracy == accuracy) {
        this removeByCode key.indexCode
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      * Remove a block data by code
      * Not recommend to using
      * @param key
      * @return
      */
    @deprecated(message = "careful while using this function and ensure the accuracy is same")
    def remove(key: Long): Option[T] = removeByCode(key)

    protected def removeByCode(key: Long): Option[T] = data remove key

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

    /**
      * Add two heatmap into one
      * @param heatMap
      * @return
      */
    def +(heatMap: GeoHeatMap[T]): GeoHeatMap[T] = if (heatMap.accuracy == accuracy) {
        val mapReturn = new GeoHeatMap[T](baseValue, accuracy)
        val keySet = data.keySet | heatMap.data.keySet
        keySet.foreach(
            key => {
                mapReturn.data.put(key, this.applyByCode(key) + heatMap.applyByCode(key))
            }
        )
        mapReturn
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      * += operation
      * @param heatMap
      */
    def +=(heatMap: GeoHeatMap[T]): Unit = if (heatMap.accuracy == accuracy) {
        heatMap.data.foreach(kv => appendByCode(kv._1, kv._2))
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      * Calculate jaccard similarity
      * @param x
      * @return
      */
    override def jaccardSimilarity(x: GeoHeatMap[T]): Double = if (accuracy == x.accuracy) {
        (x.data.keySet intersect data.keySet).size * 1.0 / (x.data.keySet union data.keySet).size
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      * Do operation on value
      * @param f
      */
    def valueFix(f: T => T): Unit = data.foreach(kv => {
        data(kv._1) = f(kv._2)
    })

    override def iterator: Iterator[(Long, T)] = data.iterator

    /**
      * Get points with value (for visualization)
      * @param coordinateType
      * @return
      */
    def getGeoPoints(coordinateType: String = "wgs84"): Iterable[GeoPointValued[T]] = {
        val convertor = ConvertorFactory(coordinateType)
        this.map(kv => {
            val geoInfo = convertor.transform(IHashableGeoBlock.revertFromCode(kv._1, accuracy))
            new GeoPointValued[T](geoInfo.getLongitude, geoInfo.getLatitude, kv._2)
        })
    }

    def getGeoPointsJava(coordinateType: String = "wgs84"): util.List[GeoPointValued[T]] = getGeoPoints(coordinateType).toIndexedSeq.asJava

}

object GeoHeatMap {

    def buildFromPoints[T <: Addable[T]](values: Traversable[(IGeoPoint, T)], baseValue: T, accuracy: Long = 0x10000): GeoHeatMap[T] = {
        val newMap = new GeoHeatMap[T](baseValue, accuracy)
        values.groupBy(
            pointValue => {
                IHashableGeoBlock.createCodeFromGps(pointValue._1, accuracy)
            }
        ).map(kv => {
            newMap.data.put(kv._1, kv._2.map(_._2).reduce(_ + _))
        })
        newMap
    }

    @deprecated(message = "Be careful while using this API and ensure your accuracy is right")
    def buildFromCodes[T <: Addable[T]](values: Traversable[(Long, T)], baseValue: T, accuracy: Long): GeoHeatMap[T] = {
        val newMap = new GeoHeatMap[T](baseValue, accuracy)
        values.groupBy(
            _._1
        ).map(kv => {
            newMap.data.put(kv._1, kv._2.map(_._2).reduce(_ + _))
        })
        newMap
    }

    def buildFromMap[T <: Addable[T]](value: GeoHeatMap[T], baseValue: T, accuracy: Long = 0x10000): GeoHeatMap[T] = {
        val newMap = new GeoHeatMap(baseValue, accuracy)
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
