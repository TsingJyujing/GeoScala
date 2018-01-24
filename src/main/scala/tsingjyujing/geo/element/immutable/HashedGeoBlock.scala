package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.{IGeoPoint, IHashableGeoBlock}

final class HashedGeoBlock(code: Long, accuracy: Long = 12000) extends IHashableGeoBlock {

    private lazy val centerPoint = IHashableGeoBlock.revertFromCode(indexCode, getGeoHashAccuracy)

    override def getGeoHashAccuracy: Long = accuracy

    /**
      * Get a unique indexCode as type T
      *
      * @return
      */
    override def indexCode: Long = code

    override def getCenterPoint: IGeoPoint = centerPoint
}

object HashedGeoBlock {

    def apply(code: Long, accuracy: Long) = new HashedGeoBlock(code, accuracy)

    def apply(point: IGeoPoint, accuracy: Long = 12000): HashedGeoBlock = apply(IHashableGeoBlock.createCodeFromGps(point, accuracy), accuracy)

    def apply(longitude: Double, latitude: Double, accuracy: Long): HashedGeoBlock = apply(new GeoPoint(longitude, latitude), accuracy)
}
