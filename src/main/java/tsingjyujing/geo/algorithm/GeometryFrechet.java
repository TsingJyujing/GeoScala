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
 * An implementation of Frechet algorithm
 *
 * @author Tsing Jyujing
 * @mail tsingjyujing@163.com
 * @tel 182-2085-2215
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
        routeComparator = new GeometryHashConnectionLayer<>();
    }

    /**
     * @param layer1Accuracy set layer 1 accuracy
     * @param layer2Accuracy set layer 2 accuracy
     */
    public GeometryFrechet(long layer1Accuracy, long layer2Accuracy) {
        routeComparator = new GeometryHashConnectionLayer<>(layer1Accuracy, layer2Accuracy);
    }

    /**
     * user defined information Double is the time of the map,
     * you can ignore it and set it to zero
     *
     * @param points load route to map
     */
    public void addRoute(List<GeometryPoint<DirectionalPoint>> points) {
        routeComparator.insert(points);
    }

    /**
     * @param points           point set
     * @param resampleDistance the \delta s of resample length
     * @param resampleBeta     if the distance between 2 points larger than resampleBeta*resampleDistance, it will refill point by linear interpolation
     */
    public void addRoute(
            List<GeometryPoint<Double>> points,
            double resampleDistance,
            double resampleBeta
    ) {
        routeComparator.insert(
                genDirectionRoute(
                        isometricalResampling(
                                points,
                                resampleDistance,
                                resampleBeta
                        )
                )
        );
    }


    /**
     * @param points
     * @return directional points
     */
    public List<GeometryPoint<DirectionalPoint>> genDirectionRoute(
            List<GeometryPoint<Double>> points
    ) {
        List<GeometryPoint<DirectionalPoint>> geometryPointArrayList = new ArrayList<GeometryPoint<DirectionalPoint>>();
        for (int i = 1; i < points.size(); ++i) {
            DirectionalPoint d = new DirectionalPoint();
            d.writeDirection(points.get(i - 1), points.get(i));
            d.ds = points.get(i - 1).distance(points.get(i));
            d.dt = points.get(i).userInfo - points.get(i - 1).userInfo;
            d.tick = points.get(i).userInfo;
            d.userDefinedIndex = (long) i;
            geometryPointArrayList.add(new GeometryPoint<>(
                    points.get(i).longitude, points.get(i).latitude, d));
        }
        return geometryPointArrayList;
    }

    /**
     * resample gps points isometrically to ensure the validity of direction
     *
     * @param points
     * @param minDistance
     * @param minSampleRate
     * @return
     */
    public List<GeometryPoint<Double>> isometricalResampling(
            List<GeometryPoint<Double>> points,
            double minDistance,
            double minSampleRate) {
        List<GeometryPoint<Double>> returnValue = new ArrayList<>();
        returnValue.add(points.get(0));
        int lastSamplePointIndex = 0;
        for (int i = 1; i < points.size(); ++i) {
            double distance = points.get(
                    lastSamplePointIndex
            ).distance(
                    points.get(i)
            );
            if (distance > minSampleRate * minDistance) {
                returnValue.addAll(
                        insertPoints(
                                points.get(i - 1), points.get(i),
                                (int) Math.floor(distance / minDistance)
                        )
                );
                lastSamplePointIndex = i;
            } else if (distance >= minDistance) {
                returnValue.add(points.get(i));
                lastSamplePointIndex = i;
            }
        }
        return returnValue;
    }

    private List<GeometryPoint<Double>> insertPoints(
            GeometryPoint<Double> point1,
            GeometryPoint<Double> point2,
            int pointCount
    ) {
        List<GeometryPoint<Double>> insertedPoints = new ArrayList<>();
        double deltaLongitude = (point2.longitude - point1.longitude) / (pointCount + 1);
        double deltaLatitude = (point2.latitude - point1.latitude) / (pointCount + 1);
        double deltaTime = (point2.userInfo - point1.userInfo) / (pointCount + 1);
        insertedPoints.add(point1);
        for (int i = 1; i <= pointCount; i++) {
            insertedPoints.add(
                    new GeometryPoint<>(
                            point1.longitude + i * deltaLongitude,
                            point1.latitude + i * deltaLatitude,
                            point1.userInfo + i * deltaTime
                    )
            );
        }
        insertedPoints.add(point2);
        return insertedPoints;
    }

    /**
     * TODO MOVE THIS SHIT TO TOOL
     *
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
            double2list.add(new GeometryPoint<>(lng[i], lat[i], time[i]));
        }
        return double2list;
    }

    /**
     * Basic frechet distance without distance
     *
     * @param route
     * @param radius
     * @return
     */
    public double[] frechetDistance(List<GeometryPoint> route, double radius) {
        double[] returnValue = new double[route.size()];
        for (int i = 0; i < route.size(); ++i) {
            returnValue[i] =
                    routeComparator.searchNearestDistance(route.get(i), radius);
        }
        return returnValue;
    }

    /**
     * @param directionalRoute
     * @param radius
     * @return {distance theta ds} as array
     */
    public double[][] frechetDirectionalDistance(
            List<GeometryPoint<DirectionalPoint>> directionalRoute,
            double radius
    ) {
        double[][] return_value = new double[directionalRoute.size()][4];
        for (int i = 0; i < directionalRoute.size(); ++i) {
            GeometryPoint<DirectionalPoint> point_got;
            try {
                point_got = routeComparator.searchNearestPoint(
                        directionalRoute.get(i), radius
                );
                return_value[i][0] = point_got.distance(directionalRoute.get(i));
                return_value[i][1] = Math.acos(
                        point_got.userInfo.getInnerProduct(
                                directionalRoute.get(i).userInfo
                        )
                );
                return_value[i][2] = point_got.userInfo.ds;
                return_value[i][3] = point_got.userInfo.userDefinedIndex;
            } catch (Exception e) {
                return_value[i][0] = GeometryPoint.EARTH_RADIUS * Math.PI;
                return_value[i][1] = -1;
                return_value[i][2] = -1;
                return_value[i][3] = -1;
            }
        }
        return return_value;
    }

    public List<RouteSearchResult> frechetFetch(
            List<GeometryPoint<Double>> route,
            double sampleStepLength,
            double beta,
            double radius,
            double thetaLimit,
            double minOnRouteTime
    ) {
        List<RouteSearchResult> resultList = new ArrayList<RouteSearchResult>();

        //sample data by geodesic distance isometrically
        List<GeometryPoint<Double>> sampleRoute = isometricalResampling(
                route,
                sampleStepLength,
                beta
        );

        if (sampleRoute.size() <= 1) {
            return resultList;
        }

        List<GeometryPoint<DirectionalPoint>> directionalRoute = genDirectionRoute(
                sampleRoute
        );

        boolean[] isOnRoute = new boolean[directionalRoute.size()];

        double[][] serarchDistance = frechetDirectionalDistance(
                directionalRoute,
                radius
        );

        // Initial value
        isOnRoute[0] = serarchDistance[0][0] < radius &&
                !Double.isNaN(serarchDistance[0][1]) &&
                serarchDistance[0][1] < thetaLimit;

        for (int i = 1; i < directionalRoute.size(); ++i) {
            if (Double.isNaN(serarchDistance[i][1])) {
                isOnRoute[i] = isOnRoute[i - 1];
            } else {
                isOnRoute[i] = serarchDistance[i][0] < radius && serarchDistance[i][1] < thetaLimit;
            }
        }

        // Search the range
        DoubleTimeSeries sortedRouteIndexMapper = new DoubleTimeSeries();
        for (int i = 0; i < route.size(); ++i) {
            sortedRouteIndexMapper.append(
                    new TimeUnit<>(
                            route.get(i).userInfo,
                            0.0D
                    )
            );
        }
        sortedRouteIndexMapper.sort(false);
        boolean isLastValue = false;
        int t = -1;
        for (int i = 0; i < directionalRoute.size(); ++i) {
            if (isLastValue == false && isOnRoute[i] == true) {
                resultList.add(new RouteSearchResult());
                t = resultList.size() - 1;
                resultList.get(t).inTime = directionalRoute.get(i).userInfo.tick;
                resultList.get(t).inIndex =
                        sortedRouteIndexMapper.searchInSorted(
                                resultList.get(t).inTime,
                                false
                        );
            } else if (
                    (
                            isLastValue == true && isOnRoute[i] == false
                    )
                            ||
                            (
                                    isOnRoute[i] == true && i == (directionalRoute.size() - 1)
                            )
                    ) {
                resultList.get(t).outTime = directionalRoute.get(i).userInfo.tick;
                resultList.get(t).outIndex =
                        sortedRouteIndexMapper.searchInSorted(
                                resultList.get(t).outTime,
                                true
                        );
                if (resultList.get(t).outTime - resultList.get(t).inTime < minOnRouteTime) {
                    resultList.remove(t);
                }
            }
            isLastValue = isOnRoute[i];
        }
        return resultList;
    }

}
