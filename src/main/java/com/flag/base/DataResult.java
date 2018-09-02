package com.flag.base;

import java.util.List;

public class DataResult<T> {

    private List<? extends T> data;
    private int     count;

    /**
     * 查询是否成功
     */
    private boolean isSuccess = true;

    /**
     * 错误消息
     */
    private String  errMsg;

    public List<? extends T> getData() {
        return data;
    }

    public void setData(List<? extends T> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
