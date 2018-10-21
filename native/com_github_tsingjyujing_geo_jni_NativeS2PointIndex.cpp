//
// Created by yuanyifan on 18-10-21.
//

#include <s2/s2point_index.h>
#include <s2/s2latlng.h>
#include <s2/s2closest_point_query.h>
#include <s2/s2earth.h>

#include "com_github_tsingjyujing_geo_jni_NativeS2PointIndex.h"

#define GPS_COORDINATE_TO_INTEGER_PRODUCT 1000000
// #define __DEBUG_MODE__

jlong
Java_com_github_tsingjyujing_geo_jni_NativeS2PointIndex_newS2Index(JNIEnv *, jclass) {
    return (jlong) (new S2PointIndex<jlong>());
}

void
Java_com_github_tsingjyujing_geo_jni_NativeS2PointIndex_deleteS2Index(JNIEnv *, jclass, jlong s2IndexPtr) {
    delete ((S2PointIndex<jlong> *) s2IndexPtr);
}

void
Java_com_github_tsingjyujing_geo_jni_NativeS2PointIndex_insertPoint(
        JNIEnv *, jclass,
        jlong s2IndexPtr,
        jdouble longitude,
        jdouble latitude,
        jlong value
) {
    ((S2PointIndex<jlong> *) s2IndexPtr)->Add(S2Point(
            S2LatLng::FromDegrees(latitude, longitude)
    ), value);
}


jlongArray
Java_com_github_tsingjyujing_geo_jni_NativeS2PointIndex_queryPointsNative(
        JNIEnv *env, jclass cls,
        jlong s2IndexPtr,
        jdouble longitude,
        jdouble latitude,
        jdouble radius,
        jint pointCountLimit
) {
    S2ClosestPointQuery<jlong> query(
            (S2PointIndex<jlong> *) s2IndexPtr
    );

    query.mutable_options()->set_max_distance(
            S1Angle::Radians(
                    S2Earth::KmToRadians(radius)
            )
    );
    if (pointCountLimit > 0) {
        query.mutable_options()->set_max_results(
                (int) pointCountLimit
        );
    }


    S2ClosestPointQuery<int>::PointTarget target(
            S2LatLng::FromDegrees(latitude, longitude).ToPoint()
    );
    auto results = query.FindClosestPoints(&target);

    unsigned long resultCount = results.size();

#ifdef __DEBUG_MODE__
    printf(
            "Query data for center (%3.6f,%3.6f) in radius %f km, get %ld results.\n",
            longitude,
            latitude,
            radius,
            resultCount
    );
#endif

    jlong result[resultCount * 3] = {0};

    for (unsigned long i = 0; i < resultCount; i++) {
        auto data = results.at(i);
        auto point = S2LatLng();
        auto resultLongitude = S2LatLng::Longitude(data.point()).degrees();
        auto resultLatitude = S2LatLng::Latitude(data.point()).degrees();
        result[i * 3 + 0] = static_cast<jlong>( resultLongitude * GPS_COORDINATE_TO_INTEGER_PRODUCT);
        result[i * 3 + 1] = static_cast<jlong>(resultLatitude * GPS_COORDINATE_TO_INTEGER_PRODUCT);
        result[i * 3 + 2] = data.data();

#ifdef __DEBUG_MODE__
        printf(
                "\t Result %ld: (%3.6f,%3.6f)\n",
                i,
                resultLongitude,
                resultLatitude
        );
#endif
    }

    auto bufferSize = (jsize) (resultCount * 3);
    jlongArray jResult = env->NewLongArray(bufferSize);
    env->ReleaseLongArrayElements(jResult, result, JNI_COMMIT);
    return jResult;
}
