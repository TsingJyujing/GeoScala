![geo-scala](logo.png)

[![CircleCI](https://circleci.com/gh/TsingJyujing/GeoScala.svg?style=svg)](https://circleci.com/gh/TsingJyujing/GeoScala)


这是一个用Scala（为主）写成的地理数据计算库，包括：
- GPS 球面几何
- GPS 轨迹压缩与热力图
- GPS 点索引
- 其它一些基于球面集合重新实现的机器学习算法

## 文档

可以看 [wiki](https://github.com/TsingJyujing/GeoScala/wiki)。

也可以看 [Scala Doc](https://tsingjyujing.github.io/geo-scala-doc/) 来查看类的细节

## Maven使用

你可以在Maven上使用它

```xml
<dependencies>
    <dependency>
        <groupId>com.github.tsingjyujing</groupId>
        <artifactId>geo-library</artifactId>
        <!--Add the version you prefer here-->
    </dependency>
</dependencies>
```

## 从源码安装

```shell script
mvn clean install \
    -Dscala.version.main=2.13 \
    -Dscala.version.sub=3
```

你可以手动设置Scala版本：

Scala 2.12

```shell script
mvn clean install \
    -Dscala.version.main=2.12 \
    -Dscala.version.sub=12
```

Scala 2.11
```shell script
mvn clean install \
    -Dscala.version.main=2.11 \
    -Dscala.version.sub=12
```