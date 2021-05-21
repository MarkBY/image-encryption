package com.markby;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    public static int[] getSHA256(int[] image) {
        MessageDigest messageDigest;
        int[] result = new int[32];
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(intArrayToByteArray(image));
            result = byteArrayToIntArray(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * int[]到byte[]
     */
    public static byte[] intArrayToByteArray(int[] array) {
        byte[] result = new byte[4 * array.length];
        int i = 0;
        while (i < result.length) {
            int t = i / 4;
            //由高位到低位
            result[i++] = (byte) ((array[t] >> 24) & 0xFF);
            result[i++] = (byte) ((array[t] >> 16) & 0xFF);
            result[i++] = (byte) ((array[t] >> 8) & 0xFF);
            result[i++] = (byte) (array[t] & 0xFF);
        }
        return result;
    }

    /**
     * byte[]转int[]
     */
    public static int[] byteArrayToIntArray(byte[] bytes) {
        int[] result = new int[bytes.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = bytes[i];
        }

        return result;
    }
}
