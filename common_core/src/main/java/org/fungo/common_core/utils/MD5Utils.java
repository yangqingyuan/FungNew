/**
 * Copyright (c) 2013 NewSenceNetwork.Co.Ltd. 
 * 
 * All rights reserved.
 */
package org.fungo.common_core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * @author Xutao
 *
 */
public class MD5Utils {
    
    private static char md5Chars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
          'e', 'f' };
    //private static MessageDigest messagedigest;
        
    /*获取一个文件的md5码 */
    public static String getFileMD5String(File file) throws Exception {
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
         FileInputStream in = new FileInputStream(file);
         FileChannel ch = in.getChannel();
         MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
           file.length());
         messagedigest.update(byteBuffer);
         in.close();
         return bufferToHex(messagedigest.digest());
    }
    
    /*获取一个字符串的md5码 */
    public static String getStringMD5String(String str) throws Exception {
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        messagedigest.update(str.getBytes()); 
        return bufferToHex(messagedigest.digest());
    }

    /*获取一个字符串的md5码 */
    public static String getStringMD5(String str){
        String md5String = null;
        try {
            md5String = getStringMD5String(str);
        } catch (Exception e) {
            e.printStackTrace();
            md5String = null;
        }
        return md5String;
    }
    
    /*验证一个字符串和一个MD5码是否相等 */
    public static boolean check(String str, String md5) throws Exception {
        return getStringMD5String(str).equals(md5);
    }
        
    /*验证一个文件和一个MD5码是否相等 */
    public static boolean check(File f, String md5) throws Exception {
        return  getFileMD5String(f).equals(md5);
    }
        
    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }
    
    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
          appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }
    
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = md5Chars[(bt & 0xf0) >> 4];
        char c1 = md5Chars[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
