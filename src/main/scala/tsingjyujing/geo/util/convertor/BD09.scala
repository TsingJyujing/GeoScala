package tsingjyujing.geo.util.convertor

import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.basic.operations.GeoTransformable
import tsingjyujing.geo.element.immutable.GeoPoint

/**
  * 中国境内的WGS84坐标和Baidu09坐标系的互相转换
  * Baidu09坐标系是在国测局坐标的基础上加了一个三角函数噪声
  * 俗称2B坐标系
  */
object BD09 extends GeoTransformable {
    /**
      * Encrypt WGS84 location to other format
      *
      * @param x WGS84 position
      * @return
      */
    override def transform(x: IGeoPoint): IGeoPoint = if (GCJ02.needTransform(x)) {
        val gcjPoint = GCJ02.transform(x)
        val radius = math.sqrt(x.getLatitude * x.getLatitude + x.getLongitude * x.getLongitude)
        val phase = math.atan2(x.getLatitude, x.getLongitude)
        val fixedRadius = radius + 2e-5 * math.sin(gcjPoint.getLatitude * math.Pi * 3000 / 180)
        val fixedPhase = phase + 3e-6 * math.cos(gcjPoint.getLongitude * math.Pi * 3000 / 180)
        val turnBackX = fixedRadius * math.cos(fixedPhase) + 0.0065
        val turnBackY = fixedRadius * math.sin(fixedPhase) + 0.006
        new GeoPoint(turnBackX, turnBackY)
    } else {
        new GeoPoint(x.getLongitude, x.getLatitude)
    }

}
