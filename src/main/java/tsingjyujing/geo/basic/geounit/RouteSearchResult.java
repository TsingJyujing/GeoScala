package tsingjyujing.geo.basic.geounit;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class RouteSearchResult {
    public int enterIndex;
    public double enterTime;
    public int exitIndex;
    public double exitTime;

    @Override
    public String toString() {
        return String.format("In time:%f\t In index:%d\t Out time:%f\t Out index:%d\t",
                enterTime, enterIndex, exitTime, exitIndex);
    }

    public String getCSV(String dec) {
        return String.format("%f%s%d%s%f%s%d\t",
                enterTime, dec, enterIndex, dec, exitTime, dec, exitIndex);
    }

    public static String getCSVHeader(String dec) {
        return String.format("Time(in)%sIndex(in)%sTime(out)%sIndex(out)\t",
                dec, dec, dec);
    }
}