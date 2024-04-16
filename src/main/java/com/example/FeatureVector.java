package com.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

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
    private Set<String> titleHashed;

    private Set<String> firstCapitalizedWordGrams;

    private Optional<Article.LABEL> label = Optional.empty();

    public enum METRIC {
        EUCLIDEAN,
        TAXICAB,
        CHEBYSHEV
    };

    public enum FEATURE {
        WEST_GERMAN_POLITICAL_COUNT ((short)0b0000000000000001),
        CANADIAN_CITY_FREQ          ((short)0b0000000000000010),
        FRENCH_BANK_PRESENCE        ((short)0b0000000000000100),
        UK_ACRONYM_PRESENCE         ((short)0b0000000000001000),
        JAPANESE_COMPANY_PRESENCE   ((short)0b0000000000010000),
        USA_STATE_PRESENCE          ((short)0b0000000000100000),
        CAPITALS_PRESENCE           ((short)0b0000000001000000),
        CURRENCIES_PRESENCE         ((short)0b0000000010000000),
        FIRST_CAPITALIZED_WORD      ((short)0b0000000100000000),
        FIRST_NUMBER                ((short)0b0000001000000000),
        MOST_FREQUENT_ACRONYM       ((short)0b0000010000000000),
        TITLE                       ((short)0b0000100000000000);

        public final short id;

        private FEATURE(short id) {
            this.id = id;
        }
    };

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

        this.titleHashed = new HashSet<String>(title.size());
        for (Token token : title) {
            this.titleHashed.add(token.getValue());
        }

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
            this.mostFrequentAcronym = getMostFrequentAcronym(acronymCounts);
        }

        this.firstCapitalizedWordGrams
            = generateNGrams(3, this.firstCapitalizedWord);
    }

    public float getSimilarity(
        FeatureVector other,
        int westGermanPoliticianMaxCount,
        float canadianCityMaxFreq,
        short featureFlags
    ) {
        List<Float> similarities = new ArrayList<>(FEATURE.values().length);

        if ((featureFlags & FEATURE.WEST_GERMAN_POLITICAL_COUNT.id) != 0) {
            similarities.add(
                1.0f - Math.abs(
                    this.getWestGermanPoliticianCount()
                    - other.getWestGermanPoliticianCount()
                ) / (float)westGermanPoliticianMaxCount
            );
        }
        if ((featureFlags & FEATURE.CANADIAN_CITY_FREQ.id) != 0) {
            similarities.add(
                1.0f - Math.abs(
                    this.getCanadianCityFreq() - other.getCanadianCityFreq()
                ) / (float)canadianCityMaxFreq
            );
        }
        if ((featureFlags & FEATURE.FRENCH_BANK_PRESENCE.id) != 0) {
            similarities.add(
                (this.isFrenchBankPresent() == other.isFrenchBankPresent())
                    ? 1.0f : 0.0f
            );
        }
        if ((featureFlags & FEATURE.UK_ACRONYM_PRESENCE.id) != 0) {
            similarities.add(
            (this.isUKAcronymPresent() == other.isUKAcronymPresent())
                ? 1.0f : 0.0f
            );
        }
        if ((featureFlags & FEATURE.JAPANESE_COMPANY_PRESENCE.id) != 0) {
            similarities.add(
                (this.isJapaneseCompanyPresent()
                    == other.isJapaneseCompanyPresent())
                        ? 1.0f : 0.0f
            );
        }
        if ((featureFlags & FEATURE.USA_STATE_PRESENCE.id) != 0) {
            similarities.add(
                (this.isUSAStatePresent() == other.isUSAStatePresent())
                    ? 1.0f : 0.0f
            );
        }
        if ((featureFlags & FEATURE.CAPITALS_PRESENCE.id) != 0) {
            similarities.add(
                1.0f - getHammingDistance(this.capitals, other.capitals)
                    / 6.0f
            );
        }
        if ((featureFlags & FEATURE.CURRENCIES_PRESENCE.id) != 0) {
            similarities.add(
                1.0f - getHammingDistance(this.currencies, other.currencies)
                    / 6.0f
            );
        }
        if ((featureFlags & FEATURE.FIRST_CAPITALIZED_WORD.id) != 0) {
            similarities.add(
                getGramsSimilarity(
                    this.firstCapitalizedWordGrams,
                    other.firstCapitalizedWordGrams,
                    other.firstCapitalizedWord.length(),
                    other.firstCapitalizedWord.length()
                )
            );
        }
        if ((featureFlags & FEATURE.FIRST_NUMBER.id) != 0) {
            similarities.add(
                (this.getFirstNumber().equals(other.getFirstNumber()))
                    ? 1.0f : 0.0f
            );
        }
        if ((featureFlags & FEATURE.MOST_FREQUENT_ACRONYM.id) != 0) {
            similarities.add(
                (this.getMostFrequentAcronym()
                    .equals(other.getMostFrequentAcronym()))
                        ? 1.0f : 0.0f
            );
        }
        if ((featureFlags & FEATURE.TITLE.id) != 0) {
            similarities.add(
                getTitlesSimilarity(this.titleHashed, other.titleHashed)
            );
        }

        float sum = 0.0f;
        for (float diff : similarities) {
            sum += Math.pow(diff - 1.0f, 2);
        }

        return 1.0f - (float)(Math.sqrt(sum) / Math.sqrt(similarities.size()));
    }

    public float getDistance(
        FeatureVector other,
        int westGermanPoliticianMaxCount,
        float canadianCityMaxFreq,
        short featureFlags,
        METRIC metric
    ) {
        List<Float> distances = new ArrayList<>(FEATURE.values().length);

        if ((featureFlags & FEATURE.WEST_GERMAN_POLITICAL_COUNT.id) != 0) {
            distances.add(
                Math.abs(
                    this.getWestGermanPoliticianCount()
                    - other.getWestGermanPoliticianCount()
                ) / (float)westGermanPoliticianMaxCount
            );
        }
        if ((featureFlags & FEATURE.CANADIAN_CITY_FREQ.id) != 0) {
            distances.add(
                Math.abs(
                    this.getCanadianCityFreq() - other.getCanadianCityFreq()
                ) / (float)canadianCityMaxFreq
            );
        }
        if ((featureFlags & FEATURE.FRENCH_BANK_PRESENCE.id) != 0) {
            distances.add(
                (this.isFrenchBankPresent() == other.isFrenchBankPresent())
                    ? 0.0f : 1.0f
            );
        }
        if ((featureFlags & FEATURE.UK_ACRONYM_PRESENCE.id) != 0) {
            distances.add(
                (this.isUKAcronymPresent() == other.isUKAcronymPresent())
                    ? 0.0f : 1.0f
            );
        }
        if ((featureFlags & FEATURE.JAPANESE_COMPANY_PRESENCE.id) != 0) {
            distances.add(
                (this.isJapaneseCompanyPresent()
                    == other.isJapaneseCompanyPresent())
                        ? 0.0f : 1.0f
            );
        }
        if ((featureFlags & FEATURE.USA_STATE_PRESENCE.id) != 0) {
            distances.add(
                (this.isUSAStatePresent() == other.isUSAStatePresent())
                    ? 0.0f : 1.0f
            );
        }
        if ((featureFlags & FEATURE.CAPITALS_PRESENCE.id) != 0) {
            distances.add(
                getHammingDistance(this.capitals, other.capitals) / 6.0f
            );
        }
        if ((featureFlags & FEATURE.CURRENCIES_PRESENCE.id) != 0) {
            distances.add(
                getHammingDistance(this.currencies, other.currencies) / 6.0f
            );
        }
        if ((featureFlags & FEATURE.FIRST_CAPITALIZED_WORD.id) != 0) {
            distances.add(
                1.0f - getGramsSimilarity(
                    this.firstCapitalizedWordGrams,
                    other.firstCapitalizedWordGrams,
                    other.firstCapitalizedWord.length(),
                    other.firstCapitalizedWord.length()
                )
            );
        }
        if ((featureFlags & FEATURE.FIRST_NUMBER.id) != 0) {
            distances.add(
                (this.getFirstNumber().equals(other.getFirstNumber()))
                    ? 0.0f : 1.0f
            );
        }
        if ((featureFlags & FEATURE.MOST_FREQUENT_ACRONYM.id) != 0) {
            distances.add(
                (this.getMostFrequentAcronym()
                    .equals(other.getMostFrequentAcronym()))
                        ? 0.0f : 1.0f
            );
        }
        if ((featureFlags & FEATURE.TITLE.id) != 0) {
            distances.add(
                1.0f - getTitlesSimilarity(this.titleHashed, other.titleHashed)
            );
        }

        if (metric == METRIC.EUCLIDEAN) {
            float sum = 0.0f;
            for (float diff : distances) {
                sum += Math.pow(diff, 2);
            }

            return (float)Math.sqrt(sum);
        } else if (metric == METRIC.TAXICAB) {
            float sum = 0.0f;
            for (float diff : distances) {
                sum += Math.abs(diff);
            }

            return sum;
        } else {
            float max_dist = distances.getFirst();
            for (float diff: distances) {
                if (diff > max_dist) {
                    max_dist = diff;
                }
            }

            return max_dist;
        }
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
        short featureFlags;

        public SimilarityComparator(
            FeatureVector vector,
            int westGermanPoliticianMaxCount,
            float canadianCityMaxFreq,
            short featureFlags
        ) {
            this.vector = vector;
            this.westGermanPoliticianMaxCount = westGermanPoliticianMaxCount;
            this.canadianCityMaxFreq = canadianCityMaxFreq;
            this.featureFlags = featureFlags;
        }

        @Override
        public int compare(FeatureVector v1, FeatureVector v2) {
            int np = this.westGermanPoliticianMaxCount;
            float cf = this.canadianCityMaxFreq;
            short ff = this.featureFlags;

            float s1 = this.vector.getSimilarity(v1, np, cf, ff);
            float s2 = this.vector.getSimilarity(v2, np, cf, ff);

            if (s1 > s2) {
                return -1;
            } else if (s1 < s2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static class DistanceComparator
        implements Comparator<FeatureVector> {

        FeatureVector vector;
        int westGermanPoliticianMaxCount;
        float canadianCityMaxFreq;
        short featureFlags;
        METRIC metric;

        public DistanceComparator(
            FeatureVector vector,
            int westGermanPoliticianMaxCount,
            float canadianCityMaxFreq,
            short featureFlags,
            METRIC metric
        ) {
            this.vector = vector;
            this.westGermanPoliticianMaxCount = westGermanPoliticianMaxCount;
            this.canadianCityMaxFreq = canadianCityMaxFreq;
            this.featureFlags = featureFlags;
            this.metric = metric;
        }

        @Override
        public int compare(FeatureVector v1, FeatureVector v2) {
            int np = this.westGermanPoliticianMaxCount;
            float cf = this.canadianCityMaxFreq;
            short ff = this.featureFlags;

            float s1 = this.vector.getDistance(v1, np, cf, ff, metric);
            float s2 = this.vector.getDistance(v2, np, cf, ff, metric);

            if (s1 < s2) {
                return -1;
            } else if (s1 > s2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private static String getMostFrequentAcronym(
        Map<String, Integer> acronyms
    ) {
        int max_count = 0;
        String most_frequent = "";

        for (Entry<String, Integer> a : acronyms.entrySet()) {
            if (a.getValue() > max_count
                || a.getValue() == max_count
                    && a.getKey().compareTo(most_frequent) < 0) {

                max_count = a.getValue();
                most_frequent = a.getKey();
            }
        }

        return most_frequent;
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

    private static Set<String> generateNGrams(int N, String input) {
        int init_capacity = input.length() - 2;
        if (init_capacity <= 0) return new HashSet<>();

        Set<String> output = new HashSet<>(init_capacity);
        int maxStartIndex = (input.length() == N) ? 1 : input.length() - N;

        for (int i = 0; i < maxStartIndex; ++i) {
            output.add(input.substring(i, i + N));
        }

        return output;
    }

    private static float getGramsSimilarity(
        Set<String> g1,
        Set<String> g2,
        int s1len,
        int s2len
    ) {
        if (s1len == 0 && s2len == 0) return 1.0f;
        else if (s1len < 3 || s2len < 3) return 0.0f;

        int sum = 0;

        int len = (s1len > s2len) ? s1len : s2len;

        for (String gram : g1) {
            if (g2.contains(gram)) sum += 1;
        }

        return (1.0f / (float)(len - 2)) * (float)sum;
    }

    private static float getTitlesSimilarity(Set<String> t1, Set<String> t2) {
        int sum = 0;

        for (String s : t1) {
            if (t2.contains(s)) sum += 1;
        }

        int t1len = t1.size();
        int t2len = t2.size();
        int len = (t1len > t2len) ? t1len : t2len;

        if (t1len == 0 && t2len == 0) return 1.0f;
        else if (t1len == 0 || t2len == 0) return 0.0f;

        return (float)sum / (float)len;
    }
}
