package com.github.tsingjyujing.geo.util

import java.io.{File, PrintWriter}

import com.github.tsingjyujing.geo.algorithm.containers.LabeledPoint
import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.GeoPointTimeSeries
import com.github.tsingjyujing.geo.element.immutable.TimeElement

import scala.io.Source

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


    def readPoints(filename: String): IndexedSeq[IGeoPoint] = {
        Source.fromFile(filename).getLines().flatMap(
            l => try {
                val coordinates = l.replaceAll("\n", "").split(",").map(_.toDouble)
                Some(IGeoPoint(coordinates(0), coordinates(1)))
            } catch {
                case ex: Throwable =>
                    None
            }
        ).toIndexedSeq
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

    def writeGeoPointTimeSeries[T <: IGeoPoint](filename: String, points: GeoPointTimeSeries[T]): Unit = {
        val writer = new PrintWriter(new File(filename))
        try {
            points.foreach(
                point => {
                    writer.write(
                        "%f,%3.6f,%3.6f\n".format(
                            point.getTick,
                            point.getValue.getLongitude,
                            point.getValue.getLatitude
                        )
                    )
                }
            )
        } finally {
            writer.close()
        }
    }


    def readGeoPointTimeSeries(filename: String): GeoPointTimeSeries[IGeoPoint] = {
        GeoPointTimeSeries(
            Source.fromFile(filename).getLines().flatMap(
                l => try {
                    val coordinates = l.replaceAll("\n", "").split(",").map(_.toDouble)
                    Some(TimeElement(
                        coordinates(0),
                        IGeoPoint(coordinates(1), coordinates(2))
                    ))
                } catch {
                    case ex: Throwable =>
                        None
                }
            )
        )
    }


}
