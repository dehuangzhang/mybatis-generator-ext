package com.flag.base;

import java.util.List;

/**
 * Created by wb-zdh274635 on 2017/9/22.
 */
public interface BaseService<D extends BaseEntity, E extends BaseExample> {

    /**
     * 插入
     *
     * @param dataObject
     * @return
     */
    Long insert(D dataObject);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int insertBatch(List<D> list);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delete(Long id);

    /**
     * 更新
     *
     * @param dataObject
     * @return
     */
    int update(D dataObject);

    /**
     * 更新
     *
     * @param dataObject
     * @param example
     * @return
     */
    int updateByExample(D dataObject, E example);

    /**
     * 查询
     *
     * @param id
     * @return
     */
    D selectById(Long id);

}
