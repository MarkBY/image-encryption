package com.markby.encryption;

import com.markby.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;
import java.util.Comparator;

public class ParallelCompressiveSensingAndDNASequence_v2 {

    public static void main(String[] args) {
        // 1 获取并处理数据
//        int[][] image = new int[][]{
//                {214, 18, 2, 52, 88, 178, 106, 65, 180, 60, 36, 161, 79, 246, 191, 251},
//                {238, 215, 41, 20, 62, 175, 84, 170, 178, 53, 137, 198, 236, 204, 155, 46},
//                {103, 68, 155, 240, 106, 212, 101, 21, 29, 45, 155, 235, 69, 216, 199, 197},
//                {117, 250, 186, 52, 29, 79, 82, 37, 116, 37, 200, 71, 219, 242, 117, 25},
//                {33, 104, 220, 45, 221, 33, 116, 252, 21, 204, 225, 116, 155, 252, 244, 85},
//                {34, 231, 249, 33, 223, 26, 203, 187, 5, 231, 11, 167, 107, 177, 88, 199},
//                {155, 132, 10, 170, 17, 30, 111, 78, 156, 232, 242, 193, 149, 236, 152, 5},
//                {187, 68, 187, 180, 91, 231, 23, 209, 63, 51, 3, 41, 215, 65, 13, 169},
//                {163, 7, 124, 188, 242, 169, 210, 241, 129, 177, 212, 168, 2, 211, 110, 182},
//                {1, 90, 9, 48, 220, 56, 34, 230, 113, 186, 237, 206, 112, 192, 169, 248},
//                {48, 191, 224, 8, 237, 149, 254, 19, 216, 131, 159, 149, 189, 159, 64, 218},
//                {245, 91, 106, 243, 240, 12, 125, 90, 183, 130, 170, 72, 133, 211, 7, 159},
//                {12, 3, 164, 190, 186, 69, 50, 161, 77, 130, 197, 223, 29, 192, 97, 225},
//                {26, 15, 152, 207, 80, 231, 101, 56, 105, 88, 96, 85, 127, 33, 102, 205},
//                {205, 133, 12, 195, 121, 195, 197, 2, 219, 119, 38, 182, 222, 1, 135, 140},
//                {51, 107, 19, 166, 77, 149, 64, 142, 57, 198, 31, 221, 172, 43, 155, 173},
//        };
        int m = 4096;
        int[][] image = ImageUtils.getRandomData(m, m, 2021421);

        int height = image.length, width = image[0].length;
        int[] data = new int[height * width];
        for (int i = 0; i < data.length; i++) {
            data[i] = image[i / height][i % width];
        }

        // sha256给图像加密得到
        int[] sha256 = SHA256.getSHA256(data);

        long startTime = System.currentTimeMillis(); //获取开始时间
        // 2 加密
        int[] encrypt = encrypt(image, sha256);
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("单线程程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间

//        // 3 解密
//        int[] decrypt = decrypt(encrypt, height, width, sha256);
//
//        System.out.println();

    }

    private static int[] encrypt(int[][] image, int[] sha256) {

        int height = image.length, width = image[0].length;
        int[] data = new int[height * width];
        for (int i = 0; i < data.length; i++) {
            data[i] = image[i / height][i % width];
        }

        // 1 密钥结构
        // 用户设置初值
        double x_01 = 1, r_01 = 1,

                x_02 = 2, r_02 = 2,

                x_03 = 3, r_03 = 3;

        // 2 并行压缩感知过程
        // 对图像进行离散小波变换后进行稀疏处理。
        double[] data_dwt = DCT.DCT1(MatrixUtils.intArrayToDoubleArray(data));
        // 然后用初始值a = 15, b = 8的Arnold变换对生成的图像进行排列，并重复25次以求得平均稀疏度。
        double[] arnold_dataOut = new double[data_dwt.length];
        int count = 25, a = 15, b = 8;
        Arnold.arnold(data_dwt, arnold_dataOut, height, count, a, b);

        // 2.1 计算LTS的初值x01和控制参数r01
        // PCS测量矩阵的LTS的参数
        double sum_x01 = sha256[0] + sha256[1] + sha256[2] + sha256[3] + sha256[4] + sha256[5];
        double sum_r01 = sha256[6] + sha256[7] + sha256[8] + sha256[9] + sha256[10] + sha256[11];
        double x01 = (sum_x01 / 256 + x_01) % 1;
        double r01 = (sum_r01 / 256 + r_01) % 4;

        // 2.2 迭代LTS 1000 + 2N次，得到混沌序列{T}，去除前1000个值，避免瞬态效应。
        int N = height;
        int M = N / 2;
        int t = 1000;
        double[] lts = CompositeChaos.LTS(x01, r01, 2 * N, t);

        // 2.3 每两对，分别计算初值和控制参数。
        // 2.3 用x2i−1,r2i 对LTS进行 1000 + M N d次迭代，得到混沌序列{Ti}。
        // d为采样间隔。利用混沌序列按列方式构造测量矩阵Phi _{i}。
        double[][] Phi = new double[N][];
        for (int i = 0; i < N; i++) {
            double x2i_1 = (x01 + lts[2 * i]) % 1;
            double r2i = (r01 + 4 * lts[2 * i + 1]) % 4;
            // TODO 间隔采样
            int d = 1;
            double[] lts1 = CompositeChaos.LTS(x2i_1, r2i, M * N * d, t);
            Phi[i] = lts1;
        }

        // 2.5 利用上面得到的测量矩阵，计算每一列的测量值yi。将所有测量值组合成矩阵I和输出。
        RealMatrix matrix = new Array2DRowRealMatrix(MatrixUtils.matrix1DTo2D(arnold_dataOut, height, width));

        RealMatrix[] Phis = MatrixUtils.doubleArrayToMatrix(Phi, N);
        RealMatrix matrixI = new Array2DRowRealMatrix(M, N);

        for (int i = 0; i < N; i++) {
            RealMatrix columnMatrix = matrix.getColumnMatrix(i);
            RealMatrix multiply = Phis[i].multiply(columnMatrix);
            matrixI.setColumnMatrix(i, multiply);
        }

        double[][] I_d = matrixI.getData();
        // 3 DNA操作的过程
        // 3.1 为方便DNA操作，将测量值矩阵I量化到[0,255]的范围内。
        int[] I = MatrixUtils.normalize4ScaleToArray(I_d, 255);

        // 3.2 计算LSS的初值x02和控制参数r02
        // 获得DNA操作矩阵的LSS初始值
        double xor_x02 = sha256[12] ^ sha256[13] ^ sha256[14] ^ sha256[15] ^ sha256[16] ^ sha256[17];
        double xor_r02 = sha256[18] ^ sha256[19] ^ sha256[20] ^ sha256[21] ^ sha256[22] ^ sha256[23];
        double x02 = (xor_x02 / 256 + x_02) % 1;
        double r02 = (xor_r02 / 256 + r_02) % 4;

        // 3.3 LSS迭代1000 + NM次，得到混沌序列{P}，去除前1000个值，避免瞬态效应。
        double[] P = CompositeChaos.LSS(x02, r02, M * N, 1000);

        // 3.4 将序列{P}中的每个元素转换为0 ~ 255范围内的整数。然后将该序列转换为一个M × N阶的二维随机矩阵R。
        int[] R = new int[M * N];
        for (int i = 0; i < P.length; i++) {
            R[i] = (int) (Math.round(P[i] * 1000) % 256);
        }

        // 3.5 将I, R分成4 × 4个图像块。共有M N/16个。生成另一个长度为1000 + M N/4的LSS混沌序列{G}。
        double sumI = 0;
        double sumR = 0;
        for (int i = 0; i < I.length; i++) {
            sumI += I[i];
            sumR += R[i];
        }
        double x2 = (sumI / M / N) % 1;
        double r2 = (sumR / M / N) % 4;

        double[] G = CompositeChaos.LSS(x2, r2, M * N / 4, 1000);

        // 3.6 将{G}等分为长度M N / 16的四个混沌序列{A}， {B}， {C}， {D}。根据式(16)对序列{A}和{B}中的所有元素进行变换。
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
            D[i] = (int) (Math.round(D_copy[i] * 1000) % 8 + 1);

            C[i] = (int) (Math.round(C_copy[i] * 1000) % 3 + 1);
        }

        // 混沌序列{A}、{B}的值分别决定了I、R各子块的DNA编码规则。显然，{A},{B}的取值范围在1到8之间，对应8条DNA编码规则。
        // 例如，I中的第I个子图像块(从上到下、从左到右的顺序号)的DNA编码规则是ai。
        //
        //I和R之间的DNA运算由混沌序列{C}的值决定。有三种情况，如果ci= 1，使用加法。如果ci= 2，使用减法。If ci= 3, XOR。
        int[][] I_DNA = DNASequence.operationsOfDNA(I, R, A, B, C);

        // 3.7 对DNA操作后的DNA序列进行重新加密。
        // 3.8 对上一步得到的DNA序列进行解码，将像素值由二进制转换为十进制
        int[] data_DNAOut = DNASequence.re_encrypt(I_DNA, R, B, D);

        // 3.9 TSS生成伪随机序列，混淆图像
        // 置换过程中适用的TSS初始值
        double xor_x03 = (sha256[24] + sha256[25]) ^ (sha256[26] + sha256[27]);
        double xor_r03 = (sha256[28] + sha256[29]) ^ (sha256[30] + sha256[31]);
        double x03 = (xor_x03 / 256 + x_03) % 1;
        double r03 = (xor_r03 / 256 + r_03) % 4;

        double[] tss = CompositeChaos.TSS(x03, r03, data_DNAOut.length, 1000);

        ChaoticWindows[] cw_tss = new ChaoticWindows[tss.length];
        for (int i = 0; i < tss.length; i++) {
            cw_tss[i] = new ChaoticWindows(tss[i], i);
        }
        Arrays.sort(cw_tss, Comparator.comparing(ChaoticWindows::getXi));

        int[] dataOut = new int[data_DNAOut.length];
        for (int i = 0; i < dataOut.length; i++) {
            dataOut[i] = data_DNAOut[cw_tss[i].getPosition()];
        }

        return dataOut;
    }

    private static int[] decrypt(int[] encrypt,int height, int width, int[] sha256) {
        // 解密与加密过程相反
        // 1 密钥结构
        // 用户设置初值
        double x_01 = 1, r_01 = 1,

                x_02 = 2, r_02 = 2,

                x_03 = 3, r_03 = 3;

        // 置换过程中适用的TSS初始值
        double xor_x03 = (sha256[24] + sha256[25]) ^ (sha256[26] + sha256[27]);
        double xor_r03 = (sha256[28] + sha256[29]) ^ (sha256[30] + sha256[31]);
        double x03 = (xor_x03 / 256 + x_03) % 1;
        double r03 = (xor_r03 / 256 + r_03) % 4;

        double[] tss = CompositeChaos.TSS(x03, r03, encrypt.length, 1000);

        ChaoticWindows[] cw_tss = new ChaoticWindows[tss.length];
        for (int i = 0; i < tss.length; i++) {
            cw_tss[i] = new ChaoticWindows(tss[i], i);
        }
        Arrays.sort(cw_tss, Comparator.comparing(ChaoticWindows::getXi));

        int[] tss_out = new int[encrypt.length];
        for (int i = 0; i < tss_out.length; i++) {
            // 反向操作
            tss_out[cw_tss[i].getPosition()] = encrypt[i];
        }

        return null;
    }

}
