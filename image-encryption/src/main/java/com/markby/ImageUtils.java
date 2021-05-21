package com.markby;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageUtils {
    public static int[] getImageData(String path){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("图像文件读取失败");
            e.printStackTrace();
        }

        // 获取图片宽度和高度
        int width = image.getWidth();
        int height = image.getHeight();

        // 存储图片信息的数组
        // int 32位
        // Alpha(0-7)
        // Red(8-15)
        // Blue(16-23)
        // Green(24-31)
        int[] imageData = new int[width * height];
        image.getRGB(0, 0, width, height, imageData, 0, width);
        return imageData;
    }


    // 10240 * 10240 = 104857600
    public static int[][] getRandomData(int m, int n, int seed){
        Random random = new Random(seed);

        int[][] result = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = random.nextInt(256) ;
            }
        }

        return result;
    }
}
