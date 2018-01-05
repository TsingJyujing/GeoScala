package tsingjyujing.geo.basic.geounit;

import com.google.common.collect.Sets;
import tsingjyujing.geo.basic.timeseries.ATimerSeries;
import tsingjyujing.geo.basic.timeseries.TimeUnit;

import java.util.*;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class GeometryHashConnectionLayer<T> implements java.io.Serializable {
    // Only valid on the planet smaller than earth and ball-shape
    private static final double MAGIC_MAX_DISTANCE = 6500.00 * Math.PI;
    private Map<Long, GeometryHashFinalLayer<T>> data;

    public long getAccuracy() {
        return accuracy;
    }

    private long accuracy = 256;

    public long getNextAccuracy() {
        return nextAccuracy;
    }

    private long nextAccuracy = 32768;
    private int pointsCount = 0;


    public int insersectSize(GeometryHashConnectionLayer<T> layer) {
        if (accuracy == layer.getAccuracy()) {
            Set<Long> numHash = Sets.newHashSet(data.keySet());
            numHash.retainAll(layer.data.keySet());
            return numHash.size();
        } else {
            throw new RuntimeException("Accuracy not equal.");
        }
    }

    public int unionSize(GeometryHashConnectionLayer<T> layer) {
        if (accuracy == layer.getAccuracy()) {
            Set<Long> denHash = Sets.newHashSet(data.keySet());
            denHash.addAll(layer.data.keySet());
            return denHash.size();
        } else {
            throw new RuntimeException("Accuracy not equal.");
        }
    }


    public double jaccardDistanceFast(GeometryHashConnectionLayer<T> layer) {
        return insersectSize(layer) * 1.0 / unionSize(layer);
    }

    public double jaccardDistance(GeometryHashConnectionLayer<T> layer) {
        Set<Long> numHash = Sets.newHashSet(data.keySet());
        Set<Long> denHash = Sets.newHashSet(data.keySet());
        denHash.addAll(layer.data.keySet());
        numHash.retainAll(layer.data.keySet());
        int num = 0;
        int den = 0;
        for (Long blockHash : denHash) {
            if (numHash.contains(blockHash)) {
                num += data.get(blockHash).insersectSize(layer.data.get(blockHash));
                den += data.get(blockHash).unionSize(layer.data.get(blockHash));
            } else {
                if (data.containsKey(blockHash)) {
                    den += data.get(blockHash).data.size();
                }
                if (layer.data.containsKey(blockHash)) {
                    den += layer.data.get(blockHash).data.size();
                }
            }
        }
        return num * 1.0 / den;
    }

    /**
     * create a default size map
     */
    public GeometryHashConnectionLayer() {
        data = new HashMap<>();
    }

    /**
     * @param accuracyConn  connection layer map size
     * @param accuracyFinal final layer map size
     */
    public GeometryHashConnectionLayer(long accuracyConn, long accuracyFinal) {
        accuracy = accuracyConn;
        nextAccuracy = accuracyFinal;
        data = new HashMap<>();
    }

    /**
     * Insert a point in map
     *
     * @param point
     */
    public void insert(GeometryPoint<T> point) {
        long HashCode = point.geohashcode(accuracy);
        if (!data.containsKey(HashCode)) {
            data.put(HashCode, new GeometryHashFinalLayer<T>(nextAccuracy));
        }
        data.get(HashCode).insert(point);
        pointsCount++;
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
        if (this.pointsCount <= 0) {
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
