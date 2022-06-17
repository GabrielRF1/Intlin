/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.controller;

import globalExceptions.SRSNotLoadedException;
import ine.ufsc.model.dictionaries.Dictionary;
import ine.ufsc.model.dictionaries.IntlinDictionary;
import ine.ufsc.model.subtitle.BadlyFomattedSubtitleFileException;
import ine.ufsc.model.subtitle.Subtitle;
import ine.ufsc.model.subtitle.parser.SrtParser;
import ine.ufsc.srs.Card;
import ine.ufsc.srs.SRS;
import ine.ufsc.utils.Message;
import ine.ufsc.utils.Observable;
import ine.ufsc.utils.Observer;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class Controller implements Observable {

    public static Controller instance = new Controller();

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyUpdate(Message message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public static enum SupportedLanguage {
        PORTUGUESE_TO_ENGLISH_INTLIN,
    }

    private final Map<SupportedLanguage, Dictionary> loadedDictionaries;
    private final Set<SupportedLanguage> supportedlangsList;

    private final Map<Integer, String> linesToSave;

    private SupportedLanguage selectedLanguage;

    private SRS loadedSRS;

    private final Map<String, HashSet<Card>> reviews;

    private Controller() {
        loadedDictionaries = new HashMap<>();
        supportedlangsList = new HashSet<>();
        linesToSave = new TreeMap<>();
        reviews = new HashMap<>();
        SupportedLanguage suports[] = SupportedLanguage.values();
        for (SupportedLanguage suport : suports) {
            supportedlangsList.add(suport);
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

    public void checkAndLoadLastStudiedLanguage() {
        SupportedLanguage lastStudiedLanguage = ConfigController.instance.getLastStudiedLanguage();
        if (lastStudiedLanguage != null) {
            try {
                selectLanguage(lastStudiedLanguage);
            } catch (ClassNotFoundException | SQLException | IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        Message.MessageType[] types = {Message.MessageType.UPDATE_SRS, Message.MessageType.SRS_HAS_BEEN_LOADED};
        Message message = new Message(types);
        notifyUpdate(message);
    }

    public Set<String> getDecks() {
        return loadedSRS.getDeckNames();
    }

    public HashSet<Card> getDecksReview(String deckName) throws SQLException {
        String lastOpenedDate = ConfigController.instance.getLastUsage();
        HashSet<Card> forToday = loadedSRS.getTodaysReviewByDeck(deckName);
        if (!lastOpenedDate.equals(LocalDate.now().toString())) {
            forToday.addAll(loadedSRS.getReviewsFromPeriodByDeckName(LocalDate.parse(lastOpenedDate), LocalDate.now(), deckName));
        }
        reviews.put(deckName, forToday);
        return reviews.get(deckName);
    }

    public void tryAndCreateDeck(String deckName) throws SQLException, SRSNotLoadedException {
        if (selectedLanguage == null) {
            throw new SRSNotLoadedException("You must choose a language before creating decks and cards");
        }
        if (!reviews.keySet().contains(deckName)) {
            loadedSRS.createDeck(deckName);
            notifyUpdate(new Message(Message.MessageType.UPDATE_SRS));
        }
    }

    public LocalDate answerCard(Card card, Card.Difficulty difficulty) {
        LocalDate nextReview = card.calcNextReview(difficulty);
        notifyUpdate(new Message(Message.MessageType.UPDATE_SRS));
        return nextReview;
    }

    public void addCardToDeck(String deckName, Card card) throws SQLException {
        loadedSRS.addToDeck(deckName, card);
        notifyUpdate(new Message(Message.MessageType.UPDATE_SRS));
    }

    public void updateCards(Card card) throws SQLException {
        loadedSRS.updateCard(card);
        notifyUpdate(new Message(Message.MessageType.UPDATE_SRS));
    }

    public void setAsReviewed(String deckName, Card card) {
        reviews.get(deckName).remove(card);
        ArrayList<Message.MessageType> types = new ArrayList<>();
        types.add(Message.MessageType.UPDATE_SRS);
        if (reviews.values().stream().allMatch((cardsFromADeck) -> {
            return cardsFromADeck.isEmpty();
        })) {
            types.add(Message.MessageType.UPDATE_LAST_DATE);
        }
        notifyUpdate(new Message(types.toArray(new Message.MessageType[types.size()])));
    }

    public LinkedList<Subtitle> parseSubs(File srtFile) throws IOException, BadlyFomattedSubtitleFileException {
        SrtParser parser = new SrtParser();
        return parser.parse(srtFile);
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
            info.word = wordRs.getString("word");
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

    public TreeMap<Integer, String> getLinesToSave() {
        return (TreeMap<Integer, String>) linesToSave;
    }

    public void saveLine(String line, int id) {
        linesToSave.put(id, line);
    }

    public void removeLineIfSaved(int id) {
        linesToSave.remove(id);
    }
}
