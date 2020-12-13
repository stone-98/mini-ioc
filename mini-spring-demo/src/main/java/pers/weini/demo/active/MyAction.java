package pers.weini.demo.active;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.weini.demo.service.IModifyService;
import pers.weini.demo.service.IQueryService;
import pers.weini.mini.springformework.beans.annotation.MiniAutowired;
import pers.weini.mini.springformework.mvc.MiniModelAndView;
import pers.weini.mini.springformework.mvc.annotation.MiniController;
import pers.weini.mini.springformework.mvc.annotation.MiniRequestMapping;
import pers.weini.mini.springformework.mvc.annotation.MiniRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
@MiniController
@MiniRequestMapping("/web")
public class MyAction {

    @MiniAutowired
    private IQueryService queryService;

    @MiniAutowired
    private IModifyService modifyService;

    @MiniRequestMapping("/query.json")
    public MiniModelAndView query(HttpServletRequest request, HttpServletResponse response, @MiniRequestParam("name") String name) {
        Logger logger = LoggerFactory.getLogger(MyAction.class.getName());
        logger.error("Hello world.");
        String result = queryService.query(name);
        return out(response, result);
    }

    @MiniRequestMapping("/add.json")
    public MiniModelAndView add(HttpServletRequest request, HttpServletResponse response,
                                @MiniRequestParam("name") String name, @MiniRequestParam("addr") String addr) {
        try {
            String result = modifyService.add(name, addr);
            return out(response, result);
        } catch (Throwable e) {
            Map<String, String> model = new HashMap<String, String>();
            model.put("detail", e.getCause().getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return new MiniModelAndView("500", model);
        }
    }

    @MiniRequestMapping("/remove.json")
    public MiniModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                   @MiniRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @MiniRequestMapping("/edit.json")
    public MiniModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                                 @MiniRequestParam("id") Integer id, @MiniRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        return out(response, result);
    }


    private MiniModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
