package com.flag.base;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;

@Resource
public interface BaseMapper<D extends BaseEntity, E extends BaseExample> {
    /**
     * 插入
     *
     * @param record
     * @return
     */
    Long insertSelective(D record);

    /**
     * 根据主键删除
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 删除
     */
    int deleteByExample(@Param("example") E example);

    /**
     * 更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(D record);

    /**
     * 更新
     *
     * @param record
     * @param example
     * @return
     */
    int updateByExampleSelective(@Param("record") D record, @Param("example") E example);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int insertBatch(@Param("list") List<D> list);

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    D selectByPrimaryKey(Long id);

    /**
     * 计算总数量
     *
     * @param example
     * @return
     */
    int countByExample(E example);

    /**
     * 根据条件查询
     *
     * @param example
     * @return
     */
    List<D> selectByExample(E example);

}
