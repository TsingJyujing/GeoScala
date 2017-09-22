package tsingjyujing.geo.basic.geounit;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class DirectionalPoint {
    public double ds;
    public double dt;

    public Long userDefinedIndex = 0L;
    public double tick = 0.0D;

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
        double d_east = (b.longitude - a.longitude) * Math.cos((a.latitude + b.latitude) / 2 * GeometryPoint.DEG2RAD);
        double d_north = b.latitude - a.latitude;
        writeDirection(d_east, d_north);
    }
}