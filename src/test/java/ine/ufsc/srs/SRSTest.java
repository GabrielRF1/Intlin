/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    static Path dbPath;
    static Path dbBackupPath;

    public SRSTest() {
    }

    @BeforeAll
    public static void setUpClass() throws ClassNotFoundException, SQLException, IOException {
        testIntance = new SRS(dbName);
        dbPath = new File("srs" + File.separator + "TestSpanishSRS.db").toPath();
        dbBackupPath = new File("srs" + File.separator + "TestSpanishSRS(1).db").toPath();

        Files.copy(dbPath, dbBackupPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterAll
    public static void tearDownClass() throws SQLException, IOException {
        testIntance.closeConnection();
        Files.delete(dbPath);
        dbBackupPath.toFile().renameTo(new File("srs" + File.separator + "TestSpanishSRS.db"));
    }

    /**
     * Test of createDeck method, of class SRS.
     */
    @Test
    public void testCreateDeck_String() {
        try {
            System.out.println("createDeck");
            String deckName = "Sentences";
            SRS instance = testIntance;
            boolean expResult = true;
            boolean result = instance.createDeck(deckName);
            assertEquals(expResult, result);
        } catch (SQLException ex) {
            fail("Could not create deck. exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test of createDeck method, of class SRS.
     */
    @Test
    public void testCreateDeck_String_int() {
        try {
            System.out.println("createDeck");
            String deckName = "verbs";
            int parentDeckId = 1;
            SRS instance = testIntance;
            boolean expResult = true;
            boolean result = instance.createDeck(deckName, parentDeckId);
            assertEquals(expResult, result);
        } catch (SQLException ex) {
            fail("Could not create deck. exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test of addToDeck method, of class SRS.
     */
    @Test
    public void testAddToDeck() {
        try {
            System.out.println("addToDeck");
            String deckName = "Grammar";
            CardContent front = new CardContent();
            CardContent back = new CardContent();
            front.addText("mantequilla");
            back.addText("butter");
            Card card = new Card(front, back);
            SRS instance = testIntance;
            boolean expResult = true;
            boolean result = instance.addToDeck(deckName, card);

            Statement stmCont = instance.con.createStatement();
            ResultSet rsCont = stmCont.executeQuery("SELECT content, count(*) as total FROM Content WHERE "
                    + "content = \'mantequilla\' OR content = \'butter\'");
            if (rsCont.isClosed() || rsCont.getInt("total") != 2) {
                fail("card not properly inserted");
            }

            Statement stmFront = instance.con.createStatement();
            ResultSet rsFront = stmFront.executeQuery("SELECT * FROM Content c INNER JOIN"
                    + " FrontContent fc ON c.contentId = fc.contentId INNER JOIN "
                    + " Card cd ON cd.cardId = fc.CardId WHERE "
                    + "c.content = \'mantequilla\'");
            if (rsFront.isClosed()) {
                fail("card front not properly inserted");
            }
            Statement stmBack = instance.con.createStatement();
            ResultSet rsBack = stmBack.executeQuery("SELECT * FROM Content c INNER JOIN"
                    + " BackContent bc ON c.contentId = bc.contentId INNER JOIN "
                    + " Card cd ON cd.cardId = bc.CardId WHERE "
                    + "c.content = \'butter\'");
            if (rsBack.isClosed()) {
                fail("card back not properly inserted");
            }

            assertEquals(expResult, result);
        } catch (SQLException ex) {
            fail("could not add card to deck. exception: " + ex.getMessage());
        }
    }

    /**
     * Test of getTodaysReviewByDeck method, of class SRS.
     */
    @Test
    public void testGetTodaysReviewByDeck() {
        try {
            System.out.println("getTodaysReview");
            SRS instance = testIntance;

            HashSet<Card> result = instance.getTodaysReviewByDeck("Grammar");
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
        } catch (SQLException ex) {
            fail("exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test of updateCard method, of class SRS.
     */
    @Test
    public void testUpdateCard() {
        try {
            System.out.println("updateCard");
            SRS instance = testIntance;
            Card card = new Card(new CardContent(), new CardContent());
            instance.addToDeck("Grammar", card);
            card.calcNextReview(Card.Difficulty.easy);
            boolean result = instance.updateCard(card);
            boolean expResult = true;
            PreparedStatement stm = instance.con.prepareStatement("SELECT ease, "
                    + "nextReview, level FROM Card where cardId=?");
            stm.setInt(0, card.getId());

            ResultSet res = stm.executeQuery();
            double ease = res.getDouble("ease");
            String nextReview = res.getString("nextReview");
            String level = res.getString("level");

            result &= (ease == 0.25)
                    && nextReview.equals(LocalDate.now().plusDays(1).toString())
                    && level.equals("comfortable");

            assertEquals(expResult, result);
        } catch (SQLException ex) {
            fail("Could not create deck. exception thrown: " + ex.getMessage());
        }
    }
}
