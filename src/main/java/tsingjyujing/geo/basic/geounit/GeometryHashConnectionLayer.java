package tsingjyujing.geo.basic.geounit;

import com.google.common.collect.Sets;
import tsingjyujing.geo.basic.timeseries.BaseTimeSeries;
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
        long HashCode = point.geoHashCode(accuracy);
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
        List<GeometryPoint<T>> returnValue = new ArrayList<GeometryPoint<T>>();
        double searchMinInnerProduct = Math.cos(range / GeometryPoint.EARTH_RADIUS);
        List<Long> searchRange = new ArrayList<Long>();
        Long thisHash = centerPoint.geoHashCode(accuracy);

        for (Long geoHashCode : data.keySet()) {
            if (thisHash.equals(geoHashCode)) {
                //Search the block which center point in
                searchRange.add(geoHashCode);
            } else {
                // If block has some part in the range we should add on
                double maxInnerProduct = centerPoint.maxInnerProduct(
                        centerPoint.geometryHashBlockBoundary(
                                geoHashCode, accuracy, centerPoint
                        )
                );
                if (maxInnerProduct >= searchMinInnerProduct) {
                    searchRange.add(geoHashCode);
                }
            }
        }
        //Process the selected blocks
        for (Long geoHashCode : searchRange) {
            returnValue.addAll(searchInRange(geoHashCode, centerPoint, range));
        }
        return returnValue;
    }

    /**
     * @param geoHashCode The block key
     * @param centerPoint as name
     * @param radius      as name
     * @return
     */
    public List<GeometryPoint<T>> searchInRange(
            Long geoHashCode,
            GeometryPoint<T> centerPoint,
            double radius
    ) {
        if (data.containsKey(geoHashCode)) {
            // If contains the key
            return data.get(geoHashCode).searchInRange(centerPoint, radius);
        } else {
            return new ArrayList<>();
        }
    }

    public GeometryPoint<T> searchNearestPoint(GeometryPoint<T> centerPoint) throws Exception {
        return searchNearestPoint(centerPoint, MAGIC_MAX_DISTANCE);
    }

    public GeometryPoint<T> searchNearestPoint(
            GeometryPoint<T> centerPoint,
            double maxDistanceTolerance
    ) throws Exception {
        if (this.pointsCount <= 0) {
            throw new Exception("No point in the map.");
        }

        double minInnerProductTolerance = Math.cos(
                maxDistanceTolerance / GeometryPoint.EARTH_RADIUS
        );//Max inner product of target vectors

        BaseTimeSeries<Long> searchRange = new BaseTimeSeries<Long>() {
        };
        Long thisHash = centerPoint.geoHashCode(accuracy);

        for (Long geoHashCode : data.keySet()) {
            if (thisHash.equals(geoHashCode)) {
                //Search the block which center point in
                searchRange.append(new TimeUnit<>(1.0D, geoHashCode));
            } else {
                double minInnerProduct = centerPoint.maxInnerProduct(
                        centerPoint.geometryHashBlockBoundary(
                                geoHashCode, accuracy, centerPoint
                        )
                );
                if (minInnerProduct > minInnerProductTolerance) {
                    searchRange.append(new TimeUnit<>(minInnerProduct, geoHashCode));
                }
            }
        }

        if (searchRange.size() == 0) {
            throw new Exception("No point found in given range.");
        }

        // Descend sort by inner product
        searchRange.sort(true);

        double searchRangeInnerProduct = centerPoint.minInnerProduct(
                centerPoint.geometryHashBlockBoundary(
                        searchRange.get(0).value,
                        accuracy,
                        centerPoint
                )
        );//Find the farthest point in the nearest block

        int maxIndex = searchRange.size();
        for (int i = 0; i < searchRange.size(); ++i) {
            if (searchRange.get(i).time < searchRangeInnerProduct) {
                maxIndex = i + 1;
                break;
            }
        }

        // Search every block for nearest point
        double maxInnerProduct = -1.0D;
        GeometryPoint<T> returnPoint = new GeometryPoint<>(0, 0);
        for (int i = 0; i < maxIndex; ++i) {
            GeometryPoint<T> thisPoint = data.get(searchRange.get(i).value).
                    searchNearestPoint(centerPoint, maxDistanceTolerance);

            double thisPointIP = thisPoint.getInnerProduct(centerPoint);
            if (thisPointIP > maxInnerProduct) {
                maxInnerProduct = thisPointIP;
                returnPoint = thisPoint;
            }
        }

        if (maxInnerProduct <= -1.0D) {
            throw new Exception("No point found in given range.");
        } else {
            return returnPoint;
        }
    }

    /**
     * @param centerPoint
     * @return min distance to centerPoint, MAGIC_MAX_DISTANCE if not found in map
     */
    public double searchNearestDistance(GeometryPoint<T> centerPoint) {
        try {
            return searchNearestPoint(centerPoint).distance(centerPoint);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

    /**
     * @param centerPoint
     * @param maxRadius   search only in range
     * @return min distance to centerPoint, MAGIC_MAX_DISTANCE if not found in map
     */
    public double searchNearestDistance(
            GeometryPoint<T> centerPoint,
            double maxRadius
    ) {
        try {
            return centerPoint.distance(searchNearestPoint(centerPoint, maxRadius));
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

    /**
     * @param centerPoint
     * @param geoHashCode
     * @return min distance to centerPoint which in given hashcode, MAGIC_MAX_DISTANCE if not found in block
     */
    public double searchNearestDistance(
            GeometryPoint<T> centerPoint,
            long geoHashCode
    ) {
        try {
            return data.get(geoHashCode).searchNearestPoint(centerPoint).distance(centerPoint);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }
}
