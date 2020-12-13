package pers.weini.demo.service.impl;

import pers.weini.demo.service.IModifyService;
import pers.weini.mini.springformework.mvc.annotation.MiniService;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
@MiniService
public class ModifyServiceImpl implements IModifyService {

    /**
     * 增加
     */
    @Override
    public String add(String name, String addr) throws Exception {
        throw new Exception("这是故意抛出来的异常");
    }

    /**
     * 修改
     */
    @Override
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * 删除
     */
    @Override
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }

}
