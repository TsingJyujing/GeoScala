package tsingjyujing.geo.util;

import tsingjyujing.geo.basic.timeseries.ITick;

/**
 * @author yuanyifan
 */
public class TickImpl implements ITick {
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

    @Override
    public int compareTo(ITick t) {
        return Double.compare(getTick(),t.getTick());
    }
}
