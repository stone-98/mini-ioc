package pers.weini.demo.service.impl;


import pers.weini.demo.service.IQueryService;
import pers.weini.mini.springformework.mvc.annotation.MiniService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
@MiniService
public class QueryServiceImpl implements IQueryService {

    /**
     * 查询
     */
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        System.out.println("这是在业务方法中打印的：" + json);
        return json;
    }

}
