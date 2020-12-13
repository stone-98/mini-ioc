package pers.weini.mini.springformework.core;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/9
 */
public interface MiniBeanFactory {
    Object getBean(String beanName);

    Object getBean(Class<?> beanClass);
}
