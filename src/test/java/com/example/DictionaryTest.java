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
    void TestIsWestGermanyPoliticianIfEmptyListOfTokensThenReturnsFalse() {
        assertFalse(dict.isWestGermanyPolitician(new ArrayList<Token>(), 0));
    }

    @Test
    void TestIsWestGermanyPoliticianIfSingleTokenThenReturnsFalse() {
        assertFalse(dict.isWestGermanyPolitician(
            Arrays.asList(new Token(Token.Type.CAPITALIZED_WORD, "Richard")),
            0
        ));
    }

    @Test
    void TestIsWestGermanyPoliticianIfValidFullNameProvidedThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Richard Weizsaecker");

        assertTrue(dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfOnlyLastNameIsValidThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Eljot Weizsaecker");

        assertFalse(dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfNonZeroPositionButContainingCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo bar Richard Weizsaecker baz");

        assertTrue(dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 2
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfInvalidPositionThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Helmut Kohl");

        assertFalse(dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 4
        ));
    }

    @Test
    void TestIsWestGermanPoliticianIfValidNameAtLastPositionThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz Helmut Kohl");

        assertTrue(dict.isWestGermanyPolitician(
            stemmer.stemTokens(tokenizer.scanTokens()), 3
        ));
    }

    @Test
    void TestIsCanadianCityIfEmptyListOfTokensThenReturnsFalse() {
        assertFalse(dict.isCanadianCity(new ArrayList<Token>(), 0));
    }

    @Test
    void TestIsCanadianCityIfValidSingleTokenNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Toronto");

        assertTrue(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfMultiWordCityNameThenHandlesItCorrectly() {
        Tokenizer tokenizer = new Tokenizer("Quebec City");

        assertTrue(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfLastPartOfMultiWordNameIsInvalidThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Quebec Foo");

        assertFalse(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfFirstPartOfMultiWordNameIsInvalidThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Neo City");

        assertFalse(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCityIfNonZeroPositionButContainingCorrectCityNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("spam eggs foo Toronto bar baz");

        assertTrue(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 3
        ));
    }

    @Test
    void TestIsCanadianCityIfNonZeroPositionWithMultiWordCityNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo Quebec City bar");

        assertTrue(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 1
        ));
    }

    @Test
    void TestIsCanadianCityIfValidButContainsSpecialCharactersThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("St. John's");

        assertTrue(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCAnadianCityIfLastTokenOf3TokenNameIsInvalidThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("St. John'X");

        assertFalse(dict.isCanadianCity(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchBankIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Banque Francaise");

        assertTrue(dict.isFrenchBank(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchBankIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar");

        assertFalse(dict.isFrenchBank(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchBankIfLongButCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Banque de l'Union Europeenne");

        assertTrue(dict.isFrenchBank(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCompanyIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("asdf");

        assertFalse(dict.isJapaneseCompany(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCompanyIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Toyota");

        assertTrue(dict.isJapaneseCompany(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsStateInUSAIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("hjkl");

        assertFalse(dict.isStateInUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsStateInUSAIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("New York");

        assertTrue(dict.isStateInUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestisCapitalOfWestGermanIfEmptyTokenListThenReturnsFalse() {
        assertFalse(dict.isCapitalOfWestGerman(new ArrayList<Token>(), 0));
    }

    @Test
    void TestisCapitalOfWestGermanIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("womp womp");

        assertFalse(dict.isCapitalOfWestGerman(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestisCapitalOfWestGermanIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Bonn");

        assertTrue(dict.isCapitalOfWestGerman(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfUSAIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("A!");

        assertFalse(dict.isCapitalOfUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfUSAIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Washington");

        assertTrue(dict.isCapitalOfUSA(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfFranceIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Json");

        assertFalse(dict.isCapitalOfFrance(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfFranceIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Paris");

        assertTrue(dict.isCapitalOfFrance(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfUKIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Hogward");

        assertFalse(dict.isCapitalOfUK(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfUKIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Londyn");

        assertTrue(dict.isCapitalOfUK(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfCanadaIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Khartoum");

        assertFalse(dict.isCapitalOfCanada(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfCanadaIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Ottawa");

        assertTrue(dict.isCapitalOfCanada(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCaptialOfJapanIfIncorrectNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Kyoto");

        assertFalse(dict.isCapitalOfJapan(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCapitalOfJapanIfCorrectNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Tokyo");

        assertTrue(dict.isCapitalOfJapan(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanCurrencyIfInvalidNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("xyzzy");

        assertFalse(dict.isWestGermanCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanCurrencyIfNativeCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("deutschemark");

        assertTrue(dict.isWestGermanCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsWestGermanCurrencyIfEnglishCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("mark");

        assertTrue(dict.isWestGermanCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfInvalidNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Chronodollar");

        assertFalse(dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfFullCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("dollar");

        assertTrue(dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfShortenedCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("dlr");

        assertTrue(dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUSACurrencyNameIfPartOfCanadianCurrencyNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("canadian dollar");

        assertFalse(dict.isUSACurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 1
        ));
    }

    @Test
    void TestIsFrenchCurrencyNameIfInvalidNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Coins");

        assertFalse(dict.isFrenchCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsFrenchCurrencyNameIfValidNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("franc");

        assertTrue(dict.isFrenchCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUKCurrencyNameIfInvalidNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("knut");

        assertFalse(dict.isUKCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUKCurrencyNameIfFullCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("sterling");

        assertTrue(dict.isUKCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsUKCurrencyNameIfShortenedCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("stg");

        assertTrue(dict.isUKCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCurrencyNameIfInvalidNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("leaf");

        assertFalse(dict.isCanadianCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCurrencyNameIfFullCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("canadian dollar");

        assertTrue(dict.isCanadianCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsCanadianCurrencyNameIfShortenedCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("canadian dlr");

        assertTrue(dict.isCanadianCurrency(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCurrencyNameIfInvalidNameThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Nuyen");

        assertFalse(dict.isJapaneseCurrencty(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }

    @Test
    void TestIsJapaneseCurrencyNameIfValidCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("yen");

        assertTrue(dict.isJapaneseCurrencty(
            stemmer.stemTokens(tokenizer.scanTokens()), 0
        ));
    }
}
