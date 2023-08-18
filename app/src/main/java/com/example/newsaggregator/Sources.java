package com.example.newsaggregator;

import java.util.List;

public class Sources {
    private String respStatus;
    private List<Source> newsSources;

    public Sources(String apiResponseStatus, List<Source> sources){
        this.respStatus = apiResponseStatus;
        this.newsSources = sources;
    }

    public String getRespStatus() {
        return respStatus;
    }

    public void setRespStatus(String respStatus) {
        this.respStatus = respStatus;
    }

    public List<Source> getNewsSources() {
        return newsSources;
    }

    public void setNewsSources(List<Source> newsSources) {
        this.newsSources = newsSources;
    }
}
