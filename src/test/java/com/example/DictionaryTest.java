package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DictionaryTest {

    private Dictionary dict;
    private Stemmer stemmer;

    @BeforeEach
    void setUp() throws JsonMappingException, JsonProcessingException {
        String json = """
        {
            "P": {
                "west_germany": [
                    "Richard Weizsaecker",
                    "Helmut Kohl"
                ]
            },
            "W": {
                "canada": [
                    "Toronto",
                    "Quebec City",
                    "St. John's"
                ]
            },
            "O": {
                "france": [
                    "Banque Francaise",
                    "Banque de l'Union Europeenne"
                ]
            },
            "C": {
                "japan": [
                    "Toyota",
                    "Sony"
                ]
            },
            "H": {
                "usa": [
                    "Alabama",
                    "New York"
                ]
            },
            "s": {
                "west_germany": [
                    "Bonn"
                ],
            "usa": [
                "Washington"
                ],
            "france": [
                "Paris"
                ],
            "uk": [
                "Londyn"
                ],
            "canada": [
                "Ottawa"
                ],
            "japan": [
                "Tokyo"
                ]
            },
            "m": {
                "west_germany": [
                    "deutschemark",
                    "mark"
                ],
                "usa": [
                    "dollar",
                    "dlr"
                ],
                "france": [
                    "franc"
                ],
                "uk": [
                    "sterling",
                    "stg"
                ],
                "canada": [
                    "canadian dollar",
                    "canadian dlr"
                ],
                "japan": [
                    "yen"
                ]
            }
        }
        """;
        dict = new Dictionary(json);
        stemmer = new Stemmer();
    }

    @Test
    void TestIsWestGermanyPoliticianIfEmptyListOfTokensThenReturnsZero() {
        assertEquals(0, dict.isWestGermanyPolitician(new ArrayList<Token>(), 0));
    }

    @Test
    void TestIsWestGermanyPoliticianIfSingleTokenThenReturnsZero() {
        assertEquals(0, dict.isWestGermanyPolitician(
            Arrays.asList(new Token(Token.Type.CAPITALIZED_WORD, "Richard")),
            0
        ));
    }

    @Test
    void TestIsWestGermanyPoliticianIfValidFullNameProvidedThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Richard Weizsaecker");

        assertNotEquals(0, dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfOnlyLastNameIsValidThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Eljot Weizsaecker");

        assertEquals(0, dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfNonZeroPositionButContainingCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("foo bar Richard Weizsaecker baz");

        assertNotEquals(0, dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 2
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfInvalidPositionThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Helmut Kohl");

        assertEquals(0, dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 4
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfValidNameAtLastPositionThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz Helmut Kohl");

        assertNotEquals(0, dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 3
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfValidNameThenRetunsNumberOfWordsInAName() {
        Tokenizer tokenizer = new Tokenizer("Helmut Kohl");

        assertEquals(2, dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfEmptyListOfTokensThenReturnsZero() {
        assertEquals(0, dict.isCanadianCity(new ArrayList<Token>(), 0));
    }

    @Test
    void TestIsCanadianCityIfValidSingleTokenNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Toronto");

        assertNotEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfMultiWordCityNameThenHandlesItCorrectly() {
        Tokenizer tokenizer = new Tokenizer("Quebec City");

        assertNotEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfLastPartOfMultiWordNameIsInvalidThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Quebec Foo");

        assertEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfFirstPartOfMultiWordNameIsInvalidThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Neo City");

        assertEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfNonZeroPositionButContainingCorrectCityNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("spam eggs foo Toronto bar baz");

        assertNotEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 3
        ));
    }

    @Test
    void TestIsCanadianCityIfNonZeroPositionWithMultiWordCityNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("foo Quebec City bar");

        assertNotEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 1
        ));
    }

    @Test
    void TestIsCanadianCityIfValidButContainsSpecialCharactersThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("St. John's");

        assertNotEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCAnadianCityIfLastTokenOf3TokenNameIsInvalidThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("St. John'X");

        assertEquals(0, dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchBankIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Banque Francaise");

        assertNotEquals(0, dict.isFrenchBank(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchBankIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("foo bar");

        assertEquals(0, dict.isFrenchBank(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchBankIfLongButCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Banque de l'Union Europeenne");

        assertNotEquals(0, dict.isFrenchBank(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchBankIfLongButCorrectNameThenReturnsNumberOfTokensComprisingThatName() {
        Tokenizer tokenizer = new Tokenizer("Banque de l'Union Europeenne");

        assertEquals(5, dict.isFrenchBank(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCompanyIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("asdf");

        assertEquals(0, dict.isJapaneseCompany(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCompanyIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Toyota");

        assertNotEquals(0, dict.isJapaneseCompany(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsStateInUSAIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("hjkl");

        assertEquals(0, dict.isStateInUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsStateInUSAIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("New York");

        assertNotEquals(0, dict.isStateInUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestisCapitalOfWestGermanIfEmptyTokenListThenReturnsZero() {
        assertEquals(0, dict.isCapitalOfWestGerman(new ArrayList<Token>(), 0));
    }

    @Test
    void TestisCapitalOfWestGermanIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("womp womp");

        assertEquals(0, dict.isCapitalOfWestGerman(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestisCapitalOfWestGermanIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Bonn");

        assertNotEquals(0, dict.isCapitalOfWestGerman(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfUSAIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("A!");

        assertEquals(0, dict.isCapitalOfUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfUSAIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Washington");

        assertNotEquals(0, dict.isCapitalOfUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfFranceIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Json");

        assertEquals(0, dict.isCapitalOfFrance(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfFranceIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Paris");

        assertNotEquals(0, dict.isCapitalOfFrance(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfUKIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Hogward");

        assertEquals(0, dict.isCapitalOfUK(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfUKIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Londyn");

        assertNotEquals(0, dict.isCapitalOfUK(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfCanadaIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Khartoum");

        assertEquals(0, dict.isCapitalOfCanada(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfCanadaIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Ottawa");

        assertNotEquals(0, dict.isCapitalOfCanada(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfJapanIfIncorrectNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Kyoto");

        assertEquals(0, dict.isCapitalOfJapan(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfJapanIfCorrectNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("Tokyo");

        assertNotEquals(0, dict.isCapitalOfJapan(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanCurrencyIfInvalidNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("xyzzy");

        assertEquals(0, dict.isWestGermanCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanCurrencyIfNativeCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("deutschemark");

        assertNotEquals(0, dict.isWestGermanCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanCurrencyIfEnglishCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("mark");

        assertNotEquals(0, dict.isWestGermanCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfInvalidNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Chronodollar");

        assertEquals(0, dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfFullCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("dollar");

        assertNotEquals(0, dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfShortenedCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("dlr");

        assertNotEquals(0, dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfPartOfCanadianCurrencyNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("canadian dollar");

        assertEquals(0, dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 1
        ));
    }

    @Test
    void TestIsFrenchCurrencyNameIfInvalidNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Coins");

        assertEquals(0, dict.isFrenchCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchCurrencyNameIfValidNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("franc");

        assertNotEquals(0, dict.isFrenchCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUKCurrencyNameIfInvalidNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("knut");

        assertEquals(0, dict.isUKCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUKCurrencyNameIfFullCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("sterling");

        assertNotEquals(0, dict.isUKCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUKCurrencyNameIfShortenedCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("stg");

        assertNotEquals(0, dict.isUKCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCurrencyNameIfInvalidNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("leaf");

        assertEquals(0, dict.isCanadianCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCurrencyNameIfFullCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("canadian dollar");

        assertNotEquals(0, dict.isCanadianCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCurrencyNameIfShortenedCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("canadian dlr");

        assertNotEquals(0, dict.isCanadianCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCurrencyNameIfInvalidNameThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("Nuyen");

        assertEquals(0, dict.isJapaneseCurrencty(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCurrencyNameIfValidCurrencyNameThenReturnsNonZero() {
        Tokenizer tokenizer = new Tokenizer("yen");

        assertNotEquals(0, dict.isJapaneseCurrencty(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }
}
