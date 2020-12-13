package pers.weini.mini.springformework.mvc.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.weini.mini.springformework.context.MiniApplicationContext;
import pers.weini.mini.springformework.mvc.*;
import pers.weini.mini.springformework.mvc.annotation.MiniController;
import pers.weini.mini.springformework.mvc.annotation.MiniRequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
public class MiniDispatcherServlet extends HttpServlet {
    /**
     * 日志记录
     */
    private final static Logger log = LoggerFactory.getLogger(MiniDispatcherServlet.class);

    /**
     * 上下文配置的位置
     */
    private final String LOCATION = "contextConfigLocation";

    /**
     * 处理映射
     */
    private List<MiniHandlerMapping> handlerMappings = new ArrayList<MiniHandlerMapping>();

    /**
     * 处理器适配器
     */
    private Map<MiniHandlerMapping, MiniHandlerAdapter> handlerAdapters = new HashMap<MiniHandlerMapping, MiniHandlerAdapter>();

    /**
     * 视图解析器
     */
    private List<MiniViewResolver> viewResolvers = new ArrayList<MiniViewResolver>();

    /**
     * 应用上下文
     */
    private MiniApplicationContext applicationContext;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 委派，根据URL去找到一个对应的Method并通过response返回
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("<font size = '25' color = 'blue'>500 Exception</font><br/>" +
                    "Details:<br/>" +
                    Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll("\\s", "\r\n")
                    + "<font color='green'><i>Copyright@shikui</i></font>");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 完成了对HandlerMapping的封装
        // 完成了对方法返回值的封装，ModelAndView

        // 通过URL获取一个HandlerMapping
        MiniHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new MiniModelAndView("404"));
            return;
        }
        // 根据一个HandlerMapping获取一个HandlerAdapter
        MiniHandlerAdapter ha = getHandlerAdapter(handler);

        // 解析某一个方法的形参和返回值之后，统一封装为ModelAndView对象
        MiniModelAndView mv = ha.handler(req, resp, handler);
        // 就把ModelAndView变成一个ViewResolver
        processDispatchResult(req, resp, mv);

    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MiniModelAndView miniModelAndView) throws Exception {
        if (miniModelAndView == null) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }
        for (MiniViewResolver viewResolver : this.viewResolvers) {
            MiniView view = viewResolver.resolverViewName(miniModelAndView.getViewName());
            // 直接往浏览器输出
            view.render(miniModelAndView.getModel(), req, resp);
            return;
        }
    }

    private MiniHandlerAdapter getHandlerAdapter(MiniHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        return this.handlerAdapters.get(handler);
    }


    private MiniHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (MiniHandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return mapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("MiniDispatcherServlet start init");

        // 初始化Spring的核心IOC容器
        applicationContext = new MiniApplicationContext(config.getInitParameter(LOCATION));

        // 初始化MVC九大组件
        initStrategies(applicationContext);

        System.out.println("MiniDispatcherServlet initialization is complete ");
    }

    private void initStrategies(MiniApplicationContext context) {
        // 文件上传
        //initMultipartResolver(context);
        // 初始化本地语言环境
        // initLocalResolver(context);
        // 初始化模板处理器
        // initThemResolver(context);
        // 通过HandlerAdapter进行多类型得参数动态匹配
        initHandlerMapping(context);
        // 初始化参数适配器
        initHandlerAdapters(context);
        // 初始化异常拦截器
        // initHandlerExceptionResolvers(context);
        // 初始化视图预处理器
        //initRequestToViewNameTranslator(context);
        // 初始化视图转换器
        initViewResolvers(context);
        // Flash管理器
        // initFlashMapManager(context);
    }

    private void initHandlerMapping(MiniApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(MiniController.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(MiniRequestMapping.class)) {
                    MiniRequestMapping Mapping = clazz.getAnnotation(MiniRequestMapping.class);
                    baseUrl = Mapping.value();
                }
                // 扫描所有得public修饰得方法
                for (Method method : clazz.getMethods()) {
                    // 方法没有加注解的跳过
                    if (!method.isAnnotationPresent(MiniRequestMapping.class)) {
                        continue;
                    }
                    MiniRequestMapping requestMapping = method.getAnnotation(MiniRequestMapping.class);
                    // 对于配置了“/” 和没有配置“/”的通过正则表达式统一处理
                    // 将路径中的*改为正则表达式".*"的方式
                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    handlerMappings.add(new MiniHandlerMapping(controller, method, pattern));
                    log.info("mapped:{} , {}", regex, method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViewResolvers(MiniApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new MiniViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(MiniApplicationContext context) {
        for (MiniHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new MiniHandlerAdapter());
        }
    }

}
