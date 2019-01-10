package com.iflytek.mscv5plusdemo.retrofit.bean;

import java.util.List;

/**
 * Created by taoxingyu on 2019/1/10.
 */

public class Data {

    private List<DataList> list;
    public void setList(List<DataList> list) {
        this.list = list;
    }
    public List<DataList> getList() {
        return list;
    }
}
