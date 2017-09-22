package tsingjyujing.geo.basic.geounit;

import tsingjyujing.geo.basic.timeseries.ATimerSeries;
import tsingjyujing.geo.basic.timeseries.TimeUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class GeometryHashConnectionLayer<T> implements java.io.Serializable {
    //Only valid on the planet smaller than earth and ball-shape
    private static final double MAGIC_MAX_DISTANCE = 6500.00 * Math.PI;
    private TreeMap<Long, GeometryHashFinalLayer<T>> data;
    private long accuracy = 256;
    private long next_accuracy = 32768;
    private int points_count = 0;

    /**
     * create a default size map
     */
    public GeometryHashConnectionLayer() {
        data = new TreeMap<Long, GeometryHashFinalLayer<T>>();
    }

    /**
     * @param accuracy_conn  connection layer map size
     * @param accuracy_final final layer map size
     */
    public GeometryHashConnectionLayer(long accuracy_conn, long accuracy_final) {
        accuracy = accuracy_conn;
        next_accuracy = accuracy_final;
        data = new TreeMap<Long, GeometryHashFinalLayer<T>>();
    }

    /**
     * Insert a point in map
     *
     * @param point
     */
    public void insert(GeometryPoint<T> point) {
        long HashCode = point.geohashcode(accuracy);
        if (!data.containsKey(HashCode)) {
            data.put(HashCode, new GeometryHashFinalLayer<T>(next_accuracy));
        }
        data.get(HashCode).insert(point);
        points_count++;
    }

    /**
     * Insert points in map
     *
     * @param points
     */
    public void insert(List<GeometryPoint<T>> points) {
        for (GeometryPoint<T> point : points) {
            insert(point);
        }
    }

    /**
     * @param centerPoint Center point position
     * @param range       search radius
     * @return the points list in rad
     */
    public List<GeometryPoint<T>> searchInRange(GeometryPoint<T> centerPoint, double range) {
        List<GeometryPoint<T>> return_value = new ArrayList<GeometryPoint<T>>();
        double search_min_ip = Math.cos(range / GeometryPoint.EARTH_RADIUS);
        List<Long> search_range = new ArrayList<Long>();
        Long thisHash = centerPoint.geohashcode(accuracy);

        for (Long HashCode : data.keySet()) {
            if (thisHash.equals(HashCode)) {
                //Search the block which center point in
                search_range.add(HashCode);
            } else {
                // If block has some part in the range we should add on
                double maxip = centerPoint.maxInnerProduct(
                        centerPoint.geometryHashBlockBoundary(
                                HashCode, accuracy, centerPoint
                        )
                );
                if (maxip >= search_min_ip) {
                    search_range.add(HashCode);
                }
            }
        }
        //Process the selected blocks
        for (Long HashCode : search_range) {
            return_value.addAll(searchInRange(HashCode, centerPoint, range));
        }
        return return_value;
    }

    /**
     * @param HashCode    The block key
     * @param centerPoint as name
     * @param radius      as name
     * @return
     */
    public List<GeometryPoint<T>> searchInRange(
            Long HashCode,
            GeometryPoint<T> centerPoint,
            double radius
    ) {
        if (data.containsKey(HashCode)) {
            // If contains the key
            return data.get(HashCode).searchInRange(centerPoint, radius);
        } else {
            return new ArrayList<GeometryPoint<T>>();
        }
    }

    public GeometryPoint<T> searchNearestPoint(GeometryPoint<T> center_point) throws Exception {
        return searchNearestPoint(center_point, MAGIC_MAX_DISTANCE);
    }

    public GeometryPoint<T> searchNearestPoint(
            GeometryPoint<T> center_point,
            double maxDistanceTolerance
    ) throws Exception {
        if (this.points_count <= 0) {
            throw new Exception("No point in the map.");
        }

        double minIPTorlerance = Math.cos(
                maxDistanceTolerance / GeometryPoint.EARTH_RADIUS
        );//Max inner productor of target vectors

        ATimerSeries<Long> searchRange = new ATimerSeries<Long>() {
        };
        Long thisHash = center_point.geohashcode(accuracy);

        for (Long HashCode : data.keySet()) {
            if (thisHash.equals(HashCode)) {
                //Search the block which center point in
                searchRange.append(new TimeUnit<Long>(1.0D, HashCode));
            } else {
                double min_ip = center_point.maxInnerProduct(
                        center_point.geometryHashBlockBoundary(
                                HashCode, accuracy, center_point
                        )
                );
                if (min_ip > minIPTorlerance) {
                    searchRange.append(new TimeUnit<Long>(min_ip, HashCode));
                }
            }
        }

        if (searchRange.size() == 0) {
            throw new Exception("No point found in given range.");
        }

        searchRange.sort(true);// Descend sort by inner productor

        double search_range_ip = center_point.minInnerProduct(
                center_point.geometryHashBlockBoundary(
                        searchRange.get(0).value,
                        accuracy,
                        center_point
                )
        );//Find the farthest point in the nearest block

        int maxIndex = searchRange.size();
        for (int i = 0; i < searchRange.size(); ++i) {
            if (searchRange.get(i).time < search_range_ip) {
                maxIndex = i + 1;
                break;
            }
        }

        // Search every block for nearest point
        double maxIP = -1.0D;
        GeometryPoint<T> return_point = new GeometryPoint<T>(0, 0);
        for (int i = 0; i < maxIndex; ++i) {
            GeometryPoint<T> thisPoint = data.get(searchRange.get(i).value).
                    searchNearestPoint(center_point, maxDistanceTolerance);

            double thisPointIP = thisPoint.getInnerProduct(center_point);
            if (thisPointIP > maxIP) {
                maxIP = thisPointIP;
                return_point = thisPoint;
            }
        }

        if (maxIP <= -1.0D) {
            throw new Exception("No point found in given range.");
        } else {
            return return_point;
        }
    }

    /**
     * @param center_point
     * @return min distance to center_point, MAGIC_MAX_DISTANCE if not found in map
     */
    public double searchNearestDistance(GeometryPoint<T> center_point) {
        try {
            return searchNearestPoint(center_point).distance(center_point);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

    /**
     * @param center_point
     * @param max_radius   search only in range
     * @return min distance to center_point, MAGIC_MAX_DISTANCE if not found in map
     */
    public double searchNearestDistance(
            GeometryPoint<T> center_point,
            double max_radius
    ) {
        try {
            return center_point.distance(searchNearestPoint(center_point, max_radius));
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

    /**
     * @param center_point
     * @param HashCode
     * @return min distance to center_point which in given hashcode, MAGIC_MAX_DISTANCE if not found in block
     */
    public double searchNearestDistance(
            GeometryPoint<T> center_point,
            long HashCode
    ) {
        try {
            return data.get(HashCode).searchNearestPoint(center_point).distance(center_point);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }
}
