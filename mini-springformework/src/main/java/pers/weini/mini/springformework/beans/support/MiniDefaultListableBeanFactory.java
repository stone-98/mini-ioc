package pers.weini.mini.springformework.beans.support;

import pers.weini.mini.springformework.beans.config.MiniBeanDefinition;
import pers.weini.mini.springformework.context.support.MiniAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/9
 */
public class MiniDefaultListableBeanFactory extends MiniAbstractApplicationContext {

    protected final Map<String, MiniBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MiniBeanDefinition>();

    @Override
    public void refresh() throws Exception {
    }
}
