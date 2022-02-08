/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Gabriel
 */
public class SRS {

    private final String language;
    private final HashMap<String, Integer> deckToId;
    protected Connection con;

    public SRS(String language) throws ClassNotFoundException, SQLException {
        this.language = language;
        deckToId = new HashMap<>();
        connect();
        if (!bdExists()) {
            build();
        }
        getDecks();
    }

    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:srs" + File.separator + language + ".db");
    }

    public String getLanguage() {
        return language;
    }

    public void closeConnection() throws SQLException {
        con.close();
    }

    private void getDecks() throws SQLException {
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT deckId, name FROM Deck");
        if (!rs.isClosed()) {
            while (rs.next()) {
                deckToId.put(rs.getString("name"), rs.getInt("deckId"));
            }
        }
    }

    public boolean createDeck(String deckName) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO Deck(name) "
                + "VALUES(?)");
        stm.setString(1, deckName);
        boolean success = !stm.execute();
        if (success) {
            int id = con.prepareStatement("SELECT last_insert_rowid() AS id").executeQuery().getInt("ID");
            deckToId.put(deckName, id);
        }
        return success;
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

    private boolean bdExists() {
        File file = new File("srs/" + File.separator + language + ".db");
        return file.length() > 0;
    }

    private void build() throws SQLException {
        Statement stm = con.createStatement();
        stm.execute("CREATE TABLE \"Deck\" (\n"
                + "	\"deckId\"	INTEGER,\n"
                + "	\"name\"	TEXT NOT NULL,\n"
                + "	\"parentDeckId\"	INTEGER,\n"
                + "	FOREIGN KEY(\"parentDeckId\") REFERENCES \"Deck\"(\"deckId\"),\n"
                + "	PRIMARY KEY(\"deckId\" AUTOINCREMENT)\n"
                + ")");
        stm = con.createStatement();
        stm.execute("CREATE TABLE \"Card\" (\n"
                + "	\"cardId\"	INTEGER,\n"
                + "	\"deckId\"	INTEGER,\n"
                + "	\"reviewDate\"	TEXT,\n"
                + "	\"isSuspended\"	INTEGER NOT NULL,\n"
                + "	PRIMARY KEY(\"cardId\" AUTOINCREMENT),\n"
                + "	FOREIGN KEY(\"deckId\") REFERENCES \"Deck\"(\"deckId\")\n"
                + ")");
        stm = con.createStatement();
        stm.execute("CREATE TABLE \"Content\" (\n"
                + "	\"contentId\"	INTEGER,\n"
                + "	\"content\"	TEXT NOT NULL,\n"
                + "	\"contentType\"	INTEGER NOT NULL,\n"
                + "	PRIMARY KEY(\"contentId\" AUTOINCREMENT)\n"
                + ")");
        stm = con.createStatement();
        stm.execute("CREATE TABLE \"FrontContent\" (\n"
                + "	\"cardId\"	INTEGER,\n"
                + "	\"contentId\"	INTEGER,\n"
                + "	\"placement\"	INTEGER NOT NULL,\n"
                + "	FOREIGN KEY(\"cardId\") REFERENCES \"Deck\"(\"deckId\"),\n"
                + "	FOREIGN KEY(\"contentId\") REFERENCES \"Content\"(\"contentId\"),\n"
                + "	PRIMARY KEY(\"cardId\",\"contentId\")\n"
                + ")");
        stm = con.createStatement();
        stm.execute("CREATE TABLE \"BackContent\" (\n"
                + "	\"cardId\"	INTEGER,\n"
                + "	\"contentId\"	INTEGER,\n"
                + "	\"placement\"	INTEGER NOT NULL,\n"
                + "	FOREIGN KEY(\"cardId\") REFERENCES \"Deck\"(\"deckId\"),\n"
                + "	PRIMARY KEY(\"cardId\",\"contentId\")\n"
                + ")");
    }
}
