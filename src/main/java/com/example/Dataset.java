package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class Dataset {

    private static final Random rnd = new Random(1337);

    public static Dataset loadFromSGMLFile(
        String filename,
        List<Article.LABEL> ignoredLabels
    ) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);

        SAXParser parser = factory.newSAXParser();
        DatasetHandler datasetHandler = new DatasetHandler(ignoredLabels);

        parser.parse(filename, datasetHandler);
        return datasetHandler.getDataset();
    }

    private List<Article> articleList;

    public Dataset() {
        articleList = new ArrayList<>();
    }

    public Dataset(List<Article> articles) {
        articleList = articles;
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

    public void shuffleArticles() {
        Collections.shuffle(articleList, rnd);
    }

    public Pair<Dataset, Dataset> split(float ratio) {
        assert(ratio >= 0.0 && ratio <= 1.0);

        Map<Article.LABEL, ArrayList<Article>> articlesCategorized
            = new HashMap<>();

        for (Article.LABEL label : Article.LABEL.values()) {
            articlesCategorized.put(label, new ArrayList<>());
        }

        for (int i = 0; i < getSize(); ++i) {
            Article article = getArticle(i);
            articlesCategorized.get(article.getLabel()).addLast(article);
        }

        List<Article> trainingSet = new ArrayList<>();
        List<Article> testingSet = new ArrayList<>();

        for (Article.LABEL label : Article.LABEL.values()) {
            List<Article> cat = articlesCategorized.get(label);
            int catSize = cat.size();
            int splitIdx = (int)Math.round((catSize - 1) * ratio);
            assert(splitIdx >= 1 && splitIdx <= catSize);

            trainingSet.addAll(cat.subList(0, splitIdx));
            testingSet.addAll(cat.subList(splitIdx, catSize));
        }

        return new Pair<>(
            new Dataset(trainingSet),
            new Dataset(testingSet)
        );
    }
}
