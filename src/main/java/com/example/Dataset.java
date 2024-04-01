package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class Dataset {

    public static Dataset loadFromSGMLFile(
        String filename
    ) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);

        SAXParser parser = factory.newSAXParser();
        DatasetHandler datasetHandler = new DatasetHandler();

        parser.parse(filename, datasetHandler);
        return datasetHandler.getDataset();
    }

    private List<Article> articleList;

    public Dataset() {
        articleList = new ArrayList<>();
    }

    public void addArticle(Article article) {
        articleList.addLast(article);
    }

    public Article getArticle(int index) {
        return articleList.get(index);
    }

    public int getSize() {
        return articleList.size();
    }
}
