package com.markby;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

/**功能说明：JavaCV工具类
 *
 */
public class JavaCVUtil {
    /**
     *
     * 功能说明:显示图像
     * @param mat
     * 要显示的mat类型图像
     * @param title
     * 窗口标题
     * @exception:
     *
     */
    public static void imShow(Mat mat, String title) {
        //opencv自带的显示模块，跨平台性欠佳，转为Java2D图像类型进行显示
        ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame(title, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));

    }
    /**
     *
     * 功能说明:保存mat到指定路径
     * @param mat
     * 要保存的Mat
     * @param filePath
     * 保存路径
     * @exception:
     *
     */
    public static boolean imWrite(Mat mat,String filePath){
        //不包含中文，直接使用opencv原生方法进行保存
        if(!containChinese(filePath)){
            return opencv_imgcodecs.imwrite(filePath, mat);
        }
        try {
            /**
             * 将mat转为java的BufferedImage
             */
            ToMat convert= new ToMat();
            Frame frame= convert.convert(mat);
            Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
            BufferedImage bufferedImage= java2dFrameConverter.convert(frame);
            ImageIO.write(bufferedImage, "PNG", new File(filePath));

            return true;
        } catch (Exception e) {
            System.out.println("保存文件出现异常:"+filePath);
            e.printStackTrace();
        }
        return false;
    }
    /**
     *
     * 功能说明:判断字符是否包含中文
     * @param inputString
     * @return boolean
     * @exception:
     *
     */
    private static boolean containChinese(String inputString){
        //四段范围，包含全面
        String regex ="[\\u4E00-\\u9FA5\\u2E80-\\uA4CF\\uF900-\\uFAFF\\uFE30-\\uFE4F]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        return matcher.find();
    }
}
