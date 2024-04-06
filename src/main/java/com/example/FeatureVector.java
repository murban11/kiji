package com.example;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FeatureVector {

    private int westGermanPoliticianCount;
    private float canadianCityFreq;
    private boolean frenchBank;
    private boolean ukAcronym;
    private boolean japaneseCompany;
    private boolean usaState;
    private Map<Article.LABEL, Boolean> capitals;
    private Map<Article.LABEL, Boolean> currencies;
    private String firstCapitalizedWord;
    private String firstNumber;
    private String mostFrequentAcronym;
    private List<Token> title;

    private Optional<Article.LABEL> label = Optional.empty();

    public FeatureVector(
        List<Token> content,
        List<Token> title,
        Dictionary dict,
        Optional<Article.LABEL> label
    ) {
        this(content, title, dict);
        this.label = label;
    }

    public FeatureVector(
        List<Token> content,
        List<Token> title,
        Dictionary dict
    ) {
        this.capitals = new HashMap<>();
        this.currencies = new HashMap<>();
        this.firstCapitalizedWord = "";
        this.firstNumber = "";
        this.mostFrequentAcronym = "";

        if (content.size() == 0) return;

        int canadianCityNameCount = 0;
        int canadianCityTokenCount = 0;

        Map<String, Integer> acronymCounts = new HashMap<>();

        for (int i = 0; i < content.size(); ++i) {
            int count = dict.isWestGermanyPolitician(content, i);
            if (count > 0) {
                ++this.westGermanPoliticianCount;
                i += count - 1;
                continue;
            }

            count = dict.isCanadianCity(content, i);
            if (count > 0) {
                ++canadianCityNameCount;
                canadianCityTokenCount += count;
                i += count - 1;
                if (dict.isCapitalOfCanada(content, i) > 0) {
                    this.capitals.put(Article.LABEL.CANADA, true);
                    continue;
                }
                continue;
            }

            count = dict.isFrenchBank(content, i);
            if (count > 0) {
                this.frenchBank = true;
                i += count - 1;
                continue;
            }

            if (content.get(i).getValue().equals("uk")) {
                this.ukAcronym = true;
                continue;
            }

            count = dict.isJapaneseCompany(content, i);
            if (count > 0) {
                this.japaneseCompany = true;
                i += count - 1;
                continue;
            }

            count = dict.isStateInUSA(content, i);
            if (count > 0) {
                this.usaState = true;
                i += count - 1;
                if (dict.isCapitalOfUSA(content, i) > 0) {
                    this.capitals.put(Article.LABEL.USA, true);
                    continue;
                }
                continue;
            }

            for (Article.LABEL country : Article.LABEL.values()) {
                if (dict.isCapital(country, content, i) > 0) {
                    this.capitals.put(country, true);
                    continue;
                }
                if (dict.isCurrency(country, content, i) > 0) {
                    this.currencies.put(country, true);
                    continue;
                }
            }

            Token curr = content.get(i);

            if (curr.getType() == Token.Type.CAPITALIZED_WORD
                && this.firstCapitalizedWord.isEmpty()) {

                this.firstCapitalizedWord = curr.getValue();
            }

            if (curr.getType() == Token.Type.NUMBER
                && this.firstNumber.isEmpty()) {

                this.firstNumber = curr.getValue();
            }

            if (curr.getType() == Token.Type.ACRONIM) {
                if (acronymCounts.containsKey(curr.getValue())) {
                    acronymCounts.put(
                        curr.getValue(),
                        acronymCounts.get(curr.getValue()) + 1
                    );
                } else {
                    acronymCounts.put(curr.getValue(), 1);
                }
            }
        }

        this.canadianCityFreq
            = (float)canadianCityNameCount
                / (float)(content.size()
                    - (canadianCityTokenCount - canadianCityNameCount));

        if (acronymCounts.size() > 0) {
            this.mostFrequentAcronym = sortAcronyms(acronymCounts)
                .entrySet()
                .iterator()
                .next()
                .getKey();
        }
    }

    public float getSimilarity(
        FeatureVector other,
        int westGermanPoliticianMaxCount,
        float canadianCityMaxFreq
    ) {
        return 0.0f;
    }

    public int getWestGermanPoliticianCount() {
        return this.westGermanPoliticianCount;
    }

    public float getCanadianCityFreq() {
        return this.canadianCityFreq;
    }

    public boolean isFrenchBankPresent() {
        return this.frenchBank;
    }

    public boolean isUKAcronymPresent() {
        return this.ukAcronym;
    }

    public boolean isJapaneseCompanyPresent() {
        return this.japaneseCompany;
    }

    public boolean isUSAStatePresent() {
        return this.usaState;
    }

    public boolean isCapitalOfWestGermanyPresent() {
        return this.capitals.getOrDefault(Article.LABEL.WEST_GERMANY, false);
    }

    public boolean isCapitalOfUSAPresent() {
        return this.capitals.getOrDefault(Article.LABEL.USA, false);
    }

    public boolean isCapitalOfFrancePresent() {
        return this.capitals.getOrDefault(Article.LABEL.FRANCE, false);
    }

    public boolean isCapitalOfUKPresent() {
        return this.capitals.getOrDefault(Article.LABEL.UK, false);
    }

    public boolean isCapitalOfCanadaPresent() {
        return this.capitals.getOrDefault(Article.LABEL.CANADA, false);
    }

    public boolean isCapitalOfJapanPresent() {
        return this.capitals.getOrDefault(Article.LABEL.JAPAN, false);
    }

    public boolean isWestGermanCurrencyPresent() {
        return this.currencies.getOrDefault(Article.LABEL.WEST_GERMANY, false);
    }

    public boolean isUSACurrencyPresent() {
        return this.currencies.getOrDefault(Article.LABEL.USA, false);
    }

    public boolean isFrenchCurrencyPresent() {
        return this.currencies.getOrDefault(Article.LABEL.FRANCE, false);
    }

    public boolean isUKCurrencyPresent() {
        return this.currencies.getOrDefault(Article.LABEL.UK, false);
    }

    public boolean isCanadianCurrencyPresent() {
        return this.currencies.getOrDefault(Article.LABEL.CANADA, false);
    }

    public boolean isJapaneseCurrencyPresent() {
        return this.currencies.getOrDefault(Article.LABEL.JAPAN, false);
    }

    public String getFirstCapitalizedWord() {
        return this.firstCapitalizedWord;
    }

    public String getFirstNumber() {
        return this.firstNumber;
    }

    public String getMostFrequentAcronym() {
        return this.mostFrequentAcronym;
    }

    public List<Token> getTitle() {
        return this.title;
    }

    public Optional<Article.LABEL> getLabel() {
        return this.label;
    }

    public static class SimilarityComparator
        implements Comparator<FeatureVector> {

        FeatureVector vector;
        int westGermanPoliticianMaxCount;
        float canadianCityMaxFreq;

        public SimilarityComparator(
            FeatureVector vector,
            int westGermanPoliticianMaxCount,
            float canadianCityMaxFreq
        ) {
            this.vector = vector;
            this.westGermanPoliticianMaxCount = westGermanPoliticianMaxCount;
            this.canadianCityMaxFreq = canadianCityMaxFreq;
        }

        @Override
        public int compare(FeatureVector arg0, FeatureVector arg1) {
            throw new UnsupportedOperationException(
                "Unimplemented method 'compare'"
            );
        }
    }

    private static Map<String, Integer> sortAcronyms(
        Map<String, Integer> map
    ) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new ValueThenKeyComparator<String, Integer>());

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private static class ValueThenKeyComparator
        <K extends Comparable<? super K>, V extends Comparable<? super V>>
        implements Comparator<Map.Entry<K, V>> {

        public int compare(Map.Entry<K, V> a, Map.Entry<K, V> b) {
            int cmp1 = b.getValue().compareTo(a.getValue());
            if (cmp1 != 0) {
                return cmp1;
            } else {
                return a.getKey().compareTo(b.getKey());
            }
        }

    }
}
