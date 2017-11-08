package tsingjyujing.geo.util;

import tsingjyujing.geo.basic.timeseries.Tickable;

/**
 * @author yuanyifan
 */
public class TickImpl implements Tickable {

    private double tick = Double.NaN;

    @Override
    public double getTick() {
        return tick;
    }

    @Override
    public void setTick(double tick) {
        this.tick = tick;
    }

    public TickImpl() {
    }

    public TickImpl(double tick) {
        setTick(tick);
    }

}
