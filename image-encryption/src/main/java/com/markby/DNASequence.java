package com.markby;

import java.util.Arrays;

public class DNASequence {

    public static void main(String[] args) {
        int i = 126;
        for (int j = 1; j <= 8; j++) {
            int[] ints = intToDNA(i, j);
            System.out.println(Arrays.toString(ints));
            System.out.println(DNAToInt(ints, j));
        }

    }

    // A - 0
    // T - 1
    // G - 2
    // C - 3
    private static final int[][] add = {
            {3, 2, 1, 0},
            {2, 3, 0, 1},
            {1, 0, 3, 2},
            {0, 1, 2, 3},
    };

    private static final int[][] sub = {
            {3, 0, 1, 2},
            {2, 3, 0, 1},
            {1, 2, 3, 0},
            {0, 1, 2, 3},
    };

    private static final int[][] xor = {
            {0, 1, 2, 3},
            {1, 0, 3, 2},
            {2, 3, 0, 1},
            {3, 2, 1, 0},
    };


    public static int[] intToDNA(int num, int rule) {
        int[] nums = new int[]{
                (num >> 6) & 0x3,
                (num >> 4) & 0x3,
                (num >> 2) & 0x3,
                num & 0x3,
        };

        int[] result = new int[4];
        switch (rule) {
            case 1:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 0)
                        result[i] = 0;
                    else if (nums[i] == 3)
                        result[i] = 1;
                    else if (nums[i] == 2)
                        result[i] = 2;
                    else
                        result[i] = 3;
                break;

            case 2:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 0)
                        result[i] = 0;
                    else if (nums[i] == 3)
                        result[i] = 1;
                    else if (nums[i] == 1)
                        result[i] = 2;
                    else
                        result[i] = 3;
                break;
            case 3:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 3)
                        result[i] = 0;
                    else if (nums[i] == 0)
                        result[i] = 1;
                    else if (nums[i] == 2)
                        result[i] = 2;
                    else
                        result[i] = 3;
                break;
            case 4:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 2)
                        result[i] = 0;
                    else if (nums[i] == 1)
                        result[i] = 1;
                    else if (nums[i] == 3)
                        result[i] = 2;
                    else
                        result[i] = 3;
                break;

            case 5:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 1)
                        result[i] = 0;
                    else if (nums[i] == 2)
                        result[i] = 1;
                    else if (nums[i] == 0)
                        result[i] = 2;
                    else
                        result[i] = 3;
                break;

            case 6:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 2)
                        result[i] = 0;
                    else if (nums[i] == 1)
                        result[i] = 1;
                    else if (nums[i] == 0)
                        result[i] = 2;
                    else
                        result[i] = 3;
                break;

            case 7:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 1)
                        result[i] = 0;
                    else if (nums[i] == 2)
                        result[i] = 1;
                    else if (nums[i] == 3)
                        result[i] = 2;
                    else
                        result[i] = 3;
                break;

            default:
                for (int i = 0; i < 4; i++)
                    if (nums[i] == 3)
                        result[i] = 0;
                    else if (nums[i] == 0)
                        result[i] = 1;
                    else if (nums[i] == 1)
                        result[i] = 2;
                    else
                        result[i] = 3;
        }
        return result;
    }

    public static int DNAToInt(int[] nums, int rule) {

        int result = 0;
        int[] p = new int[]{6, 4, 2, 0};
        switch (rule) {
            case 1:
                for (int i = 0; i < 4; i++) {
                    if (nums[i] == 0)
                        result += 0;
                    else if (nums[i] == 1)
                        result += 3 << p[i];
                    else if (nums[i] == 2)
                        result += 2 << p[i];
                    else
                        result += 1 << p[i];
                }
                break;
            case 2:
                for (int i = 0; i < 4; i++) {

                    if (nums[i] == 0)
                        result += 0;
                    else if (nums[i] == 1)
                        result += 3 << p[i];
                    else if (nums[i] == 2)
                        result += 1 << p[i];
                    else
                        result += 2 << p[i];
                }
                break;
            case 3:
                for (int i = 0; i < 4; i++) {

                    if (nums[i] == 0)
                        result += 3 << p[i];
                    else if (nums[i] == 1)
                        result += 0;
                    else if (nums[i] == 2)
                        result += 2 << p[i];
                    else
                        result += 1 << p[i];
                }
                break;
            case 4:
                for (int i = 0; i < 4; i++) {

                    if (nums[i] == 0)
                        result += 2 << p[i];
                    else if (nums[i] == 1)
                        result += 1 << p[i];
                    else if (nums[i] == 2)
                        result += 3 << p[i];
                    else
                        result += 0;
                }
                break;
            case 5:
                for (int i = 0; i < 4; i++) {

                    if (nums[i] == 0)
                        result += 1 << p[i];
                    else if (nums[i] == 1)
                        result += 2 << p[i];
                    else if (nums[i] == 2)
                        result += 0;
                    else
                        result += 3 << p[i];
                }
                break;

            case 6:
                for (int i = 0; i < 4; i++) {

                    if (nums[i] == 0)
                        result += 2 << p[i];
                    else if (nums[i] == 1)
                        result += 1 << p[i];
                    else if (nums[i] == 2)
                        result += 0;
                    else
                        result += 3 << p[i];
                }
                break;

            case 7:
                for (int i = 0; i < 4; i++) {

                    if (nums[i] == 0)
                        result += 1 << p[i];
                    else if (nums[i] == 1)
                        result += 2 << p[i];
                    else if (nums[i] == 2)
                        result += 3 << p[i];
                    else
                        result += 0;
                }
                break;

            default:
                for (int i = 0; i < 4; i++) {

                    if (nums[i] == 0)
                        result += 3 << p[i];
                    else if (nums[i] == 1)
                        result += 0;
                    else if (nums[i] == 2)
                        result += 1 << p[i];
                    else
                        result += 2 << p[i];
                }
        }
        return result;
    }

    public static int[][] operationsOfDNA(int[] I, int[] R, int[] A, int[] B, int[] C) {
        int block = A.length;
        int len = I.length / block;
        int[][] result = new int[I.length][4];
        for (int i = 0; i < block; i++) {
            int a = A[i], b = B[i], c = C[i];
            int tmp = i * len;
            for (int j = 0; j < len; j++) {
                int[] si = intToDNA(I[tmp + j], a);
                int[] sr = intToDNA(R[tmp + j], b);
                int[] operations = operations(si, sr, c);
                result[tmp + j] = operations;

            }
        }

        return result;
    }

    private static int[] operations(int[] si, int[] sr, int c) {

        switch (c) {
            case 1:
                return addDNA(si, sr);
            case 2:
                return subDNA(si, sr);
            case 3:
                return xorDNA(si, sr);
        }

        return null;
    }

    private static int[] xorDNA(int[] si, int[] sr) {
        return new int[]{
                xor[si[0]][sr[0]],
                xor[si[1]][sr[1]],
                xor[si[2]][sr[2]],
                xor[si[3]][sr[3]],
        };
    }

    private static int[] subDNA(int[] si, int[] sr) {
        return new int[]{
                sub[si[0]][sr[0]],
                sub[si[1]][sr[1]],
                sub[si[2]][sr[2]],
                sub[si[3]][sr[3]],
        };
    }

    private static int[] addDNA(int[] si, int[] sr) {
        return new int[]{
                add[si[0]][sr[0]],
                add[si[1]][sr[1]],
                add[si[2]][sr[2]],
                add[si[3]][sr[3]],
        };
    }

    public static int[] re_encrypt(int[][] I_DNA, int[] R, int[] B, int[] D) {
        int block = D.length;
        int len = I_DNA.length / block;
        int[] result = new int[I_DNA.length];
        for (int i = 0; i < block; i++) {
            int b = B[i], d = D[i];
            int tmp = i * len;
            for (int j = 0; j < len; j++) {
                int[] sr = intToDNA(R[tmp + j], b);
                if (tmp == 0) {
                    I_DNA[tmp + j] = addDNA(I_DNA[tmp + j], sr);
                } else {
                    I_DNA[tmp + j] = addDNA(addDNA(I_DNA[tmp + j - 1], I_DNA[tmp + j]), sr);
                }
                result[tmp + j] = DNAToInt(I_DNA[tmp + j], d);

            }
        }
        return result;
    }
}
