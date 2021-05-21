package com.markby;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConcurrentByPosition_v1 {
    public static void main(String[] args) throws IOException {
        // 读取图片
        String path = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lena.jpg";
        BufferedImage image = ImageIO.read(new File(path));

        // 获取图片宽度和高度
        int width = image.getWidth();
        int height = image.getHeight();

        int[] data;

        data = new int[width * height];
        image.getRGB(0, 0, width, height, data, 0, width);

        long startTime = System.currentTimeMillis(); //获取开始时间

        for (int i = 1; i <= 8; i++) {
            ImageRunnable ImageRunnable = new ImageRunnable("线程" + i, data, i);
            new Thread(ImageRunnable).start();
        }

        long endTime = System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间



    }

}

/**
 *
 */
class ImageRunnable implements Runnable {
    private int[] data;
    String name;

    // 位数
    int position;

    ImageRunnable(String name, int[] data, int position) {

        this.name = name;
        this.data = data;

        // 位数
        this.position = position;
    }

    @Override
    public void run() {
        for (int i = 0; i < data.length; i++) {
            System.out.println(name);
            int red = (data[i] >> 16) & 0xFF;
            // 分别处理8个比特位
            switch (position) {
                case 1:
                    System.out.println((red & 0x80) >> 7);
                    break;
                case 2:
                    System.out.println((red & 0x40) >> 6);
                    break;
                case 3:
                    System.out.println((red & 0x20) >> 5);
                    break;
                case 4:
                    System.out.println((red & 0x10) >> 4);
                    break;
                case 5:
                    System.out.println((red & 0x08) >> 3);
                    break;
                case 6:
                    System.out.println((red & 0x04) >> 2);
                    break;
                case 7:
                    System.out.println((red & 0x02) >> 1);
                    break;
                case 8:
                    System.out.println(red & 0x01);
            }
        }
    }
}
