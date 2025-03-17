package com.atguigu.tingshu.common.cache;

import java.lang.annotation.*;

/**
 * @date: 2024/6/22 9:22
 * @author: yz
 * @version: 1.0
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GuiGuCache {


    /**
     * 缓存key的前缀
     * @return
     */
    String prefix() default "cache:";

    /**
     * 缓存key的后缀
     * @return
     */
    String suffix() default ":data";


}
