package com.example;

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
                dataset.addArticle(new Article());
                inPlaces = true;
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
                    getLastArticle().addLabel(elementValue.toString());
                }
                break;
            case TITLE:
                getLastArticle().setTitle(elementValue.toString());
                break;
            case BODY:
                getLastArticle().setContent(elementValue.toString());
                break;
        }
    }

    private Article getLastArticle() {
        return dataset.getArticle(dataset.getSize() - 1);
    }

    public Dataset getDataset() {
        return dataset;
    }
}
