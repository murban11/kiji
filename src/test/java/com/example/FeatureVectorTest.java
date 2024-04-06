package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class FeatureVectorTest {

    private Dictionary dict;
    private Stemmer stemmer;
    private double delta = 0.001;
    private final static List<Token> title = new ArrayList<>();

    @BeforeEach
    void setUp() throws JsonMappingException, JsonProcessingException {
        String json = """
            {
                "P": {
                    "west_germany": [
                        "Richard Weizsaecker",
                        "Helmut Kohl",
                        "Otto Schlecht",
                        "Gerhard Stoltenberg",
                        "Helmut Schmidt",
                        "Heinz Riesenhuber",
                        "Willy Brandt",
                        "Walter Wallmann",
                        "Hans Tietmeyer",
                        "Gerhard Stoltenberg"
                    ]
                },
                "W": {
                    "canada": [
                        "Toronto",
                        "Montreal",
                        "Vancouver",
                        "Calgary",
                        "Edmonton",
                        "Ottawa",
                        "Winnipeg",
                        "Quebec City",
                        "Hamilton",
                        "Kitchener",
                        "Victoria",
                        "Halifax",
                        "Oshawa",
                        "Windsor",
                        "Saskatoon",
                        "Regina",
                        "St. John's",
                        "Barrie",
                        "Sherbrooke"
                    ]
                },
                "O": {
                    "france": [
                        "Banque Francaise",
                        "Banque Nationale de Paris",
                        "Credit Lyonnais",
                        "Societe Generale",
                        "Credit Agricole",
                        "Banque Indosuez",
                        "Banque Paribas",
                        "Credit Industriel et Commercial",
                        "Banque de l'Union Europeenne",
                        "Banque Hervet"
                    ]
                },
                "C": {
                    "japan": [
                        "Toyota",
                        "Sony",
                        "Honda",
                        "Nissan",
                        "Mitsubishi",
                        "Hitachi",
                        "Toshiba",
                        "Canon",
                        "Suzuki",
                        "Mazda",
                        "Fujitsu"
                    ]
                },
                "H": {
                    "usa": [
                        "Alabama",
                        "Alaska",
                        "Arizona",
                        "Arkansas",
                        "California",
                        "Colorado",
                        "Connecticut",
                        "Delaware",
                        "Florida",
                        "Georgia",
                        "Hawaii",
                        "Idaho",
                        "Illinois",
                        "Indiana",
                        "Iowa",
                        "Kansas",
                        "Kentucky",
                        "Louisiana",
                        "Maine",
                        "Maryland",
                        "Massachusetts",
                        "Michigan",
                        "Minnesota",
                        "Mississippi",
                        "Missouri",
                        "Montana",
                        "Nebraska",
                        "Nevada",
                        "New Hampshire",
                        "New Jersey",
                        "New Mexico",
                        "New York",
                        "North Carolina",
                        "North Dakota",
                        "Ohio",
                        "Oklahoma",
                        "Oregon",
                        "Pennsylvania",
                        "Rhode Island",
                        "South Carolina",
                        "South Dakota",
                        "Tennessee",
                        "Texas",
                        "Utah",
                        "Vermont",
                        "Virginia",
                        "Washington",
                        "West Virginia",
                        "Wisconsin",
                        "Wyoming"
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
                        "London"
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
    void TestGetWestGermanPoliticianCountIfEmptyTokenListThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features
            = new FeatureVector(tokenizer.scanTokens(), title, dict);

        assertEquals(0, features.getWestGermanPoliticianCount());
    }

    @Test
    void TestGetWestGermanPoliticianCountIfNoPoliticianThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features
            = new FeatureVector(tokenizer.scanTokens(), title, dict);

        assertEquals(0, features.getWestGermanPoliticianCount());
    }

    @Test
    void TestGetWestGermanPoliticianCountIfASinglePoliticianThenReturnsOne() {
        Tokenizer tokenizer = new Tokenizer("lorem ipsum Otto Schlecht dolor");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(1, features.getWestGermanPoliticianCount());
    }

    @Test
    void TestGetWestGermanPoliticianCountIfMultiplePoliticiansThenRetunsCorrectCount() {
        Tokenizer tokenizer = new Tokenizer("""
            lorem
            Otto Schlecht
            ipsum
            Willy Brandt
            dolor
            Hans Tietmeyer
            sit
        """);
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(3, features.getWestGermanPoliticianCount());
    }

    @Test
    void TestGetWestGermanPoliticianCountIfExampleFromReportThenReturnsCorrectCount() {
        Tokenizer tokenizer = new Tokenizer("""
            In 1987, Willy Brandt, the former Chancellor of West Germany,
            made an important speech regarding East-West relations
        """);
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(1, features.getWestGermanPoliticianCount());
    }

    @Test
    void TestGetWestGermanPoliticianCountIfRepetitionThenCountsTheRepetitionsToo() {
        Tokenizer tokenizer = new Tokenizer("foo Helmut Kohl bar Helmut Kohl");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(2, features.getWestGermanPoliticianCount());
    }

    @Test
    void TestGetCanadianCityFreqIfEmptyTokenListThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertEquals(0, features.getCanadianCityFreq());
    }

    @Test
    void TestGetCanadianCityFreqIfNoCanadianCityThenReturnsZero() {
        Tokenizer tokenizer = new Tokenizer("spam eggs");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertEquals(0, features.getCanadianCityFreq());
    }

    @Test
    void TestGetCanadianCityFreqIfASingleCanadianCityThenReturnsCorrectFreq() {
        Tokenizer tokenizer = new Tokenizer("spam Oshawa eggs");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(1.0/3.0, features.getCanadianCityFreq(), delta);
    }

    @Test
    void TestGetCanadianCityFreqIfMultipleCanadianCitiesThenReturnCorrectFreq() {
        Tokenizer tokenizer = new Tokenizer("""
            spam
            Oshawa
            eggs
            Ottawa
            qux
        """);
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(2.0/5.0, features.getCanadianCityFreq(), delta);
    }

    @Test
    void TestGetCanadianCityFreqIfDoubleWordCityNameThenReturnsCorrectFreq() {
        Tokenizer tokenizer = new Tokenizer("foo Quebec City");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(1.0/2.0, features.getCanadianCityFreq(), delta);
    }

    @Test
    void TestGetCanadianCityFreqIfMultiTokenCityNameThenReturnsCorrectFreq() {
        // The `s` lexeme will be removed by the stoplist normally, but let's
        // test it anyway.
        Tokenizer tokenizer = new Tokenizer("foo St. John's bar");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(1.0/3.0, features.getCanadianCityFreq(), delta);
    }

    @Test
    void TestGetCanadianCityFreqIfMultipleMultiTokenCityNamesThenReturnsCorrectFreq() {
        Tokenizer tokenizer = new Tokenizer("""
            foo
            St. John's
            bar
            Quebec City
            baz
        """);
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(2.0/5.0, features.getCanadianCityFreq(), delta);
    }

    @Test
    void TestGetCanadianCityFreqIfExampleFromReportThenReturnsCorrectFreq() {
        Tokenizer tokenizer = new Tokenizer("""
            Tourists came to Canada in record numbers last year, attracted
            by the relatively weak Canadian dollar and Expo 86 in Vancouver,
            which alone had more than 22 mln visitors.
        """);
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(1.0/29.0, features.getCanadianCityFreq(), delta);
    }

    @Test
    void TestGetCanadianCityFreqIfRepetitionThenCountsTheRepetitionsToo() {
        Tokenizer tokenizer = new Tokenizer("foo Vancouver bar Vancouver");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals(2.0/4.0, features.getCanadianCityFreq(), delta);
    }

    @Test
    void TestIsFrenchBankPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isFrenchBankPresent());
    }

    @Test
    void TestIsFrenchBankPresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isFrenchBankPresent());
    }

    @Test
    void TestIsFrenchBankPresentIfDoubleWordNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Societe Generale");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isFrenchBankPresent());
    }

    @Test
    void TestIsFrenchBankPresentIfMultiTokenNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Banque de l'Union Europeenne");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isFrenchBankPresent());
    }

    @Test
    void TestIsUKAcronymPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isUKAcronymPresent());
    }

    @Test
    void TestIsUKAcronymPresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isUKAcronymPresent());
    }

    @Test
    void TestIsUKAcronymPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo U.K. bar");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUKAcronymPresent());
    }

    @Test
    void TestIsJapaneseCompanyPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isJapaneseCompanyPresent());
    }

    @Test
    void TestIsJapaneseCompanyPresnetIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isJapaneseCompanyPresent());
    }

    @Test
    void TestIsJapaneseCompanyPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Toyota");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isJapaneseCompanyPresent());
    }

    @Test
    void TestIsUSAStatePresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isUSAStatePresent());
    }

    @Test
    void TestIsUSAStatePresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isUSAStatePresent());
    }

    @Test
    void TestIsUSAStatePresentIfSingleTokenNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Idaho");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUSAStatePresent());
    }

    @Test
    void TestIsUSAStatePresentIfMultiTokenNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("New York");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUSAStatePresent());
    }

    @Test
    void TestIsCapitalOfWestGermanyPresentIfNotThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Lorem ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isCapitalOfWestGermanyPresent());
    }

    @Test
    void TestIsCapitalOfWestGermanyPresentIfYesThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Lorem Bonn ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCapitalOfWestGermanyPresent());
    }

    @Test
    void TestIsCapitalOfUSAPresentIfNotThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Lorem ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isCapitalOfUSAPresent());
    }

    @Test
    void TestIsCapitalOfUSAPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Lorem Washington D.C. ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCapitalOfUSAPresent());
    }

    @Test
    void TestIsCapitalOfFrancePresentIfNotThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Lorem ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isCapitalOfFrancePresent());
    }

    @Test
    void TestIsCapitalOfFrancePresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Lorem Paris ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCapitalOfFrancePresent());
    }

    @Test
    void TestIsCapitalOfUKPresentIfNotThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Lorem ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isCapitalOfUKPresent());
    }

    @Test
    void TestIsCapitalOfUKPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Lorem London ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCapitalOfUKPresent());
    }

    @Test
    void TestIsCapitalOfCanadaPresentIfNotThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Lorem ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isCapitalOfCanadaPresent());
    }

    @Test
    void TestIsCapitalOfCanadaPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Lorem Ottawa ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCapitalOfCanadaPresent());
    }

    @Test
    void TestIsCapitalOfJapanPresentIfNotThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("Lorem ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isCapitalOfJapanPresent());
    }

    @Test
    void TestIsCapitalOfJapanPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("Lorem Tokyo ipsum");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCapitalOfJapanPresent());
    }

    @Test
    void TestIsWestGermanyCurrencyPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isWestGermanCurrencyPresent());
    }

    @Test
    void TestIsWestGermanyCurrencyPresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isWestGermanCurrencyPresent());
    }

    @Test
    void TestIsWestGermanCurrencyPresentIfNativeNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo deutschemark bar");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isWestGermanCurrencyPresent());
    }

    @Test
    void TestIsWestGermanyCurrencyPresentIfEnglishNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo mark bar");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isWestGermanCurrencyPresent());
    }

    @Test
    void TestIsUSACurrencyPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isUSACurrencyPresent());
    }

    @Test
    void TestIsUSACurrencyPresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isUSACurrencyPresent());
    }

    @Test
    void TestIsUSACurrencyPresentIfFullNamePresentThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo dollar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUSACurrencyPresent());
    }

    @Test
    void TestIsUSACurrencyPresentIfShortenedNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("foo dlr baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUSACurrencyPresent());
    }

    @Test
    void TestIsUSACurrencyPresentIfCanadianCurrencyThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo canadian dollar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUSACurrencyPresent());
    }

    @Test
    void TestIsFrenchCurrencyPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isFrenchCurrencyPresent());
    }

    @Test
    void TestIsFrenchCurrencyPresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isFrenchCurrencyPresent());
    }

    @Test
    void TestIsFrenchCurrencyPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("franc");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isFrenchCurrencyPresent());
    }

    @Test
    void TestIsUKCurrencyPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isUKCurrencyPresent());
    }

    @Test
    void TestIsUKCurrencyPresentIfNotPrensentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isUKCurrencyPresent());
    }

    @Test
    void TestIsUKCurrencyPresentIfFullCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("sterling");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUKCurrencyPresent());
    }

    @Test
    void TestIsUKCurrencyPresentIfShortenedCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("stg");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isUKCurrencyPresent());
    }

    @Test
    void TestIsCanadianCurrencyPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isCanadianCurrencyPresent());
    }

    @Test
    void TestIsCanadianCurrencyPresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isCanadianCurrencyPresent());
    }

    @Test
    void TestIsCanadianCurrencyPresentIfFullCurrencyNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("canadian dollar");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCanadianCurrencyPresent());
    }

    @Test
    void TestIsCanadianCurrencyPresentIfShortenedNameThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("canadian dlr");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isCanadianCurrencyPresent());
    }

    @Test
    void TestIsJapaneseCurrencyPresentIfEmptyTokenListThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertFalse(features.isJapaneseCurrencyPresent());
    }

    @Test
    void TestIsJapaneseCurrencyPresentIfNotPresentThenReturnsFalse() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertFalse(features.isJapaneseCurrencyPresent());
    }

    @Test
    void TestIsJapaneseCurrencyPresentIfYesThenReturnsTrue() {
        Tokenizer tokenizer = new Tokenizer("yen");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertTrue(features.isJapaneseCurrencyPresent());
    }

    @Test
    void TestGetFirstCapitalizedWordIfEmptyTokenListThenReturnsEmptyString() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertEquals("", features.getFirstCapitalizedWord());
    }

    @Test
    void TestGetFirstCapitalizedWordIfThereIsNoCapitalizedWordThenReturnsEmptyString() {
        Tokenizer tokenizer = new Tokenizer("lorem ipsum 22 dolor");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals("", features.getFirstCapitalizedWord());
    }

    @Test
    void TestGetFirstCapitalizedWordIfFirstWordIsCapitalizedThenReturnsItsTokenValue() {
        Tokenizer tokenizer = new Tokenizer("Foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer
            .stemTokens(tokenizer.scanTokens())
            .getFirst()
            .getValue();

        assertEquals(expected, features.getFirstCapitalizedWord());
    }

    @Test
    void TestGetFirstCapitalizedWordIfNonFirstWordIsCapitalizedThenReturnsItsTokenValue() {
        Tokenizer tokenizer = new Tokenizer("foo Bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer
            .stemTokens(tokenizer.scanTokens())
            .get(1)
            .getValue();

        assertEquals(expected, features.getFirstCapitalizedWord());
    }

    @Test
    void TestGetFirstCapitalizedWordIfMultipleCapitalizedWordsThenReturnsTheTokenValueOfTheFirstOne() {
        Tokenizer tokenizer = new Tokenizer("foo bar Baz Qux");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer
            .stemTokens(tokenizer.scanTokens())
            .get(2)
            .getValue();

        assertEquals(expected, features.getFirstCapitalizedWord());
    }

    @Test
    void TestGetFirstCapitalizedWordIfItsADictionaryEntryThenStillReturnsIt() {
        Tokenizer tokenizer = new Tokenizer("foo Tokyo Baz Qux");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer
            .stemTokens(tokenizer.scanTokens())
            .get(1)
            .getValue();

        assertEquals(expected, features.getFirstCapitalizedWord());
    }

    @Test
    void TestGetFirstNumberIfEmptyTokenListThenReturnsEmptyString() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertEquals("", features.getFirstNumber());
    }

    @Test
    void TestGetFirstNumberIfThereIsNoNumberThenReturnsEmptyString() {
        Tokenizer tokenizer = new Tokenizer("Foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals("", features.getFirstNumber());
    }

    @Test
    void TestGetFirstNumberIfSingleNumberThenReturnsIt() {
        Tokenizer tokenizer = new Tokenizer("asdf 42 hjkl");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals("42", features.getFirstNumber());
    }

    @Test
    void TestGetFirstNumberIfMultipleNumbersThenReturnsTheFirstOne() {
        Tokenizer tokenizer = new Tokenizer("foo 11 22 bar 33");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals("11", features.getFirstNumber());
    }

    @Test
    void TestGetMostFrequentAcronymIfEmptyTokenListThenReturnsEmptyString() {
        Tokenizer tokenizer = new Tokenizer("");
        FeatureVector features = new FeatureVector(
            tokenizer.scanTokens(), title, dict
        );

        assertEquals("", features.getMostFrequentAcronym());
    }

    @Test
    void TestGetMostFrequentAcronymIfThereIsNoAcronymThenReturnsEmptyString() {
        Tokenizer tokenizer = new Tokenizer("foo bar baz");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        assertEquals("", features.getMostFrequentAcronym());
    }

    @Test
    void TestGetMostFrequentAcronymIfSingleAcronymThenReturnsItsTokenValue() {
        Tokenizer tokenizer = new Tokenizer("foo K.I.S.S. bar");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer.stemTokens(tokenizer.scanTokens())
            .get(1).getValue();

        assertEquals(expected, features.getMostFrequentAcronym());
    }

    @Test
    void TestGetMostFrequentAcronymIfSingleAcronymRepeatedThenReturnsItsTokenValue() {
        Tokenizer tokenizer = new Tokenizer("foo U.W.U. bar U.W.U.");
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer.stemTokens(tokenizer.scanTokens())
            .get(1).getValue();

        assertEquals(expected, features.getMostFrequentAcronym());
    }

    @Test
    void TestGetMostFrequentAcronymIfMultipleAcronymsWithDifferentFrequenciesThenReturnsTheMostFrequentOne() {
        Tokenizer tokenizer = new Tokenizer("""
            K.I.S.S. foo K.I.S.S. bar U.W.U. baz U.W.U. qux K.I.S.S. spam
            A.R.C.H. eggs U.W.U.
        """);
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer.stemTokens(tokenizer.scanTokens())
            .get(4).getValue();

        assertEquals(expected, features.getMostFrequentAcronym());
    }

    @Test
    void TestGetMostFrequentAcronymIfTwoAcronymsAreMostFrequentThenReturnsTheOneThatIsFirstAlphabetically() {
        Tokenizer tokenizer = new Tokenizer("""
            K.I.S.S. foo A.R.C.H. bar K.I.S.S. baz A.R.C.H.
        """);
        FeatureVector features = new FeatureVector(
            stemmer.stemTokens(tokenizer.scanTokens()), title, dict
        );

        String expected = stemmer.stemTokens(tokenizer.scanTokens())
            .get(2).getValue();

        assertEquals(expected, features.getMostFrequentAcronym());
    }
}
