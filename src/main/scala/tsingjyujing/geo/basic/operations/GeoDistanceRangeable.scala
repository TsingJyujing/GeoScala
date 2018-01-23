package tsingjyujing.geo.basic.operations

trait GeoDistanceRangeable[T] {
    def getMinDistance(x:T):Double
    def getMaxDistance(x:T):Double
}
