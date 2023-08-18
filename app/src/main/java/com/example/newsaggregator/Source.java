package com.example.newsaggregator;

public class Source {
    private String id;
    private String sourceName;
    private String newsCategory;
    private String Color;

    public Source(String id, String sourceName, String newsCategory){
        this.id = id;
        this.sourceName = sourceName;
        this.newsCategory = newsCategory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }
}
