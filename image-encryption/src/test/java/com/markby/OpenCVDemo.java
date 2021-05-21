package com.markby;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class OpenCVDemo {
    public static void main(String[] args) {
        //URL resource = OpenCVDemo.class.getResource("");
        String url = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lena.jpg";

        // 将彩色图像灰度图像
        Mat image = imread(url, IMREAD_GRAYSCALE);
        UByteIndexer indexer = image.createIndexer();

        int rows = image.rows();
        int cols = image.cols();

        int[] data = new int[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[cols * i + j] = indexer.get(i, j);
            }
        }


        display(image, "Input");

        JavaCVUtil.imWrite(image,"D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lenaGray.jpg");

        Mat imageOut = new Mat(rows, cols);
    }

    private static void display(Mat image, String caption) {
        CanvasFrame canvas = new CanvasFrame(caption, 1);

        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        canvas.showImage(converter.convert(image));
    }
}
