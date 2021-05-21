package com.markby.opencv;

import com.markby.Histogram1D;
import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.image.BufferedImage;
import java.io.File;

import static com.markby.OpenCVUtils.*;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_GRAYSCALE;

public class ComputeHistogramDemo {
    public static void main(String[] args) {
//        ex1ComputeHistogram();
        ex2ComputeHistogramGraph();
    }

    static void ex1ComputeHistogram(){
        // 将图像加载为灰度，因为我们将计算单个通道的图像直方图
        Mat src = loadAndShowOrExit(new File("D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lena.jpg"), IMREAD_GRAYSCALE);

        //计算直方图
        Histogram1D h = new Histogram1D();
        double[] histogram = h.getHistogramAsArray(src);

        //打印直方图值
        for (int i = 0; i < histogram.length; i++) {
            System.out.println("" + i + ": " + Math.round(histogram[i]));
        }
        int numberOfPixels = src.cols() * src.rows();
        System.out.println("Number of pixels     : " + numberOfPixels);

        //验证直方图计算
        //Math.round(histogram.sum);
    }

    static void ex2ComputeHistogramGraph(){
        Mat src = loadAndShowOrExit(new File("D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lena.jpg"), IMREAD_GRAYSCALE);

        // Calculate histogram
        Histogram1D h = new Histogram1D();
        BufferedImage histogram = h.getHistogramImage(src);
        // Display the graph
        show(histogram, "Histogram");
    }
}
