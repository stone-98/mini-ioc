package pers.weini.demo.active;

import pers.weini.demo.service.IQueryService;
import pers.weini.mini.springformework.mvc.MiniModelAndView;
import pers.weini.mini.springformework.beans.annotation.MiniAutowired;
import pers.weini.mini.springformework.mvc.annotation.MiniController;
import pers.weini.mini.springformework.mvc.annotation.MiniRequestMapping;
import pers.weini.mini.springformework.mvc.annotation.MiniRequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
@MiniController
@MiniRequestMapping("/")
public class PageAction {

    @MiniAutowired
    private IQueryService queryService;

    @MiniRequestMapping("/first.html")
    public MiniModelAndView query(@MiniRequestParam("coder") String teacher) {
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("coder", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new MiniModelAndView("first.html", model);
    }

}
