package com.chinadaily.utils;


import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lin
 * @version V1.0
 * @Title EncryptUtil.java
 * @Package com.chinadailyhk.vdoenglish.english.utils
 * @Description 数据加密&解密
 * @Company
 * @date 2015-10-03 12:13:00
 */
public class EncryptUtil {

    /**
     * 缓存
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * 编码字符集UTF-8
     */
    private static final String CHARESET_UTF8 = "UTF-8";

    /**
     * 加密算法MD5
     */
    private static final String ALGORITHM_MD5 = "MD5";

    /**
     * 加密算法SHA-1
     */
    private static final String SHA1 = "SHA-1";

    /**
     * 字符匹配常量
     */
    private static final String COMPARE_STRING = "0123456789abcdef";

    /**
     * 算法DES-偏移向量
     */
    private static final byte[] BYTE_IV = {1, 2, 3, 4, 5, 6, 7, 8};//"12345678".getBytes();

    /**
     * 加密算法DES
     */
    private static final String ALGORITHM_DES = "DES";

    /**
     * 加密算法DES
     */
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";//DES/CBC/PKCS5Padding

    /**
     * Base64编码合法字符集
     */
    private static final char[] LEGAL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    /**
     * @param encryptString 待加密的数据
     * @param encryptKey    加密的KEY
     * @return byte[] 返回类型
     * @throws Exception
     * @Title: encryptDES
     * @Description: DES加密算法
     */
    private static byte[] encryptDES(String encryptString, String encryptKey) throws Exception {

        try {
            IvParameterSpec zeroIv = new IvParameterSpec(BYTE_IV);
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), ALGORITHM_DES);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            return cipher.doFinal(encryptString.getBytes(CHARESET_UTF8));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * @param byteMi     待解密的数据
     * @param decryptKey 解密的KEY
     * @return
     * @throws
     * @Title: decryptDES
     * @Description: DES解密算法
     */
    private static String decryptDES(byte[] byteMi, String decryptKey) {
        String strDecrypt = null;
        if (null == decryptKey) {
            return null;
        }
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(BYTE_IV);
            SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), ALGORITHM_DES);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte decryptedData[] = cipher.doFinal(byteMi);
            strDecrypt = new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strDecrypt;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @throws
     * @Title: encodeBase64
     * @Description: 对byte数组进行Base64编码
     */
    private static String encodeBase64(byte[] data) throws Exception {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);

        try {

            int end = len - 3;
            int i = start;
            int n = 0;

            while (i <= end) {
                int d = ((((int) data[i]) & 0x0ff) << 16)
                        | ((((int) data[i + 1]) & 0x0ff) << 8)
                        | (((int) data[i + 2]) & 0x0ff);

                buf.append(LEGAL_CHARS[(d >> 18) & 63]);
                buf.append(LEGAL_CHARS[(d >> 12) & 63]);
                buf.append(LEGAL_CHARS[(d >> 6) & 63]);
                buf.append(LEGAL_CHARS[d & 63]);

                i += 3;

                if (n++ >= 14) {
                    n = 0;
                    buf.append(" ");
                }
            }

            if (i == start + len - 2) {
                int d = ((((int) data[i]) & 0x0ff) << 16)
                        | ((((int) data[i + 1]) & 255) << 8);

                buf.append(LEGAL_CHARS[(d >> 18) & 63]);
                buf.append(LEGAL_CHARS[(d >> 12) & 63]);
                buf.append(LEGAL_CHARS[(d >> 6) & 63]);
                buf.append("=");
            } else if (i == start + len - 1) {
                int d = (((int) data[i]) & 0x0ff) << 16;

                buf.append(LEGAL_CHARS[(d >> 18) & 63]);
                buf.append(LEGAL_CHARS[(d >> 12) & 63]);
                buf.append("==");
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

        return buf.toString();
    }

    /**
     * @param c
     * @return
     * @throws Exception
     * @throws
     * @Title: decodeBase64
     * @Description: 字符转ASCII编码
     */
    private static int decodeBase64(char c) throws Exception {
        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else
            switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    //throw new RuntimeException("unexpected code: " + c);
                    throw new Exception("" + c);
            }
    }

    /**
     * @param s
     * @return
     * @throws Exception
     * @throws
     * @Title: decodeBase64
     * @Description: Base64编码字符串转byte数组
     */
    private static byte[] decodeBase64(String s) throws Exception {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decodeBase64(s, bos);
        } catch (IOException e) {
            throw new Exception(e);
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException ex) {
            throw new Exception(ex);
        }
        return decodedBytes;
    }

    /**
     * @param s
     * @param os
     * @throws Exception
     * @throws IOException
     * @throws
     * @Title decodeBase64
     * @Description: Base64编码字符串转输出
     */
    private static void decodeBase64(String s, OutputStream os) throws IOException, Exception {
        int i = 0;

        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= ' ')
                i++;

            if (i == len)
                break;

            int tri = (decodeBase64(s.charAt(i)) << 18)
                    + (decodeBase64(s.charAt(i + 1)) << 12)
                    + (decodeBase64(s.charAt(i + 2)) << 6)
                    + (decodeBase64(s.charAt(i + 3)));

            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);

            i += 4;
        }
    }

    /**
     * @param hexString
     * @return
     * @throws
     * @Title: hexStringToBytes
     * @Description: 十六进制字符转换byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * @param c
     * @return
     * @throws
     * @Title: charToByte
     * @Description: char类型转byte类型
     */
    private static byte charToByte(char c) {
        return (byte) COMPARE_STRING.indexOf(c);
    }

    /**
     * @param srcBytes
     * @return
     * @throws
     * @Title: bytesToHexString
     * @Description: byte[] 转换十六进制字符
     */
    public static String bytesToHexString(byte[] srcBytes) {

        StringBuilder stringBuilder = new StringBuilder();

        if (srcBytes == null || srcBytes.length <= 0) {
            return null;
        }

        for (byte srcByte : srcBytes) {
            int v = srcByte & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }

        return stringBuilder.toString();
    }

    /**
     * @param string
     * @return
     * @throws
     * @Title: stringToMD5
     * @Description: 将字符串转成32位MD5
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance(ALGORITHM_MD5).digest(string.getBytes(CHARESET_UTF8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }


    /**
     * @param strTmp
     * @return
     * @throws Exception
     * @throws
     * @Title: encryptGZIP
     * @Description: GZIP加密
     */
    public static byte[] encryptGZIP(String strTmp) throws Exception {
        byte[] encode;
        if (strTmp == null || strTmp.length() == 0) {
            return null;
        }

        try {
            // gzip压缩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(strTmp.getBytes(CHARESET_UTF8));
            gzip.close();

            encode = baos.toByteArray();
            baos.flush();
            baos.close();

        } catch (Exception e) {
            throw new Exception(e);
        }

        return encode;
    }

    /**
     * @param strTmp
     * @return
     * @throws
     * @Title: decryptGZIP
     * @Description: GZIP解密
     */
    public static String decryptGZIP(String strTmp) {
        if (strTmp == null || strTmp.length() == 0) {
            return null;
        }

        try {

            byte[] decode = strTmp.getBytes(CHARESET_UTF8);

            ByteArrayInputStream bais = new ByteArrayInputStream(decode);
            GZIPInputStream gzip = new GZIPInputStream(bais);

            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ((len = gzip.read(buf, 0, BUFFER_SIZE)) != -1) {
                baos.write(buf, 0, len);
            }
            gzip.close();
            baos.flush();

            decode = baos.toByteArray();

            baos.close();

            return new String(decode, CHARESET_UTF8);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param @param  data
     * @param @return 设定文件
     * @return int    返回类型
     * @throws
     * @Title: getShort
     * @Description: 字节转int类型
     */
    private static int getShort(byte[] data) {
        return (data[0] << 8) | data[1] & 0xFF;
    }

    /**
     * @param json       JOSN数据
     * @param encryptKey 加密数据
     * @return String        返回加密过后的数据字符串
     * @throws Exception
     * @throws
     * @Title: getEncodeData
     * @Description: 将数据加密
     */
    public static String getEncodeData(String json, String encryptKey) {
        if (TextUtils.isEmpty(json) || TextUtils.isEmpty(encryptKey)) {
            return null;
        }
        try {
            byte[] des = EncryptUtil.encryptDES(json, encryptKey);
            return EncryptUtil.encodeBase64(des);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @param json       JOSN数据
     * @param encryptKey 解密数据
     * @return String        返回解密过后的数数据字符串
     * @throws
     * @Title: getEncodeData
     * @Description: 将数据解密码
     */
    public static String getDecodeData(String json, String encryptKey) {
        if (null == json || null == encryptKey) {
            return null;
        }
        byte[] ebase64 = null;    // decodeBase64解密
        String des = null;        // DES解密
        try {
            ebase64 = EncryptUtil.decodeBase64(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        des = EncryptUtil.decryptDES(ebase64, encryptKey);
        return des;
    }

    /**
     * @param string
     * @return
     * @throws
     * @Title: stringToSHA1
     * @Description: 将字符串转成SHA1
     */
    public static String stringToSHA1(String string) {
        MessageDigest md = null;
        String result = "";

        try {
            md = MessageDigest.getInstance("SHA-1");

            byte[] digest = md.digest(string.getBytes());

            result = byteToStr(digest);

            System.out.println("result加密后的字符串：" + result);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (byte aByteArray : byteArray) {
            strDigest += byteToHexStr(aByteArray);
        }

        return strDigest;
    }


    /**
     * 将一个字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] temp = new char[2];

        temp[0] = Digit[(mByte >>> 4) & 0X0F];
        temp[1] = Digit[mByte & 0X0F];

        String s = new String(temp);

        return s;
    }
}
 