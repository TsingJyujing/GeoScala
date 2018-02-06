# Geographical Line

## Introduction
Important trait:`IGeoLine`
It's a data structure to describe a geodesic line on 2d-sphere, you may implement this function:
```scala
def getTerminalPoints: (IGeoPoint, IGeoPoint)
```
This function should return two terminal points on this geodesic line segment.

## After extends this trait you can ...


## 1. Get distance to IGeoPoint
To get distance from a point to nearest point on line, you may use this function:
```scala
def geoTo(point: IGeoPoint): Double
```

## 2. Convert self to GeoJSON Point
See:<a href="https://tools.ietf.org/html/rfc7946#section-3.1.4">LineString</a> for more details about GeoJSON

## Classes extends `IGeoLine`
- `com.github.tsingjyujing.geo.element.mutable.GeoLine`