package tsingjyujing.geo.element

import tsingjyujing.geo.basic.{IGeoPoint, IHashableGeoBlock}
import tsingjyujing.geo.basic.operations.{Angleable, InnerProductable, Normable}
import tsingjyujing.geo.element.mutable.DoubleValue

/**
  * Heatmap of geo points
  */
class GeoHeatMap(val accuracyOver: Long = 0x10000) extends GeoHeatMapCommon[DoubleValue](new DoubleValue(0.0), accuracyOver)
    with InnerProductable[GeoHeatMap]
    with Normable
    with Angleable[GeoHeatMap] {


    /**
      * Implement inner product by vectorization of sparse-map
      *
      * @param point
      * @return
      */
    override def innerProduct(point: GeoHeatMap): Double = if (accuracy == point.accuracy) {
        (data.keySet & point.data.keySet).map(k => this (k) * point(k)).map(_.value).sum
    } else {
        throw new RuntimeException("Accuracy not same")
    }

    /**
      *
      * @param n
      * @return
      */
    override def norm(n: Double): Double = math.pow(data.map(kv => math.pow(kv._2.value, n)).sum, 1.0 / n)

    override def norm2: Double = math.sqrt(data.map(kv => kv._2 * kv._2).map(_.value).sum)

    /**
      * Get cosed angle value of this and x
      *
      * @param x compare unit
      * @return
      */
    override def conAngle(x: GeoHeatMap): Double = innerProduct(x) / (x.norm2 * norm2)

}

object GeoHeatMap {

    def buildFromPoints(values: Iterable[(IGeoPoint, Double)], accuracy: Long = 0x10000): GeoHeatMap = {
        val newMap = new GeoHeatMap(accuracy)
        values.groupBy(_._1).map(kv => {
            newMap.data.put(IHashableGeoBlock.createCodeFromGps(kv._1, accuracy), new DoubleValue(kv._2.map(_._2).sum))
        })
        newMap
    }

    def buildFromMap(value: GeoHeatMap, accuracy: Long = 0x10000): GeoHeatMap = {
        val newMap = new GeoHeatMap(accuracy)
        if (value.accuracy == newMap.accuracy) {
            value.foreach(kv => newMap.data.put(kv._1, kv._2))
        } else {
            value.getGeoPoints().foreach(
                x => newMap.appendByCode(IHashableGeoBlock.createCodeFromGps(x, accuracy), x.getValue)
            )
        }
        newMap
    }
}
