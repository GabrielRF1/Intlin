/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

/**
 *
 * @author Gabriel
 */
public class ConfigController {

    public static ConfigController instance = new ConfigController();

    private String lastUsage;
    private Controller.SupportedLanguage lastStudiedLanguage;
    final private Properties properties;

    private ConfigController() {
        properties = new Properties();
    }

    public void bootUpConfigurations() throws IOException {
        properties.loadFromXML(new FileInputStream("config/config.xml"));
        String lastOpened = properties.getProperty("LastOpened");
        if (lastOpened == null) {
            String now = LocalDate.now().toString();
            properties.setProperty("LastOpened", now);
            lastOpened = now;
        }
        String lastLanguage = properties.getProperty("LastStudiedLanguage");
        if (lastLanguage == null) {
            properties.setProperty("LastStudiedLanguage", "");
            lastLanguage = "";
        }
        lastUsage = lastOpened;
        lastStudiedLanguage = Controller.instance.StringLanguageToEnum(lastLanguage);
        properties.storeToXML(new FileOutputStream("config/config.xml"), "");
    }

    public String getLastUsage() {
        return lastUsage;
    }

    public Controller.SupportedLanguage getLastStudiedLanguage() {
        return lastStudiedLanguage;
    }

    
}
