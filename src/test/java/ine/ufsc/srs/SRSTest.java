/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class SRSTest {

    static SRS testIntance;
    static String dbName = "TestSpanishSRS";
    static String deckName = "Spanish";

    public SRSTest() {
    }

    @BeforeAll
    public static void setUpClass() throws ClassNotFoundException, SQLException {
        testIntance = new SRS(deckName, dbName);
    }

    @AfterAll
    public static void tearDownClass() throws SQLException {
        testIntance.closeConnection();
        File dbFile = new File("srs" + File.separator + dbName + ".db");
        dbFile.delete();
    }

    /**
     * Test of createDeck method, of class SRS.
     */
    @Test
    public void testCreateDeck_String() {
        System.out.println("createDeck");
        String deckName = SRSTest.deckName;
        SRS instance = testIntance;
        boolean expResult = true;
        boolean result = instance.createDeck(deckName);
        assertEquals(expResult, result);
    }

    /**
     * Test of createDeck method, of class SRS.
     */
    @Test
    public void testCreateDeck_String_int() {
        System.out.println("createDeck");
        String deckName = SRSTest.deckName;
        int parentDeckId = 0;
        SRS instance = testIntance;
        boolean expResult = true;
        boolean result = instance.createDeck(deckName, parentDeckId);
        assertEquals(expResult, result);
    }

    /**
     * Test of addToDeck method, of class SRS.
     */
    @Test
    public void testAddToDeck() {
        System.out.println("addToDeck");
        int deckId = 0;
        CardContent front = new CardContent();
        CardContent back = new CardContent();
        front.addText("mantequilla");
        back.addText("butter");
        Card card = new Card(front, back);
        SRS instance = testIntance;
        boolean expResult = true;
        boolean result = instance.addToDeck(deckId, card);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTodaysReview method, of class SRS.
     */
    @Test
    public void testGetTodaysReview() {
        System.out.println("getTodaysReview");
        SRS instance = testIntance;

        HashSet<Card> result = instance.getTodaysReview();
        HashSet<Integer> ids = new HashSet<>();

        for (Card card : result) {
            if (!card.getNextReview().equals(LocalDate.now())) {
                fail("Only today's reviews are supposed to be returned");
            }
            ids.add(card.getId());
        }

        boolean res = true;
        String today = LocalDate.now().toString();
        try {
            PreparedStatement stm = testIntance.con.prepareStatement("SELECT cardId FROM Card WHERE reviewDate = ? or reviewDate = NULL");
            stm.setString(1, today);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                if (!ids.contains(rs.getInt("cardId"))) {
                    res = false;
                }

            }
        } catch (SQLException ex) {
            fail("SQL error");
        }

        assertTrue(res, res == false ? "There are missing cards in the result set" : null);
    }

}
