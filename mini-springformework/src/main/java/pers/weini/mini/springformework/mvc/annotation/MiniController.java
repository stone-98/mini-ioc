package pers.weini.mini.springformework.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author Lambert丶Shi
 * @description control layer
 * @date 2020/12/9
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniController {
    String value() default "";
}
