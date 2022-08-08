package com.example.monitorserver.utils;


import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @program: Dream
 * @description: md5密码加密
 * @author: Jiao
 * @create: 2022-08-08 10:15
 **/

@Component
public class Md5Utils {

    /**
     * @Description: md5加密
     * @Param: [str]
     * @return: java.lang.String
     * @Author: Jiao
     * @Date: 2022-08-08 10:15
     */
    public static String getMD5(String str) throws NoSuchAlgorithmException {

        String md5String = null;

        //mds算法加密摘要
        MessageDigest md = MessageDigest.getInstance("MD5");
        //加密
        md.update(str.getBytes());
        //转换为32位哈希值
        md5String = new BigInteger(1, md.digest()).toString(32);

        return md5String;
    }
}
