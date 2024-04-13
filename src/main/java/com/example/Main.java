package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import org.apache.commons.cli.*;

public class Main 
{
    public static void main(String[] args) {
        Options options = new Options();

        Option neighbourCount = Option.builder("k")
            .longOpt("neighbour-count")
            .argName("neighbour-count")
            .hasArg()
            .desc("Set the number of neighbours used in k-NN algorithm")
            .build();
        options.addOption(neighbourCount);

        Option trainingRatio = Option.builder("r")
            .longOpt("training-ratio")
            .argName("training-ratio")
            .hasArg()
            .desc("Set the training to testing ratio")
            .build();
        options.addOption(trainingRatio);

        CommandLine cmd;
        CommandLineParser parser = new PosixParser();

        try {
            cmd = parser.parse(options, args);

            int K = 2;
            if (cmd.hasOption("k")) {
                K = Integer.parseInt(cmd.getOptionValue("k"));
            }
            System.out.println("K: " + K);

            float ratio = 0.1f;
            if (cmd.hasOption("r")) {
                ratio = Float.parseFloat(cmd.getOptionValue("r"));
            }
            System.out.println("training ratio: " + ratio);

            System.out.println();

            Dataset dataset = Dataset.loadFromSGMLFile("dataset.sgm");
            System.out.println("Databset size: " + dataset.getSize());
            Pair<Dataset, Dataset> splitted = dataset.split(ratio);

            Dataset training = splitted.first;
            System.out.println("Training set size: " + training.getSize());
            Dataset testing = splitted.second;
            System.out.println("Testing set size: " + testing.getSize());

            List<FeatureVector> trainingVectors
                = new ArrayList<>(training.getSize());
            List<FeatureVector> testingVectors
                = new ArrayList<>(testing.getSize());

            Stemmer stemmer = new Stemmer("stoplist.txt");

            Dictionary dict = new Dictionary(new File("dictionary.json"));

            int westGermanPoliticianMaxCount = 0;
            float canadianCityMaxFreq = 0;

            int trainingSize = training.getSize();
            System.out.println("\nTraining...");
            for (int i = 0; i < trainingSize; ++i) {
                System.out.print("\r" + (i+1) + " of " + trainingSize);
                Article article = training.getArticle(i);
                Tokenizer contentTokenizer
                    = new Tokenizer(article.getContent());
                Tokenizer titleTokenizer
                    = new Tokenizer(article.getTitle());
                FeatureVector vector = new FeatureVector(
                    stemmer.stemTokens(contentTokenizer.scanTokens()),
                    stemmer.stemTokens(titleTokenizer.scanTokens()),
                    dict,
                    Optional.of(article.getLabel())
                );

                if (vector.getWestGermanPoliticianCount()
                    > westGermanPoliticianMaxCount) {

                    westGermanPoliticianMaxCount
                        = vector.getWestGermanPoliticianCount();
                }

                if (vector.getCanadianCityFreq() > canadianCityMaxFreq) {
                    canadianCityMaxFreq = vector.getCanadianCityFreq();
                }

                trainingVectors.add(vector);
            }

            int testingSize = testing.getSize();
            System.out.println("\n\nParsing...");
            for (int i = 0; i < testingSize; ++i) {
                System.out.print("\r" + (i+1) + " of " + testingSize);
                Article article = testing.getArticle(i);
                Tokenizer contentTokenizer
                    = new Tokenizer(article.getContent());
                Tokenizer titleTokenizer
                    = new Tokenizer(article.getTitle());
                FeatureVector vector = new FeatureVector(
                    stemmer.stemTokens(contentTokenizer.scanTokens()),
                    stemmer.stemTokens(titleTokenizer.scanTokens()),
                    dict,
                    Optional.of(article.getLabel())
                );

                if (vector.getWestGermanPoliticianCount()
                    > westGermanPoliticianMaxCount) {

                    westGermanPoliticianMaxCount
                        = vector.getWestGermanPoliticianCount();
                }

                if (vector.getCanadianCityFreq() > canadianCityMaxFreq) {
                    canadianCityMaxFreq = vector.getCanadianCityFreq();
                }

                testingVectors.add(vector);
            }

            System.out.println("\n\nClassifying...");
            Classifier classifier = new Classifier(
                K,
                trainingVectors,
                testingVectors,
                westGermanPoliticianMaxCount,
                canadianCityMaxFreq
            );
            classifier.clasify();

            System.out.println("\n\nAccuracy: " + classifier.getAccuracy());
            System.out.println();
            for (Article.LABEL label : Article.LABEL.values()) {
                System.out.println(
                    label.name()
                        + " sensitivity: "
                        + classifier.getSensitivity(label)
                );
            }
            System.out.println("Weighted mean of sensitivity: "
                + classifier.getWeightedMeanOfSensitivity());
            System.out.println();
            for (Article.LABEL label : Article.LABEL.values()) {
                System.out.println(
                    label.name()
                        + " precision: "
                        + classifier.getPrecision(label)
                );
            }
            System.out.println("Weighted mean of precistion: "
                + classifier.getWeightedMeanOfPrecision());
            System.out.println();
            for (Article.LABEL label : Article.LABEL.values()) {
                System.out.println(
                    label.name()
                        + " F1: "
                        + classifier.getF1(label)
                );
            }
            System.out.println("Weighted mean of F1: "
                + classifier.getWeightedMeanOfF1());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
