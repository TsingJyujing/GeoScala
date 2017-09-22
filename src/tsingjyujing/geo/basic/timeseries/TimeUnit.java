package tsingjyujing.geo.basic.timeseries;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class TimeUnit<T> implements Comparable<TimeUnit> {

    public T value;
    public double time = 0.0D;
    public boolean remove = false;

    /**
     * Initial by no parameters
     */
    public TimeUnit() {
    }

    /**
     * @param time_in  initial time
     * @param value_in initial value
     */
    public TimeUnit(double time_in, T value_in) {
        time = time_in;
        value = value_in;
    }

    @Override
    public int compareTo(TimeUnit o) {
        if (o.time < this.time) {
            return 1;
        } else if (o.time > this.time) {
            return -1;
        } else {
            return 0;
        }
    }
}
