package tsingjyujing.geo.basic.geounit;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class RouteSearchResult {
    public int inIndex;
    public double inTime;
    public int outIndex;
    public double outTime;

    @Override
    public String toString() {
        return String.format("In time:%f\t In index:%d\t Out time:%f\t Out index:%d\t",
                inTime, inIndex, outTime, outIndex);
    }

    public String toCSV(String dec) {
        return String.format("%f%s%d%s%f%s%d\t",
                inTime, dec, inIndex, dec, outTime, dec, outIndex);
    }

    public static String toCSVHead(String dec) {
        return String.format("Time(in)%sIndex(in)%sTime(out)%sIndex(out)\t",
                dec, dec, dec);
    }
}