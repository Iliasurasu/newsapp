package com.example.news;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsResponse {
    private List<News> articles;

    public List<News> getArticles() {
        return articles;
    }

    public void setArticles(List<News> articles) {
        this.articles = articles;
    }
}
