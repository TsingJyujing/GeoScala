package tsingjyujing.geo.scala.basic

import tsingjyujing.geo.scala.basic.operations.{Angleable, InnerProductable, Normable}

/**
  * Heatmap of geo points
  */
class GeoHeatMap extends InnerProductable[GeoHeatMap] with Normable with Angleable[GeoHeatMap] {

    private val data = scala.collection.mutable.Map[Long, Double]()

    def append(k: IHashableGeoPoint, v: Double): Unit = {
        this append(k.indexCode, v)
    }

    def append(k: Long, v: Double): Unit = {
        if (data contains k) {
            data.put(k, v + data(k))
        } else {
            data.put(k, v)
        }
    }

    def remove(key: IHashableGeoPoint): Unit = this remove key.indexCode

    def remove(key: Long): Unit = data remove key

    def apply(key: Long): Double = if (data contains key) {
        data(key)
    } else {
        0.0D
    }

    def apply(key: IHashableGeoPoint): Double = this (key.indexCode)

    def +(heatMap: GeoHeatMap): GeoHeatMap = {
        val mapReturn = new GeoHeatMap()
        val keySet = data.keySet | heatMap.data.keySet
        keySet.foreach(
            key => {
                mapReturn.data.put(key, this (key) + heatMap(key))
            }
        )
        mapReturn
    }

    def +=(heatMap: GeoHeatMap): Unit = heatMap.data.foreach(kv => append(kv._1, kv._2))

    /**
      * Implement inner product by vectorization of sparse-map
      *
      * @param point
      * @return
      */
    override def innerProduct(point: GeoHeatMap): Double = (data.keySet & point.data.keySet).map(k => this (k) * point(k)).sum

    /**
      *
      * @param n
      * @return
      */
    override def norm(n: Double): Double = math.pow(data.map(kv => math.pow(kv._2, n)).sum, 1.0 / n)

    override def norm2: Double = math.sqrt(data.map(kv => kv._2 * kv._2).sum)

    /**
      * Get cosed angle value of this and x
      *
      * @param x compare unit
      * @return
      */
    override def conAngle(x: GeoHeatMap): Double = innerProduct(x) / (x.norm2 * norm2)
}
