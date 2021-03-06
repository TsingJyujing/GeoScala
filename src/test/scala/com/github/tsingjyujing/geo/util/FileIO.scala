package com.github.tsingjyujing.geo.util

import java.io.{File, PrintWriter}

import com.github.tsingjyujing.geo.algorithm.containers.LabeledPoint
import com.github.tsingjyujing.geo.basic.IGeoPoint

/**
  * Basic File input output utility
  */
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

    def writePoints3D(filename: String, points: TraversableOnce[IGeoPoint]): Unit = {
        val writer = new PrintWriter(new File(filename))
        try {
            points.foreach(point => {
                val v = point.toIVector3
                writer.write("%f,%f,%f\n".format(v.getX, v.getY, v.getZ))
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

    def writeString(filename: String, data: String): Unit = {
        val writer = new PrintWriter(new File(filename))
        try {
            writer.write(data)
        } finally {
            writer.close()
        }
    }

}
