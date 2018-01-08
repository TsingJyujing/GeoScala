package tsingjyujing.geo.basic.timeseries

trait Tickable {
    
    def getTick: Double
    
    def setTick(tick: Double): Unit
}
