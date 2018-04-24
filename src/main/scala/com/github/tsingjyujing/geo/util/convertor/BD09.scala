package com.github.tsingjyujing.geo.util.convertor

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.basic.operations.GeoTransformable
import com.github.tsingjyujing.geo.element.immutable.GeoPoint

/**
  * 中国境内的WGS84坐标和Baidu09坐标系的互相转换
  * Baidu09坐标系是在国测局坐标的基础上加了一个三角函数噪声，俗称2B坐标系
  *
  * 还嫌GCJ02丢人丢的不够吗？
  *
  * 经过测试，所有的坐标点都可以完美的和WGS84相互转换
  * @author tsingjyujing@163.com
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
        val radius = math.sqrt(gcjPoint.getLatitude * gcjPoint.getLatitude + gcjPoint.getLongitude * gcjPoint.getLongitude)
        val phase = math.atan2(gcjPoint.getLatitude, gcjPoint.getLongitude)
        val fixedRadius = radius + 2e-5 * math.sin(gcjPoint.getLatitude * math.Pi * 3000 / 180)
        val fixedPhase = phase + 3e-6 * math.cos(gcjPoint.getLongitude * math.Pi * 3000 / 180)
        val turnBackX = fixedRadius * math.cos(fixedPhase) + 0.0065
        val turnBackY = fixedRadius * math.sin(fixedPhase) + 0.006
        GeoPoint(turnBackX, turnBackY)
    } else {
        GeoPoint(x.getLongitude, x.getLatitude)
    }

}
