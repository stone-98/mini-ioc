package pers.weini.mini.springformework.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author Lambert丶Shi
 * @description Identifies as the service layer
 * @date 2020/12/9
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniRequestParam {
    String value() default "";
}
