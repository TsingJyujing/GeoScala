package tsingjyujing.geo.basic.geounit;

import tsingjyujing.geo.util.mathematical.MatrixUtil;
import tsingjyujing.geo.util.mathematical.VectorUtil;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class GeodesicLine {

    private GeometryPoint point1;
    private GeometryPoint point2;
    private double[][] iM;
    private double[] n;

    private static final int WORLD_DIMENSION = 3;

    public GeodesicLine(GeometryPoint p1, GeometryPoint p2) {
        point1 = p1;
        point2 = p2;
        double[] v1 = point1.get3DPos();
        double[] v2 = point2.get3DPos();
        n = VectorUtil.norm2Vector(1.0,VectorUtil.outerProduct(v1, v2));
        double[][] M = {v1, v2, n};
        iM = MatrixUtil.inverseOrder3Matrix(M);
    }

    public double distance(GeometryPoint point) {
        double[] v3 = point.get3DPos();
        double[] params = {0, 0, 0};
        for (int i = 0; i < WORLD_DIMENSION; i++) {
            for (int j = 0; j < WORLD_DIMENSION; j++) {
                params[j] += iM[i][j] * v3[j];
            }
        }
        if (params[0] > 0 && params[1] > 0) {
            //返回和n的内积的角度的余角计算出的数值
            return Math.asin(
                    n[0] * v3[0] + n[1] * v3[1] + n[2] * v3[2]
            ) * GeometryPoint.EARTH_RADIUS;
        } else {
            //两个端点中选一个近的
            return Math.min(point.distance(point1), point.distance(point2));
        }
    }

}
