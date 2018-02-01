package tsingjyujing.geo.element

import tsingjyujing.geo.basic.{IGeoPoint, IGeoPointSet, IHashableGeoBlock}
import tsingjyujing.geo.element.immutable.HashedGeoBlock


class GeoPointTree[T <: IGeoPoint](
                                      currentDepth: Int = 4,
                                      currentCode: Long = 0,
                                      maxDepth: Int = 17,
                                      depthStep: Int = 3
                                  ) extends IGeoPointSet[T] with IHashableGeoBlock {

    private val isLastLevel = currentDepth >= maxDepth

    val nextLayer: scala.collection.mutable.Map[IHashableGeoBlock, GeoPointTree[T]] = if (!isLastLevel) {
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
        nextLayer.flatMap(_._2.getPoints)
    }

    private val currentAccuracy: Long = math.pow(2, currentDepth).toLong

    override def getGeoHashAccuracy: Long = currentAccuracy

    /**
      * center point of the block for immutable index code and faster performance
      */
    private lazy val centerPoint = IHashableGeoBlock.revertFromCode(indexCode, getGeoHashAccuracy)

    override def getCenterPoint: IGeoPoint = centerPoint

    /**
      * Get a unique indexCode as type T
      *
      * @return
      */
    override def indexCode: Long = currentCode

    /**
      * 草稿：
      * 给每个Block评分，优先在符合条件的，且最近的Block里面找，
      * 找到的符合条件的最近点的distance作为maxDistanceLimit传入下一个Block，
      * 本质上是一个状态机
      * 这样找最快(单核条件下)，但是并行度低
      *
      * 如果只是确定有没有半径内的点，还可以更快一点，存在范围内的Block有点就可以返回True了
      *
      * @param point
      * @param maxDistance
      * @return
      */
    override def geoNear(point: IGeoPoint, maxDistance: Double): Option[T] = if (isLastLevel) {
        val nearestPoint = dataList.minBy(_.geoTo(point))
        if ((nearestPoint geoTo point) <= maxDistance) {
            Option(nearestPoint)
        } else {
            None
        }
    } else {
        val filteredRange = nextLayer.map(b => {
            val blockMinDistance = b._1.getMinDistance(point)
            (b, blockMinDistance)
        }).filter(
            _._2 <= maxDistance
        ).toSeq

        if (filteredRange.isEmpty) {
            None
        } else {
            var namedMaxDistance: Double = maxDistance
            var nearestPoint: Option[T] = None

            filteredRange.sortBy(_._2).foreach(b => {
                // If current block min distance is less than current nearest distance
                if (b._1._1.getMinDistance(point) < namedMaxDistance) {
                    val currentNearest = b._1._2.geoNear(point, maxDistance)
                    if (currentNearest.isDefined) {

                        val isUpdatePoint = if (nearestPoint.isDefined) {
                            (nearestPoint.get geoTo point) > (currentNearest.get geoTo point)
                        } else {
                            true
                        }
                        if (isUpdatePoint) {
                            namedMaxDistance = math.min(currentNearest.get.geoTo(point), namedMaxDistance)
                            nearestPoint = currentNearest
                        }
                    }
                }
            })
            nearestPoint
        }

    }

    override def geoWithin(point: IGeoPoint, minDistance: Double, maxDistance: Double): Iterable[T] = if (isLastLevel) {
        dataList.filter(
            p => {
                val d = p.geoTo(point)
                d >= minDistance && d <= maxDistance
            }
        )
    } else {
        nextLayer.filter(
            b => {
                val blockMinDistance = b._1.getMinDistance(point)
                val blockMaxDistance = b._1.getMaxDistance(point)
                !(maxDistance < blockMinDistance || minDistance > blockMaxDistance)
            }
        ).flatMap(
            x => {
                x._2.geoWithin(point, minDistance, maxDistance)
            }
        )
    }

    override def appendPoint(point: T): Unit = if (isLastLevel) {
        dataList.append(point)
    } else {
        val currentPointHash = HashedGeoBlock(point, getGeoHashAccuracy)
        if (!(nextLayer contains currentPointHash)) {
            nextLayer(currentPointHash) = new GeoPointTree[T](
                currentDepth = currentDepth + depthStep,
                currentCode = currentPointHash.indexCode,
                maxDepth = maxDepth,
                depthStep = depthStep
            )
        }
        nextLayer(currentPointHash).appendPoint(point)
    }
}
