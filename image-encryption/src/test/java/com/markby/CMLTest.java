package com.markby;

public class CMLTest {
    public static void main(String[] args) {

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


        double miu = 3.99 + 0.01 * K[0] / 0xfffffff;

        double e = 1.0 * K[1] / 0xfffffff;

        double[] x = new double[10];

        for (int i = 0; i < x.length; i++) {
            x[i] = (double) K[i + 2] / 0xfffffff;
        }

        for (double d : x) {
            System.out.println(d);
        }

        double[][] cml = CML(x, e, miu, 20, 200);

        System.out.println();

    }

    public static double[][] CML(double[] x, double e, double r, int num, int t) {

        int len = x.length;
        double[][] result = new double[len][num + t];

        double[] tmp = new double[len];
        for (int i = 0; i < len; i++) {

            int i_1 = i - 1;
            int i$1 = i + 1;

            if (i == 0) i_1 = len - 1;
            if (i == len - 1) i$1 = 0;

            tmp[i] = (1 - e) * r * x[i] * (1 - x[i]) +
                    (e / 2) * (r * x[i_1] * (1 - x[i_1]) + r * x[i$1] * (1 - x[i$1]));
        }

        for (int i = 0; i < len; i++) {
            result[i][0] = tmp[i];
        }

        for (int n = 1; n < num + t; n++) {
            for (int i = 0; i < len; i++) {

                int i_1 = i - 1;
                int i$1 = i + 1;

                if (i == 0) i_1 = len - 1;
                if (i == len - 1) i$1 = 0;

                result[i][n] = (1 - e) * r * result[i][n - 1] * (1 - result[i][n - 1]) +
                        (e / 2) * (r * result[i_1][n - 1] * (1 - result[i_1][n - 1]) + r * result[i$1][n - 1] * (1 - result[i$1][n - 1]));
            }

        }

        return result;

    }
}
