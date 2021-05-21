package com.markby;

public class MatrixTest {
    public static void main(String[] args) {
        int[][] randomData = ImageUtils.getRandomData(4, 6, 20210518);

        int[] ints = MatrixUtils.matrix2DTo1D(randomData);

        int[][] ints1 = MatrixUtils.matrix1DTo2D(ints, 6, 4);


    }
}
