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

    //此程序只有在比地球小的球星星体上有效
    private static final double MAGIC_MAX_DISTANCE = GeometryPoint.EARTH_RADIUS * Math.PI;

    /**
     * Initialization by given parameters
     *
     * @param accuracy_input
     */
    public GeometryHashFinalLayer(long accuracy_input) {
        accuracy = accuracy_input;
    }

    /**
     * Initialization by default parameters
     */
    public GeometryHashFinalLayer() {
    }

    public int insersectSize(GeometryHashFinalLayer layer) {
        if (accuracy == layer.accuracy) {
            Set<Long> numHash = Sets.newHashSet(data.keySet());
            numHash.retainAll(layer.data.keySet());
            return numHash.size();
        } else {
            throw new RuntimeException("Accuracy not equal.");
        }
    }

    public int unionSize(GeometryHashFinalLayer layer) {
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
        Long HashCode = point.geoHashCode(accuracy);
        if (!data.containsKey(HashCode)) {
            data.put(HashCode, new ArrayList<GeometryPoint<T>>());
        }
        data.get(HashCode).add(point);
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
     * @param user_info
     * @return points deleted
     */
    public int delete(T user_info) {
        int returnvalue = 0;
        for (Long HashCode : data.keySet()) {
            returnvalue += delete(HashCode, user_info);
        }
        return returnvalue;
    }

    /**
     * @param HashCode
     * @param user_info
     * @return points deleted
     */
    public int delete(Long HashCode, T user_info) {
        int returnvalue = 0;
        if (data.containsKey(HashCode)) {
            List<GeometryPoint<T>> search_subset = data.get(HashCode);
            for (GeometryPoint<T> tmp : search_subset) {
                if (tmp.userInfo.equals(user_info)) {
                    data.get(HashCode).remove(tmp);
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
     * @param center_point
     * @param radius
     * @return points searched
     */
    public List<GeometryPoint<T>> searchInRange(GeometryPoint<T> center_point, double radius) {

        List<GeometryPoint<T>> return_value = new ArrayList<GeometryPoint<T>>();
        Long thisHash = center_point.geoHashCode(accuracy);
        double search_min_ip = Math.cos(radius / GeometryPoint.EARTH_RADIUS);

        for (Long HashCode : data.keySet()) {
            if (thisHash.equals(HashCode)) {
                return_value.addAll(
                        search_inrad(
                                HashCode,
                                center_point,
                                radius
                        )
                );
            } else {
                double maxip = center_point.maxInnerProduct(
                        center_point.geometryHashBlockBoundary(
                                HashCode,
                                accuracy,
                                center_point
                        )
                );

                if (maxip >= search_min_ip) {
                    return_value.addAll(
                            search_inrad(
                                    HashCode,
                                    center_point,
                                    radius
                            )
                    );
                }
            }
        }
        return return_value;
    }

    /**
     * @param HashCode
     * @param center_point
     * @param radius
     * @return point in range which searched in block
     */
    public List<GeometryPoint<T>> search_inrad(Long HashCode, GeometryPoint<T> center_point, double radius) {
        double search_min_ip = Math.cos(radius / GeometryPoint.EARTH_RADIUS);
        List<GeometryPoint<T>> return_value = new ArrayList<GeometryPoint<T>>();

        if (data.containsKey(HashCode)) {
            List<GeometryPoint<T>> compare_list = data.get(HashCode);
            for (GeometryPoint<T> pn : compare_list) {
                if (pn.getInnerProduct(center_point) >= search_min_ip) {
                    return_value.add(pn);
                }
            }
        }

        return return_value;
    }

    /**
     * @param center_point
     * @return searched in all blocks
     * @throws Exception exception while can't find a point
     */
    public GeometryPoint<T> searchNearestPoint(GeometryPoint<T> center_point) throws Exception {
        return searchNearestPoint(center_point, MAGIC_MAX_DISTANCE);
    }

    /**
     * @param center_point
     * @param max_distance_tolerance
     * @return the nearest point
     * @throws Exception point can't find
     */
    public GeometryPoint<T> searchNearestPoint(
            GeometryPoint<T> center_point,
            double max_distance_tolerance
    ) throws Exception {
        if (this.pointsCount <= 0) {
            throw new Exception("No point in the map.");
        }

        double min_ip_torlerance = Math.cos(
                max_distance_tolerance / GeometryPoint.EARTH_RADIUS);

        BaseTimeSeries<Long> search_range = new BaseTimeSeries<Long>() {
        };
        Long thisHash = center_point.geoHashCode(accuracy);

        for (Long HashCode : data.keySet()) {
            if (thisHash.equals(HashCode)) {
                search_range.append(new TimeUnit<Long>(1.0D, HashCode));
            } else {
                double max_ip = center_point.maxInnerProduct(
                        center_point.geometryHashBlockBoundary(
                                HashCode,
                                accuracy,
                                center_point
                        )
                );
                if (max_ip > min_ip_torlerance) {
                    search_range.append(new TimeUnit<Long>(max_ip, HashCode));
                }
            }
        }

        if (search_range.size() == 0) {
            throw new Exception("No point found in map.");
        }

        search_range.sort(true);

        double search_range_ip = center_point.minInnerProduct(
                center_point.geometryHashBlockBoundary(
                        search_range.get(0).value,
                        accuracy,
                        center_point
                )
        );

        int max_index = 1;
        for (int i = 0; i < search_range.size(); ++i) {
            if (search_range.get(i).time < search_range_ip) {
                max_index = i;
                break;
            }
        }

        double max_ip = -1.0D;
        GeometryPoint<T> return_point = new GeometryPoint<T>(0, 0);
        for (int i = 0; i < max_index; ++i) {
            GeometryPoint<T> this_point = searchNearestPoint(
                    search_range.get(i).value,
                    center_point);
            double ip = this_point.getInnerProduct(center_point);
            if (ip > max_ip) {
                return_point = this_point;
                max_ip = ip;
            }
        }
        if (max_ip <= -1.0D) {
            throw new Exception("No point found in map.");
        } else {
            return return_point;
        }
    }

    /**
     * @param HashCode
     * @param center_point
     * @return nearest point
     * @throws Exception can't find
     */
    public GeometryPoint searchNearestPoint(Long HashCode, GeometryPoint center_point) throws Exception {
        if (!data.containsKey(HashCode)) {
            throw new Exception("No point found in given block.");
        }
        return center_point.nearestPoint(
                data.get(
                        HashCode
                )
        );
    }

    /**
     * @param center_point
     * @return
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
     * @param max_radi
     * @return
     */
    public double searchNearestDistance(GeometryPoint<T> center_point, double max_radi) {
        try {
            return searchNearestPoint(center_point, max_radi).distance(center_point);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

    /**
     * @param center_point
     * @param HashCode
     * @return
     */
    public double searchNearestDistance(GeometryPoint<T> center_point, Long HashCode) {
        try {
            return searchNearestPoint(HashCode, center_point).distance(center_point);
        } catch (Exception e) {
            return MAGIC_MAX_DISTANCE;
        }
    }

}
