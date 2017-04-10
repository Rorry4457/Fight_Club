package com.auto.auto.Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Rorry on 2017/2/27.
 */

public class AES {

    /**
     * Turns array of bytes into string
     *
     * @param buf 字节数组转换为十六进制字符串(字节数组中按ascii码换成int)
     *            Array of bytes to convert to hex string
     * @return Generated hex string（十六进制值的字符串）
     */
    public static String asHexString(byte buf[]) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }

    /**
     * Converts a hexadecimal String to a byte array.
     * 将一个十六进制字符串转换为字节数组。
     *
     * @param hexStr
     * @return
     */
    public static byte[] asByteArray(String hexStr) {
        byte bArray[] = new byte[hexStr.length() / 2];
        for (int i = 0; i < (hexStr.length() / 2); i++) {
            byte firstNibble = Byte.parseByte(hexStr.substring(2 * i, 2 * i + 1), 16); // [x,y)
            byte secondNibble = Byte.parseByte(hexStr.substring(2 * i + 1, 2 * i + 2), 16);
            int finalByte = (secondNibble) | (firstNibble << 4); // bit-operations
            // only with
            // numbers, not
            // bytes.
            bArray[i] = (byte) finalByte;
        }
        return bArray;
    }

    /**
     * 加密
     * 给定一个输入字符串和一个十六进制的AES密钥，返回十六进制的字符串（密文）
     * Given an input string and a hexadecimal AES secret key, this method
     * outputs the encrypted hexadecimal value.
     *
     * @param whatToEncrypt 明文
     * @param aesHexKey     密钥
     * @return 经加密后得到btye转化成的String密文
     * @throws Exception
     */
    public static String encrypt(String whatToEncrypt, String aesHexKey) throws Exception {

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec skeySpec = new SecretKeySpec(aesHexKey.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encryptedBytes = cipher.doFinal(whatToEncrypt.getBytes());
        return asHexString(encryptedBytes);

    }

    /**
     * 解密
     * Given an input encrypted string (in hexadecimal format) and a hexadecimal
     * 给定一个输入加密字符串（十六进制格式）和十六进制
     * AES secret key, this method outputs a decrypted string value.
     *
     * @param whatToDecrypt 解密密文
     * @param aesHexKey     密钥
     * @return 经解密后得到btye转化成的String密文
     * @throws Exception
     */
    public static String decrypt(String whatToDecrypt, String aesHexKey) throws Exception {

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec skeySpec = new SecretKeySpec(aesHexKey.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decryptedBytes = cipher.doFinal(asByteArray(whatToDecrypt));
        return new String(decryptedBytes);

    }

    /**
     * Creates an AES 128-bit secret key as a hexadecimal string.
     * 获取128bit的AES密钥
     *
     * @return 密钥的String类型
     * @throws Exception
     */
    public static String getAesHexKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128); // Higher than 128-bit encryption requires a download of additional provider implementations for the JDK.
        // Generate the secret key specs.
        //超过128位的加密要求JDK下载额外的提供程序实现。
        //规格生成秘密密钥。
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();

        return asHexString(raw);
    }
}
