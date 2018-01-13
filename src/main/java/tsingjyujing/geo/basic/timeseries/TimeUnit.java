package tsingjyujing.geo.basic.timeseries;

import javax.annotation.Nonnull;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class TimeUnit<T> implements Comparable<TimeUnit>, Tickable {

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    private T value;
    private double tick = 0.0D;
    private boolean removed = false;

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
        tick = timeInput;
        value = valueInput;
    }

    @Override
    public int compareTo(@Nonnull TimeUnit obj) {
        return Double.compare(this.tick, obj.tick);
    }

    @Override
    public double getTick() {
        return tick;
    }

    @Override
    public void setTick(double tick) {
        this.tick = tick;
    }
}
