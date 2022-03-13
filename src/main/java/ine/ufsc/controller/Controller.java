/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.controller;

import ine.ufsc.model.dictionaries.Dictionary;
import ine.ufsc.model.dictionaries.IntlinDictionary;
import ine.ufsc.srs.Card;
import ine.ufsc.srs.SRS;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Gabriel
 */
public class Controller {

    public static Controller instance = new Controller();

    public static enum SupportedLanguage {
        PORTUGUESE_TO_ENGLISH_INTLIN,
    }

    private final Map<SupportedLanguage, Dictionary> loadedDictionaries;
    private final Set<SupportedLanguage> supportedlangsList;

    private SupportedLanguage selectedLanguage;

    private SRS loadedSRS;

    private Map<String, HashSet<Card>> reviews;

    private Controller() {
        loadedDictionaries = new HashMap<>();
        supportedlangsList = new HashSet<>();
        reviews = new HashMap<>();
        SupportedLanguage suports[] = SupportedLanguage.values();
        for (int i = 0; i < suports.length; i++) {
            supportedlangsList.add(suports[i]);
        }
    }

    public Set<SupportedLanguage> getSupportedlangsList() {
        return supportedlangsList;
    }

    public SupportedLanguage getSelectedLanguage() {
        return selectedLanguage;
    }

    public String supportedLanguageToString(SupportedLanguage lang) {
        switch (lang) {
            case PORTUGUESE_TO_ENGLISH_INTLIN:
                return "Portuguese to English";
            default:
                return "invalid";
        }
    }

    public SupportedLanguage StringLanguageToEnum(String lang) {
        switch (lang) {
            case "Portuguese to English":
                return SupportedLanguage.PORTUGUESE_TO_ENGLISH_INTLIN;
            default:
                return null;
        }
    }

    //public boolean isJmdict(SupportedLanguage lang);
    //public boolean isCedict(SupportedLanguage lang);
    //any other that gets supported eventually
    public boolean isIntlin(SupportedLanguage lang) {
        switch (lang) {
            case PORTUGUESE_TO_ENGLISH_INTLIN:
                return true;
            default:
                return false;
        }
    }

    public void selectLanguage(SupportedLanguage languageToLoad) throws ClassNotFoundException, SQLException, IOException {
        if (!loadedDictionaries.containsKey(languageToLoad)) {
            switch (languageToLoad) {
                case PORTUGUESE_TO_ENGLISH_INTLIN:
                    String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "ine" + File.separator + "ufsc" + File.separator + "model" + File.separator + "dictionaries";
                    String name = "pt_en";
                    loadedDictionaries.put(SupportedLanguage.PORTUGUESE_TO_ENGLISH_INTLIN, new IntlinDictionary(name, path));
                    break;
                default:
            }
        }
        selectedLanguage = languageToLoad;
        loadedSRS = new SRS(supportedLanguageToString(languageToLoad));
    }

    public Set<String> getDecks() {
        return loadedSRS.getDeckNames();
    }

    public HashSet<Card> getDecksReview(String deckName) throws SQLException {
        reviews.put(deckName, loadedSRS.getTodaysReviewByDeck(deckName));
        return reviews.get(deckName);
    }

    public void refetchReview(String deckName) throws SQLException {
        reviews.put(deckName, loadedSRS.getTodaysReviewByDeck(deckName));
    }

    public void tryAndCreateDeck(String deckName) throws SQLException {
        if (!reviews.keySet().contains(deckName)) {
            loadedSRS.createDeck(deckName);
        }
    }

    public LocalDate answerCard(Card card, Card.Difficulty difficulty) {
        return card.calcNextReview(difficulty);
    }

    public void addCardToDeck(String deckName, Card card) throws SQLException {
        loadedSRS.addToDeck(deckName, card);
    }

    public void updateCards(Card card) throws SQLException {
        loadedSRS.updateCard(card);
    }

    public void setAsReviewed(String deckName, Card card) {
        reviews.get(deckName).remove(card);
    }

    public ArrayList<IntlinDictionary.IntlinInfo> searchIntlinWord(SupportedLanguage dictKey, String word) throws SQLException {
        IntlinDictionary dict = (IntlinDictionary) loadedDictionaries.get(dictKey);
        ArrayList<IntlinDictionary.IntlinInfo> result = new ArrayList<>();

        ResultSet wordRs = dict.searchDefinition(word);
        if (wordRs.isClosed()) {
            return null;
        }
        while (wordRs.next()) {
            IntlinDictionary.IntlinInfo info = new IntlinDictionary.IntlinInfo();

            info.wordId = wordRs.getInt("word_id");
            info.def = wordRs.getString("definition");
            ResultSet altRS = dict.searchAlternativeForm(info.def);
            while (altRS.next()) {
                info.alts.add(altRS.getString("alternative"));
            }
            ResultSet synRS = dict.searchSynonyms(info.def);
            while (synRS.next()) {
                info.syns.add(synRS.getString("syn"));
            }
            ResultSet antRS = dict.searchAntonyms(info.def);
            while (antRS.next()) {
                info.ants.add(antRS.getString("ant"));
            }
            ResultSet extraRS = dict.searchExtras(info.def);
            while (extraRS.next()) {
                info.extras.add(extraRS.getString("extra"));
            }
            info.gender = wordRs.getString("gender");
            info.wordClass = wordRs.getString("word_class");

            result.add(info);
        }

        return result;
    }
}
