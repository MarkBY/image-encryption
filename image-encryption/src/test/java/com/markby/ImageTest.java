package com.markby;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageTest {

    public static void main(String[] args) throws IOException {
        String path = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lena.jpg";
        BufferedImage image = ImageIO.read(new File(path));

        int[][] array = convertImageToArray(image);
        Color color = new Color(array[0][0]);

        System.out.println(array[0][0]);
        System.out.println(color.getRGB());
        System.out.println(color.getRed());

        // 输出图片到指定文件
        // writeImageFromArray("D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lenaOut.jpg", "png", rgbArray1);//这里写你要输出的绝对路径+文件名
        System.out.println("图片输出完毕!");

    }

    public static int[][] convertImageToArray(BufferedImage bf) {
        // 获取图片宽度和高度
        int width = bf.getWidth();
        int height = bf.getHeight();
        // 将图片sRGB数据写入一维数组
        int[] data = new int[width * height];

        bf.getRGB(0, 0, width, height, data, 0, width);

        // 将一维数组转换为为二维数组
        int[][] rgbArray = new int[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                rgbArray[i][j] = data[i * width + j];
        return rgbArray;
    }

    public static void writeImageFromArray(String imageFile, String type, int[][] rgbArray){
        // 获取数组宽度和高度
        int width = rgbArray[0].length;
        int height = rgbArray.length;
        // 将二维数组转换为一维数组
        int[] data = new int[width*height];
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                data[i*width + j] = rgbArray[i][j];
        // 将数据写入BufferedImage
        BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        bf.setRGB(0, 0, width, height, data, 0, width);
        // 输出图片
        try {
            File file= new File(imageFile);
            ImageIO.write((RenderedImage)bf, type, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
