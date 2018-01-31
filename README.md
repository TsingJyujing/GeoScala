# Geo-Scala
A geo library written by Java and Scala, contains GPS 2d-spherical geometry, GPS track compression and heat-map.

## Todo

- Geo-Frechet algorithm to evaluate similarity of two routes
- DBScan algorithm on 2d-sphere
- K-means algorithm on 2d-sphere
- Distance matrix algorithm (if have a great linear-algebra library)

## Document

### Entity Objects

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