package tsingjyujing.geo.element

import tsingjyujing.geo.basic.timeseries.ITimeIndexSeq
import tsingjyujing.geo.element.immutable.TimeElement
import tsingjyujing.geo.element.mutable.GeoPoint

class GeoPointTimeSeries extends ITimeIndexSeq[TimeElement[GeoPoint]] {

    override def getValue(time: Double): TimeElement[GeoPoint] = {
        val indexInfo = query(time)
        if (indexInfo._1 == (-1) && indexInfo._2 == (-1)) {
            throw new RuntimeException("Error while querying value")
        } else if (indexInfo._1 == (-1)) {
            apply(indexInfo._2)
        } else if (indexInfo._2 == (-1)) {
            apply(indexInfo._1)
        } else {
            val dv = apply(indexInfo._2).getValue - apply(indexInfo._1).getValue
            val dt = apply(indexInfo._2).getTick - apply(indexInfo._1).getTick
            new TimeElement[GeoPoint](
                time,
                apply(indexInfo._1).getValue + dv * (time - apply(indexInfo._1).getTick) / dt
            )
        }
    }

    def mileage: Double = this.sliding(2).map(p2 => p2.head.getValue.geoTo(p2.last.getValue)).sum

    def getSpeed(ratio: Double = 1.0D): DoubleTimeSeries = DoubleTimeSeries(this.sliding(2).map(p2 => {
        val ds = p2.last.getValue.geoTo(p2.head.getValue)
        val dt = p2.last.getTick - p2.head.getTick
        new TimeElement[Double]((p2.head.getTick + p2.last.getTick) / 2.0, ds * ratio / dt)
    }))

    private def cleanSliding(cleanFunc: (TimeElement[GeoPoint], TimeElement[GeoPoint]) => Boolean): GeoPointTimeSeries = GeoPointTimeSeries(sliding(2).filter(p2 => cleanFunc(p2.head, p2.last)).map(_.head))

    def cleanOverSpeed(speedLimit: Double): GeoPointTimeSeries = this.cleanSliding(
        (p1, p2) => {
            val ds = p1.getValue geoTo p2.getValue
            val dt = p2.getTick - p1.getTick
            (ds / dt) > speedLimit
        }
    )



}

object GeoPointTimeSeries {
    def apply(data: TraversableOnce[TimeElement[GeoPoint]]): GeoPointTimeSeries = {
        val result = new GeoPointTimeSeries()
        result.append(data)
        result
    }
}
