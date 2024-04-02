package com.example;

public class Token {

    public enum Type {
        NUMBER,
        WORD,
        CAPITALIZED_WORD,
        ACRONIM,
    };

    private Type type;
    private String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public Type getType() {
        return this.type;
    }
}
