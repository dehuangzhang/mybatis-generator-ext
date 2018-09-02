package com.flag.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Page {
    // 分页查询开始记录位置
    private int begin;
    // 分页查看下结束位置
    private int end;
    // 每页显示记录数
    private int length;

    public Page() {
    }

    /**
     * 构造函数
     *
     * @param begin
     * @param length
     */
    public Page(int begin, int length) {
        this.begin = begin;
        this.length = length;
        this.end = this.begin + this.length;
     
    }

}
