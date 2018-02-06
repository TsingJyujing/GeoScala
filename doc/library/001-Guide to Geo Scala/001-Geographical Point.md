# Geographical Point

## Introduction
Important trait:`IGeoPoint`
All the geo point type has extends `com.github.tsingjyujing.geo.basic.IGeoPoint`, to extends this trait, you should implement these functions:
```scala
def getLongitude: Double
def getLatitude: Double
```
After implements these functions, you can:

## 1. Get distance of two IGeoPoint
For get geodesic distance for any two points on earth, you may use `geoTo` operator.
The define of `geoTo` is:
```scala
def geoTo(point: IGeoPoint): Double
```
The return value in unit of kilometer.

## 2. Convert self to GeoJSON Point
See:<a href="https://tools.ietf.org/html/rfc7946#section-3.1.2">Point</a> for more details about GeoJSON


## 3. Convert to Vectors
### 3.1 Convert to Vector2
Use Mercator coordinates to describe point, getX AKA getLongitude and getY AKA getAltitude.

### 3.2 Convert to Vector3
As we known, GPS point can be described as a 3d point on a 2d sphere like manifold. 
So we can convert 2-d described (longitude and latitude) into 3d position.

### 4 Do basic `+` and `-` operations to `IGeoPoint` or `IVector2`
- Two geo points do minus operation will get a `IVector2`
- A geo point minus or add a `IVector2` will get another `IGeoPoint`
- Can't add two `IGeoPoint`.

## Classes extends IGeoPoint in this library
- `com.github.tsingjyujing.geo.element.mutable.GeoPoint`
- `com.github.tsingjyujing.geo.element.immutable.GeoPoint`
- `com.github.tsingjyujing.geo.element.immutable.GeoPointValued`
- `com.github.tsingjyujing.geo.element.immutable.HashedGeoBlock`(because `com.github.tsingjyujing.geo.element.basic.IHashableGeoBlock` extends `IGeoPoint`)
