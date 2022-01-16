/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries.parsers;

import static ine.ufsc.model.dictionaries.parsers.CCCedictParserTest.con;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class JMDictParserTest {

    static JMDictParser instance;
    static Connection con;
    static ArrayList<File> files = new ArrayList<>();

    public JMDictParserTest() {
    }

    private static void setUpDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:testDict" + File.separator + "dbJMDictTest.db");
        Statement stm = con.createStatement();
        //create tables
        stm.execute("CREATE TABLE IF NOT EXISTS Word("
                + "word_id INTEGER PRIMARY KEY)");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS RElement("
                + "r_id INTEGER PRIMARY KEY,"
                + "priority INTEGER,"
                + "reading STRING NOT NULL,"
                + "word_id INTEGER NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS KElement("
                + "k_id INTEGER PRIMARY KEY,"
                + "priority INTEGER,"
                + "kanji STRING NOT NULL,"
                + "word_id INTEGER NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS Definition("
                + "def_id INTEGER PRIMARY KEY,"
                + "type STRING,"
                + "word_id INTEGER NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS PartOfSpeech("
                + "pos_id INTEGER PRIMARY KEY,"
                + "pos STRING NOT NULL)");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS Gloss("
                + "gloss_id INTEGER PRIMARY KEY,"
                + "gloss STRING NOT NULL,"
                + "def_id INTEGER NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id))");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS Dialect("
                + "dial_id INTEGER PRIMARY KEY,"
                + "dial STRING NOT NULL)");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS Field("
                + "field_id INTEGER PRIMARY KEY,"
                + "field STRING NOT NULL)");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS Misc("
                + "misc_id INTEGER PRIMARY KEY,"
                + "misc STRING NOT NULL)");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS DefPos("
                + "def_id INTEGER NOT NULL,"
                + "pos_id INTEGER NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                + "FOREIGN KEY(pos_id) REFERENCES PartOfSpeech(pos_id),"
                + "PRIMARY KEY(def_id, pos_id))");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS DefMisc("
                + "def_id INTEGER NOT NULL,"
                + "misc_id INTEGER NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                + "FOREIGN KEY(misc_id) REFERENCES Misc(misc_id),"
                + "PRIMARY KEY(def_id, misc_id))");
        stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS DefDial("
                + "def_id INTEGER NOT NULL,"
                + "dial_id INTEGER NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                + "FOREIGN KEY(dial_id) REFERENCES Dialect(dial_id),"
                + "PRIMARY KEY(def_id, dial_id))");
    }

    private static File setUpFile() throws IOException {
        File resFile = new File("testDict" + File.separator + "dbJMDictTest" + File.separator + "JMDictTest.xml");
        return resFile;
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws ClassNotFoundException, SQLException, IOException, ParserConfigurationException {
        setUpDB();
        File file = setUpFile();
        files.add(file);
        instance = new JMDictParser(con);
        instance.doParsing(files);
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws IOException, SQLException {
        con.close();
        File dbFile = new File("testDict" + File.separator + "dbJMDictTest.db");
        dbFile.delete();
    }

    /**
     * Test of doParsing method, of class JMDictParser.
     */
    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectAmount() {
        int expectedAmount = 9;
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) AS total FROM Word");
            int actualAmount = rs.getInt("total");
            assertEquals(expectedAmount, actualAmount);
        } catch (SQLException | UnsupportedOperationException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingWieldsCorrectWord() throws Exception {
        System.out.println("doParsing");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
