package com.markby.encryption;

import com.markby.CompositeChaos;
import com.markby.ImageUtils;
import com.markby.MatrixUtils;
import com.markby.Sequence;

import java.util.Arrays;
import java.util.Comparator;

public class FastEncryptionParallelComputingSystem {
    public static void main(String[] args) {

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

        int len = 7680;

        int[][] image = ImageUtils.getRandomData(len, len, 2020425);

        int m = image.length, n = image[0].length;
        int p = 4;

        int[] K = {0x0123456,
                0xabcdef0,
                0x456789a,
                0xef01234,
                0x89abcde,
                0x2345678,
                0xcdef012,
                0x6789abc,
                0x0123456,
                0xabcdef0,
                0x456789a,
                0xef01234};


        long startTime = System.currentTimeMillis(); //??????????????????
        double miu = 3.99 + 0.01 * K[0] / 0xfffffff;

        double e = 1.0 * K[1] / 0xfffffff;

        double[] x = new double[10];

        for (int i = 0; i < x.length; i++) {
            x[i] = (double) K[i + 2] / 0xfffffff;
        }

        // ??????3:???????? e, x 0 (j)(j = 3,4,6?????????12)??????CML?????????CML 200 + max(m, n, 2p +  4)??????????????????????????????m??????????????????
        // ??????m (i)(i = 1,2,3?????????m)???????????????????????????n????????????????????????n (i)(i =  1,2,3?????????n)?????????2 p + 4????????????????????????
        // ??????(i) (i = 1???2???3???2 p + 4)?????????2 p + 4?????????????????????????????????B (i)  (i = 1???2???3???2 p + 4)?????????2 p + 4??????
        // ????????????????????????D(???)(i = 1???2???3???????????????6????????????2???p + 4????????????????????????E  (i)(i = 1,2,3?????????2p + 4)???
        double[][] cml = CompositeChaos.CML(x, e, miu, Math.max(m, Math.max(n, 2 * p + 4)), 200);

        double[] M = new double[m];
        double[] N = new double[n];

        double[] A = new double[2 * p + 4];
        double[] B = new double[2 * p + 4];
        double[] D = new double[2 * p + 4];
        double[] E = new double[2 * p + 4];

        for (int i = 0; i < m; i++) {
            M[i] = cml[0][cml[0].length - m + i];
        }

        for (int i = 0; i < n; i++) {
            N[i] = cml[1][cml[1].length - n + i];
        }

        for (int i = 0; i < A.length; i++) {
            A[i] = cml[2][cml[2].length - A.length + i];
            B[i] = cml[3][cml[3].length - A.length + i];
            D[i] = cml[4][cml[4].length - A.length + i];
            E[i] = cml[5][cml[5].length - A.length + i];
        }


        int[] Mi = new int[m];
        int[] H = new int[m];
        int[] Ni = new int[n];
        int[] S = new int[n];

        for (int i = 0; i < m; i++) {
            Mi[i] = (int) Math.floor(M[i] * Math.pow(10, 8)) + i;
            H[i] = Mi[i] % n;
        }

        for (int i = 0; i < n; i++) {
            Ni[i] = (int) Math.floor(N[i] * Math.pow(10, 8)) + i;
            S[i] = Ni[i] % m;
        }

        int[][] g1 = MatrixUtils.transpose(image);
        int[][] g3 = sortByArray(g1, Ni);
        int[][] g4 = circleShift(g3, S);
        g4 = MatrixUtils.transpose(g4);
        int[][] g6 = sortByArray(g4, Mi);
        int[][] g8 = circleShift(g6, H);

        int[] Fr = MatrixUtils.matrix2DTo1D(g8);
        int t = m * n / p;
        int[][] F = MatrixUtils.matrix1DTo2D(Fr, p, t);

        int[][] Gr = new int[F.length][F[0].length];
        for (int i = 0; i < p; i++) {
            Gr[i] = diffuse(F[i], A[i], B[i], D[i], E[i]);
        }

        for (int i = 0; i < p; i++) {
            int tmp = Gr[i][0];
            Gr[i][0] = Gr[i][Gr[0].length - 1];
            Gr[i][Gr[0].length - 1] = tmp;
            tmp = Gr[i][1];
            Gr[i][1] = Gr[i][Gr[0].length - 2];
            Gr[i][Gr[0].length - 2] = tmp;
        }

        int[][] transpose = MatrixUtils.transpose(Gr);

        int[] GA1 = transpose[0];
        int[] GA2 = transpose[1];

        int[] GB1 = diffuse(GA1, A[p], B[p], D[p], E[p]);
        int[] GB2 = diffuse(GA2, A[p + 2], B[p + 2], D[p + 2], E[p + 2]);

        int[] GC1 = invert(GB1);
        int[] GC2 = invert(GB2);

        int[] GD1 = diffuse(GC1, A[p + 1], B[p + 1], D[p + 1], E[p + 1]);
        int[] GD2 = diffuse(GC2, A[p + 3], B[p + 3], D[p + 3], E[p + 3]);

        for (int i = 0; i < p; i++) {
            Gr[i][0] = GD1[i];
            Gr[i][2] = GD2[i];
        }

        int[][] O = new int[Gr.length][Gr[0].length];

        for (int i = p + 4; i < 2 * p + 4; i++) {
            O[i - p - 4] = diffuse(Gr[i - p - 4], A[i], B[i], D[i], E[i]);
        }

        int[] O_1 = MatrixUtils.matrix2DTo1D(O);
        int[][] C = MatrixUtils.matrix1DTo2D(O_1, m, n);

        long endTime = System.currentTimeMillis(); //??????????????????
        System.out.println("??????????????????????????????" + (endTime - startTime) + "ms"); //????????????????????????
        System.out.println();
    }

    private static int[] invert(int[] ints) {
        int[] result = new int[ints.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = ints[ints.length - 1 - i];
        }

        return result;
    }

    private static int[] diffuse(int[] ints, double a, double b, double d, double e) {
        int len = ints.length;

        int[] Gr = new int[len + 2];

        Gr[0] = (int) ((a * 100000) % 256);
        Gr[1] = (int) ((b * 100000) % 256);

        double r = 3.99 + 0.01 * d;
        double x1 = e;

        for (int i = 0; i < len; i++) {
            x1 = r * x1 * (1 - x1);
            Gr[i + 2] = (int) ((Math.floor(Gr[i] + 100000 * Gr[i + 1] / 255 + x1 * 100000) + ints[i]) % 256);
        }

        return Arrays.copyOfRange(Gr, 2, Gr.length);
    }


    private static int[][] circleShift(int[][] g3, int[] s) {

        int[][] g4 = new int[g3.length][g3[0].length];

        for (int i = 0; i < g4.length; i++) {
            for (int j = 0; j < g4[0].length; j++) {
                g4[i][j] = g3[i][(j + s[i]) % g4[0].length];
            }
        }

        return g4;

    }

    private static int[][] sortByArray(int[][] g1, int[] ni) {

        Sequence[] sequences = new Sequence[ni.length];
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = new Sequence(ni[i], i);
        }


        Arrays.sort(sequences, Comparator.comparingInt(Sequence::getNum));

        int[][] g2 = new int[g1.length][g1[0].length];

        for (int i = 0; i < g2.length; i++) {
            g2[i] = g1[sequences[i].getPosition()];
        }

        return g2;

    }


}
