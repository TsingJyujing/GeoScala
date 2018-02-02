package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.IValue

final case class GeoPointValued[T](longitude: Double, latitude: Double, value: T) extends IGeoPoint with IValue[T] {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    override def getValue: T = value
}
