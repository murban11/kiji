package com.example;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DatasetHandler extends DefaultHandler {

    private static final String PLACES = "PLACES";
    private static final String TITLE = "TITLE";
    private static final String BODY = "BODY";
    private static final String D = "D";

    private Dataset dataset;
    private StringBuilder elementValue;
    private boolean inPlaces;
    private boolean containsUnsupportedLabels;

    private String title;
    private List<Article.LABEL> labels;
    private String content;

    @Override
    public void characters(
        char[] ch,
        int start,
        int length
    ) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        dataset = new Dataset();
        inPlaces = false;
    }

    @Override
    public void startElement(
        String uri,
        String lName,
        String qName,
        Attributes attr
    ) throws SAXException {
        switch (qName) {
            case PLACES:
                this.labels = new ArrayList<>();
                inPlaces = true;
                containsUnsupportedLabels = false;
                break;
            case D:
                if (inPlaces) {
                    elementValue = new StringBuilder();
                }
                break;
            case TITLE:
                elementValue = new StringBuilder();
                break;
            case BODY:
                elementValue = new StringBuilder();
                break;
        }
    }

    @Override
    public void endElement(
        String uri,
        String lName,
        String qName
    ) throws SAXException {
        switch (qName) {
            case PLACES:
                inPlaces = false;
                break;
            case D:
                if (inPlaces) {
                    String labelStr = elementValue
                        .toString().toUpperCase().replaceAll("-", "_");
                    if (Article.isValidLabel(labelStr)) {
                        labels.add(Article.LABEL.valueOf(labelStr));
                    } else {
                        containsUnsupportedLabels = true;
                    }
                }
                break;
            case TITLE:
                this.title = elementValue.toString();
                break;
            case BODY:
                this.content = elementValue.toString();

                if (labels.size() == 1 && !containsUnsupportedLabels) {
                    dataset.addArticle(
                        new Article(
                            this.title, this.labels.get(0), this.content
                        )
                    );
                }

                break;
        }
    }

    public Dataset getDataset() {
        return dataset;
    }
}
