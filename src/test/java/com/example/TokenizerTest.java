package com.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TokenizerTest {

    @Test
    void TestScanTokensIfEmptyInputThenReturnsEmptyList() {
        Tokenizer tokenizer = new Tokenizer("");
        List<Token> result = tokenizer.scanTokens();
        assertTrue(result.isEmpty());
    }

    @Test
    void TestScanTokensIfLowercaseWordThenReturnsSingleToken() {
        String lexeme = "foo";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfLowercaseWordThenResultingTokenHasCorrectValue() {
        String lexeme = "bar";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(lexeme, result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfLowercaseWordThenResultingTokenHasCorrectType() {
        String lexeme = "baz";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.WORD, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfCapitalizedWordThenReturnsSingleToken() {
        String lexeme = "Lain";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfCapitalizedWordThenResultingTokenHasCorrectValue() {
        String lexeme = "Iwakura";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(lexeme.toLowerCase(), result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfCapitalizedWordThenResultingTokenHasCorrectType() {
        String lexeme = "Alice";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.CAPITALIZED_WORD, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfAcronimWithoutDotsThenReturnsSingleToken() {
        String lexeme = "SEL";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfAcronimWithoutDotsThenResultingTokenHasCorrentValue() {
        String lexeme = "KISS";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(lexeme.toLowerCase(), result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfAcronimWithoutDotsThenResultingTokenHasCorrectType() {
        String lexeme = "DRY";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.ACRONIM, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfAcronimWithDotsThenReturnsSingleToken() {
        String lexeme = "A.I.";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfAcronimWithDotsThenResultingTokenHasCorrectValue() {
        String lexeme = "D.D.O.S.";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("ddos", result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfCamelCaseWordThenReturnsSingleToken() {
        String lexeme = "cAmElCaSe";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfCamelCaseWordThenResultingTokenHasCorrectValue() {
        String lexeme = "kAwAiI";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("kawaii", result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfCamelCaseWordThenResultingTokenHasCorrectType() {
        String lexeme = "kErNeL";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.WORD, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfPascalCaseWordThenReturnsSingleToken() {
        String lexeme = "NeoVim";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfPascalCaseWordThenResultingTokenHasCorrectValue() {
        String lexeme = "UwU";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("uwu", result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfPascalCaseWordThenResultingTokenHasCorrectType() {
        String lexeme = "FreeBSD";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.CAPITALIZED_WORD, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfMultipleWordsThenReturnsCorrectNumberOfTokens() {
        String text = "Boku no chinchin wa chiisai";
        Tokenizer tokenizer = new Tokenizer(text);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(5, result.size());
    }

    @Test
    void TestScanTokensIfMultipleWordsThenResultingTokensHaveCorrectValues() {
        String text = "Lorem ipsum dolor";
        Tokenizer tokenizer = new Tokenizer(text);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("lorem", result.get(0).getValue());
        assertEquals("ipsum", result.get(1).getValue());
        assertEquals("dolor", result.get(2).getValue());
    }

    @Test
    void TestScanTokensIfMultipleWordsThenResultingTokensHaveCorrectTypes() {
        String text = "Cap cAm Pas";
        Tokenizer tokenizer = new Tokenizer(text);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.CAPITALIZED_WORD, result.get(0).getType());
        assertEquals(Token.Type.WORD, result.get(1).getType());
        assertEquals(Token.Type.CAPITALIZED_WORD, result.get(2).getType());
    }

    @Test
    void TestScanTokensIfMultipleSentencesThenReturnsCorrectNumberOfTokens() {
        String text = "First sentence. Second sentence.";
        Tokenizer tokenizer = new Tokenizer(text);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(4, result.size());
    }

    @Test
    void TestScanTokensIfMultipleSentencesWithoutSpaceAfterDotThenReturnsCorrectNumberOfTokens() {
        String text = "Lorem.Ipsum";
        Tokenizer tokenizer = new Tokenizer(text);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(2, result.size());
    }

    @Test
    void TestScanTokensIfSingleUpperCaseLetterThenResultingTokenHasCorrectType() {
        String lexeme = "I";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.CAPITALIZED_WORD, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfSingleUpperCaseLetterFollowedByDotThenResultingTokenHasCorrectType() {
        String lexeme = "I.";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.CAPITALIZED_WORD, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfSingleUpperCaseLetterPrecededByDotThenResultingTokenHasCorrectType() {
        String lexeme = ".I";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.CAPITALIZED_WORD, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfUnsupportedCharactersThenIgnoresThem() {
        String text = "int main() { printf(\"Hello world!\\n\"); }";
        Tokenizer tokenizer = new Tokenizer(text);
        List<Token> result = tokenizer.scanTokens();

        assertEquals(6, result.size());

        assertEquals(Token.Type.WORD, result.get(0).getType());
        assertEquals(Token.Type.WORD, result.get(1).getType());
        assertEquals(Token.Type.WORD, result.get(2).getType());
        assertEquals(Token.Type.CAPITALIZED_WORD, result.get(3).getType());
        assertEquals(Token.Type.WORD, result.get(4).getType());
        assertEquals(Token.Type.WORD, result.get(5).getType());

        assertEquals("int", result.get(0).getValue());
        assertEquals("main", result.get(1).getValue());
        assertEquals("printf", result.get(2).getValue());
        assertEquals("hello", result.get(3).getValue());
        assertEquals("world", result.get(4).getValue());
        assertEquals("n", result.get(5).getValue());
    }

    @Test
    void TestScanTokensIfNumberThenReturnsSingleToken() {
        String lexeme = "42";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfNumberThenResultingTokenHasCorrectType() {
        String lexeme = "34";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.NUMBER, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfNumberThenResultingTokenHasCorrectValue() {
        String lexeme = "1337";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("1337", result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfNumberWithDecimalPointThenReturnsSingleToken() {
        String lexeme = "3.14";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfNumberWithDecimalPointThenResultingTokenHasCorrectType() {
        String lexeme = "6.02";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.NUMBER, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfNumberWithDecimalPointThenResultingTokenHasCorrectValue() {
        String lexeme = "2.72";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("2.72", result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfNegativeNumberThenReturnsSingleToken() {
        String lexeme = "-22";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfNegativeNumberThenResultingTokenHasCorrectType() {
        String lexeme = "-11";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.NUMBER, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfNegativeNumberThenResultingTokenHasCorrectValue() {
        String lexeme = "-278";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(lexeme, result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfNegativeNumberWithDecimalPointThenRetunsSingleToken() {
        String lexeme = "-286.32";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfNegativeNumberWithDecimalPointThenResultingTokenHasCorrectType() {
        String lexeme = "-294.32";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.NUMBER, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfNegativeNumberWithDecimalPointThenResultingTokenHasCorrectValue() {
        String lexeme = "-302.32";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(lexeme, result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfNumberWithSingleCommaThenReturnsSingleToken() {
        String lexeme = "100,000";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfNumberWithSingleCommaThenResultingTokenHasCorrectType() {
        String lexeme = "200,000";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.NUMBER, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfNumberWithSingleCommaThenResultingTokenHasCorrectValue() {
        String lexeme = "300,000";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("300000", result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfNumberWithMultipleCommasThenReturnsSingleToken() {
        String lexeme = "100,000,000,000";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfNumberWithMultipleCommasThenResultingTokenHasCorrectType() {
        String lexeme = "200,000,000,000";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.NUMBER, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfNumberWithMultipleCommasThenResultingTokenHasCorrectValue() {
        String lexeme = "300,000,000,000";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("300000000000", result.getFirst().getValue());
    }

    @Test
    void TestScanTokensIfNumberWithCommaAndDotThenReturnsSingleToken() {
        String lexeme = "358,169.35";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(1, result.size());
    }

    @Test
    void TestScanTokensIfNumberWithCommaAndDotThenResultingTokenHasCorrectType() {
        String lexeme = "366,983.92";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals(Token.Type.NUMBER, result.getFirst().getType());
    }

    @Test
    void TestScanTokensIfNumberWithCommaAndDotThenResultingTokenHasCorrectValue() {
        String lexeme = "374,392.38";
        Tokenizer tokenizer = new Tokenizer(lexeme);
        List<Token> result = tokenizer.scanTokens();
        assertEquals("374392.38", result.getFirst().getValue());
    }
}
