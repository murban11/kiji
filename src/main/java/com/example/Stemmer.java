package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import opennlp.tools.stemmer.PorterStemmer;

public class Stemmer {

    private Set<String> stopList;
    private PorterStemmer stemmer = new PorterStemmer();

    public Stemmer() {
        this.stopList = new HashSet<>();
    }

    public Stemmer(Set<String> stopList) {
        List<String> stopWords = new ArrayList<>();

        for (String stopWord : stopList) {
            stopWords.add(stemmer.stem(stopWord.toLowerCase()));
        }

        this.stopList = new HashSet<>(stopWords);
    }

    public Stemmer(String stopListFileName) throws FileNotFoundException {
        List<String> stopWords = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(stopListFileName))) {
            scanner.useDelimiter(" +");

            while (scanner.hasNext()) {
                stopWords.add(stemmer.stem(scanner.next().toLowerCase()));
            }
        }

        this.stopList = new HashSet<>(stopWords);
    }

    public List<Token> stemTokens(List<Token> tokens) {
        List<Token> result = new ArrayList<>();

        for (Token token : tokens) {
            if (token.getType() != Token.Type.WORD
                && token.getType() != Token.Type.CAPITALIZED_WORD
            ) {
                continue;
            }

            String stemmedValue = stemmer.stem(token.getValue());

            if (stopList.contains(stemmedValue)) {
                continue;
            }

            result.add(new Token(token.getType(), stemmedValue));
        }

        return result;
    }
}
