package tsingjyujing.geo.basic

import tsingjyujing.geo.basic.operations.GeoJSONable

import scala.util.parsing.json.JSONObject

/**
  * A point set to query points easily
  *
  * @tparam T Type to save geo points, any type extends IGeoPoint
  */
trait IGeoPointSet[T <: IGeoPoint] extends Iterable[T] with GeoJSONable{

    override def toGeoJSON: JSONObject = GeoJSONable.createMultiPoints(getPoints)

    def appendPoint(point: T): Unit

    def getPoints: Iterable[T]

    override def iterator: Iterator[T] = getPoints.iterator

    def geoNear(point: IGeoPoint, maxDistance: Double = 1.0): Option[T]

    def geoWithin(point: IGeoPoint, minDistance: Double = 0.0, maxDistance: Double = 1.0): Iterable[T]

}
