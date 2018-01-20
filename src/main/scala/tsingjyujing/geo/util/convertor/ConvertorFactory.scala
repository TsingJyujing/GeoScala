package tsingjyujing.geo.util.convertor

import tsingjyujing.geo.basic.operations.GeoTransformable

/**
  * 转换器工厂方法
  */
object ConvertorFactory {
    /**
      *
      * @param coordinateType type of the coordinate
      * @return
      */
    def apply(coordinateType:String):GeoTransformable = coordinateType.toLowerCase match {
        case "gcj02" => GCJ02
        case "bd09" => BD09
        case _ => WGS84
    }
}