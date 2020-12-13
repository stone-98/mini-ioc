package pers.weini.mini.springformework.beans;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/9
 */
public class MiniBeanWrapper {
    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public MiniBeanWrapper(Object instance){
        this.wrappedInstance = instance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }


    public Class<?> getWrappedClass() {
        return this.wrappedClass.getClass();
    }

}
