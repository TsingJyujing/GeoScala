package tsingjyujing.geo.util.convertor

import java.io.{File, PrintWriter}

import org.scalatest._
import tsingjyujing.geo.element.immutable.GeoPoint

class GeoConvertErrorAnalysis extends FlatSpec with Matchers {
    "BD09" should "Generate error file" in {
        val writer = new PrintWriter(new File("bd09_error.csv"))
        val eps = 0.001
        val T = BD09
        70.0D.to(150, 0.1).foreach(lng => {
            15.0D.to(55, 0.1).foreach(lat => {
                val rawPoint = GeoPoint(lng, lat)
                val transformedPoint = T.inverseTransform(T.transform(rawPoint), eps = eps)
                val err = rawPoint geoTo transformedPoint
                if (err > eps) {
                    val writeString = "%3.6f,%3.6f,%f\n".format(lng, lat, err)
                    writer.write(writeString)
                    println(writeString)
                }
            })
        })
        writer.close()
    }

    "GCJ02" should "Generate error file" in {
        val writer = new PrintWriter(new File("gcj02_error.csv"))
        val eps = 0.001
        val T = GCJ02
        70.0D.to(150, 0.1).foreach(lng => {
            15.0D.to(55, 0.1).foreach(lat => {
                val rawPoint = GeoPoint(lng, lat)
                val transformedPoint = T.inverseTransform(T.transform(rawPoint), eps = eps)
                val err = rawPoint geoTo transformedPoint
                if (err > eps) {
                    val writeString = "%3.6f,%3.6f,%f\n".format(lng, lat, err)
                    writer.write(writeString)
                    println(writeString)
                }
            })
        })
        writer.close()
    }
}
