package tsingjyujing.geo.algorithm;

import tsingjyujing.geo.basic.geounit.HeatPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class HeatMap implements Serializable, Cloneable {

    /**
     * Heat map data stores
     */
    private HashMap<HeatPoint<Integer>, Double> heatData = new HashMap<>();

    private long accuracy;

    /**
     * @return Accuracy
     */
    public long getAccuracy() {
        return accuracy;
    }

    /**
     * @param acc Accuracy
     */
    public HeatMap(long acc) {
        accuracy = acc;
    }

    /**
     * @param gpsInfo A matrix which each row is [longitude,latitude,heat]
     * @param acc     Accuracy
     */
    public HeatMap(double[][] gpsInfo, long acc) {
        accuracy = acc;
        append(gpsInfo);
    }


    /**
     * @return A hash-map which records heat
     */
    public HashMap<HeatPoint<Integer>, Double> getHeatMap() {
        return heatData;
    }

    /**
     * @param gpsInfo A matrix which each row is [longitude,latitude,heat] to
     */
    public void append(double[][] gpsInfo) {
        for (double[] pointInfo : gpsInfo) {
            assert pointInfo.length == 3;
            HeatPoint<Integer> gp = new HeatPoint<>(pointInfo[0], pointInfo[1], 0, accuracy);
            if (!heatData.containsKey(gp)) {
                heatData.put(gp, pointInfo[2]);
            } else {
                heatData.put(gp, heatData.get(gp) + pointInfo[2]);
            }
        }
    }


    /**
     * @param longitudeSearch longitude to search
     * @param latitudeSearch  latitude to search
     * @return search result
     */
    public double apply(double longitudeSearch, double latitudeSearch) {
        return apply(new HeatPoint<Integer>(longitudeSearch, latitudeSearch, accuracy));
    }

    /**
     * @param heatPointInput Point to search
     * @return search result
     */
    public double apply(HeatPoint heatPointInput) {
        if (heatPointInput.getAccuracy() != getAccuracy()) {
            throw new RuntimeException("Accuracy not match, can't add to this HeatMap");
        }
        return getHeatMap().getOrDefault(heatPointInput, 0.0D);
    }


    /**
     * @param map Another heat-map
     * @return Points count of the intersection of two maps
     */
    public int intersectSize(HeatMap map) {
        return intersectSize(this, map);
    }

    /**
     * @param map1 heat-map 1
     * @param map2 heat-map 2
     * @return Points count of the intersect size of two maps
     */
    public static int intersectSize(HeatMap map1, HeatMap map2) {
        Set<HeatPoint<Integer>> objSet = map1.getHeatMap().keySet();
        objSet.retainAll(map2.getHeatMap().keySet());
        return objSet.size();
    }

    /**
     * @param map Another heat-map
     * @return Inner product of two heat-maps
     */
    public double innerProduct(HeatMap map) {
        return innerProduct(this, map);
    }

    /**
     * @param map1 heat-map 1
     * @param map2 heat-map 2
     * @return Inner product of two heat-maps
     */
    public static double innerProduct(HeatMap map1, HeatMap map2) {
        Set<HeatPoint<Integer>> intersectSet = map1.getHeatMap().keySet();
        intersectSet.retainAll(map2.getHeatMap().keySet());
        double sum = 0;
        for (HeatPoint<Integer> heatPoint : intersectSet) {
            sum += map2.apply(heatPoint) * map1.apply(heatPoint);
        }
        return sum;
    }

    /**
     * @param map map to merge
     */
    public void merge(HeatMap map) {
        assert getAccuracy() == map.getAccuracy();
        for (Map.Entry<HeatPoint<Integer>, Double> entry : map.getHeatMap().entrySet()) {
            getHeatMap().put(entry.getKey(), apply(entry.getKey()) + entry.getValue());
        }
    }

    /**
     * @param map1 heat map 1
     * @param map2 heat map 2
     * @return Merged map
     * @throws CloneNotSupportedException
     */
    @Deprecated
    public static HeatMap merge(HeatMap map1, HeatMap map2) throws CloneNotSupportedException {
        HeatMap mapRet = (HeatMap) map1.clone();
        mapRet.merge(map2);
        return mapRet;
    }

    /**
     * @return Cloned Object
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        HeatMap ret = (HeatMap) super.clone();
        ret.heatData = (HashMap<HeatPoint<Integer>, Double>) ret.heatData.clone();
        return ret;
    }

    /**
     * @return Norm 2 of heatmap
     */
    public double norm() {
        double sum = 0;
        for (double val : getHeatMap().values()) {
            sum += val * val;
        }
        return sum;
    }

    /**
     * @param n Norm N
     * @return Norm N value
     */
    public double norm(int n) {
        double sum = 0;
        for (double val : getHeatMap().values()) {
            sum += Math.abs(Math.pow(val, n));
        }
        return sum;
    }

    /**
     * @return export heat matrix which row is [longitude,latitude,heat]
     */
    public double[][] exportMatrix() {
        double[][] cooMatrix = new double[heatData.size()][3];
        Iterator<Map.Entry<HeatPoint<Integer>, Double>> iterator = heatData.entrySet().iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Map.Entry<HeatPoint<Integer>, Double> entry = iterator.next();
            cooMatrix[i][0] = entry.getKey().getLongitude();
            cooMatrix[i][1] = entry.getKey().getLatitude();
            cooMatrix[i][2] = entry.getValue();
        }
        return cooMatrix;
    }

    /**
     * Print heat-map
     */
    public void display() {
        for (HeatPoint<Integer> heatPoint : getHeatMap().keySet()) {
            System.out.printf("%s --> %s\n", heatPoint.toString(), getHeatMap().get(heatPoint).toString());
        }
    }
}
