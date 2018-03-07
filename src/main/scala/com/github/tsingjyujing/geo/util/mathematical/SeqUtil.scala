package com.github.tsingjyujing.geo.util.mathematical

import com.github.tsingjyujing.geo.element.immutable.Vector2

import scala.collection.mutable
import scala.util.control.Breaks

/**
  * Common methods process
  *
  * @author tsingjyujing
  */
object SeqUtil {

    /**
      * Search index in sorted
      * @param f from index to value
      * @param targetValue the value aim to
      * @param indexStart index start to search
      * @param indexEnd index end to search
      * @return
      */
    def searchInSorted(f: Int => Double,
                       targetValue: Double,
                       indexStart: Int,
                       indexEnd: Int
                      ): (Int, Int) = if (f(indexStart) >= targetValue) {
        (indexStart, indexStart)
    } else if (f(indexEnd) <= targetValue) {
        (indexEnd, indexEnd)
    } else {
        var upperBound = indexEnd
        var lowerBound = indexStart
        var guessPoint = (indexEnd + indexStart) / 2
        val maxIterSize = math.floor(math.log(indexEnd - indexStart) / math.log(2) + 3.0).toInt
        val loop = new Breaks
        loop.breakable {
            for (_ <- 0 to maxIterSize) {
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

    /**
      * Split data into segments
      * @param objs split data by function
      * @param func data in class or not
      * @tparam T type to return
      * @return
      */
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
            } else if (currentResult && (!lastResult)) {
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

    /**
      * Get mean value
      * @param values
      * @return
      */
    def getMean(values: TraversableOnce[Double]): Double = {
        val result = values.map(x => Vector2(x, 1.0)).reduce(_ + _)
        result.getX / result.getY
    }
}
