package com.markby;

import java.awt.*;
import java.lang.reflect.Type;

public class NumTest {

    public static void main(String[] args) {
        int[] ints = {-0b10101010,0b01101010,0b00101010,0b10101010,0b10101010,0b00101010,0b00101010 };

        byte tmp1 = 0;
        for (int j = 0; j < 7; j++) {
            tmp1 += getSpecifiedBitValue(ints[j],8) << (7 - j);
        }
        System.out.println(Integer.toBinaryString(tmp1));
    }

    /**
     * Set the specified bit to 1
     *
     * @param originByte Raw byte value
     * @param bitIndex   bit index (From 0~7)
     * @return Final byte value
     */
    public static byte setSpecifiedBitTo1(byte originByte, int bitIndex) {
        return originByte |= (1 << bitIndex);
    }

    /**
     * Set the specified bit to 0
     *
     * @param originByte Raw byte value
     * @param bitIndex   bit index (From 0~7)
     * @return Final byte value
     */
    public static byte setSpecifiedBitTo0(byte originByte, int bitIndex) {
        return originByte &= ~(1 << bitIndex);
    }

    /**
     * Invert the specified bit
     *
     * @param originByte Raw byte value
     * @param bitIndex   bit index (From 0~7)
     * @return Final byte value
     */
    public static byte setSpecifiedBitToReverse(byte originByte, int bitIndex) {
        return originByte ^= (1 << bitIndex);
    }

    public static int setSpecifiedBitToReverse(int originByte, int bitIndex) {
        return originByte ^= (1 << bitIndex);
    }

    /**
     * Get the value of the specified bit
     *
     * @param originByte Raw byte value
     * @param bitIndex   bit index (From 0~7)
     * @return Final byte value
     */
    public static byte getSpecifiedBitValue(int originByte, int bitIndex) {
        return (byte) ((originByte) >> (bitIndex) & 1);
    }

}
