package tsingjyujing.geo.basic.geounit;

import tsingjyujing.geo.basic.timeseries.Tickable;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class DirectionalPoint implements Tickable {
    public double getDs() {
        return ds;
    }

    public void setDs(double ds) {
        this.ds = ds;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public Long getUserDefinedIndex() {
        return userDefinedIndex;
    }

    public void setUserDefinedIndex(Long userDefinedIndex) {
        this.userDefinedIndex = userDefinedIndex;
    }

    @Override
    public double getTick() {
        return tick;
    }

    @Override
    public void setTick(double tick) {
        this.tick = tick;
    }

    private double ds;
    private double dt;

    private Long userDefinedIndex = 0L;
    private double tick = 0.0D;

    public double getVelocity() {
        return ds / dt;
    }

    private double x;
    private double y;

    public static double getInnerProduct(DirectionalPoint a, DirectionalPoint b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    public static double getAngle(DirectionalPoint a, DirectionalPoint b) {
        return Math.acos(getInnerProduct(a, b));
    }

    public double getInnerProduct(DirectionalPoint a) {
        return getInnerProduct(this, a);
    }

    public double getAngle(DirectionalPoint a) {
        return getAngle(this, a);
    }

    public void writeDirection(double ix, double iy) {
        double norm = Math.sqrt(ix * ix + iy * iy);
        x = ix / norm;
        y = iy / norm;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void writeDirection(GeometryPoint a, GeometryPoint b) {
        double deltaEast = (b.longitude - a.longitude) * Math.cos((a.latitude + b.latitude) / 2 * GeometryPoint.DEG2RAD);
        double deltaNorth = b.latitude - a.latitude;
        writeDirection(deltaEast, deltaNorth);
    }
}