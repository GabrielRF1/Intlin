/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.controller;

import ine.ufsc.utils.Message;
import ine.ufsc.utils.Observer;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class ConfigController implements Observer {

    public static ConfigController instance = new ConfigController();

    private String lastUsage;
    private Controller.SupportedLanguage lastStudiedLanguage;
    final private Properties properties;

    private ConfigController() {
        properties = new Properties();
        Controller.instance.attach(this);
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

    public void setLastUsage(LocalDate lastUsage) throws IOException {
        String lastUsageString = lastUsage.toString();
        this.lastUsage = lastUsage.toString();
        properties.setProperty("LastOpened", lastUsageString);
        properties.storeToXML(new FileOutputStream("config/config.xml"), "");
    }

    public void setLastStudiedLanguage(Controller.SupportedLanguage lastStudiedLanguage) throws IOException {
        this.lastStudiedLanguage = lastStudiedLanguage;
        properties.setProperty("LastStudiedLanguage", Controller.instance.supportedLanguageToString(lastStudiedLanguage));
        properties.storeToXML(new FileOutputStream("config/config.xml"), "");
    }

    @Override
    public void update(Message message) {
        var types = message.getTypes();
        for (Message.MessageType type : types) {
            switch (type) {
                case UPDATE_LAST_DATE: {
                    try {
                        setLastUsage(LocalDate.now());
                    } catch (IOException ex) {
                        // tratar
                        Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                case SRS_HAS_BEEN_LOADED: {
                    try {
                        setLastStudiedLanguage(Controller.instance.getSelectedLanguage());
                    } catch (IOException ex) {
                        // tratar 
                        Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            }
        }
    }

}
