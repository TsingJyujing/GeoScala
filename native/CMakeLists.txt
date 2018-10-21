cmake_minimum_required(VERSION 2.8)

project(geo-scala-native-index)

set(CMAKE_CXX_STANDARD 11)

# Put your include files here
include_directories(
        /usr/lib/jvm/java-8-openjdk-amd64/include
        /usr/lib/jvm/java-8-openjdk-amd64/include/linux
        /usr/local/include
)

link_directories(
        /usr/local/lib
)

add_library(
        GeoScalaPointIndex SHARED
        com_github_tsingjyujing_geo_jni_NativeS2PointIndex.cpp
)


target_link_libraries(
        GeoScalaPointIndex
        s2
)