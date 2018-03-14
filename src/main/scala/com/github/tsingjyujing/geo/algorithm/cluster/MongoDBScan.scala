package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.github.tsingjyujing.geo.element.GeoPolygon
import com.github.tsingjyujing.geo.element.immutable.{GeoPoint, Vector2}
import com.github.tsingjyujing.geo.util.mathematical.ConvexHull2
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bson.types.ObjectId

import scala.collection.JavaConverters._

/**
  * @author tsingjyujing@163.com
  * @param pointCollection   mongodb collection to save points
  * @param polygonCollection mongo collection to save polygons
  * @param searchRadius      db-scan algorithm parameters to search points in radius
  * @param needInit          need to initialize collection while start
  * @param isMergeClass      need to merge class if find more than one class in searchRadius
  * @param updatePolygon     is update polygon automatically while inserted an point
  * @param extendedIndexes   other indexes to set as Map: field name -> index type
  */
class MongoDBScan(
                     val pointCollection: MongoCollection[Document],
                     val polygonCollection: MongoCollection[Document] = null,
                     val searchRadius: Double = 0.5,
                     val needInit: Boolean = false,
                     val isMergeClass: Boolean = false,
                     val updatePolygon: Boolean = false,
                     val extendedIndexes: Map[String, Object] = Map.empty
                 ) {

    private val withPolygon: Boolean = polygonCollection != null

    if (needInit) {
        try {
            pointCollection.createIndex(new Document(MongoDBScan.pointFieldName, "2dsphere"))
            pointCollection.createIndex(new Document(MongoDBScan.classIdFieldName, "hashed"))
            extendedIndexes.foreach(
                kv => {
                    pointCollection.createIndex(new Document(kv._1, kv._2))
                }
            )
        } catch {
            case ex: Throwable =>
                println("Init failed caused by:")
                ex.printStackTrace()
        }
    }

    /**
      * Insert one point into collection
      *
      * @param point      point to insert
      * @param appendInfo other field add to point
      * @return
      */
    def appendPoint(point: IGeoPoint, appendInfo: Document): Int = {
        val classId = if (isMergeClass) {
            appendPointWithMerge(point, appendInfo)
        } else {
            appendPointWithoutMerge(point, appendInfo)
        }
        if (updatePolygon && withPolygon) {
            generatePolygon(classId)
        }
        classId
    }

    /**
      * Insert one point into collection without merge
      *
      * @param point      point to insert
      * @param appendInfo other field add to point
      * @return
      */
    private def appendPointWithoutMerge(point: IGeoPoint, appendInfo: Document): Int = {

        try {
            val nearPoint = pointCollection.find(
                new Document(MongoDBScan.pointFieldName, MongoDBScan.getGeoSearchCondition(point, searchRadius))
            ).first()
            val classId = nearPoint.getInteger(MongoDBScan.classIdFieldName)
            insertPoint(point, classId, appendInfo)
            classId
        } catch {
            case _: Throwable =>
                val classId = getNewClassId
                insertPoint(point, classId, appendInfo)
                classId
        }
    }

    /**
      * Insert one point into collection with merge
      *
      * @param point      point to insert
      * @param appendInfo other field add to point
      * @return
      */
    private def appendPointWithMerge(point: IGeoPoint, appendInfo: Document): Int = {
        val nearestDocument = pointCollection.distinct(
            "classId",
            new Document(MongoDBScan.pointFieldName, MongoDBScan.getGeoSearchCondition(point, searchRadius)),
            classOf[java.lang.Integer]
        ).asScala.toSet

        try {
            if (nearestDocument.size <= 0) {
                throw new RuntimeException("Can't query data")
            } else if (nearestDocument.size == 1) {
                val classId = nearestDocument.head
                insertPoint(point, classId, appendInfo)
                classId
            } else {
                val classId = nearestDocument.min
                pointCollection.updateMany(
                    new Document(MongoDBScan.pointFieldName, MongoDBScan.getGeoSearchCondition(point, searchRadius)),
                    new Document("$set", new Document(MongoDBScan.classIdFieldName, classId))
                )
                insertPoint(point, classId, appendInfo)
                classId
            }
        } catch {
            case _: Throwable =>
                val classId = getNewClassId
                insertPoint(point, classId, appendInfo)
                classId
        }
    }

    /**
      * Insert one point into collection
      *
      * @param point      point to insert
      * @param classId    class of point
      * @param appendInfo other field add to point
      * @return
      */
    private def insertPoint(point: IGeoPoint, classId: Int, appendInfo: Document): ObjectId = {
        val document: Document = new Document(
            appendInfo
        ).append(
            MongoDBScan.classIdFieldName, classId
        ).append(
            MongoDBScan.pointFieldName, Document.parse(point.toGeoJSONString)
        )
        pointCollection.insertOne(document)
        document.getObjectId("_id")
    }

    /**
      * Get an unused class id
      *
      * @return
      */
    private def getNewClassId: Int = {
        if (pointCollection.count() == 0) {
            0
        } else {
            pointCollection.aggregate(
                IndexedSeq(
                    new Document(
                        "$group", new Document(
                            "_id", "id"
                        ).append(
                            "maxId", new Document(
                                "$max", "$" + MongoDBScan.classIdFieldName
                            )
                        )
                    )
                ).asJava
            ).first().getInteger("maxId") + 1
        }
    }


    /**
      * Generate polygon of some class
      *
      * @param classId class id
      */
    def generatePolygon(classId: Int): Unit = if (withPolygon) {

        removePolygon(classId)

        val points: IndexedSeq[Vector2] = pointCollection.find(new Document(MongoDBScan.classIdFieldName, classId)).asScala.map(doc => {
            val point = doc.get(MongoDBScan.pointFieldName, classOf[Document]).get("coordinates", classOf[java.util.ArrayList[Double]])
            Vector2(point.get(0), point.get(1))
        }).toIndexedSeq

        if (points.size >= 3) {
            val convexData = ConvexHull2(points).map(p => {
                GeoPoint(p.getX, p.getY)
            })
            if (convexData.size >= 3) {
                val polygon = GeoPolygon(convexData)
                polygonCollection.insertOne(new Document("_id", classId).append("area", Document.parse(polygon.toGeoJSONString)))
            } else {
                throw new RuntimeException("Less than 3 points got in polygon")
            }
        } else {
            throw new RuntimeException("Less than 3 points got in raw data")
        }
    }

    /**
      * Delete polygon of some class
      *
      * @param classId class id
      */
    def removePolygon(classId: Int): Unit = if (withPolygon) polygonCollection.deleteOne(new Document("_id", classId))

    /**
      * Regenerate all polygons
      */
    def regenerateAllPolygons(): Unit = if (withPolygon) {
        polygonCollection.deleteMany(new Document())
        val classSet = pointCollection.distinct(MongoDBScan.classIdFieldName, classOf[java.lang.Integer]).asScala.toSet
        classSet.foreach(x => try {
            generatePolygon(x.toInt)
        } catch {
            case ex: Throwable =>
                System.err.println("Error while processing polygon of class %d.".format(x.toInt))
                ex.printStackTrace()
        })
    }

}

object MongoDBScan {

    private val classIdFieldName: String = "classId"
    private val pointFieldName: String = "point"

    private def getGeoSearchCondition(point: IGeoPoint, searchRadius: Double): Document = new Document(
        "$nearSphere", new Document(
            "$geometry", Document.parse(point.toGeoJSONString)
        ).append(
            "$minDistance", 0.0
        ).append(
            "$maxDistance", searchRadius * 1000.0
        )
    )


}
