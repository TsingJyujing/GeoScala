package com.github.tsingjyujing.geo.util.mathematical;

import com.github.tsingjyujing.geo.basic.IGeoPoint;
import scala.collection.IndexedSeq;

/**
 * Polygon common methods
 *
 * @author tsingjyujing@163.com
 * @version 1.0
 * @since 2.0
 */
public class PolygonUtil {

    /**
     * Ray-Casting algorithm: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     *
     * @param point   point to judge
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
            final double xi = polygon.apply(i).getLongitude(), yi = polygon.apply(i).getLatitude();
            final double xj = polygon.apply(j).getLongitude(), yj = polygon.apply(j).getLatitude();
            final boolean intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }
}
