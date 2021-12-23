/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries.parsers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class IntlinParserTest {

    static IntlinParser instance;
    static Connection con;
    static ArrayList<File> files;

    public IntlinParserTest() {
    }

    private static void setUpDB() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:testDict"+File.separator+"dbtest.db");
        Statement stm = con.createStatement();
        // Word table 
        stm.execute("CREATE TABLE IF NOT EXISTS Word("
                + "word_id INTEGER PRIMARY KEY,"
                + "word TEXT NOT NULL,"
                + "word_class TEXT NOT NULL,"
                + "gender text)");
        stm = con.createStatement();
        // Definition table 
        stm.execute("CREATE TABLE IF NOT EXISTS Definition("
                + "def_id INTEGER PRIMARY KEY,"
                + "def TEXT NOT NULL,"
                + "word_id INTEGER NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))");
        stm = con.createStatement();
        // Alternative table 
        stm.execute("CREATE TABLE IF NOT EXISTS Alternative("
                + "alt_id INTEGER PRIMARY KEY,"
                + "word_id INTEGER NOT NULL,"
                + "alt TEXT NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))"
                /*+ "PRIMARY KEY(word_id, alt))"*/);
        stm = con.createStatement();
        // Synonym table 
        stm.execute("CREATE TABLE IF NOT EXISTS Synonym("
                + "syn_id INTEGER PRIMARY KEY,"
                + "def_id INTEGER NOT NULL,"
                + "syn TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id))");
        stm = con.createStatement();
        // Antonym table 
        stm.execute("CREATE TABLE IF NOT EXISTS Antonym("
                + "ant_id INTEGER PRIMARY KEY,"
                + "def_id INTEGER NOT NULL,"
                + "ant TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id))");
        stm = con.createStatement();
        // Extra table 
        stm.execute("CREATE TABLE IF NOT EXISTS Extra("
                + "extra_id INTEGER PRIMARY KEY,"
                + "def_id INTEGER NOT NULL,"
                + "extra TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id))");
    }

    private static ArrayList<File> setUpFiles() throws IOException {
        File f1 = new File("testDict"+File.separator+"intlinTest"+File.separator+"intlinTest1.json");
        File f2 = new File("testDict"+File.separator+"intlinTest"+File.separator+"intlinTest2.json");
        files = new ArrayList<>();
        files.add(f1);
        files.add(f2);
        return files;
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
        setUpDB();
        ArrayList<File> files = setUpFiles();
        instance = new IntlinParser(con);
        instance.doParsing(files);
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
        con.close();
        File dbFile = new File("testDict"+File.separator+"dbtest.db");
        dbFile.delete();
    }

    /**
     * Test of doParsing method, of class IntlinParser.
     */
    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectAmount() {
        int expectedTotal = 10;
        int total = 0;
        try {
            total = verifyTotal();
        } catch (SQLException | UnsupportedOperationException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
        assertEquals(expectedTotal, total);
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectAlternativeForms() {
        boolean expected = true;
        ArrayList<String> expectedAlts = new ArrayList<>();
        expectedAlts.add("ahueonao, aweonado, aweonao (eye dialect)");
        expectedAlts.add("ahueonado (eye dialect, rare)");
        ArrayList<String> expectedAlts2 = new ArrayList<>();
        try {
            expected = expected && verifyAlternatives(expectedAlts, 10);
            expected = expected && verifyAlternatives(expectedAlts2, 1);
        } catch (SQLException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
        assertTrue(expected);
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectWordValues() {
        boolean expected = true;
        try {
            expected = expected && verifyCorrectValue(9, "gender", "m");
            expected = expected && verifyCorrectValue(3, "gender", "f");
            expected = expected && verifyCorrectValue(7, "gender", "f pl");
            expected = expected && verifyCorrectValue(8, "gender", null);
            expected = expected && verifyCorrectValue(4, "word", "abono");
            expected = expected && verifyCorrectValue(5, "word", "abono");
            expected = expected && verifyCorrectValue(4, "word_class", "Noun");
            expected = expected && verifyCorrectValue(5, "word_class", "Verb");
        } catch (SQLException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
        assertTrue(expected);
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectDefinitions() {
        boolean expected = true;
        ArrayList<String> expectedDefs = new ArrayList<>();
        expectedDefs.add("directly, firsthand");
        expectedDefs.add("outright");
        ArrayList<String> expectedDefs2 = new ArrayList<>();
        expectedDefs2.add("compost, fertilizer, manure");
        try {
            expected = expected && verifyCorrectDefs(expectedDefs, 2);
            // verifyCorrectDefs(expectedDefs2, 4) should return false, due to
            // missing definitions in expectedDefs2
            expected = expected && !verifyCorrectDefs(expectedDefs2, 4);
        } catch (SQLException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
        assertTrue(expected);
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectSynonyms() {
        boolean expected = true;
        ArrayList<String> expectedSyns = new ArrayList<>();
        expectedSyns.add("ahí");
        expectedSyns.add("allá");
        ArrayList<String> expectedSyns2 = new ArrayList<>();
        expectedSyns2.add("ogro");
        try {
            expected = expected && verifyCorrectAsset(expectedSyns,
                    "there (away from the speaker and the listener)", "Synonym");
            // verifyCorrectSyns(expectedSyns2, "") should return false, due to
            // not having synonyms
            expected = expected && !verifyCorrectAsset(expectedSyns2, "elf",
                    "Synonym");
        } catch (SQLException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
        assertTrue(expected);
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectAntonyms() {
        boolean expected = true;
        ArrayList<String> expectedAnts = new ArrayList<>();
        expectedAnts.add("aquí");
        expectedAnts.add("acá");
        ArrayList<String> expectedAnts2 = new ArrayList<>();
        expectedAnts2.add("humano");
        try {
            expected = expected && verifyCorrectAsset(expectedAnts,
                    "there (away from the speaker and the listener)", "Antonym");
            // verifyCorrectSyns(expectedSyns2, "") should return false, due to
            // not having antonym
            expected = expected && !verifyCorrectAsset(expectedAnts2, "elf",
                    "Antonym");
        } catch (SQLException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
        assertTrue(expected);
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectExtras() {
        boolean expected = true;
        ArrayList<String> expectedExt = new ArrayList<>();
        expectedExt.add("2002, Clara Inés Ríos Katto, Guía para el cultivo y aprovechamiento del botón de oro: Tithoni diversifolia (Hemsl.) Gray, Concenio Andrés Bello, page 22.");
        expectedExt.add("En menor medida se utiliza abono de vaca, vermicompost, mantillo, abono de caballo[,] restos de cultivos, restos de cocina.To a lesser extent, cow manure, vermicompost, humus, horse manure, crop residues, and kitchen scraps are used.");
        expectedExt.add("En campos de cultivos de arroz por inundación, los agricultores cosechan el botón de oro[. L]o incorporan al suelo como abono verde y mejorador de suelos.In rice paddies watered by flooding, farmers harvest buttercups. They incorporate it into the soil as a green fertilizer and soil improver.");
        expectedExt.add("2009, Alfredo Tolón Becerra &amp; Xavier B. Lastra Bravo (eds.), Actas del III Seminario Internacional de Cooperación y Desarrollo en Espacios Rurales Iberoamericanos, Editorial Universidad de  Almería, page 205.");
        ArrayList<String> expectedExt2 = new ArrayList<>();
        expectedExt2.add("2005, Tolkien, El Señor de los Anillos: el elfo come las manzanas");
        try {
            expected = expected && verifyCorrectAsset(expectedExt,
                    "compost, fertilizer, manure", "Extra");
            // verifyCorrectSyns(expectedSyns2, "") should return false, due to
            // not having synonyms
            expected = expected && !verifyCorrectAsset(expectedExt2, "elf",
                    "Extra");
        } catch (SQLException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("\nException thrown: " + ex.getMessage());
        }
        assertTrue(expected);
    }

    private static int verifyTotal() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet resSet = stmt.executeQuery("SELECT COUNT(*) AS total FROM Word");
        int total = resSet.getInt("total");
        return total;
    }

    private static boolean verifyAlternatives(ArrayList<String> expected, int wordId) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT alt.alt AS alternative "
                        + "FROM Word w INNER JOIN "
                        + "Alternative alt on alt.word_id = w.word_id "
                        + "where w.word_id = ?");
        stm.setInt(1, wordId);
        PreparedStatement stmSize = con
                .prepareStatement("SELECT COUNT(alt.alt) AS size "
                        + "FROM Word w INNER JOIN "
                        + "Alternative alt on alt.word_id = w.word_id "
                        + "where w.word_id = ?");
        stmSize.setInt(1, wordId);
        ResultSet resSet = stm.executeQuery();
        ResultSet resSetsize = stmSize.executeQuery();
        boolean result = true;
        if (resSetsize.getInt("size") == 0 && expected.isEmpty()) {
            result = true;
        } else if (expected.size() != resSetsize.getInt("size")) {
            result = false;
        } else {
            while (resSet.next()) {
                result = result && expected.contains(resSet.getString("alternative"));
            }
        }

        return result;
    }

    private static boolean verifyCorrectValue(int wordId, String column,
            String expected) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String
                .format("SELECT %s FROM Word w WHERE w.word_id = ?", column));
        stm.setInt(1, wordId);
        ResultSet resSet = stm.executeQuery();
        String colVal = resSet.getString(column);
        return (colVal == null ? expected == null : colVal.equals(expected));
    }

    private static boolean verifyCorrectDefs(ArrayList<String> expected, int wordId) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT d.def AS definition "
                        + "FROM Word w INNER JOIN "
                        + "Definition d on d.word_id = w.word_id "
                        + "where w.word_id = ?");
        stm.setInt(1, wordId);
        PreparedStatement stmSize = con
                .prepareStatement("SELECT COUNT(d.def) AS size "
                        + "FROM Word w INNER JOIN "
                        + "Definition d on d.word_id = w.word_id "
                        + "where w.word_id = ?");
        stmSize.setInt(1, wordId);
        ResultSet resSet = stm.executeQuery();
        ResultSet resSetsize = stmSize.executeQuery();
        boolean result = true;
        if (resSetsize.getInt("size") == 0 && expected.isEmpty()) {
            result = true;
        } else if (expected.size() != resSetsize.getInt("size")) {
            result = false;
        } else {
            while (resSet.next()) {
                result = result && expected.contains(resSet.getString("definition"));
            }
        }

        return result;
    }

    private boolean verifyCorrectAsset(ArrayList<String> expected, String def_text, String asset)
            throws SQLException {
        String field = asset.equals("Extra") ? "extra" 
                :  asset.equals("Antonym") ? "ant"  
                :  "syn";
        PreparedStatement stm = con
                .prepareStatement(String.format("SELECT s.%s AS %s "
                        + "FROM Definition d INNER JOIN "
                        + "%s s on d.def_id = s.def_id "
                        + "where d.def = ?", field ,asset, asset));
        stm.setString(1, def_text);
        PreparedStatement stmSize = con
                .prepareStatement(String.format("SELECT count(*) AS size "
                        + "FROM Definition d INNER JOIN "
                        + "%s s on d.def_id = s.def_id "
                        + "where d.def = ?", asset));
        stmSize.setString(1, def_text);
        ResultSet resSet = stm.executeQuery();
        ResultSet resSetsize = stmSize.executeQuery();
        boolean result = true;
        if (resSetsize.getInt("size") == 0 && expected.isEmpty()) {
            result = true;
        } else if (expected.size() != resSetsize.getInt("size")) {
            result = false;
        } else {
            while (resSet.next()) {
                result = result && expected.contains(resSet.getString(asset));
            }
        }

        return result;
    }
}
