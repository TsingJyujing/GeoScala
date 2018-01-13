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
            d.setDs(points.get(i - 1).distance(points.get(i)));
            d.setDt(points.get(i).getUserInfo() - points.get(i - 1).getUserInfo());
            d.setTick(points.get(i).getUserInfo());
            d.setUserDefinedIndex((long) i);
            geometryPointArrayList.add(new GeometryPoint<>(
                    points.get(i).getLongitude(), points.get(i).getLatitude(), d));
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
        double deltaLongitude = (point2.getLongitude() - point1.getLongitude()) / (pointCount + 1);
        double deltaLatitude = (point2.getLatitude() - point1.getLatitude()) / (pointCount + 1);
        double deltaTime = (point2.getUserInfo() - point1.getUserInfo()) / (pointCount + 1);
        insertedPoints.add(point1);
        for (int i = 1; i <= pointCount; i++) {
            insertedPoints.add(
                    new GeometryPoint<>(
                            point1.getLongitude() + i * deltaLongitude,
                            point1.getLatitude() + i * deltaLatitude,
                            point1.getUserInfo() + i * deltaTime
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
        List<GeometryPoint<Double>> double2list = new ArrayList<>();
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
        double[][] returnValue = new double[directionalRoute.size()][4];
        for (int i = 0; i < directionalRoute.size(); ++i) {
            GeometryPoint<DirectionalPoint> pointQueried;
            try {
                pointQueried = routeComparator.searchNearestPoint(
                        directionalRoute.get(i), radius
                );
                returnValue[i][0] = pointQueried.distance(directionalRoute.get(i));
                returnValue[i][1] = Math.acos(
                        pointQueried.getUserInfo().getInnerProduct(
                                directionalRoute.get(i).getUserInfo()
                        )
                );
                returnValue[i][2] = pointQueried.getUserInfo().getDs();
                returnValue[i][3] = pointQueried.getUserInfo().getUserDefinedIndex();
            } catch (Exception e) {
                returnValue[i][0] = GeometryPoint.EARTH_RADIUS * Math.PI;
                returnValue[i][1] = -1;
                returnValue[i][2] = -1;
                returnValue[i][3] = -1;
            }
        }
        return returnValue;
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

        double[][] searchDistance = frechetDirectionalDistance(
                directionalRoute,
                radius
        );

        // Initial value
        isOnRoute[0] = searchDistance[0][0] < radius &&
                !Double.isNaN(searchDistance[0][1]) &&
                searchDistance[0][1] < thetaLimit;

        for (int i = 1; i < directionalRoute.size(); ++i) {
            if (Double.isNaN(searchDistance[i][1])) {
                isOnRoute[i] = isOnRoute[i - 1];
            } else {
                isOnRoute[i] = searchDistance[i][0] < radius && searchDistance[i][1] < thetaLimit;
            }
        }

        // Search the range
        DoubleTimeSeries sortedRouteIndexMapper = new DoubleTimeSeries();
        for (GeometryPoint<Double> point : route) {
            sortedRouteIndexMapper.append(
                    new TimeUnit<>(
                            point.getUserInfo(),
                            0.0D
                    )
            );
        }
        sortedRouteIndexMapper.sort(false);
        boolean isLastValue = false;
        int t = -1;
        for (int i = 0; i < directionalRoute.size(); ++i) {
            if (!isLastValue && isOnRoute[i]) {
                resultList.add(new RouteSearchResult());
                t = resultList.size() - 1;
                resultList.get(t).enterTime = directionalRoute.get(i).getUserInfo().getTick();
                resultList.get(t).enterIndex =
                        sortedRouteIndexMapper.searchInSorted(
                                resultList.get(t).enterTime,
                                false
                        );
            } else if (
                    (
                            isLastValue && !isOnRoute[i]
                    )
                            ||
                            (
                                    isOnRoute[i] && i == (directionalRoute.size() - 1)
                            )
                    ) {
                resultList.get(t).exitTime = directionalRoute.get(i).getUserInfo().getTick();
                resultList.get(t).exitIndex =
                        sortedRouteIndexMapper.searchInSorted(
                                resultList.get(t).exitTime,
                                true
                        );
                if (resultList.get(t).exitTime - resultList.get(t).enterTime < minOnRouteTime) {
                    resultList.remove(t);
                }
            }
            isLastValue = isOnRoute[i];
        }
        return resultList;
    }

}
