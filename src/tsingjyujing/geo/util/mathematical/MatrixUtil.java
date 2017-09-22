package tsingjyujing.geo.util.mathematical;


/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class MatrixUtil {

    /**
     * @param Matrix
     * @param ColIndeces
     * @return
     */
    public static double[][] colSlice(double[][] Matrix, int[] ColIndeces) {
        double[][] result = new double[Matrix.length][ColIndeces.length];
        for (int idx : ColIndeces) {
            if (idx < 0 || idx >= Matrix[0].length) {
                throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
            }
        }
        int i, j;
        for (i = 0; i < Matrix.length; ++i) {
            for (j = 0; j < ColIndeces.length; ++j) {
                result[i][j] = Matrix[i][ColIndeces[j]];
            }
        }
        return result;
    }

    public static double[] colSlice(double[][] Matrix, int col_index) {
        double[] result = new double[Matrix.length];
        if (col_index < 0 || col_index >= Matrix[0].length) {
            throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
        }
        int i, j;
        for (i = 0; i < Matrix.length; ++i) {
            result[i] = Matrix[i][col_index];
        }
        return result;
    }

    /**
     * @param arr
     * @return
     */
    public static int[] intArray(int... arr) {
        return arr;
    }

    /**
     * @param rawdata
     * @param dim
     * @return
     * @throws Exception
     */
    public static double[][] reshape(double[] rawdata, int[] dim) throws Exception {
        if (dim.length != 2) {
            throw new Exception("Error dim parameters.");
        }
        for (int d : dim) {
            if (d < 0) {
                throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
            }
        }
        if (dim[0] * dim[1] != rawdata.length) {
            throw new Exception("Error dim parameters or data length.");
        }
        double[][] rtn = new double[dim[0]][dim[1]];
        for (int i = 0; i < dim[0]; ++i) {
            for (int j = 0; j < dim[1]; ++j) {
                rtn[i][j] = rawdata[i + j * dim[0]];
            }
        }
        return rtn;
    }

    /**
     * @param rawdata
     * @param dim
     * @return
     * @throws Exception
     */
    public static int[][] reshape(int[] rawdata, int[] dim) throws Exception {
        if (dim.length != 2) {
            throw new Exception("Error dim parameters.");
        }
        for (int d : dim) {
            if (d < 0) {
                throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
            }
        }
        if (dim[0] * dim[1] != rawdata.length) {
            throw new Exception("Error dim parameters or data length.");
        }
        int[][] rtn = new int[dim[0]][dim[1]];
        for (int i = 0; i < dim[0]; ++i) {
            for (int j = 0; j < dim[1]; ++j) {
                rtn[i][j] = rawdata[i + j * dim[0]];
            }
        }
        return rtn;
    }


    /**
     * @param x 3×3 Matrix
     * @return inv(x)->3×3 Matrix
     */
    public static double[][] invO3(double[][] x) {
        assert (x.length == 3);
        double[][] returnValue = {
                {
                        x[1][1] * x[2][2] - x[1][2] * x[2][1],
                        x[0][2] * x[2][1] - x[0][1] * x[2][2],
                        x[0][1] * x[1][2] - x[0][2] * x[1][1]
                },
                {
                        x[1][2] * x[2][0] - x[1][0] * x[2][2],
                        x[0][0] * x[2][2] - x[0][2] * x[2][0],
                        x[0][2] * x[1][0] - x[0][0] * x[1][2]
                },
                {
                        x[1][0] * x[2][1] - x[1][1] * x[2][0],
                        x[0][1] * x[2][0] - x[0][0] * x[2][1],
                        x[0][0] * x[1][1] - x[0][1] * x[1][0]
                },
        };
        double factor = x[0][0] * x[1][1] * x[2][2]
                - x[0][0] * x[1][2] * x[2][1]
                - x[0][1] * x[1][0] * x[2][2]
                + x[0][1] * x[1][2] * x[2][0]
                + x[0][2] * x[1][0] * x[2][1]
                - x[0][2] * x[1][1] * x[2][0];
        for (double[] array : returnValue) {
            assert (array.length == 3);
            array[0] = array[0] / factor;
            array[1] = array[1] / factor;
            array[2] = array[2] / factor;
        }
        return returnValue;
    }
}
