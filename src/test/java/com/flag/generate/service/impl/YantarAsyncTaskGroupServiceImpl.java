package com.flag.generate.service.impl;

import com.flag.base.AbstractBaseServiceImpl;
import com.flag.generate.YantarAsyncTaskGroupMapperExt;
import com.flag.generate.model.YantarAsyncTaskGroup;
import com.flag.generate.model.YantarAsyncTaskGroupExample;
import com.flag.generate.service.YantarAsyncTaskGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YantarAsyncTaskGroupServiceImpl extends AbstractBaseServiceImpl<YantarAsyncTaskGroup, YantarAsyncTaskGroupMapperExt, YantarAsyncTaskGroupExample> implements YantarAsyncTaskGroupService {

     @Autowired 
    public void setMapper(YantarAsyncTaskGroupMapperExt mapper) {
        setMapper(mapper);
    }
}