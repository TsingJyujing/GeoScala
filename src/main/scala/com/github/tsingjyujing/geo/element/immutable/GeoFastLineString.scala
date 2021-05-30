package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.{GeoDistanceMeasurable, GeoJSONable, IContains}
import com.github.tsingjyujing.geo.element.GeoPointTree
import com.github.tsingjyujing.geo.util.GeoUtil

import scala.util.parsing.json.JSONObject

/**
 * Geo point line string which can search/get distance quickly
 *
 * @param points       points which contribute line string
 * @param searchRadius radius to search
 */
case class GeoFastLineString[TGeoPoint <: IGeoPoint](points: IndexedSeq[TGeoPoint], searchRadius: Double)
  extends
    GeoDistanceMeasurable[IGeoPoint]
    with GeoJSONable
    with IContains[IGeoPoint] {

  assert(points.size >= 2, "Points count can't less than 2.")

  /**
   * Interpolated points
   */
  private val pointsTree: GeoPointTree[GeoPointValued[GeoLine[TGeoPoint]]] = {
    val geoPointTree = new GeoPointTree[GeoPointValued[GeoLine[TGeoPoint]]]()
    geoPointTree.appendPoint(
      GeoPointValued[GeoLine[TGeoPoint]](
        points(0),
        GeoLine[TGeoPoint](points(0), points(1))
      )
    )
    geoPointTree.appendPoints(
      points.sliding(2).flatMap(
        lineData => {
          val line = GeoLine(lineData.head, lineData.last)
          val dist = lineData.head ~> lineData.last
          val pointsProcessed = if (dist > (searchRadius / 2)) {
            val interpPoints = math.ceil(dist / (searchRadius / 2)).toInt
            GeoUtil.interp(lineData.head, lineData.last, interpPoints).toIndexedSeq.tail
          } else {
            lineData.tail
          }
          pointsProcessed.map(p => {
            GeoPointValued[GeoLine[TGeoPoint]](
              p, line
            )
          })
        }
      )
    )
    geoPointTree
  }

  /**
   * Get distance from this to point or point to this (should be same)
   *
   * @param point geo point
   * @return
   */
  override def geoTo(point: IGeoPoint): Double = {
    val searchResult = pointsTree.geoNear(point, searchRadius)
    if (searchResult.isDefined) {
      searchResult.get.value.geoTo(point)
    } else {
      Double.MaxValue
    }
  }


  /**
   * Get scala original JSON object,
   * JSON object deprecated in Scala 2.12 but still using in 2.10
   *
   * @return
   */
  override def toGeoJSON: JSONObject = GeoJSONable.createLineString(points)

  /**
   * Is x contains in self
   *
   * @param x the point
   * @return
   */
  override def contains(x: IGeoPoint): Boolean = pointsTree.geoNear(x, searchRadius).isDefined
}
