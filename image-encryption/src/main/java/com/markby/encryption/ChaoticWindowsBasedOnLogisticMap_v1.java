package com.markby.encryption;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ChaoticWindowsBasedOnLogisticMap_v1 {
    public static void main(String[] args) {
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
        Random random = new Random();
//        for (int i = 0; i < 16; i++) {
//            for (int j = 0; j < 16; j++) {
//                image[i][j] = random.nextInt(256);
//            }
//        }

        double x_sh = random.nextDouble();
        double miu_sh = 3.56 + (4 - 3.56) * random.nextDouble();
        int n_sh = 16 * 16;
        int t = 25;

        ChaoticWindows[] i_sh = log(x_sh, miu_sh, n_sh, t);
        Arrays.sort(i_sh, Comparator.comparing(ChaoticWindows::getXi));
        int[] imageOut = new int[16 * 16];
        for (int i = 0; i < n_sh; i++) {
            imageOut[i] = image[i_sh[i].getPosition() / 16][i_sh[i].getPosition() % 16];
        }


        double xs0 = random.nextDouble(), miu = 4;
        int n_window = 256;

        ChaoticWindows[] chaoticWindows = log(xs0, miu, n_window, t);
        Arrays.sort(chaoticWindows, Comparator.comparing(ChaoticWindows::getXi));

        int sum = 0;
        for (int i = 0; i < imageOut.length; i++) {
            sum += imageOut[i] ^ chaoticWindows[i % 256].getPosition();
        }

        double x_s = sum / (16 * 16 * 255);

        double x01 = 0.1,
                x02 = 0.2,
                x03 = 0.3,
                x04 = 0.4,
                x05 = 0.5,

                miu1 = 4,
                miu2 = 3.9,
                miu3 = 3.99,
                miu4 = 3.999,
                miu5 = 3.9999;

        x_s = (x_s + x01 + x02 + x03 + x04 + x05) / 6;
        double miu_s = (miu1 + miu2 + miu3 + miu4 + miu5) / 6;
        ChaoticWindows[] x = log(x_s, miu_s, 1, t);

        double x_sense = x[0].getXi();

        x01 = (x01 + x_sense) % 1;
        x02 = (x02 + x_sense) % 1;
        x03 = (x03 + x_sense) % 1;
        x04 = (x04 + x_sense) % 1;
        x05 = (x05 + x_sense) % 1;

        miu1 = (miu1 + miu_s) / 2;
        miu2 = (miu2 + miu_s) / 2;
        miu3 = (miu3 + miu_s) / 2;
        miu4 = (miu4 + miu_s) / 2;
        miu5 = (miu5 + miu_s) / 2;


        int n = 16 * 16 / 256;

        ChaoticWindows[] w_ch1 = log(x01, miu1, n, t);
        ChaoticWindows[] w_ch2 = log(x02, miu2, n, t);

        int[] ch1 = new int[n];
        int[] ch2 = new int[n];

        // CHi(j) = (floor(CHi(j) * 10^14)) mod 256
        for (int i = 0; i < n; i++) {
            ch1[i] = (int) (Math.floor(w_ch1[i].getXi() * (1 << 14)) % 256);
            ch2[i] = (int) (Math.floor(w_ch2[i].getXi() * (1 << 14)) % 256);
        }

        ChaoticWindows[] w_w1 = log(x03, miu3, 256, t);
        Arrays.sort(w_w1, Comparator.comparing(ChaoticWindows::getXi));

        ChaoticWindows[] w_w2 = log(x04, miu4, 256, t);
        Arrays.sort(w_w2, Comparator.comparing(ChaoticWindows::getXi));

        int[] w1 = new int[256];
        int[] w2 = new int[256];

        for (int i = 0; i < 256; i++) {
            w1[i] = w_w1[i].getPosition();
            w2[i] = w_w2[i].getPosition();
        }


        System.out.println();

    }

    public static ChaoticWindows[] log(double x0, double miu, int n, int t) {
        ChaoticWindows[] result = new ChaoticWindows[n];
        double tmp = miu * x0 * (1 - x0);
        for (int i = 0; i < t; i++) {
            tmp = miu * tmp * (1 - tmp);
        }
        result[0] = new ChaoticWindows(miu * tmp * (1 - tmp), 0);
        for (int i = 1; i < n; i++) {
            tmp = miu * result[i - 1].getXi() * (1 - result[i - 1].getXi());
            result[i] = new ChaoticWindows(tmp, i);
        }

        return result;
    }
}
