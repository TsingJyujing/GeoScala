# Geo-Scala
A geo library written by Java and Scala, contains GPS 2d-spherical geometry, GPS track compression and heat-map.

## Todo

- Geo-Frechet algorithm to evaluate similarity of two routes
- Verify correction of polygon
- Support Polygon with ring
- Add others auto tests
- Add documents and wiki

## Document

### Entity Objects

#### Type of points
All the geo point type has extends `com.github.tsingjyujing.geo.basic.IGeoPoint`, to extends this trait, you should implement these functions:
```scala
def getLongitude: Double
def getLatitude: Double
```

### Basic Units

#### Properties: operations

##### Angleable
Which object can get angle in rad (double) between object in type T and self.


##### DistanceMeasurable
Which object can get distance(double) between object in type T and self.
To override `to` method.

##### GeoDistanceMeasurable
Get geodesic distance (double in unit of kilometer) between self and object in type T.
To override `geoTo` method.