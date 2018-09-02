package com.flag.generate.model;

import com.flag.base.BaseEntity;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YantarAsyncTaskGroup extends BaseEntity {
    /**
     *任务组来源
     */
    private String sourceCode;

    /**
     *操作人
     */
    private String operator;

    /**
     *任务类型
     */
    private String taskType;

    /**
     *业务id
     */
    private Long businessId;

    /**
     *显示名称
     */
    private String displayName;

    /**
     *handler对应的类
     */
    private String handler;

    /**
     *上下文内容（json串）
     */
    private String context;

    /**
     *开始时间
     */
    private Date startTime;

    /**
     *结束时间
     */
    private Date endTime;

    /**
     *状态
     */
    private String status;

    /**
     *修改时间
     */
    private Date gmtModified;

    /**
     *是否删除
     */
    private String isDeleted;

    /**
     *是否隐藏任务
     */
    private String isSecret;

    /**
     *父任务组Id
     */
    private Long parentId;

    /**
     *完成任务数量
     */
    private Integer finishCount;

    /**
     *总任务数量
     */
    private Integer totalCount;

    /**
     *消息tag
     */
    private String taskTag;

    /**
     *租户ID
     */
    private Long tenantId;

    /**
     *参数（json串）
     */
    private String params;
}