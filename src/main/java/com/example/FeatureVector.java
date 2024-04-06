package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        this.firstCapitalizedWord = "";
        this.firstNumber = "";
        this.mostFrequentAcronym = "";
        this.capitals = new HashMap<>();
        this.currencies = new HashMap<>();
        this.title = title;

        for (Article.LABEL country : Article.LABEL.values()) {
            this.capitals.put(country, false);
            this.currencies.put(country, false);
        }

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
        float[] similarities = {
            1.0f - Math.abs(
                this.getWestGermanPoliticianCount()
                    - other.getWestGermanPoliticianCount()
            ) / (float)westGermanPoliticianMaxCount,
            1.0f - Math.abs(
                this.getCanadianCityFreq() - other.getCanadianCityFreq()
            ) / (float)canadianCityMaxFreq,
            (this.isFrenchBankPresent() == other.isFrenchBankPresent())
                ? 1.0f : 0.0f,
            (this.isUKAcronymPresent() == other.isUKAcronymPresent())
                ? 1.0f : 0.0f,
            (this.isJapaneseCompanyPresent() == other.isJapaneseCompanyPresent())
                ? 1.0f : 0.0f,
            (this.isUSAStatePresent() == other.isUSAStatePresent())
                ? 1.0f : 0.0f,
            1.0f - getHammingDistance(this.capitals, other.capitals) / 6.0f,
            1.0f - getHammingDistance(this.currencies, other.currencies) / 6.0f,
            getStringsSimilarity(
                this.firstCapitalizedWord, other.firstCapitalizedWord
            ),
            (this.getFirstNumber().equals(other.getFirstNumber()))
                ? 1.0f : 0.0f,
            (this.getMostFrequentAcronym().equals(other.getMostFrequentAcronym()))
                ? 1.0f : 0.0f,
            getTitlesSimilarity(this.title, other.title),
        };

        float sum = 0.0f;
        for (int i = 0; i < similarities.length; ++i) {
            sum += Math.pow(similarities[i] - 1.0f, 2);
        }

        return 1.0f - (float)(Math.sqrt(sum) / Math.sqrt(12));
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

    private static int getHammingDistance(
        Map<Article.LABEL, Boolean> m1,
        Map<Article.LABEL, Boolean> m2
    ) {
        assert(m1.keySet().equals(m2.keySet()));

        int dist = 0;

        for (Article.LABEL key : m1.keySet()) {
            if (!m1.get(key).equals(m2.get(key))) {
                dist += 1;
            }
        }

        return dist;
    }

    private static List<String> generateNGrams(int N, String input) {
        List<String> output = new ArrayList<>();
        int maxStartIndex = (input.length() == N) ? 1 : input.length() - N;

        for (int i = 0; i < maxStartIndex; ++i) {
            output.add(input.substring(i, i + N));
        }

        return output;
    }

    public static float getStringsSimilarity(String s1, String s2) {
        int s1len = s1.length();
        int s2len = s2.length();

        if (s1len == 0 && s2len == 0 || s1.equals(s2)) return 1.0f;
        else if (s1len < 3 || s2len < 3) return 0.0f;

        List<String> g1 = generateNGrams(3, s1);
        List<String> g2 = generateNGrams(3, s2);

        int sum = 0;

        for (String gram : g1) {
            if (g2.contains(gram)) sum += 1;
        }

        int len = (s1len > s2len) ? s1len : s2len;

        return (1.0f / (float)(len - 2)) * (float)sum;
    }

    public static float getTitlesSimilarity(List<Token> t1, List<Token> t2) {
        Set<String> t1set = new HashSet<String>();
        for (Token token : t1) {
            t1set.add(token.getValue());
        }
        Set<String> t2set = new HashSet<String>();
        for (Token token : t2) {
            t2set.add(token.getValue());
        }

        int sum = 0;

        for (String s : t1set) {
            if (t2set.contains(s)) sum += 1;
        }

        int t1len = t1set.size();
        int t2len = t2set.size();
        int len = (t1len > t2len) ? t1len : t2len;

        if (t1len == 0 && t2len == 0) return 1.0f;
        else if (t1len == 0 || t2len == 0) return 0.0f;

        return (float)sum / (float)len;
    }
}