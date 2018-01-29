package tsingjyujing.geo.util.mathematical

import scala.collection.mutable
import scala.util.control.Breaks

/**
  * @author tsingjyujing
  */
object SeqUtil {
    
    def searchInSorted(f: Int => Double,
                       targetValue: Double,
                       indexStart: Int,
                       indexEnd: Int
                      ): (Int, Int) = if (f(indexStart) >= targetValue) (indexStart, indexStart) else if (f(indexEnd) <= targetValue) (indexEnd, indexEnd) else {
        var upperBound = indexEnd
        var lowerBound = indexStart
        var guessPoint = (indexEnd + indexStart) / 2
        val maxIter = math.floor(math.log(indexEnd - indexStart) / math.log(2) + 3.0).toInt
        val loop = new Breaks
        loop.breakable {
            for (_ <- 0 to maxIter) {
                val currentValue = f(guessPoint)
                if (currentValue > targetValue) upperBound = guessPoint else if (currentValue < targetValue) lowerBound = guessPoint else {
                    upperBound = guessPoint
                    lowerBound = guessPoint
                    loop.break()
                }
                if ((upperBound - lowerBound) <= 1) loop.break() else guessPoint = (upperBound + lowerBound) / 2
                
            }
        }
        (lowerBound, upperBound)
    }
    
    def segmentation[T](objs: Iterable[T], func: T => Boolean): List[List[T]] = {
        val segmentedValues = new mutable.MutableList[List[T]]
        val iter = objs.iterator
        var mutableBuffer = new mutable.MutableList[T]
        var currentValue = iter.next()
        var lastResult = func(currentValue)
        var currentResult = false
        if (lastResult) mutableBuffer += currentValue
        while (iter.hasNext) {
            currentValue = iter.next()
            currentResult = func(currentValue)
            if (currentResult && lastResult) {
                mutableBuffer += currentValue
            }else if (currentResult && (!lastResult)) {
                //Upper
                mutableBuffer = new mutable.MutableList[T]
                mutableBuffer += currentValue
            } else if ((!currentResult) && lastResult) {
                segmentedValues += mutableBuffer.toList
            }
            lastResult = currentResult
        }
        if (lastResult) segmentedValues += mutableBuffer.toList
        segmentedValues.toList
    }
}
