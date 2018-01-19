package tsingjyujing.geo.util.convertor

import tsingjyujing.geo.basic.operations.GeoTransformable

object ConvertorFactory {
    def apply(coordinateType:String):GeoTransformable = coordinateType match {
        case "gcj02" => GCJ02
        case "bd09" => BD09
        case _ => WGS84
    }
}
