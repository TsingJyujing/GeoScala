package com.github.tsingjyujing.geo.algorithm.cluster

import com.github.tsingjyujing.geo.basic.IGeoPoint
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bson.types.ObjectId
import scala.collection.JavaConverters._

class MongoDBScan(
                     val collection: MongoCollection[Document],
                     val searchRadius: Double = 0.5,
                     val isInit: Boolean = false,
                     val isMergeClass: Boolean = false
                 ) {
    if (isInit) {
        collection.createIndex(new Document(MongoDBScan.pointFieldName, "2dsphere"))
        collection.createIndex(new Document(MongoDBScan.classIdFieldName, "hashed"))
    }

    def appendPoint(point: IGeoPoint): Int = if (isMergeClass) {
        appendPointWithMerge(point)
    } else {
        appendPointWithoutMerge(point)
    }

    private def appendPointWithoutMerge(point: IGeoPoint): Int = {

        try {
            val nearPoint = collection.find(
                new Document(MongoDBScan.pointFieldName, MongoDBScan.getGeoSearchCondition(point, searchRadius))
            ).first()
            val classId = nearPoint.getInteger(MongoDBScan.classIdFieldName)
            insertPoint(point, classId)
            classId
        } catch {
            case _: Throwable =>
                val classId = getNewClassId
                insertPoint(point, classId)
                classId
        }
    }

    private def appendPointWithMerge(point: IGeoPoint): Int = {
        val nearCursor = collection.find(
            new Document(MongoDBScan.pointFieldName, MongoDBScan.getGeoSearchCondition(point, searchRadius))
        )
        try {
            val nearestDocument: Set[Integer] = nearCursor.asScala.map(_.getInteger(MongoDBScan.classIdFieldName)).toSet
            if (nearestDocument.size <= 0) {
                throw new RuntimeException("Can't query data")
            } else if (nearestDocument.size == 1) {
                val classId = nearestDocument.head
                insertPoint(point, classId)
                classId
            } else {
                val classId = nearestDocument.min
                collection.updateMany(
                    new Document(MongoDBScan.pointFieldName, MongoDBScan.getGeoSearchCondition(point, searchRadius)),
                    new Document("$set", new Document(MongoDBScan.classIdFieldName, classId))
                )
                insertPoint(point, classId)
                classId
            }
        } catch {
            case _: Throwable =>
                val classId = getNewClassId
                insertPoint(point, classId)
                classId
        }
    }

    private def insertPoint(point: IGeoPoint, classId: Int): ObjectId = {
        val document: Document = new Document(MongoDBScan.classIdFieldName, classId).append(MongoDBScan.pointFieldName, Document.parse(point.toGeoJSONString))
        collection.insertOne(document)
        document.getObjectId("_id")
    }

    private def getNewClassId: Int = {
        if (collection.count() == 0) {
            0
        } else {
            collection.aggregate(
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
