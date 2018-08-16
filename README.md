
![geo-scala](icon.png)

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
    <scala.version.main>2.10</scala.version.main>
</properties>
```

And set dependency like this:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.tsingjyujing</groupId>
        <artifactId>geo-library</artifactId>
    </dependency>
</dependencies>
```

**Notice** 
Scala 2.11 or newer version is **NOT** available on maven central, you may compile and install on local by executing `mvn clean install`

## TODO
- Create new algorithm k-means++ & k-means || to initialize points with geo-optimized algorithm
- parse GeoJSON object from string
- Now support scala 2.10 only, for 2.11 or higher version should download source and `mvn clean package`

