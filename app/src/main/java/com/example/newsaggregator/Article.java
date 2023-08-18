package com.example.newsaggregator;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Article {
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedDate;
    DateTimeFormatter timeFormatterIso = DateTimeFormatter.ISO_INSTANT;
    DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public Article() {
    }

    public boolean checkIfNull(String givenString){
        if (givenString == null){
            return true;
        }
        return false;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }


    String getLocaltIme(String publishedDate) {
        String articleDate = "";
        try{
            TemporalAccessor accessor = timeFormatterIso.parse(publishedDate);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
            articleDate = localDateTime.format(dateTimeFormatter);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(articleDate.isEmpty()){
            try{
                TemporalAccessor accessor = timeFormatter.parse(publishedDate);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
                articleDate = localDateTime.format(dateTimeFormatter);
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        return articleDate;
    }
}
