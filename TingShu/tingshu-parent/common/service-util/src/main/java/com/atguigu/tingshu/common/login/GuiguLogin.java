package com.atguigu.tingshu.common.login;

import java.lang.annotation.*;

/**
 * @date: 2024/6/7 15:46
 * @author: yz
 * @version: 1.0
 *
 * @Target({ElementType.METHOD})
 * 注解作用的位置：
 *   METHOD
 *   TYPE
 *   ....
 *
 * @Retention(RetentionPolicy.RUNTIME)
 *  注解的生命周期
 *   java -class --内存
 *   SOURCE:源码时期
 *   CLASS：字节码时期
 *   RUNTIME：运行时期
 * @Inherited ：子父级继承
 * @Documented：javadoc
 *
 *
 */


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GuiguLogin {

    /**
     * 是否需要拦截
     * @return
     *
     * true:认证拦截
     * false:认证不拦截
     */
    boolean required() default true;




}
