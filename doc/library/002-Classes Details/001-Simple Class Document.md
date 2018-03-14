# Simple Class Document

## `com.github.tsingjyujing.geo.algorithm`
Signal processing and machine learning algorithms about Geo.

### `com.github.tsingjyujing.geo.algorithm.cluster`
Cluster algorithms, now it has DBScan and K-Means, these algorithms are all optimized with 2-d sphere geometry.

**BaseGeoKMeans** A `trait` which should implemented by adding `def initializePoints(points: Iterable[V], k: Int): Iterable[IGeoPoint]` to initialize center points

**GeoKMeans** Standard K-means algorithm which initialize points on sphere uniformly.

**DBScan** DBScan algorithm running on local.

**MongoDBScan** DBScan by using mongoDB's 2d-sphere index, save result to database in increasing-training easily .

### `com.github.tsingjyujing.geo.algorithm.containers`

Classes to storage results or training samples.

**ClusterResult** Cluster result, samples which be labeled

**LabeledPoint** Point with label

### `com.github.tsingjyujing.geo.algorithm.filter`

Clean GPS point time-series by algorithms. 

**CommonFilter** A `trait` which you should complete `filter` to implement it.

**MeanFilter** Filter GPS points by moving-average algorithm.

## `com.github.tsingjyujing.geo.exceptions`
Exceptions to throw in this library.

**ParameterException** Throw while function get invalid parameter.

## `com.github.tsingjyujing.geo.util`
Utility about file IO and mathematics.

