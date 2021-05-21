package com.markby;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ImageConcurrentByPosition_v2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 读取图片
//        String path = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\map.jpg";
//        BufferedImage image = ImageIO.read(new File(path));

        // 获取图片宽度和高度
//        int width = image.getWidth();
//        int height = image.getHeight();

        // 存储图片信息的数组
        // int 32位
        // Alpha(0-7)
        // Red(8-15)
        // Blue(16-23)
        // Green(24-31)
        int[][] image  = ImageUtils.getRandomData(6400, 6400, 2020425);

        int height = image.length, width = image[0].length;

        int[] imageData = new int[width * height];
        int len = imageData.length;
        for (int i = 0; i < imageData.length; i++) {
            imageData[i] = image[i / height][i % width];
        }
        // image.getRGB(0, 0, width, height, imageData, 0, width);

        Data data = new Data(imageData);
        // System.out.println(imageData[0]);

        long startTime = System.currentTimeMillis(); //获取开始时间

        final CountDownLatch latch= new CountDownLatch(8);//使用java并发库concurrent
        for (int i = 1; i <= 8; i++) {
            int finalI = i;
            new Thread(() -> {
                for (int j = 1; j < len; j++){
                    data.decode(j, finalI);
                }
                latch.countDown();//让latch中的数值减一
            }, "线程" + i).start();
        }

        //主线程
        latch.await();//阻塞当前线程直到latch中数值为零才执行
        long endTime = System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间

        BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        bf.setRGB(0, 0, width, height, data.getData(), 0, width);
        // 输出图片
        try {
            String imageFile = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\TestOut.jpg";
            String type = "jpg";
            File file= new File(imageFile);
            ImageIO.write((RenderedImage)bf, type, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class Data {
    private int[] data;

    Lock lock = new ReentrantLock();

    public Data(int[] data) {
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
