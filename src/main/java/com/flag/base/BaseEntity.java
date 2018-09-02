package com.flag.base;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sven.zhang
 * @since 2017/9/22.
 */
@Setter
@Getter
public class BaseEntity {

    private Long   id;

    private Date   gmtCreate;

    private String creator;

    private Date   gmtModified;

    private String modifier;

    private String isDeleted;

}
