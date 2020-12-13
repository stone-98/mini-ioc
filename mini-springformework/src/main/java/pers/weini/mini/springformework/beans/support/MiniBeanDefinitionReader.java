package pers.weini.mini.springformework.beans.support;

import pers.weini.mini.springformework.beans.config.MiniBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/9
 */
public class MiniBeanDefinitionReader {

    /**
     * 注册的BeanClass
     */
    private List<String> registryBeanClass = new ArrayList<String>();

    private Properties config = new Properties();

    private final String SCAN_PACKAGE = "scanPackage";

    public MiniBeanDefinitionReader(String... locations) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registryBeanClass.add(className);
            }
        }
    }

    public Properties getConfig() {
        return config;
    }

    /**
     * 把已经注册的Bean转化为MiniBeanDefinition对象
     *
     * @return
     */
    public List<MiniBeanDefinition> loadBeanDefinitions() {
        List<MiniBeanDefinition> result = new ArrayList<MiniBeanDefinition>();
        try {
            for (String registryBeanClass : registryBeanClass) {
                Class<?> beanClass = Class.forName(registryBeanClass);
                if (beanClass.isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    result.add(doCreateBeanDefinition(anInterface.getName(), beanClass.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 把配置信息解析成一个BeanDefinition
     *
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private MiniBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        MiniBeanDefinition beanDefinition = new MiniBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * 类名首字母转为小写
     *
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
