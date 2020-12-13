package pers.weini.mini.springformework.mvc;



import pers.weini.mini.springformework.mvc.annotation.MiniRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
public class MiniHandlerAdapter {
    public boolean supports(Object handler) {
        return handler instanceof MiniHandlerMapping;
    }

    public MiniModelAndView handler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MiniHandlerMapping handlerMapping = (MiniHandlerMapping)handler;
        // 保存形参列表 将参数名称和参数的位置保存起来
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();
        // 通过运行时的状态去拿到注解的值
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof MiniRequestParam) {
                    String paramName = ((MiniRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }
        // 初始化
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramterType = paramTypes[i];
            if (paramterType == HttpServletRequest.class || paramterType == HttpServletResponse.class) {
                paramIndexMapping.put(paramterType.getName(), i);
            } else if (paramterType == String.class) {

            }
        }
        // 拼接实参列表
        // http://localhost:8080/web/add.json?name=DBL&addr=chongqing
        Map<String, String[]> params = request.getParameterMap();

        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]", "")
                    .replaceAll("\\s+", "");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());

            // 允许自定义的类型转换器Converter
            paramValues[index] = castStringValue(value, paramTypes[index]);
        }

        // String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        // method.invoke(applicationContext.getBean(beanName), paramValues);
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = request;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = response;
        }
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null || result instanceof Void) {
            return null;
        }
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == MiniModelAndView.class;
        if (isModelAndView) {
            return (MiniModelAndView) result;
        }
        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }else if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else if(Double.class == paramType){
            Double.valueOf(value);
        }else{
            if(value != null ){
                return value;
            }
        }
        return null;
    }
}
