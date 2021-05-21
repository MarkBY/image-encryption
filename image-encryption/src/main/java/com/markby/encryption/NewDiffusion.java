package com.markby.encryption;

import com.markby.*;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class NewDiffusion {
    public static void main(String[] args) {
        String url = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\5.1.13.tiff";

        // 将彩色图像灰度图像
        Mat image = imread(url, IMREAD_GRAYSCALE);
        UByteIndexer indexer = image.createIndexer();

        OpenCVUtils.save(new File("D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\5.1.13_gray.png"), image);

        int rows = image.rows();
        int cols = image.cols();

        int[] imageData = new int[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                imageData[cols * i + j] = indexer.get(i, j);
            }
        }

        //imageData[imageData.length - 1] = 106;

        // 第一步，
        //将图片转换为byte数组，用SHA-256进行加密得到明文敏感的密钥，SHA-256生成的普通图像的哈希值是一个256位的密钥。密码系统的密钥由两部分组成。一部分由用户给出，另一部分由原始图像生成。
        int[] sha256 = SHA256.getSHA256(imageData);

        int[] dataOut = encrypt(imageData, sha256, rows, cols);
//        dataOut = encrypt(dataOut, sha256, rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                indexer.put(i, j, dataOut[cols * i + j]);
            }
        }
        JavaCVUtil.imShow(image, "output");


//        JavaCVUtil.imWrite(image,"D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\lenaEncryptOut.jpg");
        OpenCVUtils.save(new File("D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\5.1.13_encryptOut_1.png"), image);

        System.out.println();


        String urlOut = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\5.1.13_encryptOut_1.png";

        // 将彩色图像灰度图像
        //Mat imageEncry = imread(urlOut, IMREAD_GRAYSCALE);
        Mat imageEncry = imread(urlOut, IMREAD_GRAYSCALE);
        UByteIndexer indexerEncry = imageEncry.createIndexer();


        int[] imageEncryData = new int[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                imageEncryData[cols * i + j] = indexerEncry.get(i, j);
            }
        }


        int[] dataEncryOut = decrypt(imageEncryData, sha256, rows, cols);
//        dataEncryOut = decrypt(dataEncryOut, sha256, rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                indexerEncry.put(i, j, dataEncryOut[cols * i + j]);
            }
        }

        JavaCVUtil.imShow(imageEncry, "output");

        JavaCVUtil.imWrite(imageEncry,"D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\5.1.13_DecryptOut_1.tiff");

        System.out.println();

    }

    public static int[] encrypt(int[] imageData, int[] sha256,int rows,int cols){
        // 第二步，
        //对每一位进行像素向右移位，使用混沌序列I1，最后得到M。
        // 用户设置初值
        double x_01 = 0.1;
        double r_01 = 0.1;

        double sum_x01 = sha256[0] + sha256[1] + sha256[2] + sha256[3] + sha256[4] + sha256[5];
        double sum_r01 = sha256[6] + sha256[7] + sha256[8] + sha256[9] + sha256[10] + sha256[11];
        double x01 = (sum_x01 / 256 + x_01) % 1;
        double r01 = (sum_r01 / 256 + r_01) % 4;
        int n = rows * cols, t = 1000;

        // 用户设置初值
        double x_02 = 0.2;
        double r_02 = 0.2;
        //
        double xor_x02 = sha256[12] ^ sha256[13] ^ sha256[14] ^ sha256[15] ^ sha256[16] ^ sha256[17];
        double xor_r02 = sha256[18] ^ sha256[19] ^ sha256[20] ^ sha256[21] ^ sha256[22] ^ sha256[23];
        double x02 = (xor_x02 / 256 + x_02) % 1;
        double r02 = (xor_r02 / 256 + r_02) % 4;

        // 用户设置初值
        double x_03 = 0.3;
        double r_03 = 0.3;
        //
        double xor_x03 = (sha256[24] + sha256[25]) ^ (sha256[26] + sha256[27]);
        double xor_r03 = (sha256[28] + sha256[29]) ^ (sha256[30] + sha256[31]);
        double x03 = (xor_x03 / 256 + x_03) % 1;
        double r03 = (xor_r03 / 256 + r_03) % 4;

        // 用户设置初值
        byte H2_key = 0B1111;
        // 用户设置初值
        byte l2_key = 0B1111;

        // 洗牌
        ChaoticWindows[] I0 = ChaoticWindowsBasedOnLogisticMap_v2.log(0.01, 3.9999, n, t);
        Arrays.sort(I0, Comparator.comparing(ChaoticWindows::getXi));

        int[] imageData1 = new int[n];

        for (int i = 0; i < imageData1.length; i++) {
            // data[i] = dataOut[i_sh[i].getPosition()];
            imageData1[i] = imageData[I0[i].getPosition()];
        }

        double[] I1 = CompositeChaos.log(x01, 3.99, n, t);
        for (int i = 0; i < I1.length; i++) {
            int moveNum = (int) (Math.floor(I1[i] * 10000) % 8);
            byte[] bytes = new byte[8];
            for (int j = 0; j < bytes.length; j++) {
                bytes[bytes.length - 1 - j] = (byte) ((imageData1[i] >> j) & 1);
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
            imageData1[i] = tmp;
        }


        // 第三步，
        //将M分为高4位H1和低4位L1。H1与L1异或，得到H2。使用混沌序列I2，将L1重新排列得到L2。L2与H2异或得到L3。
        // 这一步主要是想让高四位与低四位产生联系，从而达到相互影响的目的。
        byte[] H1 = new byte[rows * cols];
        byte[] L1 = new byte[rows * cols];


        for (int i = 0; i < imageData1.length; i++) {
            L1[i] = (byte) (imageData1[i] & 0xF);
            H1[i] = (byte) (imageData1[i] >> 4);
        }


        for (int i = 0; i < H1.length; i++) {
            H1[i] = (byte) (H1[i] ^ L1[i]);
        }

        // 洗牌
        ChaoticWindows[] I2 = ChaoticWindowsBasedOnLogisticMap_v2.log(x02, 3.999, n, t);
        Arrays.sort(I2, Comparator.comparing(ChaoticWindows::getXi));

        byte[] L2 = new byte[rows * cols];

        for (int i = 0; i < L1.length; i++) {
            // data[i] = dataOut[i_sh[i].getPosition()];
            L2[i] = L1[I2[i].getPosition()];
        }


        for (int i = 0; i < L2.length; i++) {
            L2[i] = (byte) (L2[i] ^ H1[i]);
        }

        // 第四步，
        //高四位H2使用逻辑序列I3按从后向前的顺序扩散，低四位L3使用逻辑序列I4按从前向后的顺序扩散，
        // 分别得到H3，L4。这里可以用两个线程分别操作高四位和第四位。
        double[] I3 = CompositeChaos.log(x03, 3.99, n, t);
        for (int i = H1.length - 1; i >= 0; i--) {
            if (i == H1.length - 1) {
                H1[i] = (byte) (H1[i] ^ H2_key ^ (int) (Math.floor(I3[i] * 10000) % 16));
            } else {
                H1[i] = (byte) (H1[i] ^ H1[i + 1] ^ (int) (Math.floor(I3[i] * 10000) % 16));
            }
        }

        double[] I4 = CompositeChaos.log(x03, 3.99, n, t);
        for (int i = 0; i < L2.length; i++) {
            if (i == 0) {
                L2[i] = (byte) (L2[i] ^ l2_key ^ (int) (Math.floor(I4[i] * 10000) % 16));
            } else {
                L2[i] = (byte) (L2[i] ^ L2[i - 1] ^ (int) (Math.floor(I4[i] * 10000) % 16));
            }
        }


        // 第五步，
        //将H3，L4合并，得到最后的加密图像。
        int[] dataOut = new int[rows * cols];
        for (int i = 0; i < dataOut.length; i++) {
            dataOut[i] = (H1[i] << 4) + L2[i];
        }

        return dataOut;
    }

    private static int[] decrypt(int[] imageEncryData, int[] sha256, int rows, int cols) {

        // 与加密过程相反
        double x_01 = 0.1;
        double r_01 = 0.1;

        double sum_x01 = sha256[0] + sha256[1] + sha256[2] + sha256[3] + sha256[4] + sha256[5];
        double sum_r01 = sha256[6] + sha256[7] + sha256[8] + sha256[9] + sha256[10] + sha256[11];
        double x01 = (sum_x01 / 256 + x_01) % 1;
        double r01 = (sum_r01 / 256 + r_01) % 4;
        int n = rows * cols, t = 1000;

        // 用户设置初值
        double x_02 = 0.2;
        double r_02 = 0.2;
        //
        double xor_x02 = sha256[12] ^ sha256[13] ^ sha256[14] ^ sha256[15] ^ sha256[16] ^ sha256[17];
        double xor_r02 = sha256[18] ^ sha256[19] ^ sha256[20] ^ sha256[21] ^ sha256[22] ^ sha256[23];
        double x02 = (xor_x02 / 256 + x_02) % 1;
        double r02 = (xor_r02 / 256 + r_02) % 4;

        // 用户设置初值
        double x_03 = 0.3;
        double r_03 = 0.3;
        //
        double xor_x03 = (sha256[24] + sha256[25]) ^ (sha256[26] + sha256[27]);
        double xor_r03 = (sha256[28] + sha256[29]) ^ (sha256[30] + sha256[31]);
        double x03 = (xor_x03 / 256 + x_03) % 1;
        double r03 = (xor_r03 / 256 + r_03) % 4;

        // 拆成高4位和低4位
        byte[] H1 = new byte[rows * cols];
        byte[] L2 = new byte[rows * cols];


        for (int i = 0; i < imageEncryData.length; i++) {
            L2[i] = (byte) (imageEncryData[i] & 0xF);
            H1[i] = (byte) (imageEncryData[i] >> 4);
        }

        double[] log3 = CompositeChaos.log(x03, 3.99, n, t);
        byte H2_key = 0B1111;
        for (int i = 0; i < H1.length; i++) {
            if (i == H1.length - 1) {
                H1[i] = (byte) (H1[i] ^ H2_key ^ (int) (Math.floor(log3[i] * 10000) % 16));
            } else {
                H1[i] = (byte) (H1[i] ^ H1[i + 1] ^ (int) (Math.floor(log3[i] * 10000) % 16));
            }
        }

        double[] log4 = CompositeChaos.log(x03, 3.99, n, t);
        byte l2_key = 0B1111;
        for (int i = L2.length - 1; i >= 0; i--) {
            if (i == 0) {
                L2[i] = (byte) (L2[i] ^ l2_key ^ (int) (Math.floor(log4[i] * 10000) % 16));
            } else {
                L2[i] = (byte) (L2[i] ^ L2[i - 1] ^ (int) (Math.floor(log4[i] * 10000) % 16));
            }
        }

        for (int i = 0; i < L2.length; i++) {
            // data[i] = dataOut[i_sh[i].getPosition()];
            L2[i] = (byte) (L2[i] ^ H1[i]);
        }

        // 洗牌
        ChaoticWindows[] i_sh = ChaoticWindowsBasedOnLogisticMap_v2.log(x02, 3.999, n, t);
        byte[] L1 = new byte[rows * cols];
        Arrays.sort(i_sh, Comparator.comparing(ChaoticWindows::getXi));
        for (int i = 0; i < L1.length; i++) {
            L1[i_sh[i].getPosition()] = L2[i];
        }

        for (int i = 0; i < H1.length; i++) {
            H1[i] = (byte) (H1[i] ^ L1[i]);
        }


        // 第五步，
        //将H3，L4合并，用逻辑序列I5，重新排列，得到最后的加密图像。
        int[] dataOut = new int[rows * cols];
        for (int i = 0; i < dataOut.length; i++) {
            dataOut[i] = (H1[i] << 4) + L1[i];
        }

        double[] log = CompositeChaos.log(x01, 3.99, n, t);
        for (int i = 0; i < log.length; i++) {
            int moveNum = 8 - (int) (Math.floor(log[i] * 10000) % 8);
            byte[] bytes = new byte[8];
            for (int j = 0; j < bytes.length; j++) {
                bytes[bytes.length - 1 - j] = (byte) ((dataOut[i] >> j) & 1);
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
            dataOut[i] = tmp;
        }

        // 洗牌
        ChaoticWindows[] I0 = ChaoticWindowsBasedOnLogisticMap_v2.log(0.01, 3.9999, n, t);
        Arrays.sort(I0, Comparator.comparing(ChaoticWindows::getXi));

        int[] imageData1 = new int[n];

        for (int i = 0; i < imageData1.length; i++) {
            // L1[i_sh[i].getPosition()] = L2[i];
            imageData1[I0[i].getPosition()] = dataOut[i];
        }

        return imageData1;
    }
}
