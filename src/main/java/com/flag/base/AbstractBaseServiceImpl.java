package com.flag.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @param <D>
 * @param <K>
 * @param <E>
 */
public abstract class AbstractBaseServiceImpl<D extends BaseEntity, K extends BaseMapper<D, E>, E extends BaseExample>
    implements BaseService<D, E> {
    protected K mapper;

    @Autowired
    public void setMapper(K mapper) {
        this.mapper = mapper;
    }

    @Override
    public Long insert(D dataObject) {
        return mapper.insertSelective(dataObject);
    }

    @Override
    public int delete(Long id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int update(D dataObject) {
        return mapper.updateByPrimaryKeySelective(dataObject);
    }

    @Override
    public D selectById(Long id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public int insertBatch(List<D> list) {
        return mapper.insertBatch(list);
    }

    @Override
    public int updateByExample(D dataObject, E example) {
        return mapper.updateByExampleSelective(dataObject, example);
    }
}
