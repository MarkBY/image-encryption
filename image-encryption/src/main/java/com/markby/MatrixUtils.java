package com.markby;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.lang.reflect.Type;

public class MatrixUtils {

    /**
     * 线性归一化 公式：X(norm) = (X - min) / (max - min)
     *
     * @param points 原始数据
     * @return 归一化后的数据
     */
    public static int[][] normalize4Scale(double[][] points, int scale) {
        if (points == null || points.length < 1) {
            return null;
        }
        int[][] p = new int[points.length][points[0].length];
        double maxV = maxV(points);
        double minV = minV(points);
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                p[i][j] = (int) (Math.round(scale * (points[i][j] - minV) / (maxV - minV)));
            }
        }
        return p;
    }

    public static int[] normalize4ScaleToArray(double[][] points, int scale) {
        if (points == null || points.length < 1) {
            return null;
        }
        int m = points.length;
        int n = points[0].length;

        int[] p = new int[m * n];
        double maxV = maxV(points);
        double minV = minV(points);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                p[i * m + j] = (int) (Math.round(scale * (points[i][j] - minV) / (maxV - minV)));
            }
        }
        return p;
    }


    /**
     * 获取矩阵中的最小值
     *
     * @param matrix matrixJ
     * @return v
     */
    public static double minV(double[][] matrix) {
        double v = matrix[0][0];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] < v) {
                    v = matrix[i][j];
                }
            }
        }
        return v;
    }

    /**
     * 获取数组中的最大值
     *
     * @param matrix matrixJ
     * @return v
     */
    public static double maxV(double[][] matrix) {
        double v = matrix[0][0];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] > v) {
                    v = matrix[i][j];
                }
            }
        }
        return v;
    }

    public static int[] byteArrayToIntArray(byte[] data) {
        int[] result = new int[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = data[i];
        }
        return result;
    }

    public static byte[] intArrayToByteArray(int[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) data[i];
        }
        return result;
    }

    public static double[] intArrayToDoubleArray(int[] data) {
        double[] result = new double[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = data[i];
        }
        return result;
    }

    public static double[][] intArrayToDoubleArray(int[][] data) {
        double[][] result = new double[data.length][data[0].length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = data[i][j];
            }
        }
        return result;
    }

    public static int[] doubleArrayToIntArray(double[] data) {
        int[] result = new int[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) data[i];
        }
        return result;
    }

    public static int[][] doubleArrayToIntArray(double[][] data) {
        int[][] result = new int[data.length][data[0].length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = (int)data[i][j];
            }
        }
        return result;
    }

    public static double[][] matrix1DTo2D(double[] array1D, int m, int n) {
        double[][] result = new double[m][n];

        for (int i = 0; i < array1D.length; i++) {
            result[i / n][i % m] = array1D[i];
        }

        return result;
    }

    public static byte[][] matrix1DTo2D(byte[] array1D, int m, int n) {
        byte[][] result = new byte[m][n];

        for (int i = 0; i < array1D.length; i++) {
            result[i / n][i % n] = array1D[i];
        }

        return result;
    }

    public static int[][] matrix1DTo2D(int[] array1D, int m, int n) {
        int[][] result = new int[m][n];

        for (int i = 0; i < array1D.length; i++) {
            result[i / n][i % n] = array1D[i];
        }

        return result;
    }

    public static int[] matrix2DTo1D(int[][] array2D) {
        int[] result = new int[array2D.length * array2D[0].length];

        int index = 0;
        for (int[] array : array2D) {//把2维的拆成2个一维数组
            for (int element : array) {//单个数组分别输出值
                result[index++] = element;//把输出值赋给新的数组
            }
        }

        return result;
    }

    public static byte[] matrix2DTo1D(byte[][] array2D) {
        byte[] result = new byte[array2D.length * array2D[0].length];

        int index = 0;
        for (byte[] array : array2D) {//把2维的拆成2个一维数组
            for (byte element : array) {//单个数组分别输出值
                result[index++] = element;//把输出值赋给新的数组
            }
        }

        return result;
    }

    public static int[][] transpose(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] result = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = matrix[j][i];
            }
        }

        return result;

    }

    public static byte[][] transpose(byte[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        byte[][] result = new byte[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = matrix[j][i];
            }
        }

        return result;

    }

    public static RealMatrix[] doubleArrayToMatrix(double[][] doubles, int N) {
        int length = doubles.length;
        int sum = doubles[0].length;
        int M = sum / N;

        RealMatrix[] result = new RealMatrix[length];
        for (int i = 0; i < length; i++) {
            double[][] tmp = new double[M][N];
            for (int j = 0; j < sum; j++) {
                tmp[j / N][j % M] = doubles[i][j];
            }
            result[i] = new Array2DRowRealMatrix(tmp);
        }

        return result;
    }
}
