package com.github.tsingjyujing.geo.util.mathematical;


import java.lang.reflect.Array;

/**
 * Matrix and linear algebra utils
 */
public class MatrixUtil {

    /**
     * Get a column by given matrix and given index
     *
     * @param matrix  input matrix
     * @param indices which column to get (count start from 0)
     * @return columns constructed new matrix
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

    /**
     * Get a column by given matrix and given index
     *
     * @param matrix  input matrix
     * @param index which column to get (count start from 0)
     * @return column of the vector
     */
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
     * Array constructor
     * @param data array of integers
     * @return int[]
     */
    public static int[] intArray(int... data) {
        return data;
    }

    /**
     * Reshape matrix
     * @param rawData data input
     * @param dimension an array has two value, row size and column size
     * @param type data type
     * @param <T> data type
     * @return matrix reshaped
     * @throws Exception some error
     */
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
     * @param rawData data input
     * @param dimension  an array has two value, row size and column size
     * @return a double matrix
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
     * @param rawData data input
     * @param dimension  an array has two value, row size and column size
     * @return an int matrix
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

    /**
     * product  matrix and vector
     * @param M matrix
     * @param vector vector
     * @return vector result
     */
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
