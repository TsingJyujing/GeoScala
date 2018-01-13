package tsingjyujing.geo.basic.geounit;

import java.io.Serializable;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class HeatPoint<T> extends GeometryPoint<T> implements Serializable {
    private long accuracy = 180;

    public long getAccuracy() {
        return accuracy;
    }

    private void reformatGPS() {
        longitude = fixPointToCenter(longitude, accuracy);
        latitude = fixPointToCenter(latitude, accuracy);
    }

    private static double fixPointToCenter(double variable, long accuracy) {
        return ((long) Math.floor(variable / 180 * accuracy) + 0.5) * 180.0 / accuracy;
    }

    public HeatPoint(double longitude, double latitude, long accuracy) {
        super(longitude, latitude);
        this.accuracy = accuracy;
        reformatGPS();
    }

    public HeatPoint(double longitude, double latitude, T value, long accuracy) {
        super(longitude, latitude, value);
        this.accuracy = accuracy;
        reformatGPS();
    }

    public long hashCodeLong() {
        return geoHashCode(accuracy);
    }

    @Override
    public int hashCode() {
        return ((Long) geoHashCode(accuracy)).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass().equals(obj.getClass())) {
            return geoHashCode(accuracy) == ((HeatPoint) obj).hashCodeLong();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", longitude, latitude);
    }

}
