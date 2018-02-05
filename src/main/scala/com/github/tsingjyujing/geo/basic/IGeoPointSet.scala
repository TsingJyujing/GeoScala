package com.github.tsingjyujing.geo.basic

import com.github.tsingjyujing.geo.basic.operations.GeoJSONable

import scala.util.parsing.json.JSONObject

/**
  * A point set to query points easily
  *
  * @tparam T Type to save geo points, any type extends IGeoPoint
  */
trait IGeoPointSet[T <: IGeoPoint] extends Iterable[T] with GeoJSONable {

    override def toGeoJSON: JSONObject = GeoJSONable.createMultiPoint(getPoints)

    /**
      * Add point to set
      *
      * @param point point value
      */
    def appendPoint(point: T): Unit

    def appendPoints(points: TraversableOnce[T]): Unit = points.foreach(appendPoint)

    /**
      * Query all points in set
      *
      * @return
      */
    def getPoints: Iterable[T]

    override def iterator: Iterator[T] = getPoints.iterator

    /**
      * Search the points nearest to point input in radius as max distance
      *
      * @param point       center point
      * @param maxDistance max radius to search
      * @return
      */
    def geoNear(point: IGeoPoint, maxDistance: Double = 1.0): Option[T]

    /**
      * Find points in ring
      *
      * @param point       center point
      * @param minDistance ring inside radius
      * @param maxDistance ring outside radius
      * @return
      */
    def geoWithinRing(point: IGeoPoint, minDistance: Double = 0.0, maxDistance: Double = 1.0): Iterable[T]

    /**
      * Search all points in radius of maxDistance
      *
      * @param point       center point
      * @param maxDistance max radius
      * @return
      */
    def geoWithin(point: IGeoPoint, maxDistance: Double = 1.0): Iterable[T] = geoWithinRing(point, -1, maxDistance)

    /**
      * Get geo distance of nearest point in set
      *
      * @param points
      * @param searchRadius
      * @return
      */
    def geoFrechet(points: TraversableOnce[IGeoPoint], searchRadius: Double = 0.5): TraversableOnce[Double] = {
        points.map(p => {
            val searchResult = geoNear(p, searchRadius)
            if (searchResult.isDefined) {
                searchResult.get.geoTo(p)
            } else {
                IGeoPoint.EARTH_RADIUS * math.Pi
            }
        })
    }

    /**
      * Get similarity of the route and set
      *
      * @param points
      * @param searchRadius
      * @return
      */
    def geoFrechetSimilarity(points: TraversableOnce[IGeoPoint], searchRadius: Double = 0.5): Double = {
        val frechetResult = geoFrechet(points, searchRadius)
        frechetResult.count(_ <= searchRadius) / frechetResult.size
    }
}
