package tsingjyujing.geo.basic.timeseries;


import tsingjyujing.geo.basic.geounit.GeometryPoint;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class GPSTimeSeries<T> extends BaseTimeSeries<GeometryPoint<T>> {

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
            double ds = thisTimeUnit.value.distance(lastTimeUnit.value);
            double dt = (thisTimeUnit.time - lastTimeUnit.time);
            res.append(
                    new TimeUnit<>(
                            (thisTimeUnit.time + lastTimeUnit.time) / 2.0D,
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
                    (thisTimeUnit.time + lastTimeUnit.time) / 2.0D,
                    thisTimeUnit.value.distance(lastTimeUnit.value)
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
        TimeUnit<GeometryPoint<T>> last_Time_unit = data.get(0);
        TimeUnit<GeometryPoint<T>> this_Time_unit;
        for (int i = 1; i < data.size(); ++i) {
            this_Time_unit = data.get(i);
            res += this_Time_unit.value.distance(last_Time_unit.value);
            last_Time_unit = this_Time_unit;
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
                    tmp.time, tmp.value.longitude, tmp.value.latitude);
        }
    }


}
