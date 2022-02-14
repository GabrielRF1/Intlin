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
import java.time.LocalDate;
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

    public boolean createDeck(String deckName, int parentDeckId) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO Deck(name, parentDeckId) "
                + "VALUES(?, ?)");
        stm.setString(1, deckName);
        stm.setInt(2, parentDeckId);
        boolean success = !stm.execute();
        if (success) {
            int id = con.prepareStatement("SELECT last_insert_rowid() AS id").executeQuery().getInt("ID");
            deckToId.put(deckName, id);
        }
        return success;
    }

    public boolean addToDeck(String deckName, Card card) throws SQLException {
        boolean res = true;
        int deckId = deckToId.get(deckName);
        PreparedStatement cardStm = con.prepareStatement("INSERT INTO Card"
                + "(deckId, reviewDate, isSuspended, ease, level) VALUES"
                + "(?, ?, ?, ?, ?)");
        cardStm.setInt(1, deckId);
        cardStm.setString(2, card.getNextReview().toString());
        cardStm.setInt(3, card.getState() == Card.CardState.active ? 1 : 0);
        cardStm.setDouble(4, card.getEase());
        cardStm.setString(5, card.getLevel().toString());

        res &= !cardStm.execute();

        int cardId = con.prepareStatement("SELECT last_insert_rowid() AS id").executeQuery().getInt("ID");
        card.setId(cardId);

        res &= addContents(card, true);
        res &= addContents(card, false);

        return res;
    }

    public boolean updateCard(Card card) throws SQLException {
        
        return false;
    }
    
    public HashSet<Card> getTodaysReviewByDeck(String deck) throws SQLException {
        HashSet<Card> todaysCards = new HashSet<>();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM Card WHERE "
                + "deckId=? AND reviewDate=? OR reviewDate IS NULL");
        stm.setInt(1, deckToId.get(deck));
        stm.setString(2, LocalDate.now().toString());
        ResultSet cardInfo = stm.executeQuery();
        while (cardInfo.next()) {
            Card.CardProficiency level = cardInfo.getString("level").equals("toLearn")
                    ? Card.CardProficiency.toLearn
                    : cardInfo.getString("level").equals("learning") ? Card.CardProficiency.learning
                    : cardInfo.getString("level").equals("comfortable") ? Card.CardProficiency.comfortable
                    : cardInfo.getString("level").equals("mastered") ? Card.CardProficiency.mastered
                    : Card.CardProficiency.acquired;
            CardContent front = retrieveCardsFace(cardInfo.getInt("cardId"), false);
            CardContent back = retrieveCardsFace(cardInfo.getInt("cardId"), true);
            
            Card card = new Card(front, back, cardInfo.getInt("cardId"),
                    cardInfo.getString("reviewDate"), cardInfo.getInt("ease"),
                    level, cardInfo.getInt("isSuspended") == 1);
            todaysCards.add(card);
        }
        return todaysCards;
    }

    protected boolean addContents(Card card, boolean isFrontFace) throws SQLException {
        boolean res = true;
        CardContent face = isFrontFace ? card.getFront() : card.getBack();
        for (int i = 0; i < face.size(); i++) {
            PreparedStatement faceStm = con.prepareStatement("INSERT INTO Content"
                    + "(content, contentType) VALUES"
                    + "(?, ?)");
            Content content = face.getContent(i);
            switch (content.getType()) {
                case text:
                    faceStm.setString(1, (String) content.getElement());
                    break;
                case image:
                case audio:
                    faceStm.setString(1, ((File) content.getElement()).getPath());
                    break;
            }
            faceStm.setString(2, content.getType().toString());
            res &= !faceStm.execute();
            res &= addContentFace(isFrontFace ? "FrontContent" : "BackContent", content.getPosition(), card.getId());
        }
        return res;
    }

    protected boolean addContentFace(String table, int placement, int cardId) throws SQLException {
        PreparedStatement faceContStm = con.prepareStatement(String.format("INSERT INTO %s"
                + "(cardId, contentId, placement) VALUES"
                + "(?, ?, ?)", table));
        int contentId = con.prepareStatement("SELECT last_insert_rowid() AS id").executeQuery().getInt("ID");
        faceContStm.setInt(1, cardId);
        faceContStm.setInt(2, contentId);
        faceContStm.setInt(3, placement);

        return !faceContStm.execute();
    }

    protected CardContent retrieveCardsFace(int cardId, boolean isBack) throws SQLException {
        String table = isBack ? "BackContent" : "FrontContent";
        PreparedStatement contIdStm = con.prepareStatement(String.format("SELECT "
                + "contentId, placement FROM %s WHERE cardId=?", table));
        contIdStm.setInt(1, cardId);
        ResultSet contIdRs = contIdStm.executeQuery();
        HashMap<Integer, Integer> contenIds = new HashMap<>();
        while (contIdRs.next()) {
            contenIds.put(contIdRs.getInt("contentId"), contIdRs.getInt("placement"));
        }

        CardContent face = new CardContent();
        for (Integer id : contenIds.keySet()) {
            PreparedStatement contStm = con.prepareStatement("SELECT content, contentType "
                    + "FROM Content WHERE contentId=?");
            contStm.setInt(1, id);
            ResultSet contRs = contStm.executeQuery();
            while (contRs.next()) {
                Content.Type type = contRs.getString("contentType").equals("text") ? Content.Type.text
                        : contRs.getString("contentType").equals("audio") ? Content.Type.audio
                        : Content.Type.image;
                Content content = new Content(contenIds.get(id), id,
                        contRs.getString("content"), type);

                face.addContent(content);
            }
        }
        return face;
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
                + "	\"deckId\"	INTEGER NOT NULL,\n"
                + "	\"reviewDate\"	TEXT,\n"
                + "	\"isSuspended\"	INTEGER NOT NULL,\n"
                + "	\"ease\"	REAL NOT NULL,\n"
                + "	\"level\"	TEXT NOT NULL,\n"
                + "	FOREIGN KEY(\"deckId\") REFERENCES \"Deck\"(\"deckId\"),\n"
                + "	PRIMARY KEY(\"cardId\" AUTOINCREMENT)\n"
                + ")");
        stm = con.createStatement();
        stm.execute("CREATE TABLE \"Content\" (\n"
                + "	\"contentId\"	INTEGER,\n"
                + "	\"content\"	TEXT NOT NULL,\n"
                + "	\"contentType\"	TEXT NOT NULL,\n"
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
