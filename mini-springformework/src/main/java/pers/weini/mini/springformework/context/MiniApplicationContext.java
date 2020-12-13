package pers.weini.mini.springformework.context;

import pers.weini.mini.springformework.beans.MiniBeanWrapper;
import pers.weini.mini.springformework.beans.config.MiniBeanDefinition;
import pers.weini.mini.springformework.beans.config.MiniBeanPostProcessor;
import pers.weini.mini.springformework.beans.support.MiniBeanDefinitionReader;
import pers.weini.mini.springformework.beans.support.MiniDefaultListableBeanFactory;
import pers.weini.mini.springformework.core.MiniBeanFactory;
import pers.weini.mini.springformework.beans.annotation.MiniAutowired;
import pers.weini.mini.springformework.mvc.annotation.MiniController;
import pers.weini.mini.springformework.mvc.annotation.MiniService;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/9
 */
public class MiniApplicationContext extends MiniDefaultListableBeanFactory implements MiniBeanFactory {

    private String[] configLocations;

    private MiniBeanDefinitionReader reader;

    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    /**
     * 实例化缓存
     */
    private Map<String, MiniBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, MiniBeanWrapper>();

    public MiniApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 1、定位配置文件
        reader = new MiniBeanDefinitionReader(this.configLocations);
        // 2、加载配置文件
        List<MiniBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        // 3、注册
        doRegisterBeanDefinition(beanDefinitions);
        // 4、把不是延迟加载的类提前初始化
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, MiniBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<MiniBeanDefinition> beanDefinitions) throws Exception {
        for (MiniBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new RuntimeException("beanDefinition already exists!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    public Object getBean(String beanName) {
        try {
            MiniBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);

            Object instance = null;

            MiniBeanPostProcessor miniBeanPostProcessor = new MiniBeanPostProcessor();
            miniBeanPostProcessor.postProcessAfterInitialization(instance, beanName);

            instance = instantiateBean(beanDefinition);

            if (instance == null) {
                return null;
            }

            MiniBeanWrapper beanWrapper = new MiniBeanWrapper(instance);

            this.factoryBeanInstanceCache.put(beanName, beanWrapper);

            miniBeanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            populateBean(beanName, instance);
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(MiniController.class) || clazz.isAnnotationPresent(MiniService.class))) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(MiniAutowired.class)) {
                continue;
            }
            MiniAutowired autowired = field.getAnnotation(MiniAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);

            try {
                Object bean = null;
                // TODO 这里需要参考Spring源码如何解决依赖注入顺序问题的！！！暂时解决方案跳过
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实例化bean
     *
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(MiniBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if (this.factoryBeanObjectCache.containsKey(className)) {
                return factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                factoryBeanObjectCache.put(className, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Object getBean(Class<?> beanClass) {
        return null;
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }


    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }
}
