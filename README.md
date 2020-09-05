![geo-scala](logo.png)

[![CircleCI](https://circleci.com/gh/TsingJyujing/GeoScala.svg?style=svg)](https://circleci.com/gh/TsingJyujing/GeoScala)

[中文](README_ZH.md)

A robust geo library written by Java and Scala, contains:
- GPS 2d-spherical geometry
- GPS track compression and heat-map.
- GPS point index
- Machine learning algorithms implemented on sphere

## Documents

See <a href="https://github.com/TsingJyujing/GeoScala/wiki">GeoScala - wiki</a>

See [Scala Doc](https://tsingjyujing.github.io/geo-scala-doc/) for more details about classes.

## Maven

Now, you can use it on maven directly:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.tsingjyujing</groupId>
        <artifactId>geo-library</artifactId>
        <!--Add the version you prefer here-->
    </dependency>
</dependencies>
```

## Build From Source

```shell script
mvn clean install \
    -Dscala.version.main=2.12 \
    -Dscala.version.sub=12
```

You can set scala version manually like:

Scala 2.11
```shell script
mvn clean install \
    -Dscala.version.main=2.11 \
    -Dscala.version.sub=12
```