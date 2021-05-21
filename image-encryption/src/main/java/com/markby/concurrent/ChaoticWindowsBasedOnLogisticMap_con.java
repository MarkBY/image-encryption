package com.markby.concurrent;

import com.markby.ImageUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class ChaoticWindowsBasedOnLogisticMap_con {

    public static void main(String[] args) throws InterruptedException {

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

        int m=5000;
        //int n=5120;

        int[][] image = ImageUtils.getRandomData(m, m, 2020425);

        int height = image.length, width = image[0].length;
        int[] data = new int[height * width];
        for (int i = 0; i < data.length; i++) {
            data[i] = image[i / height][i % width];
        }

        // 2 获取密钥
        double key = getKey(data);

        long startTime = System.currentTimeMillis(); //获取开始时间
        // 3 加密
        int[] encrypt = encrypt(data, key);

        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("多线程程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间

        // System.out.println(Arrays.toString(encrypt));
        // 4 解密
        //int[] decrypt = decrypt(encrypt, key);

        // 5 输出数据
        //System.out.println(Arrays.toString(decrypt));

    }

    public static double getKey(int[] data) {
        Random random = new Random();
        double xs0 = random.nextDouble(), miu = 4;
        int t = 25;

        ChaoticWindows[] chaoticWindows = log(xs0, miu, 256, t);
        Arrays.sort(chaoticWindows, Comparator.comparing(ChaoticWindows::getXi));

        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i] ^ chaoticWindows[i % 256].getPosition();
        }

        return sum / (data.length * 255);
    }

    public static int[] encrypt(int[] data, double key) throws InterruptedException {
        // 初始值，可以更改但加密和解密使用的要一样
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
        int t = 1000;
        double x_s = (key + x01 + x02 + x03 + x04 + x05) / 6;
        double miu_s = (miu1 + miu2 + miu3 + miu4 + miu5) / 5;
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

        int length = data.length;
        int n = length / 256;

        ChaoticWindows[] w_ch1 = log(x01, miu1, n, t);
        ChaoticWindows[] w_ch2 = log(x02, miu2, n, t);

        int[] ch1 = new int[n];
        int[] ch2 = new int[n];

        // CHi(j) = (floor(CHi(j) * 10^14)) mod 256
        for (int i = 0; i < n; i++) {
            ch1[i] = (int) (w_ch1[i].getXi() * (1 << 14) % 256);
            ch2[i] = (int) (w_ch2[i].getXi() * (1 << 14) % 256);
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
        // 像素洗牌
        ChaoticWindows[] i_sh = log(x05, miu5, length, t);
        Arrays.sort(i_sh, Comparator.comparing(ChaoticWindows::getXi));


        // 加密后数据
        int[] dataOut = new int[length];

        int threadNumber = 8;
        int blockLength = length / threadNumber;
        // 0 10240000 20480000 30720000
        CountDownLatch latch = new CountDownLatch(threadNumber);//使用java并发库concurrent

        for (int i = 0; i < threadNumber; i++) {
            int begin = i * blockLength, end;
            if (i == threadNumber - 1) {
                end = length;
            } else {
                end = begin + blockLength;
            }
            new Thread(() -> {
                for (int j = begin; j < end; j++) {
                    int block = j % 256;
                    dataOut[j] = data[j] ^ w1[block] ^ ch1[block % n];
                }
                latch.countDown();//让latch中的数值减一
            }).start();
        }
        //主线程
        latch.await();//阻塞当前线程直到latch中数值为零才执行

        // 用data转存
        for (int i = 0; i < length; i++) {
            data[i] = dataOut[i_sh[i].getPosition()];
        }

        CountDownLatch latch1 = new CountDownLatch(threadNumber);

        // 同理这里也可以并行
        for (int i = 0; i < threadNumber; i++) {
            int begin = i * blockLength, end;
            if (i == threadNumber - 1) {
                end = length;
            } else {
                end = begin + blockLength;
            }
            new Thread(() -> {
                for (int j = begin; j < end; j++) {
                    int block = j % 256;
                    dataOut[j] = data[j] ^ w2[block] ^ ch2[block % n];
                }
                latch1.countDown();//让latch中的数值减一
            }).start();
        }

        //主线程
        latch1.await();//阻塞当前线程直到latch中数值为零才执行

        return dataOut;
    }

    private static int[] decrypt(int[] data, double key) {
        // 初始值，可以更改但加密和解密使用的要一样
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
        int t = 1000;

        double x_s = (key + x01 + x02 + x03 + x04 + x05) / 6;
        double miu_s = (miu1 + miu2 + miu3 + miu4 + miu5) / 5;
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

        int length = data.length;
        int n = length / 256;

        ChaoticWindows[] w_ch1 = log(x01, miu1, n, t);
        ChaoticWindows[] w_ch2 = log(x02, miu2, n, t);

        int[] ch1 = new int[n];
        int[] ch2 = new int[n];

        // CHi(j) = (floor(CHi(j) * 10^14)) mod 256
        for (int i = 0; i < n; i++) {
            ch1[i] = (int) (w_ch1[i].getXi() * (1 << 14) % 256);
            ch2[i] = (int) (w_ch2[i].getXi() * (1 << 14) % 256);
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

        // 像素洗牌
        ChaoticWindows[] i_sh = log(x05, miu5, length, t);
        Arrays.sort(i_sh, Comparator.comparing(ChaoticWindows::getXi));

        // 解密后数据
        int[] dataOut = new int[length];

        // 同理这里也可以并行
        for (int i = 0; i < length; i++) {
            int block = i % 256;
            dataOut[i] = data[i] ^ w2[block] ^ ch2[block % n];
        }

        // 用data转存
        for (int i = 0; i < length; i++) {
            data[i_sh[i].getPosition()] = dataOut[i];
        }

        for (int i = 0; i < length; i++) {
            int block = i % 256;
            dataOut[i] = data[i] ^ w1[block] ^ ch1[block % n];
        }

        return dataOut;
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

class ChaoticWindows {
    private Double xi;
    private int position;

    public ChaoticWindows(Double xi, int position) {
        this.xi = xi;
        this.position = position;
    }

    public void setXi(Double xi) {
        this.xi = xi;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Double getXi() {
        return xi;
    }

    public int getPosition() {
        return position;
    }
}
