package pers.weini.mini.springformework.mvc;

import java.io.File;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
public class MiniViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    /**
     * 模板目录
     */
    private File templateRootDir;


    private String viewName;

    public MiniViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public MiniView resolverViewName(String viewName) {
        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new MiniView(templateFile);
    }
}
