package com.example;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private String source;
    private List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;

    public Tokenizer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        return tokens;
    }

    private void scanToken() {
        char c = advance();

        if (Character.isLowerCase(c)) {
            consumeWord();
        } else if (Character.isUpperCase(c)) {
            consumeCapitalizedWordOrAcronim();
        } else if (Character.isDigit(c) || c == '-') {
            consumeNumber();
        }
    }

    private void consumeWord() {
        while (Character.isLetter(peek())) {
            advance();
        }
        addToken(Token.Type.WORD);
    }

    private void consumeCapitalizedWordOrAcronim() {
            Token.Type type = Token.Type.ACRONIM;

            while (Character.isLetter(peek())
                || (peek() == '.' && type == Token.Type.ACRONIM)
            ) {
                if (Character.isLowerCase(advance())) {
                    type = Token.Type.CAPITALIZED_WORD;
                }
            }

            addToken(type);
    }

    private void consumeNumber() {
            while (Character.isDigit(peek())
                || peek() == '.'
                || peek() == ',' // Ignore positions of the commas, because
                                 // there are many typos in the dataset.
            ) {
                advance();
            }
            addToken(Token.Type.NUMBER);
    }

    private void addToken(Token.Type type) {
        String value = source.substring(start, current);

        switch (type) {
            case WORD:
                value = processWordValue(value);
                break;
            case CAPITALIZED_WORD:
                value = processCapitalizedWordValue(value);
                break;
            case ACRONIM:
                value = processAcronimValue(value);
                break;
            case NUMBER:
                value = processNumberValue(value);
                break;
        }

        tokens.add(new Token(type, value));
    }

    private String processWordValue(String value) {
        return value.toLowerCase();
    }

    private String processCapitalizedWordValue(String value) {
        return value.toLowerCase();
    }

    private String processAcronimValue(String value) {
        return value.toLowerCase().replaceAll("\\.", "");
    }

    private String processNumberValue(String value) {
        return value.replaceAll(",", "");
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        return (isAtEnd()) ? '\0' : source.charAt(current);
    }
}
