package com.example.monitorserver.constant;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: aop_annotation
 * @description:
 * @author: stop.yc
 * @create: 2022-08-09 10:12
 **/
@Component
public class Constants implements InitializingBean {

    @Value("${constant.private_key}")
    private String private_key;

    @Value("${constant.aes_key}")
    private String aesKey;

    @Value("${constant.max_count_seconds}")
    private Integer maxCountSeconds;

    @Value("${constant.max_seconds}")
    private Integer maxSeconds;

    @Value("${constant.max_mail_duration_seconds}")
    private Integer maxMailDurationOfSecond;

    public static String PRIVATE_KEY;
    public static String AES_KEY;
    public static Integer MAX_COUNT_SECONDS;
    public static Integer MAX_SECONDS;
    public static Integer MAX_MAIL_DURATION_SECOND;

    public static final String REGEX_PWD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}$";
    public static final String REGEX_NAME = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,20}$";
    public static final String REGEX_PHONE = "^((13[0-9])|(14[0|5|6|7|9])|(15[0-3])|(15[5-9])|(16[6|7])|(17[2|3|5|6|7|8])|(18[0-9])|(19[1|8|9]))\\d{8}$";


    @Override
    public void afterPropertiesSet() throws Exception {
        PRIVATE_KEY = private_key;
        AES_KEY = aesKey;
        MAX_COUNT_SECONDS = maxCountSeconds;
        MAX_SECONDS = maxSeconds;
        MAX_MAIL_DURATION_SECOND = maxMailDurationOfSecond;
    }

}
