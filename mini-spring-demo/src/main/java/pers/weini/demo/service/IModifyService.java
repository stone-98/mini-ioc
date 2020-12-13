package pers.weini.demo.service;

/**
 * @author Lambert.Shi
 * @description
 * @date 2020/12/10
 */
public interface IModifyService {
    /**
     * 增加
     */
    String add(String name, String addr) throws Exception;

    /**
     * 修改
     */
    String edit(Integer id, String name);

    /**
     * 删除
     */
    String remove(Integer id);
}
