package tsingjyujing.geo.algorithm.containers

import tsingjyujing.geo.basic.IGeoPoint

case class LabeledPoint[K, V <: IGeoPoint](var classId: K, var value: V) extends IGeoPoint {
    override def getLongitude: Double = value.getLongitude
    override def getLatitude: Double = value.getLatitude
}
