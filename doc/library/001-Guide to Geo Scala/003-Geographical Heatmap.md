# Geographical Heatmap
## Introduction
Heatmap is is statistical graph of geographical density information.
![](assets/001/20180206-d3edef80.png)  

The algorithm is quiet simple:
1. Split earth into grids;
2. Put point on the earth;
3. Count how many point in each gird.

The grid looks like this:
![](assets/001/20180206-e0510194.png)  

## How to use Heatmap
The type of value you statistic should extends Addable:
```scala
def +(v: T): T
```
Recommend use `com.github.tsingjyujing.geo.element.mutable.DoubleValue` for single value and use Vector2 for two values.

For constructing heatmap, you should indicate an accuracy value and recommend value is 0x10000, you can fix it on different accuracy.

And you have 3 methods to construct a heatmap:

1. construct by points and value:
```scala
def buildFromPoints[T <: Addable[T]](values: Traversable[(IGeoPoint, T)], baseValue: T, accuracy: Long = 0x10000): GeoHeatMap[T]
```

2. construct by hashed-index-code
Notice: **not recommend to use this method unless you know what are you doing and what the program doing.**
```scala
def buildFromCodes[T <: Addable[T]](values: Traversable[(Long, T)], baseValue: T, accuracy: Long): GeoHeatMap[T]
```

3. build from an existed heatmap
Notice: the accuracy of `value: GeoHeatMap[T]` should be larger than current heatmap
```scala
def buildFromMap[T <: Addable[T]](value: GeoHeatMap[T], baseValue: T, accuracy: Long = 0x10000): GeoHeatMap[T]
```
