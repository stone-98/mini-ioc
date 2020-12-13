package pers.weini.mini.springformework.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author Lambert丶Shi
 * @description Identifies the URI corresponding to this method mapping
 * @date 2020/12/9
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniRequestMapping {
    String value() default "";
}
