package com.github.tsingjyujing.geo.model

import java.util.{ArrayList => JavaList}

import com.github.tsingjyujing.geo.element.immutable.GeoPoint

/**
  * Entity class to convert GeoJSON(Point) to GeoObject
  */
class GeoJsonPoint {
    var `type`: String = null
    var coordinates: JavaList[Double] = null
    def verify: Boolean = `type` == "Point"

    def getPoint:GeoPoint = {
        assert(verify,"type mismatch")
        assert(coordinates!=null,"")
        assert(coordinates.size()==2,"coordinates size is not 2")
        GeoPoint(coordinates.get(0), coordinates.get(1))
    }
}
