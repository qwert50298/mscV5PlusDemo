package com.iflytek.mscv5plusdemo.bean;

import java.util.List;

/**
 * Created by taoxingyu on 2019/1/10.
 */

public class SearchResult extends BaseResp {

    private Data data;
    private int total;

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

    public SearchResult(int code, String message, boolean success) {
        super(code, message, success);
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "data=" + data +
                ", total=" + total +
                '}';
    }

    public class Data {

        private List<DataList> list;

        public void setList(List<DataList> list) {
            this.list = list;
        }

        public List<DataList> getList() {
            return list;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "list=" + list +
                    '}';
        }
    }
}
