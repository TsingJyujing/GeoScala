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
public class GeometryHashFinalLayer<T> implements java.io.Serializable {

    Map<Long, List<GeometryPoint<T>>> data = new HashMap<Long, List<GeometryPoint<T>>>();

    private long accuracy = 32768;
    private int pointsCount = 0;


    /**
     * 此程序只有在比地球小的球星星体上有效
     */
    private static final double MAGIC_MAX_DISTANCE = GeometryPoint.EARTH_RADIUS * Math.PI;

    /**
     * Initialization by given parameters
     *
     * @param accuracyInput
     */
    public GeometryHashFinalLayer(long accuracyInput) {
        accuracy = accuracyInput;
    }

    /**
     * Initialization by default parameters
     */
    public GeometryHashFinalLayer() {
    }

    public int intersectSize(GeometryHashFinalLayer<T> layer) {
        if (accuracy == layer.accuracy) {
            Set<Long> numHash = Sets.newHashSet(data.keySet());
            numHash.retainAll(layer.data.keySet());
            return numHash.size();
        } else {
            throw new RuntimeException("Accuracy not equal.");
        }
    }

    public int unionSize(GeometryHashFinalLayer<T> layer) {
        if (accuracy == layer.accuracy) {
            Set<Long> denHash = Sets.newHashSet(data.keySet());
            denHash.addAll(layer.data.keySet());
            return denHash.size();
        } else {
            throw new RuntimeException("Accuracy not equal.");
        }
    }

    /**
     * @param point
     */
    public void insert(GeometryPoint<T> point) {
        Long geoHashCode = point.geoHashCode(accuracy);
        if (!data.containsKey(geoHashCode)) {
            data.put(geoHashCode, new ArrayList<>());
        }
        data.get(geoHashCode).add(point);
        pointsCount++;
    }

    /**
     * Insert a list points
     *
     * @param points
     */
    public void insert(List<GeometryPoint<T>> points) {
        for (int i = 0; i < points.size(); ++i) {
            insert(points.get(i));
        }
    }

    /**
     * Delete points by user info
     *
     * @param userInfo
     * @return points deleted
     */
    public int delete(T userInfo) {
        int returnValue = 0;
        for (Long geoHashCode : data.keySet()) {
            returnValue += delete(geoHashCode, userInfo);
        }
        return returnValue;
    }

    /**
     * @param geoHashCode
     * @param userInfo
     * @return points deleted
     */
    public int delete(Long geoHashCode, T userInfo) {
        int returnvalue = 0;
        if (data.containsKey(geoHashCode)) {
            for (GeometryPoint<T> point : data.get(geoHashCode)) {
                if (point.userInfo.equals(userInfo)) {
                    data.get(geoHashCode).remove(point);
                    returnvalue += 1;
                }
            }
        }
        return returnvalue;
    }

    /**
     * @param point input point object
     * @return points deleted
     */
    public int delete(GeometryPoint<T> point) {
        return delete(point.geoHashCode(accuracy), point.userInfo);
    }

    /**
     * @param centerPoint
     * @param radius
     * @return points searched
     */
    public List<GeometryPoint<T>> searchInRange(GeometryPoint<T> centerPoint, double radius) {

        List<GeometryPoint<T>> returnValue = new ArrayList<GeometryPoint<T>>();
        Long thisHash = centerPoint.geoHashCode(accuracy);
        double searchMinInnerProduct = Math.cos(radius / GeometryPoint.EARTH_RADIUS);

        for (Long geoHashCode : data.keySet()) {
            if (thisHash.equals(geoHashCode)) {
                returnValue.addAll(
                        searchInRange(
                                geoHashCode,
                                centerPoint,
                                radius
                        )
                );
            } else {
                double maxInnerProduct = centerPoint.maxInnerProduct(
                        centerPoint.geometryHashBlockBoundary(
                                geoHashCode,
                                accuracy,
                                centerPoint
                        )
                );

                if (maxInnerProduct >= searchMinInnerProduct) {
                    returnValue.addAll(
                            searchInRange(
                                    geoHashCode,
                                    centerPoint,
                                    radius
                            )
                    );
                }
            }
        }
        return returnValue;
    }

    /**
     * @param hashCode
     * @param centerPoint
     * @param radius
     * @return point in range which searched in block
     */
    public List<GeometryPoint<T>> searchInRange(Long hashCode, GeometryPoint<T> centerPoint, double radius) {
        double searchMinInnerProduct = Math.cos(radius / GeometryPoint.EARTH_RADIUS);
        List<GeometryPoint<T>> returnValue = new ArrayList<GeometryPoint<T>>();

        if (data.containsKey(hashCode)) {
            List<GeometryPoint<T>> compareList = data.get(hashCode);
            for (GeometryPoint<T> point : compareList) {
                if (point.getInnerProduct(centerPoint) >= searchMinInnerProduct) {
                    returnValue.add(point);
                }
            }
        }

        return returnValue;
    }

    /**
     * @param centerPoint
     * @return searched in all blocks
     * @throws Exception exception while can't find a point
     */
    public GeometryPoint<T> searchNearestPoint(GeometryPoint<T> centerPoint) throws Exception {
        return searchNearestPoint(centerPoint, MAGIC_MAX_DISTANCE);
    }

    /**
     * @param centerPoint
     * @param maxDistanceTolerance
     * @return the nearest point
     * @throws Exception point can't find
     */
    public GeometryPoint<T> searchNearestPoint(
            GeometryPoint<T> centerPoint,
            double maxDistanceTolerance
    ) throws Exception {
        if (this.pointsCount <= 0) {
            throw new Exception("No point in the map.");
        }

        double minInnerProductTorlerance = Math.cos(
                maxDistanceTolerance / GeometryPoint.EARTH_RADIUS);

        BaseTimeSeries<Long> searchRange = new BaseTimeSeries<Long>() {
        };
        Long thisHash = centerPoint.geoHashCode(accuracy);

        for (Long geoHashCode : data.keySet()) {
            if (thisHash.equals(geoHashCode)) {
                searchRange.append(new TimeUnit<>(1.0D, geoHashCode));
            } else {
                double maxInnerProduct = centerPoint.maxInnerProduct(
                        centerPoint.geometryHashBlockBoundary(
                                geoHashCode,
                                accuracy,
                                centerPoint
                        )
                );
                if (maxInnerProduct > minInnerProductTorlerance) {
                    searchRange.append(new TimeUnit<>(maxInnerProduct, geoHashCode));
                }
            }
        }

        if (searchRange.size() == 0) {
            throw new Exception("No point found in map.");
        }

        searchRange.sort(true);

        double searchRangeInnerProduct = centerPoint.minInnerProduct(
                centerPoint.geometryHashBlockBoundary(
                        searchRange.get(0).getValue(),
                        accuracy,
                        centerPoint
                )
        );

        int maxIndex = 1;
        for (int i = 0; i < searchRange.size(); ++i) {
            if (searchRange.get(i).getTick() < searchRangeInnerProduct) {
                maxIndex = i;
                break;
            }
        }

        double maxInnerProduct = -1.0D;
        GeometryPoint<T> returnPoint = new GeometryPoint<>(0, 0);
        for (int i = 0; i < maxIndex; ++i) {
            GeometryPoint<T> currentPoint = searchNearestPoint(
                    searchRange.get(i).getValue(),
                    centerPoint);
            double ip = currentPoint.getInnerProduct(centerPoint);
            if (ip > maxInnerProduct) {
                returnPoint = currentPoint;
                maxInnerProduct = ip;
            }
        }
        if (maxInnerProduct <= -1.0D) {
            throw new Exception("No point found in map.");
        } else {
            return returnPoint;
        }
    }

    /**
     * @param geoHashCode
     * @param centerPoint
     * @return nearest point
     * @throws Exception can't find
     */
    public GeometryPoint<T> searchNearestPoint(Long geoHashCode, GeometryPoint centerPoint) throws Exception {
        if (!data.containsKey(geoHashCode)) {
            throw new Exception("No point found in given block.");
        }
        return centerPoint.nearestPoint(
                data.get(
                        geoHashCode
                )
        );
    }

    /**
     * @param centerPoint
     * @return
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
     * @param maxRadius
     * @return
     */
    public double searchNearestDistance(GeometryPoint<T> centerPoint, double maxRadius) {
        try {
            return searchNearestPoint(centerPoint, maxRadius).distance(centerPoint);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

    /**
     * @param centerPoint
     * @param geoHashCode
     * @return
     */
    public double searchNearestDistance(GeometryPoint<T> centerPoint, Long geoHashCode) {
        try {
            return searchNearestPoint(geoHashCode, centerPoint).distance(centerPoint);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

}
