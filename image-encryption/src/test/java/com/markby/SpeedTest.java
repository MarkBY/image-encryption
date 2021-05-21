package com.markby;

import com.markby.concurrent.NewDiffusion_v2;

public class SpeedTest {
    public static void main(String[] args) throws InterruptedException {
        int m=9000;
        //int n=5120;

        int[][] image = ImageUtils.getRandomData(m, m, 2020425);
        int[] imageData = MatrixUtils.matrix2DTo1D(image);
        double[] K = {0.1,
                0.2,
                0.3,
                0.4,
                0.5,
                4,
                3.9,
                3.99,
                3.999,
                3.9999,
                0,
                15};
        long startTime = System.currentTimeMillis(); //获取开始时间
        double key = NewDiffusion_v2.getKey(imageData);

        int[] dataOut = NewDiffusion_v2.encrypt(imageData, m, m, key, K);

        long endTime = System.currentTimeMillis(); //获取结束时间

        System.out.println("提出的算法程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间
    }
}
