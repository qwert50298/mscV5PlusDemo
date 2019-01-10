package com.iflytek.mscv5plusdemo.retrofit.bean;

import java.util.Date;

/**
 * Created by taoxingyu on 2019/1/10.
 */

public class DataList {

    private String esId;
    private String highlightTitle;
    private String highlightContent;
    private String labels;
    private String type;
    private String mysqlId;
    private Date crawlTime;
    private Date esUpdateTime;
    private double score;
    public void setEsId(String esId) {
        this.esId = esId;
    }
    public String getEsId() {
        return esId;
    }

    public void setHighlightTitle(String highlightTitle) {
        this.highlightTitle = highlightTitle;
    }
    public String getHighlightTitle() {
        return highlightTitle;
    }

    public void setHighlightContent(String highlightContent) {
        this.highlightContent = highlightContent;
    }
    public String getHighlightContent() {
        return highlightContent;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }
    public String getLabels() {
        return labels;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setMysqlId(String mysqlId) {
        this.mysqlId = mysqlId;
    }
    public String getMysqlId() {
        return mysqlId;
    }

    public void setCrawlTime(Date crawlTime) {
        this.crawlTime = crawlTime;
    }
    public Date getCrawlTime() {
        return crawlTime;
    }

    public void setEsUpdateTime(Date esUpdateTime) {
        this.esUpdateTime = esUpdateTime;
    }
    public Date getEsUpdateTime() {
        return esUpdateTime;
    }

    public void setScore(double score) {
        this.score = score;
    }
    public double getScore() {
        return score;
    }
}
