package com.github.tsingjyujing.geo.basic.operations

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.mutable.GeoPoint

/**
  * For Chinese only
  * 注意转换后坐标的逆转换在一些国家和地区是违法的
  * 我愿意对自己的代码承担风险
  * 但是该逆转方法也不能保证全局都能做到无误差，也不保证所有的逆向方法都有效
  * 因为部分加密方法存在一定的信息损失，这些损失不一定能恢复
  */
trait GeoTransformable {

    /**
      *
      * Must override this function to transform position
      * Encrypt WGS84 location to other type of coordinate
      *
      * @param x WGS84 position
      * @return
      */
    def transform(x: IGeoPoint): IGeoPoint

    /**
      * auto reverse transform of the position, need position has a goot ability of local-liner
      *
      * @param y transformed location
      * @return
      */
    def inverseTransformFast(y: IGeoPoint): GeoPoint = {
        val ffx = transform(y)
        GeoPoint(
            y.getLongitude * 2 - ffx.getLongitude,
            y.getLatitude * 2 - ffx.getLatitude
        )
    }

    /**
      * use iter-function method to auto reverse position
      *
      * @param y transformed location
      * @return
      */
    def inverseTransform(y: IGeoPoint, eps: Double = 0.01): IGeoPoint = {
        var errorValue = Double.MaxValue
        var i = 0
        val returnValue = inverseTransformFast(y)
        do {
            val fcx = transform(returnValue)
            errorValue = fcx geoTo y
            returnValue.setLongitude(returnValue.getLongitude - fcx.getLongitude + y.getLongitude)
            returnValue.setLatitude(returnValue.getLatitude - fcx.getLatitude + y.getLatitude)
            i += 1
        } while (errorValue > eps || i >= 32)
        returnValue
    }
}
