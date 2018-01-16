package tsingjyujing.geo.scala.basic

trait IHashedGeoPoint extends IGeoPoint{
    def getAccuracy:Long

    def getGeoHash:Long = {

    }

    def getBoundaryPoints:Iterable[IGeoPoint] // TODO implement in this trait
}
