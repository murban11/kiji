package com.example;

public class Article {

    private String title;
    private LABEL label;
    private String content;

    public static enum LABEL {
        WEST_GERMANY,
        USA,
        FRANCE,
        UK,
        CANADA,
        JAPAN
    };

    public static boolean isValidLabel(String name) {
        for (LABEL label : LABEL.values()) {
            if (label.name().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Article(String title, LABEL label, String content) {
        this.title = title;
        this.label = label;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public LABEL getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }
}
