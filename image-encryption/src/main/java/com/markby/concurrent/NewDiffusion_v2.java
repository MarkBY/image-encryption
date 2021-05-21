package com.markby.concurrent;

import com.markby.CompositeChaos;
import com.markby.JavaCVUtil;
import com.markby.MatrixUtils;
import com.markby.OpenCVUtils;
import com.markby.Sequence;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class NewDiffusion_v2 {
    public static void main(String[] args) throws InterruptedException {
//        String url = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\4.1.05.tiff";
//        String url = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\map.jpg";
        String url = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\6000x4000.jpeg";

        // 将彩色图像灰度图像
        Mat image = imread(url, IMREAD_GRAYSCALE);
        UByteIndexer indexer = image.createIndexer();

        OpenCVUtils.save(new File("D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\6000x4000_gray.png"), image);

        int rows = image.rows();
        int cols = image.cols();

        int[] imageData = new int[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                imageData[cols * i + j] = indexer.get(i, j);
            }
        }
//        imageData[0] = 225;
//        System.out.println(imageData[0]);
//        imageData[12000000] = 209;
//        System.out.println(imageData[12000000]);
//        imageData[imageData.length - 1] = 171;
//        System.out.println(imageData[imageData.length - 1]);
        // 生成与明文相关的key，作为密钥的一部分需要同用户自己设置的密钥一起传输，也就是说密钥有两部分，一部分由明文生成，另一部分由用户自行设置
        double key = getKey(imageData);

        System.out.println(key);
        double[] K = {0.1,
                0.2,
                0.3,
                0.4,
                0.5,
                4,
                3.9,
                3.99,
                3.999,
                3.9999,
                0,
                15};

        long startTime = System.currentTimeMillis(); //获取开始时间

        int[] dataOut = encrypt(imageData, rows, cols, key, K);

        long endTime = System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                indexer.put(i, j, dataOut[cols * i + j]);
            }
        }

        OpenCVUtils.save(new File("D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\6000x4000_encryptOut_1.png"), image);

        System.out.println();

        String urlOut = "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\6000x4000_encryptOut_1.png";

        // 将彩色图像灰度图像
        Mat imageEncry = imread(urlOut, IMREAD_GRAYSCALE);
        UByteIndexer indexerEncry = imageEncry.createIndexer();


        int[] imageEncryData = new int[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                imageEncryData[cols * i + j] = indexerEncry.get(i, j);
            }
        }

//        K = new double[]{0xf123456,
//                0x789abcd,
//                0xef01234,
//                0x56789ab,
//                0xcdef012,
//                0x3456789,
//                0xabcdef0,
//                0x1234567,
//                0x89abcde,
//                0xf012345,
//                0x6789abc,
//                0xef01234,
//                0x56789ab,
//                0xcdef012};

        K = new double[]{0.1,
                0.2,
                0.3,
                0.4,
                0.5,
                4,
                3.9,
                3.99,
                3.999,
                3.9999,
                0,
                15};

        int[] dataEncryOut = decrypt(imageEncryData, rows, cols, key, K);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                indexerEncry.put(i, j, dataEncryOut[cols * i + j]);
            }
        }

        JavaCVUtil.imWrite(imageEncry, "D:\\workspace\\MarkBY\\IdeaProjects\\image-encryption\\src\\test\\java\\com\\markby\\image\\6000x4000_DecryptOut_1.png");

        System.out.println();

    }

    public static double getKey(int[] imageData) {

        int len = imageData.length, t = 2000;
        double[] log = CompositeChaos.log(0.111, 4, len, t);

        double sum = 0;

        for (int i = 0; i < len; i++) {
            if (i == 0) {
                sum += imageData[i] ^ (int) (Math.floor(log[i] * Math.pow(10, 14)) % 256);
            } else {
                sum += imageData[i] ^ imageData[i - 1] ^ (int) (Math.floor(log[i] * Math.pow(10, 14)) % 256);
            }
        }

        double result = sum / (len * 255.0);

        for (int i = 0; i < t; i++) {
            result = 4 * result * (1 - result);
        }

        return result;
    }

    private static int[] decrypt(int[] imageEncryData, int rows, int cols, double key, double[] K) throws InterruptedException {
        // 解析密钥
        double[] x_0 = new double[5];

        double x_mean = (K[0] + K[1] + K[2] + K[3] + K[4]) / 5;

        for (int i = 0; i < x_0.length; i++) {
            x_0[i] = (key + x_mean + K[i]) % 1;
        }

        double r_mean = (K[5] + K[6] + K[7] + K[8] + K[9]) / 5;

        double[] r = new double[5];
        for (int i = 0; i < r.length; i++) {
            r[i] = (r_mean + K[i + 5]) / 2;
        }

        // 用户设置初值
        byte H2_key = (byte) K[10];
        // 用户设置初值
        byte l2_key = (byte) K[11];

        int n = rows * cols, t = 1000;

        double[] I0 = CompositeChaos.log(x_0[0], r[0], n, t);
        double[] I1 = CompositeChaos.log(x_0[1], r[1], rows + cols, t);
        double[] I2 = CompositeChaos.log(x_0[2], r[2], rows + cols, t);
        double[] I3 = CompositeChaos.log(x_0[3], r[3], n, t);
        double[] I4 = CompositeChaos.log(x_0[4], r[4], n, t);

        byte[] H2 = new byte[rows * cols];
        byte[] L2 = new byte[rows * cols];


        for (int i = 0; i < imageEncryData.length; i++) {
            L2[i] = (byte) (imageEncryData[i] & 0xF);
            H2[i] = (byte) (imageEncryData[i] >> 4);
        }

        final CountDownLatch latch = new CountDownLatch(2);//使用java并发库concurrent
        new Thread(() -> {

            for (int i = L2.length - 1; i >= 0; i--) {
                if (i == 0) {
                    L2[i] = (byte) (L2[i] ^ l2_key ^ (int) (Math.floor(I4[i] * Math.pow(10, 14)) % 16));
                } else {
                    L2[i] = (byte) (L2[i] ^ L2[i - 1] ^ (int) (Math.floor(I4[i] * Math.pow(10, 14)) % 16));
                }
            }
            latch.countDown();//让latch中的数值减一
        }
        ).start();

        new Thread(() -> {
            for (int i = 0; i < H2.length; i++) {
                if (i == H2.length - 1) {
                    H2[i] = (byte) (H2[i] ^ H2_key ^ (int) (Math.floor(I3[i] * Math.pow(10, 14)) % 16));
                } else {
                    H2[i] = (byte) (H2[i] ^ H2[i + 1] ^ (int) (Math.floor(I3[i] * Math.pow(10, 14)) % 16));
                }
            }
            latch.countDown();//让latch中的数值减一
        }).start();

        //主线程
        latch.await();//阻塞当前线程直到latch中数值为零才执行

        for (int i = 0; i < L2.length; i++) {
            L2[i] = (byte) (L2[i] ^ H2[i]);
        }

        // 置换
        // Test
//        byte[] H2 = new byte[rows * cols];
//        byte[] L2 = new byte[rows * cols];
//        for (int i = 0; i < imageEncryData.length; i++) {
//            L2[i] = (byte) (imageEncryData[i] & 0xF);
//            H2[i] = (byte) (imageEncryData[i] >> 4);
//        }
        // Test End
        int[] Mi = new int[rows];
        int[] H = new int[rows];
        int[] Ni = new int[cols];
        int[] S = new int[cols];

        for (int i = 0; i < rows; i++) {
            Mi[i] = (int) Math.floor(I2[i] * Math.pow(10, 8)) + i;
            H[i] = cols - (Mi[i] % cols);
        }

        for (int i = 0; i < cols; i++) {
            Ni[i] = (int) Math.floor(I2[i + rows] * Math.pow(10, 8)) + i;
            S[i] = rows - (Ni[i] % rows);
        }

        byte[][] g6_H = MatrixUtils.matrix1DTo2D(H2, cols, rows);
        byte[][] g5_H = circleShift(g6_H, S);
        byte[][] g4_H = sortByArray_back(g5_H, Ni);
        byte[][] g3_H = MatrixUtils.transpose(g4_H);
        byte[][] g2_H = circleShift(g3_H, H);
        byte[][] g1_H = sortByArray_back(g2_H, Mi);

        byte[] H1 = MatrixUtils.matrix2DTo1D(g1_H);

        for (int i = 0; i < rows; i++) {
            Mi[i] = (int) Math.floor(I1[i] * Math.pow(10, 8) + i);
            H[i] = cols - (Mi[i] % cols);
        }

        for (int i = 0; i < cols; i++) {
            Ni[i] = (int) Math.floor(I1[i + rows] * Math.pow(10, 8)) + i;
            S[i] = rows - (Ni[i] % rows);
        }

        byte[][] g6_L = MatrixUtils.matrix1DTo2D(L2, cols, rows);
        byte[][] g5_L = circleShift(g6_L, S);
        byte[][] g4_L = sortByArray_back(g5_L, Ni);
        byte[][] g3_L = MatrixUtils.transpose(g4_L);
        byte[][] g2_L = circleShift(g3_L, H);
        byte[][] g1_L = sortByArray_back(g2_L, Mi);

        byte[] L1 = MatrixUtils.matrix2DTo1D(g1_L);
//
//

        for (int i = 0; i < H1.length; i++) {
            H1[i] = (byte) (H1[i] ^ L1[i]);
        }

        int[] dataOut = new int[rows * cols];
        for (int i = 0; i < dataOut.length; i++) {
            dataOut[i] = (H1[i] << 4) + L1[i];
        }


        for (int i = 0; i < I0.length; i++) {
            int moveNum = 7 - (int) (Math.floor(I0[i] * Math.pow(10, 14)) % 7);
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

        return dataOut;
    }

    public static int[] encrypt(int[] imageData, int rows, int cols, double key, double[] K) throws InterruptedException {

        // 解析密钥
        double[] x_0 = new double[5];

        double x_mean = (K[0] + K[1] + K[2] + K[3] + K[4]) / 5;

        for (int i = 0; i < x_0.length; i++) {
            x_0[i] = (key + x_mean + K[i]) % 1;
        }

        double r_mean = (K[5] + K[6] + K[7] + K[8] + K[9]) / 5;

        double[] r = new double[5];
        for (int i = 0; i < r.length; i++) {
            r[i] = (r_mean + K[i + 5]) / 2;
        }

        // 用户设置初值
        byte H2_key = (byte) K[10];
        // 用户设置初值
        byte l2_key = (byte) K[11];

        int n = rows * cols, t = 1000;

        double[] I0 = CompositeChaos.log(x_0[0], r[0], n, t);
        double[] I1 = CompositeChaos.log(x_0[1], r[1], rows + cols, t);
        double[] I2 = CompositeChaos.log(x_0[2], r[2], rows + cols, t);
        double[] I3 = CompositeChaos.log(x_0[3], r[3], n, t);
        double[] I4 = CompositeChaos.log(x_0[4], r[4], n, t);

        //对每一位进行像素向右移位，使用混沌序列I1，最后得到M。
        // 使用混沌序列I1，最后得到M。
        for (int i = 0; i < I0.length; i++) {
            int moveNum = (int) (Math.floor(I0[i] * Math.pow(10, 14)) % 7) + 1;
            byte[] bytes = new byte[8];
            for (int j = 0; j < bytes.length; j++) {
                bytes[bytes.length - 1 - j] = (byte) ((imageData[i] >> j) & 1);
            }
            // 右移moveNum
            for (int j = 1; j <= moveNum; j++) {
                byte tmp = bytes[bytes.length - 1];
                System.arraycopy(bytes, 0, bytes, 1, bytes.length - 2 + 1);
                bytes[0] = tmp;
            }
            int tmp = 0;
            for (int j = 0; j < bytes.length; j++) {
                tmp += bytes[j] << (bytes.length - 1 - j);
            }
            imageData[i] = tmp;
        }


        // 第三步，
        //将M分为高4位H1和低4位L1。H1与L1异或，得到H2。使用混沌序列I2，将L1重新排列得到L2。L2与H2异或得到L3。
        // 这一步主要是想让高四位与低四位产生联系，从而达到相互影响的目的。
        byte[] H1 = new byte[rows * cols];
        byte[] L1 = new byte[rows * cols];

        for (int i = 0; i < imageData.length; i++) {
            L1[i] = (byte) (imageData[i] & 0xF);
            H1[i] = (byte) (imageData[i] >> 4);
        }

        for (int i = 0; i < H1.length; i++) {
            H1[i] = (byte) (H1[i] ^ L1[i]);
        }

        // 置换
        int[] Mi = new int[rows];
        int[] H = new int[rows];
        int[] Ni = new int[cols];
        int[] S = new int[cols];


        for (int i = 0; i < rows; i++) {
            Mi[i] = (int) Math.floor(I1[i] * Math.pow(10, 8)) + i;
            H[i] = Mi[i] % cols;
        }

        for (int i = 0; i < cols; i++) {
            Ni[i] = (int) Math.floor(I1[i + rows] * Math.pow(10, 8)) + i;
            S[i] = Ni[i] % rows;
        }

        byte[][] g1_L = MatrixUtils.matrix1DTo2D(L1, rows, cols);
        byte[][] g2_L = sortByArray(g1_L, Mi);
        byte[][] g3_L = circleShift(g2_L, H);
        byte[][] g4_L = MatrixUtils.transpose(g3_L);
        byte[][] g5_L = sortByArray(g4_L, Ni);
        byte[][] g6_L = circleShift(g5_L, S);

        byte[] L2 = MatrixUtils.matrix2DTo1D(g6_L);

        for (int i = 0; i < rows; i++) {
            Mi[i] = (int) Math.floor(I2[i] * Math.pow(10, 8)) + i;
            H[i] = Mi[i] % cols;
        }

        for (int i = 0; i < cols; i++) {
            Ni[i] = (int) Math.floor(I2[i + rows] * Math.pow(10, 8)) + i;
            S[i] = Ni[i] % rows;
        }


        byte[][] g1_H = MatrixUtils.matrix1DTo2D(H1, rows, cols);
        byte[][] g2_H = sortByArray(g1_H, Mi);
        byte[][] g3_H = circleShift(g2_H, H);
        byte[][] g4_H = MatrixUtils.transpose(g3_H);
        byte[][] g5_H = sortByArray(g4_H, Ni);
        byte[][] g6_H = circleShift(g5_H, S);

        byte[] H2 = MatrixUtils.matrix2DTo1D(g6_H);
        // Test
//        int[] dataOut = new int[rows * cols];
//        for (int i = 0; i < dataOut.length; i++) {
//            dataOut[i] = (H2[i] << 4) + L2[i];
//        }
//        return dataOut;

        // Test End

        for (int i = 0; i < L2.length; i++) {
            L2[i] = (byte) (L2[i] ^ H2[i]);
        }

        // 第四步，
        //高四位H2使用逻辑序列I3按从后向前的顺序扩散，低四位L3使用逻辑序列I4按从前向后的顺序扩散，
        // 分别得到H3，L4。这里可以用两个线程分别操作高四位和第四位。
        final CountDownLatch latch = new CountDownLatch(2);//使用java并发库concurrent
        new Thread(() -> {
            for (int i = H2.length - 1; i >= 0; i--) {
                if (i == H2.length - 1) {
                    H2[i] = (byte) (H2[i] ^ H2_key ^ (int) (Math.floor(I3[i] * Math.pow(10, 14)) % 16));
                } else {
                    H2[i] = (byte) (H2[i] ^ H2[i + 1] ^ (int) (Math.floor(I3[i] * Math.pow(10, 14)) % 16));
                }
            }
            latch.countDown();//让latch中的数值减一
        }
        ).start();

        new Thread(() -> {
            for (int i = 0; i < L2.length; i++) {
                if (i == 0) {
                    L2[i] = (byte) (L2[i] ^ l2_key ^ (int) (Math.floor(I4[i] * Math.pow(10, 14)) % 16));
                } else {
                    L2[i] = (byte) (L2[i] ^ L2[i - 1] ^ (int) (Math.floor(I4[i] * Math.pow(10, 14)) % 16));
                }
            }
            latch.countDown();//让latch中的数值减一
        }).start();

        //主线程
        latch.await();//阻塞当前线程直到latch中数值为零才执行

        //将H3，L4合并，得到最后的加密图像。
        int[] dataOut = new int[rows * cols];
        for (int i = 0; i < dataOut.length; i++) {
            dataOut[i] = (H2[i] << 4) + L2[i];
        }

        return dataOut;
    }

    private static byte[][] sortByArray(byte[][] g1, int[] ni) {

        Sequence[] sequences = new Sequence[ni.length];
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = new Sequence(ni[i], i);
        }

        Arrays.sort(sequences, Comparator.comparingInt(Sequence::getNum));
        byte[][] result = new byte[g1.length][g1[0].length];
        for (int i = 0; i < g1.length; i++) {
            result[i] = g1[sequences[i].getPosition()];

        }
        return result;
    }

    private static byte[][] sortByArray_back(byte[][] g1, int[] ni) {

        Sequence[] sequences = new Sequence[ni.length];
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = new Sequence(ni[i], i);
        }

        Arrays.sort(sequences, Comparator.comparingInt(Sequence::getNum));
        byte[][] result = new byte[g1.length][g1[0].length];
        for (int i = 0; i < g1.length; i++) {
            result[sequences[i].getPosition()] = g1[i];

        }

        return result;
    }

    private static byte[][] circleShift(byte[][] g3, int[] s) {

        byte[][] g4 = new byte[g3.length][g3[0].length];

        for (int i = 0; i < g4.length; i++) {
            for (int j = 0; j < g4[0].length; j++) {
                g4[i][j] = g3[i][(j + s[i]) % g4[0].length];
            }
        }

        return g4;

    }
}
