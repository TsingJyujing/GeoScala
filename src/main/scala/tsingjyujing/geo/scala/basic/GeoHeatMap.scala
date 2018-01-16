package tsingjyujing.geo.scala.basic

/**
  * Heatmap of geo
  */
class GeoHeatMap {

    // TODO this is sumable, inner-productable, normable, angleable
    // TODO revert all points in hashmap

    private val data = scala.collection.mutable.Map[Long, Double]()

    def append(kv: (IHashableGeoPoint, Double)): Unit = {
        this append(kv._1.indexCode, kv._2)
    }

    def append(kv: (Long, Double)): Unit = {
        if (data contains kv._1) {
            data.put(kv._1, kv._2 + data(kv._1))
        } else {
            data.put(kv._1, kv._2)
        }
    }

    def remove(key: IHashableGeoPoint): Unit = this remove key.indexCode

    def remove(key: Long): Unit = data remove key

}
