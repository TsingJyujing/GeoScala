# Geo-Scala
A geo library written by Java and Scala, contains GPS 2d-spherical geometry, GPS track compression and heat-map.

## Todo

- Find nearest geo-points in a set of geo-point // Points Tree
- Geo-Frechet algorithm to evaluate similarity of two routes
- Sparsity route by given parameters
- 



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