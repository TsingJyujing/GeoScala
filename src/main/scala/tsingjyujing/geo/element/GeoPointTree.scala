package tsingjyujing.geo.element

import tsingjyujing.geo.basic.{IGeoPoint, IGeoPointSet, IHashableGeoBlock}


class GeoPointTree[T <: IGeoPoint](
                                      currentCode: Long,
                                      currentDepth: Int = 17,
                                      maxDepth: Int = 17
                                  ) extends IGeoPointSet[T] with IHashableGeoBlock {

    private val isLastLevel = currentDepth >= maxDepth

    val next: scala.collection.mutable.Map[IHashableGeoBlock, GeoPointTree[T]] = if (!isLastLevel) {
        new scala.collection.mutable.HashMap[IHashableGeoBlock, GeoPointTree[T]]()
    } else {
        null
    }
    val dataList: scala.collection.mutable.ArrayBuffer[T] = if (!isLastLevel) {
        null
    } else {
        new scala.collection.mutable.ArrayBuffer[T]()
    }

    override def getPoints: Iterable[T] = if (isLastLevel) {
        dataList
    } else {
        next.flatMap(_._2.getPoints)
    }

    override def getGeoHashAccuracy: Long = math.pow(2, currentDepth).toLong

    /**
      * Get a unique indexCode as type T
      *
      * @return
      */
    override def indexCode: Long = currentCode

    // Todo find nearest point in geo-tree
    override def geoNear(point: IGeoPoint, minDistance: Double, maxDistance: Double): T = {
        throw new Exception("Unimplemented function")
    }

}
