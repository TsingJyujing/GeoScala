package com.github.tsingjyujing.geo.util.mathematical;


import com.github.tsingjyujing.geo.basic.IGeoPoint;
import scala.collection.IndexedSeq;


public class PolygonUtil {
    /**
     * Ray-Casting algorithm
     * @param point point to judge
     * @param polygon polygon data
     * @return is on polygon
     */
    public static boolean polygonRayCasting(IGeoPoint point, IndexedSeq<IGeoPoint> polygon) {
        final int pointCount = polygon.size();
        final double x = point.getLongitude();
        final double y = point.getLatitude();
        int i = 0;
        int j = pointCount - 1;
        boolean inside = false;
        for (; i < pointCount; j = i++) {
            double xi = polygon.apply(i).getLongitude(), yi = polygon.apply(i).getLatitude();
            double xj = polygon.apply(j).getLongitude(), yj = polygon.apply(j).getLatitude();

            boolean intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }
}
