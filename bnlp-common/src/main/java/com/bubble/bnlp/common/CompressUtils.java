package com.bubble.bnlp.common;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩/解压缩工具
 *
 * @author wugang
 * date: 2018-11-28 15:32
 **/
public class CompressUtils {

    /**
     * 压缩，可指定是否去换行符
     *
     * @param str           要压缩的字段
     * @param removeNewline 去换行符：true，去换行符；false，不去换行符
     * @return
     * @throws IOException
     */
    public static String compress(String str, boolean removeNewline) throws IOException {
        String res = Base64(GZip(str));
        if (removeNewline) {
            // 当字符串被base64编码后，在JSON转换中，会带入换行符'\n',然后经过URLEncode，换行符\n 会变成
            // 两个字符 '\','n'
            if (res.contains("\\r\\n") || res.contains("\r\n") || res.contains("\\n") || res.contains("\n")) {
                res = res.replace("\\r\\n", "").replace("\r\n", "").replace("\\n", "").replace("\n", "");
            }
        }
        return res;
    }

    // 压缩
    public static String compress(String str) throws IOException {
        return Base64(GZip(str));
    }

    // 解压缩
    public static String uncompress(String str) throws IOException {
        return unGZip(unBase64(str));
    }

    public static byte[] GZip(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();

        // 包含任意二进制数据，不能转为UTF-8编码的字符串，否则将重新编码byte[]时会获得不同结果
        return out.toByteArray();
    }

    // 解压缩
    public static String unGZip(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }

        // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
        return out.toString();
    }

    public static String Base64(byte[] data) {
        String encoded = null;
        if (data == null || data.length == 0) {
            return encoded;
        }

        return Base64.encode(data);
    }

    public static byte[] unBase64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        return Base64.decode(str);
    }

    public static void main(String[] args) throws IOException {
        String s = "H4sIAAAAAAAAAHu+r+/p7L0AT3O+kAYAAAA=\n";
        System.out.println(uncompress(s));
        System.out.println(EncoderByMd5(s));
    }

    /**
     * 利用MD5进行加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     */
    public static String EncoderByMd5(String str) {
        if (str == null) {
            return null;
        }
        try {
            // 确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            // 加密后的字符串
            return base64en.encode(md5.digest(str.getBytes("utf-8")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

}
