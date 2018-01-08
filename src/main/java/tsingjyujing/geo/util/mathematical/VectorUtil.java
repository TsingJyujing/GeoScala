package tsingjyujing.geo.util.mathematical;


/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class VectorUtil {

    public static double[] outerProduct(double[] v1, double[] v2) {
        assert (v1.length == 3);
        assert (v2.length == 3);
        double[] returnValue = {
                v1[1] * v2[2] - v1[2] * v2[1],
                -v1[0] * v2[2] + v1[2] * v2[0],
                +v1[0] * v2[1] - v1[1] * v2[0]
        };
        return returnValue;
    }

    public static void norm2Vector(double normLen, double[] vec) {
        double sumval = 0;
        for (double value : vec) {
            sumval += value * value;
        }
        double normFact = normLen / Math.sqrt(sumval);
        for (int i = 0; i < vec.length; i++) {
            vec[i] *= normFact;
        }
    }
}
