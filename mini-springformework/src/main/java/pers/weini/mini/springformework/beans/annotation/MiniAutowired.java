package pers.weini.mini.springformework.beans.annotation;

import java.lang.annotation.*;

/**
 * @author Lambert丶Shi
 * @description auto set
 * @date 2020/12/9
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniAutowired {
    String value() default "";
}
