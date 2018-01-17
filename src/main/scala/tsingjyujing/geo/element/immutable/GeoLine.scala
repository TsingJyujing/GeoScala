package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.{IGeoLine, IGeoPoint}

final class GeoLine(val pointStart: IGeoPoint, val pointEnd: IGeoPoint) extends IGeoLine {
    val pointTuple: (IGeoPoint, IGeoPoint) = (pointStart, pointEnd)

    override def getTerminalPoints: (IGeoPoint, IGeoPoint) = pointTuple
}
