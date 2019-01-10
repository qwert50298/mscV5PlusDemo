package com.iflytek.mscv5plusdemo.retrofit.bean;

/**
 * Created by taoxingyu on 2019/1/10.
 */

public class SearchResult {
    private boolean success;
    private int code;
    private String message;
    private Data data;
    private int total;
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public boolean getSuccess() {
        return success;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    public int getTotal() {
        return total;
    }
}
