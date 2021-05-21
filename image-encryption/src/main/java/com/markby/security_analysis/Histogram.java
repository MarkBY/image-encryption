package com.markby.security_analysis;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class Histogram {
    public static void main(String[] args) {
        String url = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\5.1.13_encryptOut_1.png";

        // 将彩色图像灰度图像
        Mat image = imread(url, IMREAD_GRAYSCALE);
        UByteIndexer indexer = image.createIndexer();

        int rows = image.rows();
        int cols = image.cols();

        int[] hist = new int[256];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                hist[indexer.get(i, j)]++;
            }
        }
        System.out.println(Arrays.toString(hist));
        for (int i = 0; i < 256; i++) {
            hist[i] = i;
        }
        System.out.println(Arrays.toString(hist));
    }
}
