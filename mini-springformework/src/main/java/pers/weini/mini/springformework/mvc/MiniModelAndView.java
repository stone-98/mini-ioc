package pers.weini.mini.springformework.mvc;

import java.util.Map;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
public class MiniModelAndView {

    /**
     * 页面模板的名称
     */
    private String viewName;

    /**
     * 参数
     */
    private Map<String, ?> model;

    public MiniModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public MiniModelAndView(String viewName) {
        this(viewName, null);
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}

