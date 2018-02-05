package com.github.tsingjyujing.geo.algorithm.containers

import com.github.tsingjyujing.geo.basic.IGeoPoint

/**
  * A geo point has a label with type K and value with type V
  *
  * @param classId label info
  * @param value   geo point info
  * @tparam K type of label
  * @tparam V type of point
  */
case class LabeledPoint[K, V <: IGeoPoint](var classId: K, var value: V) extends IGeoPoint {

    override def getLongitude: Double = value.getLongitude

    override def getLatitude: Double = value.getLatitude
}
