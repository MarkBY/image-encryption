package com.markby;

import com.github.mervick.aes_everywhere.Aes256;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class AES {


    public static void main(String[] args) throws Exception {
//        String aesKey = "12345678901234567890123456789012";
//        byte[] test = {1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 0, 34, 23, 45, 25, 34};
//        System.out.println(Integer.toBinaryString(test[1]));
//        byte[] bytes = encrypt(test, aesKey);
//        byte[] decrypt = decrypt(bytes, aesKey);
//
//        System.out.println();


        String text = "10101010000010010101010010010101010001001010101010010100101010010101010011001010110";
        String pass = "PASSWORD";

        byte[] text_bytes = text.getBytes("UTF-8");
        byte[] pass_bytes = pass.getBytes("UTF-8");

        // strings encryption
        String encrypted = Aes256.encrypt(text, pass);
        System.out.println(encrypted);

        // bytes encryption
        byte[] encrypted_bytes = Aes256.encrypt(text_bytes, pass_bytes);
        System.out.println(encrypted_bytes);

        // strings decryption
        String decrypted = Aes256.decrypt(encrypted, pass);
        System.out.println(decrypted);

        // bytes decryption
        byte[] decrypted_bytes = Aes256.decrypt(encrypted_bytes, pass_bytes);
        System.out.println(decrypted_bytes);
    }


    private static final String IV_STRING = "abcdefghABCDEFGH";

    private static final String charset = "UTF-8";


    // AES 266 = KEY 长度是32个字符 = (32*8=266)
    public static byte[] encrypt(byte[] contentBytes, String key) {
        try {
            byte[] keyBytes = key.getBytes(charset);
            return aesEncryptBytes(contentBytes, keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE);
    }

    private static byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] initParam = IV_STRING.getBytes(charset);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        cipher.init(mode, keySpec, ivParameterSpec);
        return cipher.doFinal(contentBytes);
    }

    public static byte[] decrypt(byte[] encryptedBytes, String key) {
        try {
            byte[] keyBytes = key.getBytes(charset);
            return aesDecryptBytes(encryptedBytes, keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE);
    }
}

