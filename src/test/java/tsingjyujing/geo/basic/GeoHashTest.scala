package tsingjyujing.geo.basic

import org.scalatest._
import IHashableGeoPoint.{createCodeFromGps, revertFromCode}
import tsingjyujing.geo.element.immutable.GeoPoint

class GeoHashTest extends FlatSpec with Matchers {
    "GeoHashDebugTest" should "RunNormally" in {
        val accuracy = 0x4000000L
        val pointX = new GeoPoint(121.0, 25.6)
        val code = createCodeFromGps(pointX,accuracy)
        val revertPoint = revertFromCode(code,accuracy)
        print(revertPoint)
    }
}
