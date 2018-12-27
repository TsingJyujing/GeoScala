package com.github.tsingjyujing.geo;

import com.github.tsingjyujing.geo.basic.IGeoPoint;
import com.github.tsingjyujing.geo.basic.IGeoPoint$;
import com.github.tsingjyujing.geo.element.GeoPointTimeSeries;
import com.github.tsingjyujing.geo.element.GeoPointTimeSeries$;
import com.github.tsingjyujing.geo.element.GeoPolygon;
import com.github.tsingjyujing.geo.element.GeoPolygon$;
import com.github.tsingjyujing.geo.element.immutable.TimeElement;
import com.github.tsingjyujing.geo.element.mutable.GeoPoint$;
import com.google.common.collect.Lists;
import scala.collection.JavaConverters;

import java.util.ArrayList;

/**
 * Java Examples
 */
public class JavaExamples {

    public static void polygonExample() {
        final GeoPolygon polygon = GeoPolygon$.MODULE$.apply(
                JavaConverters.iterableAsScalaIterableConverter(
                        // Put your polygon info in this list
                        new ArrayList<IGeoPoint>()
                ).asScala()
        );

        boolean result = polygon.contains(
                // Point need to query
                GeoPoint$.MODULE$.apply(121, 30)
        );
        System.out.printf("Point is %sin the polygon", result ? "" : "not ");
    }

    public static void geoTimeSeriesExample() {
        final GeoPointTimeSeries ts = GeoPointTimeSeries$.MODULE$.apply(
                JavaConverters.iterableAsScalaIterableConverter(
                        Lists.newArrayList(
                                new TimeElement<IGeoPoint>(
                                        100,
                                        IGeoPoint$.MODULE$.apply(
                                                121,
                                                30
                                        )
                                ),
                                new TimeElement<IGeoPoint>(
                                        105,
                                        IGeoPoint$.MODULE$.apply(
                                                121,
                                                30
                                        )
                                )
                        )
                ).asScala()
        );

        TimeElement<IGeoPoint> unit = new TimeElement<>(
                115,
                IGeoPoint$.MODULE$.apply(
                        121,
                        30
                )
        );
    }


    public static void main(String[] args) {
        geoTimeSeriesExample();
    }
}
