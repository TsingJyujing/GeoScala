# Generate a HeatMap

## Introduction 

You may import/copy the class `com.github.tsingjyujing.geo.util.mathematical.Probability` in test code.

```scala
import com.github.tsingjyujing.geo.util.mathematical.Probability.{gaussian => randn, uniform => rand}
import com.github.tsingjyujing.geo.element.immutable.{GeoPoint, Vector2}
import com.github.tsingjyujing.geo.element.mutable.DoubleValue
import com.github.tsingjyujing.geo.element.GeoHeatMap
import com.github.tsingjyujing.geo.util.FileIO

val centerPoint = GeoPoint(108,26)
val randomPoints = (1 to 5000).map(_=>{
    centerPoint + Vector2(randn(0,0.2),randn(0,0.2))
})
val heatMap = GeoHeatMap.buildFromPoints(randomPoints.map(x=>(x,1)),DoubleValue(0),0x10000)
FileIO.writeLabeledPoints(heatMap.getGeoPoints().map(p=>LabeledPoint(p,p.getValue)))
```