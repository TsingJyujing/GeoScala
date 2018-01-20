package tsingjyujing.geo.util.convertor

import tsingjyujing.geo.basic.IGeoPoint
import tsingjyujing.geo.basic.operations.GeoTransformable
import tsingjyujing.geo.element.immutable.GeoPoint

/**
  * WGS84坐标系和GCJ02加密坐标系的互相转换
  * GSJ02坐标系适用于大部分的地图，例如高德地图
  */
object GCJ02 extends GeoTransformable {
    /**
      * Encrypt WGS84 location to other format
      *
      * @param x WGS84 position
      * @return
      */
    override def transform(x: IGeoPoint): IGeoPoint = if (needTransform(x)) {
        val a = 6378137.0
        val ee = 0.00669342162296594323
        val dLat = transformLat(x.getLongitude - 105.0, x.getLatitude - 35.0)
        val dLng = transformLon(x.getLongitude - 105.0, x.getLatitude - 35.0)
        val radLat = x.getLatitude / 180.0 * math.Pi
        var magic = math.sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = math.sqrt(magic)
        val dx = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * math.Pi)
        val dy = (dLng * 180.0) / (a / sqrtMagic * math.cos(radLat) * math.Pi)
        new GeoPoint(x.getLongitude + dx, x.getLatitude + dy)
    } else {
        new GeoPoint(x.getLongitude, x.getLatitude)
    }


    /**
      * is point in china and need to transform
      *
      * @param point
      * @return
      */
    def needTransform(point: IGeoPoint): Boolean = point.getLongitude > 72.004 && point.getLongitude < 137.8347 && point.getLatitude > 0.8293 && point.getLatitude < 55.8271

    private def transformLat(x: Double, y: Double): Double = {
        -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y +
            0.2 * math.sqrt(math.abs(x)) + (20.0 * math.sin(6.0 * x * math.Pi) +
            20.0 * math.sin(2.0 * x * math.Pi)) * 2.0 / 3.0 + (20.0 * math.sin(y * math.Pi) +
            40.0 * math.sin(y / 3.0 * math.Pi)) * 2.0 / 3.0 + (160.0 * math.sin(y / 12.0 * math.Pi) +
            320 * math.sin(y * math.Pi / 30.0)) * 2.0 / 3.0
    }

    private def transformLon(x: Double, y: Double): Double = {
        300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y +
            0.1 * math.sqrt(math.abs(x)) + (20.0 * math.sin(6.0 * x * math.Pi) +
            20.0 * math.sin(2.0 * x * math.Pi)) * 2.0 / 3.0 +
            (20.0 * math.sin(x * math.Pi) + 40.0 * math.sin(x / 3.0 * math.Pi)) * 2.0 / 3.0 +
            (150.0 * math.sin(x / 12.0 * math.Pi) + 300.0 * math.sin(x / 30.0 * math.Pi)) * 2.0 / 3.0
    }
}
