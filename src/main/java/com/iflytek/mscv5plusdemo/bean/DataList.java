package com.iflytek.mscv5plusdemo.bean;

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
    private String crawlTime;
    private String esUpdateTime;
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

    public String getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(String crawlTime) {
        this.crawlTime = crawlTime;
    }

    public String getEsUpdateTime() {
        return esUpdateTime;
    }

    public void setEsUpdateTime(String esUpdateTime) {
        this.esUpdateTime = esUpdateTime;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "DataList{" +
                "esId='" + esId + '\'' +
                ", highlightTitle='" + highlightTitle + '\'' +
                ", highlightContent='" + highlightContent + '\'' +
                ", labels='" + labels + '\'' +
                ", type='" + type + '\'' +
                ", mysqlId='" + mysqlId + '\'' +
                ", crawlTime='" + crawlTime + '\'' +
                ", esUpdateTime='" + esUpdateTime + '\'' +
                ", score=" + score +
                '}';
    }
}
