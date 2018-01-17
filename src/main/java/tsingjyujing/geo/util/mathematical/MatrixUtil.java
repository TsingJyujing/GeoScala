package tsingjyujing.geo.util.mathematical;


import java.lang.reflect.Array;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class MatrixUtil {

    /**
     * Get a column by given matrix and given index
     *
     * @param matrix
     * @param indices
     * @return
     */
    public static double[][] columnSlice(double[][] matrix, int[] indices) {
        double[][] result = new double[matrix.length][indices.length];
        for (int idx : indices) {
            if (idx < 0 || idx >= matrix[0].length) {
                throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
            }
        }
        int i, j;
        for (i = 0; i < matrix.length; ++i) {
            for (j = 0; j < indices.length; ++j) {
                result[i][j] = matrix[i][indices[j]];
            }
        }
        return result;
    }

    public static double[] columnSlice(double[][] matrix, int index) {
        double[] result = new double[matrix.length];
        if (index < 0 || index >= matrix[0].length) {
            throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
        }
        int i, j;
        for (i = 0; i < matrix.length; ++i) {
            result[i] = matrix[i][index];
        }
        return result;
    }

    /**
     * @param data
     * @return
     */
    public static int[] intArray(int... data) {
        return data;
    }

    public static <T> T[][] reshape(T[] rawData, int[] dimension, Class<T> type) throws Exception {
        if (dimension.length != 2) {
            throw new Exception("Error dimension parameter.");
        }
        for (int d : dimension) {
            if (d <= 0) {
                throw new ArrayIndexOutOfBoundsException("Dimension out of bounds");
            }
        }
        if (dimension[0] * dimension[1] != rawData.length) {
            throw new Exception("Error dimension parameters or data length.");
        }
        int rowSize = dimension[0];
        int colSize = dimension[1];
        T[][] returnValue = (T[][]) Array.newInstance(type, rowSize, colSize);
        for (int i = 0; i < rowSize; ++i) {
            for (int j = 0; j < colSize; ++j) {
                returnValue[i][j] = rawData[i + j * rowSize];
            }
        }
        return returnValue;
    }

    /**
     * @param rawData
     * @param dimension
     * @return
     * @throws Exception
     */
    public static double[][] reshape(double[] rawData, int[] dimension) throws Exception {
        if (dimension.length != 2) {
            throw new Exception("Error dimension parameters.");
        }
        for (int d : dimension) {
            if (d <= 0) {
                throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
            }
        }
        if (dimension[0] * dimension[1] != rawData.length) {
            throw new Exception("Error dimension parameters or data length.");
        }
        double[][] rtn = new double[dimension[0]][dimension[1]];
        for (int i = 0; i < dimension[0]; ++i) {
            for (int j = 0; j < dimension[1]; ++j) {
                rtn[i][j] = rawData[i + j * dimension[0]];
            }
        }
        return rtn;
    }

    /**
     * @param rawData
     * @param dimension
     * @return
     * @throws Exception
     */
    public static int[][] reshape(int[] rawData, int[] dimension) throws Exception {
        if (dimension.length != 2) {
            throw new Exception("Error dimension parameters.");
        }
        for (int d : dimension) {
            if (d < 0) {
                throw new ArrayIndexOutOfBoundsException("Dim out of bounds");
            }
        }
        if (dimension[0] * dimension[1] != rawData.length) {
            throw new Exception("Error dimension parameters or data length.");
        }
        int[][] rtn = new int[dimension[0]][dimension[1]];
        for (int i = 0; i < dimension[0]; ++i) {
            for (int j = 0; j < dimension[1]; ++j) {
                rtn[i][j] = rawData[i + j * dimension[0]];
            }
        }
        return rtn;
    }


    /**
     * @param x 3×3 Matrix
     * @return inv(x)->3×3 Matrix
     */
    public static double[][] inverseOrder3Matrix(double[][] x) {
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


    public static double[] matrixProduct(double[][] M, double[] vector) {
        final int outSize = M.length;
        final int prodSize = vector.length;
        final double[] outVector = new double[outSize];
        for (int i = 0; i < outSize; i++) {
            final double[] c = M[i];
            double sumValue = 0.0;
            if (c.length != prodSize) {
                throw new RuntimeException("Error while product M and vector: dimension not fetched.");
            }
            for (int j = 0; j < prodSize; j++) {
                sumValue += c[j] * vector[j];
            }
            outVector[i] = sumValue;
        }
        return outVector;
    }
}
