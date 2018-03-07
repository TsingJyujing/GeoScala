package com.github.tsingjyujing.geo.util.mathematical

import com.github.tsingjyujing.geo.basic.IVector2

/**
  * Convex hull algorithms for 2-d points
  *
  * @author tsingjyujing@163.com
  * @since 2.7
  * @version 1.0
  */
object ConvexHull2 {
    def apply(points: IndexedSeq[IVector2]): IndexedSeq[IVector2] = {
        val pointsSorted = points.sortWith((a, b) => if (a.getX != b.getX) {
            a.getX > b.getX
        } else {
            a.getY > b.getY
        })
        val n = pointsSorted.size
        val result = new Array[IVector2](n + 1)
        var cnt: Int = 0
        (0 until (2 * n)).foreach(i => {
            val j = if (i < n) {
                i
            } else {
                2 * n - 1 - i
            }
            while (cnt >= 2 && isNotRightTurn(result(cnt - 2), result(cnt - 1), pointsSorted(j))) {
                cnt -= 1
            }
            result(cnt) = pointsSorted(j)
            cnt += 1
        })
        (0 until (cnt - 1)).map(i => result(i))
    }

    private def isNotRightTurn(a: IVector2, b: IVector2, c: IVector2) = {
        val cross = (a.getX - b.getX) * (c.getY - b.getY) - (a.getY - b.getY) * (c.getX - b.getX)
        val dot = (a.getX - b.getX) * (c.getX - b.getX) + (a.getY - b.getY) * (c.getY - b.getY)
        cross < 0 || cross == 0 && dot <= 0
    }

    private def cross(a: IVector2, b: IVector2, c: IVector2) = (b.getX - a.getX) * (c.getY - a.getY) - (b.getY - a.getY) * (c.getX - a.getX)
}
