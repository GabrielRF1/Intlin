/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Gabriel
 */
public class SRS {

    private String srsDBName;
    private String language;
    private HashMap<String, Integer> deckToId;
    protected Connection con;

    public SRS(String srsDBname, String language) throws ClassNotFoundException, SQLException {
        this.language = language;
        this.srsDBName = srsDBname;
        connect();
        getDecks();
    }

    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:srs" + File.separator + srsDBName + ".db");
    }

    public void closeConnection() throws SQLException {
        con.close();
    }

    private void getDecks() {

    }

    public boolean createDeck(String deckName) {
        return false;
    }

    public boolean createDeck(String deckName, int parentDeckId) {
        return false;
    }

    public boolean addToDeck(int deckId, Card card) {
        return false;
    }

    public HashSet<Card> getTodaysReview() {
        return null;
    }

}
