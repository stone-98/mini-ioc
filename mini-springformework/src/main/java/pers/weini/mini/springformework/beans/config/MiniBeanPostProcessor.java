package pers.weini.mini.springformework.beans.config;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
public class MiniBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }
}
