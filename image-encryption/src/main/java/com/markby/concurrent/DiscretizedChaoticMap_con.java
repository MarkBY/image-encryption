package com.markby.concurrent;

import com.markby.ImageUtils;
import com.markby.MatrixUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;

public class DiscretizedChaoticMap_con {
    public static void main(String[] args) {

        int m = 5000;
        int n = m;
        int[][] image = ImageUtils.getRandomData(m, m, 2021421);

        long startTime = System.currentTimeMillis(); //获取开始时间
        //int[] s = {m / 2, m / 16, m / 8, m / 16, m / 4};
        int[] s = {m / 8, m / 2, m / 4, m / 8};

        int[][] k1 = kTransformation(image, s);


        for (int i = 0; i < 8; i++) {
            k1 = mTransformation(k1);
            int[][] data = ImageUtils.getRandomData(m, n, 2021421);
            k1 = aTransformation(k1, data);
            k1 = sTransformation(k1);
            k1 = kTransformation(k1, s);
        }

        k1 = mTransformation(k1);
        int[][] data1 = ImageUtils.getRandomData(m, n, 2021421);
        k1 = aTransformation(k1, data1);
        k1 = kTransformation(k1, s);

        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("多线程程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间

        System.out.println();

    }

    private static int[][][] segmentation(int[][] k1, int num) {
        int [][][] result = new int[num][][];

        for (int i = 0; i < num; i++) {
            int len = k1.length / num;
            int start = i * len, end;
            if (i == len - 1){
                end = k1.length;
            }else {
                end = start + len;
            }
            int[][] part = new int[end -start][];
            for (int j = start; j < end; j++) {
                int[] tmp = Arrays.copyOf(k1[j], k1[j].length);
                part[j-start] = tmp;
            }
            result[i] = part;
        }

        return result;
    }

    private static int[][] kTransformation(int[][] data, int[] s) {
        int m = data.length, n = data[0].length;
        int[][] result = new int[m][n];

        int[][] block = new int[s.length][];

        for (int i = 0; i < s.length; i++) {
            int[] block_i = new int[s[i]];
            for (int j = 0; j < block_i.length; j++) {
                block_i[j] = m / s[i] * j;
            }
            block[i] = block_i;
        }

        int[] colNum = new int[s.length];
        int tmp = 0;
        for (int i = 0; i < colNum.length; i++) {
            colNum[i] = tmp;
            tmp += s[i];
        }

        int row = 0;
        RealMatrix matrix = new Array2DRowRealMatrix(MatrixUtils.intArrayToDoubleArray(data));
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                int colLen = block[i].length - 1;
                int rowLen = m / block[i].length - 1;
                RealMatrix subMatrix = matrix.getSubMatrix(block[i][j], block[i][j] + rowLen, colNum[i], colNum[i] + colLen);
                result[row++] = MatrixUtils.matrix2DTo1D(MatrixUtils.doubleArrayToIntArray(subMatrix.getData()));
            }
        }
//        int startRow, endRow, startColumn, endColumn;
        return result;
    }

    private static int[][] mTransformation(int[][] data) {

        int sum = 0;
        for (int[] data1d : data) {
            for (int i : data1d) {
                sum += i;
            }
        }
        for (int[] data1d : data) {
            for (int i : data1d) {
                i = i ^ sum;
            }
        }

        return data;
    }

    private static int[][] sTransformation(int[][] data) {

        int[] d = MatrixUtils.matrix2DTo1D(S_box);
        int[] data_1d = MatrixUtils.matrix2DTo1D(data);

        for (int i = 0; i < data_1d.length; i++) {
            int iter = i / d.length * d.length;
            for (int j = 0; j < d.length; j++) {
                int tmp = data_1d[iter + j];
                data_1d[iter + j] = data_1d[iter + d[j]];
                data_1d[iter + d[j]] = tmp;
                i++;
            }
        }

        return MatrixUtils.matrix1DTo2D(data_1d, data.length, data[0].length);
    }

    private static int[][] aTransformation(int[][] data, int[][] data1) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = data[i][j] ^ data1[i][j];
            }
        }
        return data;
    }

    final static int[][] S_box = {
            {161, 85, 129, 224, 176, 50, 207, 177, 48, 205, 68, 60, 1, 160, 117, 46},
            {130, 124, 203, 58, 145, 14, 115, 189, 235, 142, 4, 43, 13, 51, 52, 19},
            {152, 153, 83, 96, 86, 133, 228, 136, 175, 23, 109, 252, 236, 49, 167, 92},
            {106, 94, 81, 139, 151, 134, 245, 72, 172, 171, 62, 79, 77, 231, 82, 32},
            {238, 22, 63, 99, 80, 217, 164, 178, 0, 154, 240, 188, 150, 157, 215, 232},
            {180, 119, 166, 18, 141, 20, 17, 97, 254, 181, 184, 47, 146, 233, 113, 120},
            {54, 21, 183, 118, 15, 114, 36, 253, 197, 2, 9, 165, 132, 204, 226, 64},
            {107, 88, 55, 8, 221, 65, 185, 234, 162, 210, 250, 179, 61, 202, 248, 247},
            {213, 89, 101, 108, 102, 45, 56, 5, 212, 10, 12, 243, 216, 242, 84, 111},
            {143, 67, 93, 123, 11, 137, 249, 170, 27, 223, 186, 95, 169, 116, 163, 25},
            {174, 135, 91, 104, 196, 208, 148, 24, 251, 39, 40, 31, 16, 219, 214, 74},
            {140, 211, 112, 75, 190, 73, 187, 244, 182, 122, 193, 131, 194, 149, 121, 76},
            {156, 168, 222, 34, 241, 70, 255, 229, 246, 90, 53, 225, 100, 30, 37, 237},
            {103, 126, 38, 200, 44, 209, 42, 29, 41, 218, 71, 155, 78, 125, 173, 28},
            {128, 87, 239, 3, 191, 158, 199, 138, 227, 59, 69, 220, 195, 66, 192, 230},
            {198, 26, 159, 6, 127, 201, 144, 206, 98, 33, 35, 7, 105, 147, 57, 110}};
}
