package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.{IGeoPoint, IHashableGeoBlock}

/**
  * Get a hashed geo block
  *
  * @param code
  * @param accuracy
  */
final class HashedGeoBlock(code: Long, accuracy: Long = 12000) extends IHashableGeoBlock {

    private val centerPoint = IHashableGeoBlock.revertFromCode(indexCode, getGeoHashAccuracy)

    override def getGeoHashAccuracy: Long = accuracy

    /**
      * Get a unique indexCode as type T
      *
      * @return
      */
    override def indexCode: Long = code

    override def getCenterPoint: IGeoPoint = centerPoint

    override def toString: String = s"code=$code, accuracy=$accuracy"
}

object HashedGeoBlock {

    def apply(code: Long, accuracy: Long) = new HashedGeoBlock(code, accuracy)

    def apply(point: IGeoPoint, accuracy: Long): HashedGeoBlock = apply(IHashableGeoBlock.createCodeFromGps(point, accuracy), accuracy)

    def apply(longitude: Double, latitude: Double, accuracy: Long): HashedGeoBlock = apply(GeoPoint(longitude, latitude), accuracy)
}
