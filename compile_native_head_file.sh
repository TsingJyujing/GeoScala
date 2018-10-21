#!/usr/bin/env bash
javah -jni -classpath target/classes -d native com.github.tsingjyujing.geo.jni.NativeS2PointIndex
cd native