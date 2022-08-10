package com.example.monitorserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YC104
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secret {

    // 参数类（用来传递加密数据,只有方法参数中有此类或此类的子类才会执行加解密）
    Class value() default Object.class;

    // 参数类中传递加密数据的的属性名，默认encryptStr
    String encryptStrName() default "encryptStr";

    // 参数类中传递加密数据的属性名，默认encryptKey
    String encryptKeyName() default "encryptKey";
}

