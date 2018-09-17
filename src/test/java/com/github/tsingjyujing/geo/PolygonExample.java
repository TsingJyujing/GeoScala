package com.github.tsingjyujing.geo;

import com.github.tsingjyujing.geo.basic.IGeoPoint;
import com.github.tsingjyujing.geo.element.GeoPolygon;
import com.github.tsingjyujing.geo.element.mutable.GeoPoint$;
import scala.collection.JavaConverters;

import java.util.ArrayList;

/**
 * Polygon example
 */
public class PolygonExample {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final GeoPolygon polygon = GeoPolygon.apply(
                JavaConverters.asScalaIterableConverter(
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
}
