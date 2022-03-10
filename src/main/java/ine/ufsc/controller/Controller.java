/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.controller;

import ine.ufsc.model.dictionaries.Dictionary;
import ine.ufsc.model.dictionaries.IntlinDictionary;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Gabriel
 */
public class Controller {

    private Controller() {
        loadedDictionaries = new HashMap<>();
        supportedlangsList = new HashSet<>();
        SupportedLanguage sups[] = SupportedLanguage.values();
        for (int i = 0; i < sups.length; i++) {
            supportedlangsList.add(sups[i]);
        }
    }

    public static Controller instance = new Controller();

    public static enum SupportedLanguage {
        PORTUGUESE_TO_ENGLISH_INTLIN,
    }

    private final Map<SupportedLanguage, Dictionary> loadedDictionaries;
    private final Set<SupportedLanguage> supportedlangsList;

    private SupportedLanguage selectedLanguage;

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

    public void selectLanguage(SupportedLanguage languageToLoad) throws ClassNotFoundException, SQLException, IOException {
        if (!loadedDictionaries.containsKey(languageToLoad)) {
            switch (languageToLoad) {
                case PORTUGUESE_TO_ENGLISH_INTLIN:
                    String path = "src"+File.separator+"main"+File.separator+"resources"+File.separator+"ine"+File.separator+"ufsc"+File.separator+"model"+File.separator+"dictionaries";
                    String name = "pt_en";
                    loadedDictionaries.put(SupportedLanguage.PORTUGUESE_TO_ENGLISH_INTLIN, new IntlinDictionary(name, path));
                    break;
                default:
            }
        }
        selectedLanguage = languageToLoad;
    }
}
