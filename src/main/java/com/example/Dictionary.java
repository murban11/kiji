package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Dictionary {

    private Map<String, Map<String, List<List<Token>>>> data;

    @SuppressWarnings("unchecked")
    public Dictionary(File file) throws IOException {
        this.parseJSON(new ObjectMapper().readValue(file, Map.class));
    }

    @SuppressWarnings("unchecked")
    public Dictionary(
        String json
    ) throws JsonMappingException, JsonProcessingException {
        this.parseJSON(new ObjectMapper().readValue(json, Map.class));
    }

    private void parseJSON(Map<String, Map<String, List<String>>> entries) {
        this.data = new HashMap<>();
        for (String outer_key : entries.keySet()) {
            this.data.put(outer_key, new HashMap<>());
            for (String inner_key : entries.get(outer_key).keySet()) {
                this.data.get(outer_key).put(inner_key, new ArrayList<>());
                for (String val : entries.get(outer_key).get(inner_key)) {
                    Stemmer stemmer = new Stemmer();
                    Tokenizer tokenizer = new Tokenizer(val);

                    this.data
                        .get(outer_key)
                        .get(inner_key)
                        .add(stemmer.stemTokens(tokenizer.scanTokens()));
                }
            }
        }
    }

    public boolean isWestGermanyPolitician(List<Token> tokens, int pos) {
        List<List<Token>> names = this.data.get("P").get("west_germany");

        return isValidEntry(names, tokens, pos);
    }

    public boolean isCanadianCity(List<Token> tokens, int pos) {
        List<List<Token>> cities = this.data.get("W").get("canada");

        return isValidEntry(cities, tokens, pos);
    }

    public boolean isFrenchBank(List<Token> tokens, int pos) {
        List<List<Token>> banks = this.data.get("O").get("france");

        return isValidEntry(banks, tokens, pos);
    }

    public boolean isJapaneseCompany(List<Token> tokens, int pos) {
        List<List<Token>> companies = this.data.get("C").get("japan");

        return isValidEntry(companies, tokens, pos);
    }

    public boolean isStateInUSA(List<Token> tokens, int pos) {
        List<List<Token>> states = this.data.get("H").get("usa");

        return isValidEntry(states, tokens, pos);
    }

    public boolean isCapitalOfWestGerman(List<Token> tokens, int pos) {
        List<List<Token>> capital = this.data.get("s").get("west_germany");

        return isValidEntry(capital, tokens, pos);
    }

    public boolean isCapitalOfUSA(List<Token> tokens, int pos) {
        List<List<Token>> capital = this.data.get("s").get("usa");

        return isValidEntry(capital, tokens, pos);
    }

    public boolean isCapitalOfFrance(List<Token> tokens, int pos) {
        List<List<Token>> capital = this.data.get("s").get("france");

        return isValidEntry(capital, tokens, pos);
    }

    public boolean isCapitalOfUK(List<Token> tokens, int pos) {
        List<List<Token>> capital = this.data.get("s").get("uk");

        return isValidEntry(capital, tokens, pos);
    }

    public boolean isCapitalOfCanada(List<Token> tokens, int pos) {
        List<List<Token>> capital = this.data.get("s").get("canada");

        return isValidEntry(capital, tokens, pos);
    }

    public boolean isCapitalOfJapan(List<Token> tokens, int pos) {
        List<List<Token>> capital = this.data.get("s").get("japan");

        return isValidEntry(capital, tokens, pos);
    }

    public boolean isWestGermanCurrency(List<Token> tokens, int pos) {
        List<List<Token>> currency = this.data.get("m").get("west_germany");

        return isValidEntry(currency, tokens, pos);
    }

    public boolean isUSACurrency(List<Token> tokens, int pos) {
        List<List<Token>> usa_currency = this.data.get("m").get("usa");
        List<List<Token>> canadian_currency = this.data.get("m").get("canada");

        if (isValidEntry(canadian_currency, tokens, pos - 1)) {
            return false;
        }

        return isValidEntry(usa_currency, tokens, pos);
    }

    public boolean isFrenchCurrency(List<Token> tokens, int pos) {
        List<List<Token>> currency = this.data.get("m").get("france");

        return isValidEntry(currency, tokens, pos);
    }

    public boolean isUKCurrency(List<Token> tokens, int pos) {
        List<List<Token>> currency = this.data.get("m").get("uk");

        return isValidEntry(currency, tokens, pos);
    }

    public boolean isCanadianCurrency(List<Token> tokens, int pos) {
        List<List<Token>> currency = this.data.get("m").get("canada");

        return isValidEntry(currency, tokens, pos);
    }

    public boolean isJapaneseCurrencty(List<Token> tokens, int pos) {
        List<List<Token>> currency = this.data.get("m").get("japan");

        return isValidEntry(currency, tokens, pos);
    }

    private boolean isValidEntry(
        List<List<Token>> entries,
        List<Token> input,
        int pos
    ) {
        if (pos < 0) {
            return false;
        }

        for (List<Token> entry : entries) {
            if (entry.size() > input.size() - pos) {
                continue;
            }

            boolean foundMismatch = false;
            for (int i = 0; i < entry.size(); ++i) {
                String input_part = input.get(pos + i).getValue();
                String entry_part = entry.get(i).getValue();

                if (!input_part.equals(entry_part)) {
                    foundMismatch = true;
                    break;
                }
            }

            if (!foundMismatch) {
                return true;
            }
        }

        return false;
    }
}
