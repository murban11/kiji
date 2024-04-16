package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.example.FeatureVector.METRIC;
import com.example.FeatureVector.FEATURE;

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

        Option metric = Option.builder("m")
            .longOpt("metric")
            .argName("metric")
            .hasArg()
            .desc("Set the metric used when comparing feature vectors")
            .build();
        options.addOption(metric);

        Option disableWestGermanPoliticalCount
            = Option.builder("disable_west_german_political_count")
                .longOpt("disable-west-german-political-count")
                .build();
        options.addOption(disableWestGermanPoliticalCount);

        Option disableCanadianCityFreq
            = Option.builder("disable_canadian_city_freq")
                .longOpt("disable-canadian-city-freq")
                .build();
        options.addOption(disableCanadianCityFreq);

        Option disableFrenchBankPresence
            = Option.builder("disable_french_bank_presence")
                .longOpt("disable-french-bank-presence")
                .build();
        options.addOption(disableFrenchBankPresence);

        Option disableUKAcronymPresence
            = Option.builder("disable_uk_acronym_presence")
                .longOpt("disable-uk-acronym-presence")
                .build();
        options.addOption(disableUKAcronymPresence);

        Option disableJapaneseCompanyPresence
            = Option.builder("disable_japanese_company_presence")
                .longOpt("disable-japanese-company-presence")
                .build();
        options.addOption(disableJapaneseCompanyPresence);

        Option disableUSAStatePresence
            = Option.builder("disable_usa_state_presence")
                .longOpt("disable-usa-state-presence")
                .build();
        options.addOption(disableUSAStatePresence);

        Option disableCapitalsPresence
            = Option.builder("disable_capitals_presence")
                .longOpt("disable-capitals-presence")
                .build();
        options.addOption(disableCapitalsPresence);

        Option disableCurrenciesPresence
            = Option.builder("disable_currencies_presence")
                .longOpt("disable-currencies-presence")
                .build();
        options.addOption(disableCurrenciesPresence);

        Option disableFirstCapitalizedWord
            = Option.builder("disable_first_capitalized_word")
                .longOpt("disable-first-capitalized-word")
                .build();
        options.addOption(disableFirstCapitalizedWord);

        Option disableFirstNumber
            = Option.builder("disable_first_number")
                .longOpt("disable-first-number")
                .build();
        options.addOption(disableFirstNumber);

        Option disableMostFrequentAcronym
            = Option.builder("disable_most_frequent_acronym")
                .longOpt("disable-most-frequent-acronym")
                .build();
        options.addOption(disableMostFrequentAcronym);

        Option disableTitle
            = Option.builder("disable_title")
                .longOpt("disable-title")
                .build();
        options.addOption(disableTitle);

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

            METRIC m = METRIC.EUCLIDEAN;
            if (cmd.hasOption("m")) {
                String optVal = cmd.getOptionValue("m").toLowerCase();
                if (optVal.equals("euclidean")) {
                    m = METRIC.EUCLIDEAN;
                } else if (optVal.equals("taxicab")) {
                    m = METRIC.TAXICAB;
                } else if (optVal.equals("chebyshev")) {
                    m = METRIC.CHEBYSHEV;
                } else {
                    System.err.println("Invalid -m option argument: " + optVal);
                }
            }
            System.out.println("metric: " + m.toString());

            short featureFlag = (short)0b1111111111111111;
            if (cmd.hasOption("disable_west_german_political_count")) {
                featureFlag ^= FEATURE.WEST_GERMAN_POLITICAL_COUNT.id;
            }
            if (cmd.hasOption("disable_canadian_city_freq")) {
                featureFlag ^= FEATURE.CANADIAN_CITY_FREQ.id;
            }
            if (cmd.hasOption("disable_french_bank_presence")) {
                featureFlag ^= FEATURE.FRENCH_BANK_PRESENCE.id;
            }
            if (cmd.hasOption("disable_uk_acronym_presence")) {
                featureFlag ^= FEATURE.UK_ACRONYM_PRESENCE.id;
            }
            if (cmd.hasOption("disable_japanese_company_presence")) {
                featureFlag ^= FEATURE.JAPANESE_COMPANY_PRESENCE.id;
            }
            if (cmd.hasOption("disable_usa_state_presence")) {
                featureFlag ^= FEATURE.USA_STATE_PRESENCE.id;
            }
            if (cmd.hasOption("disable_capitals_presence")) {
                featureFlag ^= FEATURE.CAPITALS_PRESENCE.id;
            }
            if (cmd.hasOption("disable_currencies_presence")) {
                featureFlag ^= FEATURE.CURRENCIES_PRESENCE.id;
            }
            if (cmd.hasOption("disable_first_capitalized_word")) {
                featureFlag ^= FEATURE.FIRST_CAPITALIZED_WORD.id;
            }
            if (cmd.hasOption("disable_first_number")) {
                featureFlag ^= FEATURE.FIRST_NUMBER.id;
            }
            if (cmd.hasOption("disable_most_frequent_acronym")) {
                featureFlag ^= FEATURE.MOST_FREQUENT_ACRONYM.id;
            }
            if (cmd.hasOption("disable_title")) {
                featureFlag ^= FEATURE.TITLE.id;
            }

            System.out.println("features:");
            for (var f : FeatureVector.FEATURE.values()) {
                System.out.print("    ");
                if ((featureFlag & f.id) != 0) {
                    System.out.print("+");
                } else {
                    System.out.print("-");
                }
                System.out.println(f.toString());
            }

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
                canadianCityMaxFreq,
                featureFlag,
                m
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
