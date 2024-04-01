package com.example;

import java.util.ArrayList;
import java.util.List;

public class Article {

    private String title;
    private List<String> labels;
    private String content;

    public Article() {
        this.title = "";
        this.labels = new ArrayList<>();
        this.content = "";
    }

    public Article(String title, List<String> labels, String content) {
        this.title = title;
        this.labels = new ArrayList<>(labels);
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLabels() {
        return new ArrayList<>(labels);
    }

    public void addLabel(String label) {
        this.labels.addLast(label);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
