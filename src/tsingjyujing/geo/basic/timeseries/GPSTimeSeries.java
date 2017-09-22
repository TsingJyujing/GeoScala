package tsingjyujing.geo.basic.timeseries;


import tsingjyujing.geo.basic.geounit.GeometryPoint;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class GPSTimeSeries<T> extends ATimerSeries<GeometryPoint<T>> {

    /**
     * @return velocity of GPS time series
     */
    public DoubleTimeSeries differential() {
        if (!uniqued) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<GeometryPoint<T>> last_Time_unit = ts_list.get(0);
        TimeUnit<GeometryPoint<T>> this_Time_unit;
        for (int i = 1; i < ts_list.size(); ++i) {
            this_Time_unit = ts_list.get(i);
            double ds = this_Time_unit.value.distance(last_Time_unit.value);
            double dt = (this_Time_unit.time - last_Time_unit.time);
            res.append(
                    new TimeUnit(
                            (this_Time_unit.time + last_Time_unit.time) / 2.0D,
                            ds / dt
                    )
            );
            last_Time_unit = this_Time_unit;
        }
        return res;
    }

    /**
     * @return δs of GPS time series
     */
    public DoubleTimeSeries difference() {
        if (!uniqued) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<GeometryPoint<T>> last_Time_unit = ts_list.get(0);
        TimeUnit<GeometryPoint<T>> this_Time_unit;
        for (int i = 1; i < ts_list.size(); ++i) {
            this_Time_unit = ts_list.get(i);
            res.append(new TimeUnit(
                    (this_Time_unit.time + last_Time_unit.time) / 2.0D,
                    this_Time_unit.value.distance(last_Time_unit.value)
            ));
            last_Time_unit = this_Time_unit;
        }
        return res;
    }

    /**
     * @return calculate the len of the curve on earth
     */
    public double curve_integral() {
        if (!uniqued) {
            this.unique();
        }
        double res = 0.0D;
        TimeUnit<GeometryPoint<T>> last_Time_unit = ts_list.get(0);
        TimeUnit<GeometryPoint<T>> this_Time_unit;
        for (int i = 1; i < ts_list.size(); ++i) {
            this_Time_unit = ts_list.get(i);
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
        ts_list.add(new TimeUnit(time, new GeometryPoint<T>(lng, lat)));
        uniqued = false;
        sorted = 0;
    }

    /**
     * display the time series
     */
    public void display() {
        System.out.println("Time\tLongitude\tLatitude");
        for (TimeUnit<GeometryPoint<T>> tmp : ts_list) {
            System.out.printf("%f\t%f\t%f\n",
                    tmp.time, tmp.value.longitude, tmp.value.latitude);
        }
    }


}
