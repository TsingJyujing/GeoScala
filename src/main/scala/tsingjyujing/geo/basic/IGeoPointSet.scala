package tsingjyujing.geo.basic

trait IGeoPointSet[T <: IGeoPoint] {
    def getPoints: Iterable[T]

    def geoNear(point: IGeoPoint, minDistance: Double = 0.0, maxDistance: Double = 1.0): T


}
