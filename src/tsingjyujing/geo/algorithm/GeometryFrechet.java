package tsingjyujing.geo.algorithm;

import tsingjyujing.geo.basic.geounit.DirectionalPoint;
import tsingjyujing.geo.basic.geounit.GeometryHashConnectionLayer;
import tsingjyujing.geo.basic.geounit.GeometryPoint;
import tsingjyujing.geo.basic.geounit.RouteSearchResult;
import tsingjyujing.geo.basic.timeseries.DoubleTimeSeries;
import tsingjyujing.geo.basic.timeseries.TimeUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */

public class GeometryFrechet {

    /**
     * Saving the Tree/Hash map of route
     */
    public GeometryHashConnectionLayer<DirectionalPoint> routeComparator;

    /**
     * Initial an empty GeometryHashConnectionLayer by default parameters
     */
    public GeometryFrechet() {
        routeComparator = new GeometryHashConnectionLayer<DirectionalPoint>();
    }

    /**
     * @param layer1 set layer 1 accuracy
     * @param layer2 set layer 2 accuracy
     */
    public GeometryFrechet(long layer1, long layer2) {
        routeComparator = new GeometryHashConnectionLayer<DirectionalPoint>(layer1, layer2);
    }

    /**
     * user defined information Double is the time of the map,
     * you can ignor it and set it to zero
     *
     * @param list_points load route to map
     */
    public void addRoute(List<GeometryPoint<DirectionalPoint>> list_points) {
        routeComparator.insert(list_points);
    }

    /**
     * @param list_points     point set
     * @param sample_distance
     * @param sample_beta
     */
    public void addRoute(
            List<GeometryPoint<Double>> list_points,
            double sample_distance,
            double sample_beta
    ) {
        routeComparator.insert(
                genDirectionRoute(
                        isometricalResampling(
                                list_points,
                                sample_distance,
                                sample_beta
                        )
                )
        );
    }


    /**
     * @param list_points
     * @return
     */
    public List<GeometryPoint<DirectionalPoint>> genDirectionRoute(
            List<GeometryPoint<Double>> list_points
    ) {
        List<GeometryPoint<DirectionalPoint>> gen_direction_route = new ArrayList<GeometryPoint<DirectionalPoint>>();
        for (int i = 1; i < list_points.size(); ++i) {
            DirectionalPoint d = new DirectionalPoint();
            d.writeDirection(list_points.get(i - 1), list_points.get(i));
            d.ds = list_points.get(i - 1).distance(list_points.get(i));
            d.dt = list_points.get(i).userInfo - list_points.get(i - 1).userInfo;
            d.tick = list_points.get(i).userInfo;
            d.userDefinedIndex = (long) i;
            gen_direction_route.add(new GeometryPoint<DirectionalPoint>(
                    list_points.get(i).longitude, list_points.get(i).latitude, d));
        }
        return gen_direction_route;
    }

    /**
     * resample gps points isometrically to ensure the validity of direction
     *
     * @param list_points
     * @param min_distance
     * @param min_sample_rate
     * @return
     */
    public List<GeometryPoint<Double>> isometricalResampling(
            List<GeometryPoint<Double>> list_points,
            double min_distance,
            double min_sample_rate) {
        List<GeometryPoint<Double>> return_value = new ArrayList<GeometryPoint<Double>>();
        return_value.add(list_points.get(0));
        int last_sample_point_index = 0;
        for (int i = 1; i < list_points.size(); ++i) {
            double distance = list_points.get(
                    last_sample_point_index
            ).distance(
                    list_points.get(i)
            );
            if (distance > min_sample_rate * min_distance) {
                return_value.addAll(
                        insertPoints(
                                list_points.get(i - 1), list_points.get(i),
                                (int) Math.floor(distance / min_distance)
                        )
                );
                last_sample_point_index = i;
            } else if (distance >= min_distance) {
                return_value.add(list_points.get(i));
                last_sample_point_index = i;
            }
        }
        return return_value;
    }

    private List<GeometryPoint<Double>> insertPoints(
            GeometryPoint<Double> a,
            GeometryPoint<Double> b,
            int point_num
    ) {
        List<GeometryPoint<Double>> insert_points = new ArrayList<GeometryPoint<Double>>();
        double dlng = (b.longitude - a.longitude) / (point_num + 1);
        double dlat = (b.latitude - a.latitude) / (point_num + 1);
        double dt = (b.userInfo - a.userInfo) / (point_num + 1);
        insert_points.add(a);
        for (int i = 1; i <= point_num; i++) {
            insert_points.add(
                    new GeometryPoint(
                            a.longitude + i * dlng,
                            a.latitude + i * dlat,
                            a.userInfo + i * dt
                    )
            );
        }
        insert_points.add(b);
        return insert_points;
    }

    /**
     * @param lng longitude array
     * @param lat latitude array
     * @return the list of geopoints
     */
    public List<GeometryPoint> double2List(double[] lng, double[] lat) {
        assert (lng.length == lat.length);
        List<GeometryPoint> double2list = new ArrayList<GeometryPoint>();
        for (int i = 0; i < lng.length; i++) {
            double2list.add(new GeometryPoint(lng[i], lat[i]));
        }
        return double2list;
    }

    /**
     * @param lng  longitude array
     * @param lat  latitude array
     * @param time time information
     * @return the list of geopoints
     */
    public List<GeometryPoint<Double>> double2List(
            double[] lng,
            double[] lat,
            double[] time
    ) {
        assert (lng.length == lat.length && lng.length == time.length);
        List<GeometryPoint<Double>> double2list = new ArrayList<GeometryPoint<Double>>();
        for (int i = 0; i < lng.length; i++) {
            double2list.add(new GeometryPoint(lng[i], lat[i], time[i]));
        }
        return double2list;
    }

    /**
     * Basic frechet distance
     *
     * @param route
     * @param radi
     * @return
     */
    public double[] frechetDistance(List<GeometryPoint> route, double radi) {
        double[] return_value = new double[route.size()];
        for (int i = 0; i < route.size(); ++i) {
            return_value[i] =
                    routeComparator.searchNearestDistance(route.get(i), radi);
        }
        return return_value;
    }

    /**
     * @param dir_route
     * @param radi
     * @return {distance theta ds} as array
     */
    public double[][] frechetDirectionalDistance(
            List<GeometryPoint<DirectionalPoint>> dir_route,
            double radi
    ) {
        double[][] return_value = new double[dir_route.size()][3];
        for (int i = 0; i < dir_route.size(); ++i) {
            GeometryPoint<DirectionalPoint> point_got;
            try {
                point_got = routeComparator.searchNearestPoint(
                        dir_route.get(i), radi
                );
                return_value[i][0] = point_got.distance(dir_route.get(i));
                return_value[i][1] = Math.acos(
                        point_got.userInfo.getInnerProduct(
                                dir_route.get(i).userInfo
                        )
                );
                return_value[i][2] = point_got.userInfo.ds;
            } catch (Exception e) {
                return_value[i][0] = GeometryPoint.EARTH_RADIUS * Math.PI;
                return_value[i][1] = Double.NaN;
                return_value[i][2] = 0;
            }
        }
        return return_value;
    }

    public List<RouteSearchResult> frechetGeofetch(
            List<GeometryPoint<Double>> route,
            double sample_step_length,
            double beta_num,
            double radius,
            double theta_lim,
            double min_inroute_time
    ) {
        List<RouteSearchResult> return_value = new ArrayList<RouteSearchResult>();

        //sample data by geodesic distance isometrically
        List<GeometryPoint<Double>> sample_route = isometricalResampling(
                route,
                sample_step_length,
                beta_num
        );

        if (sample_route.size() <= 1) {
            return return_value;
        }

        List<GeometryPoint<DirectionalPoint>> dir_route = genDirectionRoute(
                sample_route
        );

        boolean[] is_in = new boolean[dir_route.size()];

        double[][] search_distance = frechetDirectionalDistance(
                dir_route,
                radius
        );

        // Initializational value
        is_in[0] = search_distance[0][0] < radius &&
                !Double.isNaN(search_distance[0][1]) &&
                search_distance[0][1] < theta_lim;

        for (int i = 1; i < dir_route.size(); ++i) {
            if (Double.isNaN(search_distance[i][1])) {
                is_in[i] = is_in[i - 1];
            } else {
                is_in[i] = search_distance[i][0] < radius && search_distance[i][1] < theta_lim;
            }
        }

        // Search the range
        DoubleTimeSeries sorted_search_con = new DoubleTimeSeries();
        for (int i = 0; i < route.size(); ++i) {
            sorted_search_con.append(
                    new TimeUnit<Double>(
                            route.get(i).userInfo,
                            0.0D
                    )
            );
        }
        sorted_search_con.sort(false);
        boolean last_value = false;
        int t = -1;
        for (int i = 0; i < dir_route.size(); ++i) {
            if (last_value == false && is_in[i] == true) {
                return_value.add(new RouteSearchResult());
                t = return_value.size() - 1;
                return_value.get(t).inTime = dir_route.get(i).userInfo.tick;
                return_value.get(t).inIndex =
                        sorted_search_con.search_in_sorted(
                                return_value.get(t).inTime,
                                false
                        );
            } else if (
                    (last_value == true
                            && is_in[i] == false
                    )
                            ||
                            (is_in[i] == true &&
                                    i == (dir_route.size() - 1)
                            )) {
                return_value.get(t).outTime = dir_route.get(i).userInfo.tick;
                return_value.get(t).outIndex =
                        sorted_search_con.search_in_sorted(
                                return_value.get(t).outTime,
                                true
                        );
                if (return_value.get(t).outTime - return_value.get(t).inTime < min_inroute_time) {
                    return_value.remove(t);
                }
            }
            last_value = is_in[i];
        }
        return return_value;
    }

}
