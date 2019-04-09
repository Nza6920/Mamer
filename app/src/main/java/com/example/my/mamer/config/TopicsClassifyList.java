package com.example.my.mamer.config;

public class TopicsClassifyList {
    private int classifyId;
    private String classifyName;
    private String classifyInfo;

    public TopicsClassifyList(){

    }

    public TopicsClassifyList(int classifyId, String classifyName, String classifyInfo) {
        this.classifyId = classifyId;
        this.classifyName = classifyName;
        this.classifyInfo = classifyInfo;
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public String getClassifyInfo() {
        return classifyInfo;
    }

    public void setClassifyInfo(String classifyInfo) {
        this.classifyInfo = classifyInfo;
    }
}
