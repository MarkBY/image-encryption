package com.markby.concurrent;

import com.markby.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class First {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 读取图片
//        String path = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\map.jpg";
//        BufferedImage image = ImageIO.read(new File(path));

        // 获取图片宽度和高度
//        int width = image.getWidth();
//        int height = image.getHeight();

        int m = 5120;

        int[][] image = ImageUtils.getRandomData(m, m, 2020425);

        int height = image.length, width = image[0].length;
        // 存储图片信息的数组
        // int 32位
        // Alpha(0-7)
        // Red(8-15)
        // Blue(16-23)
        // Green(24-31)
        //int[] imageData = new int[width * height];

        //image.getRGB(0, 0, width, height, imageData, 0, width);

        int[] imageData = MatrixUtils.matrix2DTo1D(image);
        int len = imageData.length;

        long startTime = System.currentTimeMillis(); //获取开始时间
        int[] dataAfterArnold = new int[width * height];
        int count = 25, a = 15, b = 8;
        Arnold.arnold(imageData, dataAfterArnold, height, count, a, b);

        double x0 = 0.1, r = 3.99;
        int n = width * height, t = 1000;

        double[] log = CompositeChaos.log(x0, r, n, t);
        for (int i = 0; i < log.length; i++) {
            int moveNum = (int) (Math.floor(log[i] * 10000) % 8);
            byte[] bytes = new byte[8];
            for (int j = 0; j < bytes.length; j++) {
                bytes[bytes.length - 1 - j] = (byte) ((dataAfterArnold[i] >> j) & 1);
            }
            // 右移moveNum
            for (int j = 1; j <= moveNum; j++) {
                byte tmp = bytes[bytes.length - 1];
                for (int k = bytes.length - 2; k >= 0; k--) {
                    bytes[k + 1] = bytes[k];
                }
                bytes[0] = tmp;
            }
            int tmp = 0;
            for (int j = 0; j < bytes.length; j++) {
                tmp += bytes[j] << (bytes.length - 1 - j);
            }
            dataAfterArnold[i] = tmp;
        }

        byte[][] con = new byte[8][dataAfterArnold.length / 7 + 1];
        int mod = dataAfterArnold.length % 7;

        int tmp = 0;
        int index_con = 0;
        // TODO 尾部需要再处理
        for (int i = 0; i < dataAfterArnold.length; i++) {
            byte tmp1 = 0, tmp2 = 0, tmp3 = 0, tmp4 = 0, tmp5 = 0, tmp6 = 0, tmp7 = 0, tmp8 = 0;
            int len_mod = 7;
            if (i == dataAfterArnold.length - mod) len_mod = mod;
            for (int j = 0; j < len_mod; j++) {
                tmp1 += getSpecifiedBitValue(dataAfterArnold[i], 7) << (6 - j);
                tmp2 += getSpecifiedBitValue(dataAfterArnold[i], 6) << (6 - j);
                tmp3 += getSpecifiedBitValue(dataAfterArnold[i], 5) << (6 - j);
                tmp4 += getSpecifiedBitValue(dataAfterArnold[i], 4) << (6 - j);
                tmp5 += getSpecifiedBitValue(dataAfterArnold[i], 3) << (6 - j);
                tmp6 += getSpecifiedBitValue(dataAfterArnold[i], 2) << (6 - j);
                tmp7 += getSpecifiedBitValue(dataAfterArnold[i], 1) << (6 - j);
                tmp8 += getSpecifiedBitValue(dataAfterArnold[i++], 0) << (6 - j);
            }
            con[0][index_con] = tmp1;
            con[1][index_con] = tmp2;
            con[2][index_con] = tmp3;
            con[3][index_con] = tmp4;
            con[4][index_con] = tmp5;
            con[5][index_con] = tmp6;
            con[6][index_con] = tmp7;
            con[7][index_con++] = tmp8;
            tmp = i;
        }
        // System.out.println(imageData[0]);

        String aesKey = "12345678901234567890123456789012";
        final CountDownLatch latch = new CountDownLatch(8);//使用java并发库concurrent
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            new Thread(() -> {
                con[finalI] = AES.encrypt(con[finalI], aesKey);
                latch.countDown();//让latch中的数值减一
            }, "线程" + i).start();
        }

        //主线程
        latch.await();//阻塞当前线程直到latch中数值为零才执行
        long endTime = System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间

//        BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
//        bf.setRGB(0, 0, width, height, data.getData(), 0, width);
//        // 输出图片
//        try {
//            String imageFile = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\mapOut.jpg";
//            String type = "jpg";
//            File file= new File(imageFile);
//            ImageIO.write((RenderedImage)bf, type, file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    // 从后向前数
    public static byte getSpecifiedBitValue(int originByte, int bitIndex) {
        return (byte) ((originByte) >> (bitIndex) & 1);
    }

}

class FirstData {
    private int[] data;

    Lock lock = new ReentrantLock();

    public FirstData(int[] data) {
        this.data = data;
    }

    public void decode(int index, int position) {

        lock.lock();    // 加锁

        try {
            // 分别处理8个比特位
            // 把Red通道的8个比特位，每个取反
            switch (position) {
                case 1:
                    // TODO 加密操作
                    data[index] = setSpecifiedBitToReverse(data[index], 23);
                    break;
                case 2:
                    data[index] = setSpecifiedBitToReverse(data[index], 22);
                    break;
                case 3:
                    data[index] = setSpecifiedBitToReverse(data[index], 21);
                    break;
                case 4:
                    data[index] = setSpecifiedBitToReverse(data[index], 20);
                    break;
                case 5:
                    data[index] = setSpecifiedBitToReverse(data[index], 19);
                    break;
                case 6:
                    data[index] = setSpecifiedBitToReverse(data[index], 18);
                    break;
                case 7:
                    data[index] = setSpecifiedBitToReverse(data[index], 17);
                    break;
                case 8:
                    data[index] = setSpecifiedBitToReverse(data[index], 16);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();  // 解锁
        }

    }

    public int[] getData() {
        return this.data;
    }

    // 从后向前数
    public static int setSpecifiedBitToReverse(int originByte, int bitIndex) {
        return originByte ^= (1 << bitIndex);
    }


}
