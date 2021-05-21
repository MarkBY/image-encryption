package com.markby.encryption;

import com.markby.Arnold;
import com.markby.CompositeChaos;
import com.markby.MatrixUtils;
import com.markby.SHA256;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class ParallelCompressiveSensingAndDNASequence_v1 {

    public static void main(String[] args) {
        // 1 获取并处理数据
        int[][] image = new int[][]{
                {214, 18, 2, 52, 88, 178, 106, 65, 180, 60, 36, 161, 79, 246, 191, 251},
                {238, 215, 41, 20, 62, 175, 84, 170, 178, 53, 137, 198, 236, 204, 155, 46},
                {103, 68, 155, 240, 106, 212, 101, 21, 29, 45, 155, 235, 69, 216, 199, 197},
                {117, 250, 186, 52, 29, 79, 82, 37, 116, 37, 200, 71, 219, 242, 117, 25},
                {33, 104, 220, 45, 221, 33, 116, 252, 21, 204, 225, 116, 155, 252, 244, 85},
                {34, 231, 249, 33, 223, 26, 203, 187, 5, 231, 11, 167, 107, 177, 88, 199},
                {155, 132, 10, 170, 17, 30, 111, 78, 156, 232, 242, 193, 149, 236, 152, 5},
                {187, 68, 187, 180, 91, 231, 23, 209, 63, 51, 3, 41, 215, 65, 13, 169},
                {163, 7, 124, 188, 242, 169, 210, 241, 129, 177, 212, 168, 2, 211, 110, 182},
                {1, 90, 9, 48, 220, 56, 34, 230, 113, 186, 237, 206, 112, 192, 169, 248},
                {48, 191, 224, 8, 237, 149, 254, 19, 216, 131, 159, 149, 189, 159, 64, 218},
                {245, 91, 106, 243, 240, 12, 125, 90, 183, 130, 170, 72, 133, 211, 7, 159},
                {12, 3, 164, 190, 186, 69, 50, 161, 77, 130, 197, 223, 29, 192, 97, 225},
                {26, 15, 152, 207, 80, 231, 101, 56, 105, 88, 96, 85, 127, 33, 102, 205},
                {205, 133, 12, 195, 121, 195, 197, 2, 219, 119, 38, 182, 222, 1, 135, 140},
                {51, 107, 19, 166, 77, 149, 64, 142, 57, 198, 31, 221, 172, 43, 155, 173},
        };

        int height = image.length, width = image[0].length;
        int[] data = new int[height * width];
        for (int i = 0; i < data.length; i++) {
            data[i] = image[i / height][i % width];
        }

        // sha256给图像加密得到
        int[] sha256 = SHA256.getSHA256(data);

        // 用户设置初值
        double x_01 = 1;
        double r_01 = 1;
        // PCS测量矩阵的LTS的参数
        double sum_x01 = sha256[0] + sha256[1] + sha256[2] + sha256[3] + sha256[4] + sha256[5];
        double sum_r01 = sha256[6] + sha256[7] + sha256[8] + sha256[9] + sha256[10] + sha256[11];
        double x01 = (sum_x01 / 256 + x_01) % 1;
        double r01 = (sum_r01 / 256 + r_01) % 4;

        // 用户设置初值
        double x_02 = 2;
        double r_02 = 2;
        // 获得DNA操作矩阵的LSS初始值
        double xor_x02 = sha256[12] ^ sha256[13] ^ sha256[14] ^ sha256[15] ^ sha256[16] ^ sha256[17];
        double xor_r02 = sha256[18] ^ sha256[19] ^ sha256[20] ^ sha256[21] ^ sha256[22] ^ sha256[23];
        double x02 = (xor_x02 / 256 + x_02) % 1;
        double r02 = (xor_r02 / 256 + r_02) % 4;

        // 用户设置初值
        double x_03 = 3;
        double r_03 = 3;
        // 置换过程中适用的TSS初始值
        double xor_x03 = (sha256[24] + sha256[25]) ^ (sha256[26] + sha256[27]);
        double xor_r03 = (sha256[28] + sha256[29]) ^ (sha256[30] + sha256[31]);
        double x03 = (xor_x03 / 256 + x_03) % 1;
        double r03 = (xor_r03 / 256 + r_03) % 4;

        int[] arnold_dataOut = new int[data.length];
        int count = 25, a = 15, b = 8;
        Arnold.arnold(data, arnold_dataOut, image.length, count, a, b);

        int N = image.length;
        int M = N / 2;
        int t = 1000;
        double[] lts = CompositeChaos.LTS(x01, r01, 2 * N, t);

        double[][] Phi = new double[N][];
        for (int i = 0; i < N; i++) {
            double x2i_1 = (x01 + lts[2 * i]) % 1;
            double r2i = (r01 + 4 * lts[2 * i + 1]) % 4;
            // TODO 间隔采样
            int d = 1;
            double[] lts1 = CompositeChaos.LTS(x2i_1, r2i, M * N * d, t);
            Phi[i] = lts1;
        }

        RealMatrix matrix = new Array2DRowRealMatrix(intArrayToDoubleArray(image));

        RealMatrix[] Phis = MatrixUtils.doubleArrayToMatrix(Phi, N);
        RealMatrix matrixI = new Array2DRowRealMatrix(M, N);

        for (int i = 0; i < N; i++) {
            RealMatrix columnMatrix = matrix.getColumnMatrix(i);
            RealMatrix multiply = Phis[i].multiply(columnMatrix);
            matrixI.setColumnMatrix(i, multiply);
        }

        double[][] I_d = matrixI.getData();
        // 改为一维数组
        int[][] I = MatrixUtils.normalize4Scale(I_d, 255);

        double[] P = CompositeChaos.LSS(x02, r02, M * N, 1000);
        // 改为一维数组
        int[][] R = new int[M][N];
        for (int i = 0; i < P.length; i++) {
            R[i / N][i % M] = (int) (Math.round(P[i] * 1000) % 256);
        }
        // 2.5
        double sumI = 0;
        double sumR = 0;
        for (int i = 0; i < I.length; i++) {
            for (int j = 0; j < I[0].length; j++) {
                sumI += I[i][j];
                sumR += R[i][j];
            }
        }
        double x2 = (sumI / M / N) % 1;
        double r2 = (sumR / M / N) % 4;

        double[] G = CompositeChaos.LSS(x2, r2, M * N / 4, 1000);

        // 2.6
        double[] A_copy = new double[M * N / 16],
                B_copy = new double[M * N / 16],
                C_copy = new double[M * N / 16],
                D_copy = new double[M * N / 16];

        System.arraycopy(G, 0, A_copy, 0, M * N / 16);
        System.arraycopy(G, M * N / 16, B_copy, 0, M * N / 16);
        System.arraycopy(G, M * N / 16 * 2, C_copy, 0, M * N / 16);
        System.arraycopy(G, M * N / 16 * 3, D_copy, 0, M * N / 16);

        int[] A = new int[M * N / 16],
            B = new int[M * N / 16],
            C = new int[M * N / 16],
            D = new int[M * N / 16];
        for (int i = 0; i < A.length; i++) {
            A[i] = (int) (Math.round(A_copy[i] * 1000) % 8 + 1);
            B[i] = (int) (Math.round(B_copy[i] * 1000) % 8 + 1);
            C[i] = (int) (Math.round(C_copy[i] * 1000) % 3 + 1);
        }



        System.out.println();
    }

    public static double[][] intArrayToDoubleArray(int[][] iArray) {
        double[][] result = new double[iArray.length][iArray[0].length];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = iArray[i][j];
            }
        }

        return result;
    }

}
