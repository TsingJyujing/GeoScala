![geo-scala](geoscala-logo.png)
# Geo Scala

**Attention: we append 996-ICU licence to standard apache-2.0 licence, please notice that while using this code**

[![CircleCI](https://circleci.com/gh/TsingJyujing/GeoScala.svg?style=svg)](https://circleci.com/gh/TsingJyujing/GeoScala)

A robust geo library written by Java and Scala, contains:
- GPS 2d-spherical geometry
- GPS track compression and heat-map.
- GPS point index
- Machine learning algorithms implemented on sphere

## Documents

See <a href="https://github.com/TsingJyujing/GeoScala/wiki">GeoScala - wiki</a>

See [Scala Doc](https://tsingjyujing.github.io/geo-scala-doc/) for more details about classes.

## Maven

<del>Will deploy in Central Maven Server in the future, but now, use `mvn clean install` to install to local.</del>

Now, you can use it on maven directly:

Set the scala version you wanna to use in properties:

```xml
<properties>
    <scala.version.main>2.11</scala.version.main>
</properties>
```

And set dependency like this:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.tsingjyujing</groupId>
        <artifactId>geo-library</artifactId>
        <!--Add the version you prefer here-->
    </dependency>
</dependencies>
```

**Notice** 
Scala 2.10/2.12 or newer version is **NOT** available on maven central, you may compile and install on local by executing `mvn clean install`

## TODO
- Create new algorithm k-means++ & k-means || to initialize points with geo-optimized algorithm
- parse GeoJSON object from string
- Now support scala 2.11 only, for other scala version you should download source, modify `pom.xml` and `mvn clean package`

