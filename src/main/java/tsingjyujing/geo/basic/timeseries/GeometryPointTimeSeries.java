package tsingjyujing.geo.basic.timeseries;


import tsingjyujing.geo.basic.geounit.GeometryPoint;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class GeometryPointTimeSeries<T> extends BaseTimeSeries<GeometryPoint<T>> {

    /**
     * @return velocity of GPS time series
     */
    public DoubleTimeSeries differential() {
        if (!isUnique) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<GeometryPoint<T>> lastTimeUnit = data.get(0);
        TimeUnit<GeometryPoint<T>> thisTimeUnit;
        for (int i = 1; i < data.size(); ++i) {
            thisTimeUnit = data.get(i);
            double ds = thisTimeUnit.getValue().distance(lastTimeUnit.getValue());
            double dt = (thisTimeUnit.getTick() - lastTimeUnit.getTick());
            res.append(
                    new TimeUnit<>(
                            (thisTimeUnit.getTick() + lastTimeUnit.getTick()) / 2.0D,
                            ds / dt
                    )
            );
            lastTimeUnit = thisTimeUnit;
        }
        return res;
    }

    /**
     * @return δs of GPS time series
     */
    public DoubleTimeSeries difference() {
        if (!isUnique) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<GeometryPoint<T>> lastTimeUnit = data.get(0);
        TimeUnit<GeometryPoint<T>> thisTimeUnit;
        for (int i = 1; i < data.size(); ++i) {
            thisTimeUnit = data.get(i);
            res.append(new TimeUnit<>(
                    (thisTimeUnit.getTick() + lastTimeUnit.getTick()) / 2.0D,
                    thisTimeUnit.getValue().distance(lastTimeUnit.getValue())
            ));
            lastTimeUnit = thisTimeUnit;
        }
        return res;
    }

    /**
     * @return calculate the len of the curve on earth
     */
    public double curveIntegral() {
        if (!isUnique) {
            this.unique();
        }
        double res = 0.0D;
        TimeUnit<GeometryPoint<T>> lastTimeUnit = data.get(0);
        TimeUnit<GeometryPoint<T>> currentTimeUnit;
        for (int i = 1; i < data.size(); ++i) {
            currentTimeUnit = data.get(i);
            res += currentTimeUnit.getValue().distance(lastTimeUnit.getValue());
            lastTimeUnit = currentTimeUnit;
        }
        return res;
    }

    /**
     * @param time a double
     * @param lng  longitude
     * @param lat  latitude
     */
    public void append(double time, double lng, double lat) {
        data.add(new TimeUnit<>(time, new GeometryPoint<>(lng, lat)));
        isUnique = false;
        sortedStatus = 0;
    }

    /**
     * display the time series
     */
    public void display() {
        System.out.println("Time\tLongitude\tLatitude");
        for (TimeUnit<GeometryPoint<T>> tmp : data) {
            System.out.printf("%f\t%f\t%f\n",
                    tmp.getTick(), tmp.getValue().getLongitude(), tmp.getValue().getLatitude());
        }
    }

}
