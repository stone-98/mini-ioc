package pers.weini.mini.springformework.mvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
public class MiniHandlerMapping {
    private Object controller;

    private Method method;

    private Pattern pattern;

    public MiniHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
