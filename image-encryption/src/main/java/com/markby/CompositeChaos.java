package com.markby;


public class CompositeChaos {
    public static double[] LTS(double x0, double r, int n, int t) {
        double[] result = new double[n];

        double tmp = 0;

        if (x0 < 0.5) {
            tmp = (r * x0 * (1 - x0) + (4 - r) * x0 / 2) % 1;
        } else {
            tmp = (r * x0 * (1 - x0) + (4 - r) * (1 - x0) / 2) % 1;
        }

        for (int i = 0; i < t; i++) {
            if (x0 < 0.5) {
                tmp = (r * tmp * (1 - tmp) + (4 - r) * tmp / 2) % 1;
            } else {
                tmp = (r * tmp * (1 - tmp) + (4 - r) * (1 - tmp) / 2) % 1;
            }
        }

        result[0] = tmp;
        for (int i = 1; i < n; i++) {
            double v = r * result[i - 1] * (1 - result[i - 1]);
            if (tmp < 0.5) {
                tmp = (v + (4 - r) * result[i - 1] / 2) % 1;
            } else {
                tmp = (v + (4 - r) * (1 - result[i - 1]) / 2) % 1;
            }
            result[i] = tmp;
        }

        return result;
    }

    public static double[] LSS(double x0, double r, int n, int t) {

        double[] result = new double[n];

        double tmp = (r * x0 * (1 - x0) + (4 - r) * Math.sin(Math.PI * x0) / 4) % 1;

        for (int i = 0; i < t; i++) {
            tmp = (r * tmp * (1 - tmp) + (4 - r) * Math.sin(Math.PI * tmp) / 4) % 1;
        }

        result[0] = tmp;

        for (int i = 1; i < n; i++) {
            result[i] = (r * result[i - 1] * (1 - result[i - 1]) + (4 - r) * Math.sin(Math.PI * result[i - 1]) / 4) % 1;
        }

        return result;
    }

    public static double[] TSS(double x0, double r, int n, int t) {

        double[] result = new double[n];

        double tmp = 0;
        double v1 = (4 - r) * Math.sin(Math.PI * x0) / 4;
        if (x0 < 0.5) {
            tmp = (r * x0 / 2 + v1) % 1;
        } else {
            tmp = (r * (1 - x0) / 2 + v1) % 1;
        }

        for (int i = 0; i < t; i++) {
            double v = (4 - r) * Math.sin(Math.PI * tmp) / 4;
            if (x0 < 0.5) {
                tmp = (r * tmp / 2 + v) % 1;
            } else {
                tmp = (r * (1 - tmp) / 2 + v) % 1;
            }
        }

        result[0] = tmp;

        for (int i = 1; i < n; i++) {
            double v = (4 - r) * Math.sin(Math.PI * result[i - 1]) / 4;
            if (tmp < 0.5) {
                tmp = (r * result[i - 1] / 2 + v) % 1;
            } else {
                tmp = (r * (1 - result[i - 1]) / 2 + v) % 1;
            }
            result[i] = tmp;
        }

        return result;
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

    public static double[] log(double x0, double miu, int n, int t) {
        double[] result = new double[n];
        double tmp = miu * x0 * (1 - x0);
        for (int i = 0; i < t; i++) {
            tmp = miu * tmp * (1 - tmp);
        }
        result[0] = miu * tmp * (1 - tmp);
        for (int i = 1; i < n; i++) {
            result[i] = miu * result[i - 1] * (1 - result[i - 1]);
        }

        return result;
    }


}
