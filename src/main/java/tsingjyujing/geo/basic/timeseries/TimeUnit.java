package tsingjyujing.geo.basic.timeseries;

import javax.annotation.Nonnull;

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
     * @param timeInput  initial time
     * @param valueInput initial value
     */
    public TimeUnit(double timeInput, T valueInput) {
        time = timeInput;
        value = valueInput;
    }

    @Override
    public int compareTo(@Nonnull TimeUnit obj) {
        return Double.compare(this.time, obj.time);
    }
}
