package com.github.tsingjyujing.geo.util

import java.io.{File, PrintWriter}

import com.github.tsingjyujing.geo.algorithm.containers.LabeledPoint
import com.github.tsingjyujing.geo.basic.IGeoPoint

object FileIO {

    def writePoints(filename: String, points: TraversableOnce[IGeoPoint]): Unit = {
        val writer = new PrintWriter(new File(filename))
        try {
            points.foreach(point => {
                writer.write("%3.6f,%3.6f\n".format(point.getLongitude, point.getLatitude))
            })

        } finally {
            writer.close()
        }
    }


    def writeLabeledPoints[K, V <: IGeoPoint](filename: String, points: TraversableOnce[LabeledPoint[K, V]]): Unit = {
        val writer = new PrintWriter(new File(filename))
        try {
            points.foreach(
                point => {
                    writer.write("%s,%3.6f,%3.6f\n".format(point.classId.toString, point.getLongitude, point.getLatitude))
                }
            )
        } finally {
            writer.close()
        }
    }

}
