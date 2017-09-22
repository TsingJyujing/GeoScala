package tsingjyujing.geo.util.mathematical;


/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class Statistic {

    public static int[] getDistribution(double[] rawDataMatrix, double[] configure) {
        double min = configure[0];
        double range = (configure[1] - min);
        int N = (int) Math.round(configure[2]);
        boolean LeftDataContains = false;
        boolean RightDataContains = false;

        if (configure.length >= 3) {
            LeftDataContains = configure[3] > 0.0;
            RightDataContains = configure[4] > 0.0;
        }
        double Period = N / range;
        int[] result = new int[N];

        for (double d : rawDataMatrix) {
            int value = (int) Math.floor((d - min) * Period);
            if (value < 0) {
                if (LeftDataContains) {
                    result[0]++;
                }
            } else if (value >= N) {
                if (RightDataContains) {
                    result[N - 1]++;
                }
            } else {
                result[value]++;
            }
        }
        return result;
    }

    public static int[] getDistribution(double[][] rawDataMatrix, int colIndex, double[] configure) {
        double min = configure[0];
        double range = (configure[1] - min);
        int N = (int) Math.round(configure[2]);
        boolean LeftDataContains = false;
        boolean RightDataContains = false;

        if (configure.length >= 3) {
            LeftDataContains = configure[3] > 0.0;
            RightDataContains = configure[4] > 0.0;
        }
        double Period = N / range;
        int[] result = new int[N];

        for (double[] d : rawDataMatrix) {
            int value = (int) Math.floor((d[colIndex] - min) * Period);
            if (value < 0) {
                if (LeftDataContains) {
                    result[0]++;
                }
            } else if (value >= N) {
                if (RightDataContains) {
                    result[N - 1]++;
                }
            } else {
                result[value]++;
            }
        }
        return result;
    }

    public static double[] commonParams(double[][] rawDataMatrix, int colIndex) {
        double mean = 0.0f;
        double max_value = rawDataMatrix[0][colIndex];
        double min_value = rawDataMatrix[0][colIndex];
        int N = rawDataMatrix.length;
        for (int i = 0; i < N; i++) {
            mean += rawDataMatrix[i][colIndex];
            max_value = rawDataMatrix[i][colIndex] > max_value ? rawDataMatrix[i][colIndex] : max_value;
            min_value = rawDataMatrix[i][colIndex] < min_value ? rawDataMatrix[i][colIndex] : min_value;
        }
        mean /= N;
        double std = 0.0f;
        for (int i = 0; i < N; i++) {
            std += (rawDataMatrix[i][colIndex] - mean) * (rawDataMatrix[i][colIndex] - mean);
        }
        if (N > 1) {
            std /= (N - 1);
        } else {
            std = 0;
        }
        std = Math.sqrt(std);
        double[] rtn = new double[4];
        rtn[0] = min_value;
        rtn[1] = max_value;
        rtn[2] = mean;
        rtn[3] = std;
        return rtn;
    }

    /**
     * @param rawXData
     * @param rawYData
     * @param baseXData
     * @return
     */
    public static double[] linearInterp(
            double[] rawXData,
            double[] rawYData,
            double[] baseXData
    ) {
        double[] fittedValue = new double[baseXData.length];
        int n = 0;
        double maxTime = rawXData[rawXData.length - 1];
        double minTime = rawXData[0];
        for (int i = 0; i < baseXData.length; ++i) {
            double btime = baseXData[i];
            double value;
            if (btime >= maxTime) {
                value = rawYData[rawYData.length - 1];
            } else if (btime <= minTime) {
                value = rawYData[0];
            } else {
                while (rawXData[n] < btime && n < (rawXData.length - 1)) {
                    n++;
                }
                double dv = rawYData[n] - rawYData[n - 1];
                double dt = rawXData[n] - rawXData[n - 1];
                value = rawYData[n - 1] + dv / dt * (baseXData[i] - rawXData[n - 1]);
            }
            fittedValue[i] = value;
        }
        return fittedValue;
    }

    public static double[][] linearInterp(
            double[][] rawXData, int indexX,
            double[][] rawYData, int indexY,
            double[][] baseXData, int indexBaseX) {
        /*
            assert(rawXData.length==rawYData.length);
            assert(rawXData.length>0);
            assert(rawXData[0].length>indexX);
            assert(rawYData[0].length>indexY);
            assert(indexX>=0 && indexY>=0);
            assert(baseXData.leng th>0);
            assert(baseXData[0].length>indexBaseX && indexBaseX>=0);
         */
        double[][] fitted_value = new double[baseXData.length][1];
        int n = 0;
        double max_time = rawXData[rawXData.length - 1][indexX];
        double min_time = rawXData[0][indexX];
        for (int i = 0; i < baseXData.length; ++i) {
            double btime = baseXData[i][indexBaseX];
            double value;
            if (btime >= max_time) {
                value = rawYData[rawYData.length - 1][indexY];
            } else if (btime <= min_time) {
                value = rawYData[0][indexY];
            } else {
                while (rawXData[n][indexX] < btime && n < (rawXData.length - 1)) {
                    n++;
                }
                double dv = rawYData[n][indexY] - rawYData[n - 1][indexY];
                double dt = rawXData[n][indexX] - rawXData[n - 1][indexX];
                value = rawYData[n - 1][indexY] + dv / dt * (baseXData[i][indexBaseX] - rawXData[n - 1][indexX]);
            }
            fitted_value[i][0] = value;
        }
        return fitted_value;
    }

    public static double[][] meshprob(
            double[][] data_x, int index_x,
            double[][] data_y, int index_y,
            double[] configure
    ) {
        double xmin = configure[0];
        double xmax = configure[1];
        int grid_x = (int) Math.round(configure[2]);

        double ymin = configure[3];
        double ymax = configure[4];
        int grid_y = (int) Math.round(configure[5]);

        boolean edge_x = false;
        //boolean edge_y = false;

        if (configure.length >= 8) {
            edge_x = configure[6] > 0;
            //edge_y = configure[7]>0;
        }

        double prx = (double) grid_x / (xmax - xmin);
        double pry = (double) grid_y / (ymax - ymin);

        double[][] rtn = new double[grid_x][grid_y];

        int N = data_x.length;

        for (int i = 0; i < N; ++i) {
            int px = (int) Math.floor((data_x[i][index_x] - xmin) * prx);
            int py = (int) Math.floor((data_y[i][index_y] - ymin) * pry);
            if (edge_x) {
                px = Math.min(Math.max(px, 0), grid_x - 1);
            } else {
                if (px != Math.min(Math.max(px, 0), grid_x - 1)) {
                    continue;
                }
            }
            if (edge_x) {
                py = Math.min(Math.max(py, 0), grid_y - 1);
            } else {
                if (py != Math.min(Math.max(py, 0), grid_y - 1)) {
                    continue;
                }
            }

            rtn[px][py]++;
        }

        return rtn;
    }
}
