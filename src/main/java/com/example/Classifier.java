package com.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Classifier {

    private int K;

    private List<FeatureVector> trainingVectors;
    private List<FeatureVector> testingVectors;

    private int westGermanPoliticianMaxCount;
    private float canadianCityMaxFreq;

    private Map<Article.LABEL, Map<Article.LABEL, Integer>> confusionMatrix;

    public Classifier(
        int K,
        List<FeatureVector> trainingVectors,
        List<FeatureVector> testingVectors,
        int westGermanPoliticianMaxCount,
        float canadianCityMaxFreq
    ) {
        assert(trainingVectors.size() > K);

        this.K = K;
        this.trainingVectors = trainingVectors;
        this.testingVectors = testingVectors;
        this.westGermanPoliticianMaxCount = westGermanPoliticianMaxCount;
        this.canadianCityMaxFreq = canadianCityMaxFreq;

        int labelCount = Article.LABEL.values().length;
        this.confusionMatrix = new HashMap<>(labelCount);
        for (Article.LABEL label : Article.LABEL.values()) {
            this.confusionMatrix.put(label, new HashMap<>(labelCount));
            for (Article.LABEL l : Article.LABEL.values()) {
                this.confusionMatrix.get(label).put(l, 0);
            }
        }
    }

    public void clasify() {
        int n = 0;
        int testingSize = testingVectors.size();

        for (FeatureVector vec : testingVectors) {
            System.out.print("\r" + (n+1) + " of " + testingSize);
            ++n;

            Collections.sort(
                trainingVectors,
                new FeatureVector.DistanceComparator(
                    vec,
                    westGermanPoliticianMaxCount,
                    canadianCityMaxFreq
                )
            );

            assert(trainingVectors
                    .getFirst()
                    .getDistance(
                        vec,
                        westGermanPoliticianMaxCount,
                        canadianCityMaxFreq
                    )
                > trainingVectors
                    .getLast()
                    .getDistance(
                        vec,
                        westGermanPoliticianMaxCount,
                        canadianCityMaxFreq
                    )
            );

            Map<Article.LABEL, Integer> labelCounts
                = new HashMap<>(Article.LABEL.values().length);
            for (Article.LABEL label : Article.LABEL.values()) {
                labelCounts.put(label, 0);
            }

            for (int i = 0; i < K; ++i) {
                Article.LABEL nLabel
                    = trainingVectors.get(i).getLabel().orElseThrow();
                labelCounts.put(nLabel, labelCounts.get(nLabel) + 1);
            }

            Article.LABEL label
                = trainingVectors.getFirst().getLabel().orElseThrow();
            int most_frequent = 0;
            for (Entry<Article.LABEL, Integer> e : labelCounts.entrySet()) {
                if (e.getValue() > most_frequent) {
                    most_frequent = e.getValue();
                    label = e.getKey();
                }
            }
            assert(most_frequent > 0);

            Article.LABEL actualLabel = vec.getLabel().orElseThrow();

            int count = this.confusionMatrix.get(actualLabel).get(label);
            this.confusionMatrix.get(actualLabel).put(label, count + 1);
        }
    }

    public float getSensitivity(Article.LABEL label) {
        int correctly_assigned = this.confusionMatrix.get(label).get(label);

        int actually_belonging = 0;
        for (Article.LABEL l : Article.LABEL.values()) {
            actually_belonging += this.confusionMatrix.get(label).get(l);
        }

        return (float)correctly_assigned / (float)actually_belonging;
    }

    public float getPrecision(Article.LABEL label) {
        int correctly_assigned = this.confusionMatrix.get(label).get(label);

        int incorrectly_assigned = 0;
        for (Article.LABEL l : Article.LABEL.values()) {
            incorrectly_assigned += this.confusionMatrix.get(l).get(label);
        }

        return (float)correctly_assigned / (float)incorrectly_assigned;
    }

    public float getAccuracy() {
        int correctly_classified = 0;

        for (Article.LABEL l : Article.LABEL.values()) {
            correctly_classified += this.confusionMatrix.get(l).get(l);
        }

        return (float)correctly_classified / (float)testingVectors.size();
    }

    public float getWeightedMeanOfSensitivity() {
        float mean = 0.0f;
        for (Article.LABEL l : Article.LABEL.values()) {
            int assigned_count = 0;
            for (Article.LABEL m : Article.LABEL.values()) {
                assigned_count += this.confusionMatrix.get(l).get(m);
            }

            mean += (float)(getSensitivity(l) * assigned_count)
                / (float)testingVectors.size();
        }

        return mean;
    }

    public float getWeightedMeanOfPrecision() {
        float mean = 0.0f;
        for (Article.LABEL l : Article.LABEL.values()) {
            int assigned_count = 0;
            for (Article.LABEL m : Article.LABEL.values()) {
                assigned_count += this.confusionMatrix.get(l).get(m);
            }

            mean += (float)(getPrecision(l) * assigned_count)
                / (float)testingVectors.size();
        }

        return mean;
    }

    public float getF1(Article.LABEL label) {
        float sensitivity = getSensitivity(label);
        float precision = getPrecision(label);

        return 2*sensitivity*precision / (sensitivity + precision);
    }

    public float getWeightedMeanOfF1() {
        float mean = 0.0f;
        for (Article.LABEL l : Article.LABEL.values()) {
            int assigned_count = 0;
            for (Article.LABEL m : Article.LABEL.values()) {
                assigned_count += this.confusionMatrix.get(l).get(m);
            }

            mean += (float)(getF1(l) * assigned_count)
                / (float)testingVectors.size();
        }

        return mean;
    }
}
