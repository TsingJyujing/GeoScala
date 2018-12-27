package com.github.tsingjyujing.geo.element

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock

import com.github.tsingjyujing.geo.basic.{IGeoPoint, IGeoPointSet}
import com.github.tsingjyujing.geo.element.immutable.GeoPointValued
import com.github.tsingjyujing.geo.jni.NativeS2PointIndex
import com.github.tsingjyujing.geo.util.BeCarefulWhileUsing

import scala.collection.mutable.ArrayBuffer

/**
  * An index (and simple data storage)
  *
  * @param pointSearchLimit how many points to search
  * @tparam T Type to save geo points, any type extends IGeoPoint
  */
@BeCarefulWhileUsing(
    message = "Not prepared for all platform yet"
)
class GeoNativeIndexPointTree[T](pointSearchLimit: Int = -1) extends IGeoPointSet[GeoPointValued[T]] {

    private val pointDataBuffer: ArrayBuffer[GeoPointValued[T]] = ArrayBuffer[GeoPointValued[T]]()
    private val s2IndexData: Long = NativeS2PointIndex.newS2Index()
    private val isReleased = new AtomicBoolean(false)
    /**
      * Reentrant Read Write Lock
      */
    private val readWriteLock = new ReentrantReadWriteLock
    private val readLock = readWriteLock.readLock
    private val writeLock = readWriteLock.writeLock

    /**
      * Free index data manually
      */
    @BeCarefulWhileUsing(message = "Be careful while using this method and ensure data will not be use after free.")
    def free(): Unit = {

        if (!isReleased.get()) {
            writeLock.lock()
            try {
                NativeS2PointIndex.deleteS2Index(s2IndexData)
                isReleased.set(true)
            } catch {
                case _: Throwable =>
                    writeLock.unlock()
            }
        }
    }

    override def finalize(): Unit = {
        free()
        super.finalize()
    }

    /**
      * Add point to set
      *
      * @param point point value
      */
    override def appendPoint(point: GeoPointValued[T]): Unit = {
        writeLock.lock()
        try {
            NativeS2PointIndex.insertPoint(
                s2IndexData,
                point.getLongitude,
                point.getLatitude,
                pointDataBuffer.size
            )
            pointDataBuffer += point
        } finally {
            writeLock.unlock()
        }
    }

    /**
      * Query all points in set
      *
      * @return
      */
    @BeCarefulWhileUsing(message = "Don't remove/change data after get this buffer")
    override def getPoints: Iterable[GeoPointValued[T]] = pointDataBuffer

    /**
      * Search the points nearest to point input in radius as max distance
      *
      * @param point       center point
      * @param maxDistance max radius to search (km)
      * @return
      */
    override def geoNear(point: IGeoPoint, maxDistance: Double): Option[GeoPointValued[T]] = {
        queryNearPoints(point, maxDistance, 1).headOption
    }

    /**
      * Find points in ring
      *
      * @param point       center point
      * @param minDistance ring inside radius
      * @param maxDistance ring outside radius
      * @return
      */
    override def geoWithinRing(point: IGeoPoint, minDistance: Double, maxDistance: Double): Iterable[GeoPointValued[T]] = {
        assert(minDistance <= 0, "Min distance over 0 is not allow in Native mode")
        queryNearPoints(
            point, maxDistance, pointSearchLimit
        )
    }

    def queryNearPoints(point: IGeoPoint, maxDistance: Double, pointsToReturn: Int): Iterable[GeoPointValued[T]] = {
        readLock.lock()
        try {
            val result = NativeS2PointIndex.queryPointsNative(
                s2IndexData,
                point.getLongitude,
                point.getLatitude,
                maxDistance,
                pointsToReturn
            )
            if (result.length % 3 != 0) {
                throw new RuntimeException("Native error: return data length illegal:" + result.length)
            }
            val resultCount = result.length / 3

            (0 until resultCount).map(i => {
                pointDataBuffer(result(i * 3 + 2).toInt)
            })
        } finally {
            readLock.unlock()
        }
    }
}
