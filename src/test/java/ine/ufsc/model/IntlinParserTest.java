/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model;

import java.io.File;
import java.io.FileWriter;
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

    static IntlinParser instance = new IntlinParser();
    static Connection con;
    static ArrayList<File> files;
    static String json1 = "["
            + "{\"word\": \"estampido\", \"gender\": \"m\", \"defs\": [{\"_def\": \"crack, bang (noise)\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"directamente\", \"defs\": [{\"_def\": \"directly, firsthand\"}, {\"_def\": \"outright\"}], \"word_class\": \"Adverb\"},\n"
            + "{\"word\": \"crisálida\", \"gender\": \"f\", \"defs\": [{\"_def\": \"chrysalis\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"barrio bajo\", \"gender\": \"m\", \"defs\": [{\"_def\": \"slum (dilapidated neighborhood)\", \"_synonyms\": [\"pocilga\"]}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"amanerado\", \"defs\": [{\"_def\": \"mannered\"}, {\"_def\": \"effeminate, camp\", \"_synonyms\": [\"amujerado\", \"afeminado\"]}], \"word_class\": \"Adjective\"},\n"
            + "{\"word\": \"adorador\", \"gender\": \"m\", \"defs\": [{\"_def\": \"admirer, adorer, worshiper\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"altruista\", \"defs\": [{\"_def\": \"altruistic\"}], \"word_class\": \"Adjective\"},\n"
            + "{\"word\": \"altruista\", \"gender\": \"m or f\", \"defs\": [{\"_def\": \"altruist\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"apasionadamente\", \"defs\": [{\"_def\": \"passionately\"}], \"word_class\": \"Adverb\"},\n"
            + "{\"word\": \"altitud\", \"gender\": \"f\", \"defs\": [{\"_def\": \"height\", \"_synonyms\": [\"altura\"]}, {\"_def\": \"altitude\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"abono\", \"gender\": \"m\", \"defs\": [{\"_def\": \"compost, fertilizer, manure\", \"_synonyms\": [\"fertilizante\"], \"extras\": [\"2002, Clara Inés Ríos Katto, Guía para el cultivo y aprovechamiento del botón de oro: Tithoni diversifolia (Hemsl.) Gray, Concenio Andrés Bello, page 22.\", \"En menor medida se utiliza abono de vaca, vermicompost, mantillo, abono de caballo[,] restos de cultivos, restos de cocina.To a lesser extent, cow manure, vermicompost, humus, horse manure, crop residues, and kitchen scraps are used.\", \"En campos de cultivos de arroz por inundación, los agricultores cosechan el botón de oro[. L]o incorporan al suelo como abono verde y mejorador de suelos.In rice paddies watered by flooding, farmers harvest buttercups. They incorporate it into the soil as a green fertilizer and soil improver.\", \"2009, Alfredo Tolón Becerra &amp; Xavier B. Lastra Bravo (eds.), Actas del III Seminario Internacional de Cooperación y Desarrollo en Espacios Rurales Iberoamericanos, Editorial Universidad de  Almería, page 205.\"]}, {\"_def\": \"subscription; season ticket\", \"extras\": [\"2006 December 6,  “La emisora británica TV Channel 4 permitirá descargar sus programas a través de Internet”, in  La Vanguardia:BT Vision, como se llama el nuevo servicio, ofrece también cuarenta canales de TV gratuitos sin que el cliente tenga que pagar abono mensual alguno.BT Vision, as the new service is called, also offers forty free TV channels without the customer having to pay any monthly subscription.\"]}, {\"_def\": \"payment, installment\", \"_synonyms\": [\"pago\"], \"extras\": [\"1982, Brígida von Mentz, Los Pioneros del imperialismo alemán en México, CIESAS, page 490\", \"Uhink cumple con sus obligaciones, cancelándose finalmente la escritura por entero en 1875, catorce años más tarde de la última fecha estipulada originalmente para el último abono.Uhink fulfills his obligations, finally paying himself off completely in writing in 1875, fourteen years after the originally stipulated final date for the last installment.\"]}, {\"_def\": \"guarantee, security\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"abono\", \"defs\": [{\"_def\": \"First-person singular   (yo)  present indicative  form of abonar.\"}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"ángel\", \"gender\": \"m\", \"alt\": [\"ángelo (obsolete)\"], \"defs\": [{\"_def\": \"angel (an incorporeal and sometimes divine messenger from a deity)\"}, {\"_def\": \"angel (one of the lowest order of such beings, below virtues)\"}, {\"_def\": \"angel (a person having the qualities attributed to angels, such as purity or selflessness)\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"araquidonato\", \"gender\": \"m\", \"defs\": [{\"_def\": \"(organic chemistry) arachidonate\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"al pie de la letra\", \"defs\": [{\"_def\": \"(idiomatic) to the letter\", \"_synonyms\": [\"a la letra\"]}], \"word_class\": \"Adverb\"},\n"
            + "{\"word\": \"a lo largo\", \"defs\": [{\"_def\": \"lengthwise (in the long direction)\"}, {\"_def\": \"along (in a line with, moving forward)\", \"extras\": [\"Andamos a lo largo de la calle.Let's walk along the street.\"]}, {\"_def\": \"in the distance, far away\", \"_synonyms\": [\"a lo lejos\"]}], \"word_class\": \"Adverb\"},\n"
            + "{\"word\": \"apretujar\", \"defs\": [{\"_def\": \"to squash\"}, {\"_def\": \"to scrunch, scrunch up\"}, {\"_def\": \"to squeeze\"}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"allí\", \"defs\": [{\"_def\": \"there (away from the speaker and the listener)\", \"_synonyms\": [\"ahí\", \"allá\"], \"antonyms\": [\"aquí\", \"acá\"]}], \"word_class\": \"Adverb\"},\n"
            + "{\"word\": \"albóndigas\", \"gender\": \"f pl\", \"defs\": [{\"_def\": \"plural of albóndiga\"}, {\"_def\": \"A soup made with albóndigas (meatballs)\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"alinear\", \"defs\": [{\"_def\": \"(transitive) to line up, to align\"}], \"word_class\": \"Verb\"}]";
    static String json2 = "["
            + "{\"word\": \"eugenol\", \"gender\": \"m\", \"defs\": [{\"_def\": \"(organic chemistry) eugenol\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"embobar\", \"defs\": [{\"_def\": \"to fascinate\", \"_synonyms\": [\"fascinar\"]}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"etnología\", \"gender\": \"f\", \"defs\": [{\"_def\": \"ethnology\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"error\", \"gender\": \"m\", \"defs\": [{\"_def\": \"error\", \"_synonyms\": [\"equivocación\", \"yerro\"]}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"empalar\", \"defs\": [{\"_def\": \"to impale\"}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"ego\", \"gender\": \"m\", \"defs\": [{\"_def\": \"ego\", \"_synonyms\": [\"yo\"]}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"equipo\", \"gender\": \"m\", \"defs\": [{\"_def\": \"team\", \"extras\": [\"equipo editorial ― editorial team\"]}, {\"_def\": \"equipment, kit, hardware\", \"extras\": [\"equipo de audio/sonido ― audio/sound equipment\"]}, {\"_def\": \"device\"}, {\"_def\": \"computer\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"equipo\", \"defs\": [{\"_def\": \"First-person singular   (yo)  present indicative  form of equipar.\"}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"escarpado\", \"defs\": [{\"_def\": \"steep\"}, {\"_def\": \"craggy\"}], \"word_class\": \"Adjective\"},\n"
            + "{\"word\": \"esprintar\", \"defs\": [{\"_def\": \"(intransitive) to sprint\"}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"exclamación\", \"gender\": \"f\", \"defs\": [{\"_def\": \"exclamation\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"entrevista\", \"gender\": \"f\", \"defs\": [{\"_def\": \"interview\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"entrevista\", \"defs\": [{\"_def\": \"Informal second-person singular (tú) affirmative imperative form of entrevistar.\"}, {\"_def\": \"Formal  second-person singular   (usted)  present indicative  form of entrevistar.\"}, {\"_def\": \"Third-person singular   (él, ella, also used with usted?)  present indicative  form of entrevistar.\"}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"entrevista\", \"gender\": \"f sg\", \"defs\": [{\"_def\": \"Feminine singular past participle of entrever.\"}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"enfilar\", \"defs\": [{\"_def\": \"(transitive) to line up\"}, {\"_def\": \"(transitive) to thread (a needle)\"}, {\"_def\": \"(transitive) to string (pearls, beads, etc.)\", \"_synonyms\": [\"ensartar\"]}, {\"_def\": \"(intransitive) to go straight down (a path)\"}, {\"_def\": \"(intransitive) to head for\", \"_synonyms\": [\"dirigirse a\"]}], \"word_class\": \"Verb\"},\n"
            + "{\"word\": \"equis\", \"gender\": \"f\", \"defs\": [{\"_def\": \"The name of the Latin-script letter X.\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"elfo\", \"gender\": \"m\", \"defs\": [{\"_def\": \"elf\"}], \"word_class\": \"Noun\"},\n"
            + "{\"word\": \"enormemente\", \"defs\": [{\"_def\": \"enormously\"}], \"word_class\": \"Adverb\"},\n"
            + "{\"word\": \"Adviento\", \"gender\": \"m\", \"alt\": [\"adviento\"], \"defs\": [{\"_def\": \"(Christianity) Advent\"}], \"word_class\": \"Proper noun\"},\n"
            + "{\"word\": \"ahuevonado\", \"alt\": [\"ahueonao, aweonado, aweonao (eye dialect)\", \"ahueonado (eye dialect, rare)\"], \"defs\": [{\"_def\": \"(Chile) dazed\"}, {\"_def\": \"(Chile, vulgar) stupid\"}], \"word_class\": \"Adjective\"}"
            + "]";

    public IntlinParserTest() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:testDict/dbtest.db");
            Statement stm = con.createStatement();
            // Extension table
            stm.execute("CREATE TABLE IF NOT EXISTS Extension("
                    + "extension TEXT NOT NULL PRIMARY KEY)");
            stm = con.createStatement();
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
                    + "word_id INTEGER NOT NULL,"
                    + "extension TEXT NOT NULL,"
                    + "FOREIGN KEY(word_id) REFERENCES Word(word_id),"
                    + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                    + "PRIMARY KEY(word_id, extension))");
            stm = con.createStatement();
            // Synonym table 
            stm.execute("CREATE TABLE IF NOT EXISTS Synonym("
                    + "def_id INTEGER NOT NULL,"
                    + "extension TEXT NOT NULL,"
                    + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                    + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                    + "PRIMARY KEY(def_id, extension));");
            stm = con.createStatement();
            // Antonym table 
            stm.execute("CREATE TABLE IF NOT EXISTS Antonym("
                    + "def_id INTEGER NOT NULL,"
                    + "extension TEXT NOT NULL,"
                    + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                    + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                    + "PRIMARY KEY(def_id, extension));");
            stm = con.createStatement();
            // Extra table 
            stm.execute("CREATE TABLE IF NOT EXISTS Extra("
                    + "def_id INTEGER NOT NULL,"
                    + "extension TEXT NOT NULL,"
                    + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                    + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                    + "PRIMARY KEY(def_id, extension));");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(IntlinParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        File f1 = new File("testDict/test_1.json");
        f1.createNewFile();
        FileWriter fw = new FileWriter(f1.getPath());
        fw.write(json1);
        fw.close();
        File f2 = new File("testDict/test_2.json");
        f2.createNewFile();
        FileWriter fw2 = new FileWriter(f2.getPath());
        fw2.write(json2);
        fw2.close();
        files = new ArrayList<>();
        files.add(f1);
        files.add(f2);
        instance.doParsing(files, con);
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
        con.close();
        File dbFile = new File("testDict/dbtest.db");
        dbFile.delete();
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
    }

    /**
     * Test of doParsing method, of class IntlinParser.
     */
    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectAmount() {
        System.out.println("doParsing");
        int expectedTotal = 40;
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
            expected = expected && verifyAlternatives(expectedAlts, 40);
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
            expected = expected && verifyCorrectValue(37, "gender", "m");
            expected = expected && verifyCorrectValue(3, "gender", "f");
            expected = expected && verifyCorrectValue(19, "gender", "f pl");
            expected = expected && verifyCorrectValue(35, "gender", null);
            expected = expected && verifyCorrectValue(11, "word", "abono");
            expected = expected && verifyCorrectValue(12, "word", "abono");
            expected = expected && verifyCorrectValue(11, "word_class", "Noun");
            expected = expected && verifyCorrectValue(12, "word_class", "Verb");
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
        try {
            expected = expected && verifyCorrectDefs(expectedDefs, 2);
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

    private static boolean verifyAlternatives(ArrayList<String> expected, int word_id) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT alt.extension AS alternative, "
                        + "COUNT(alt.extension) AS size FROM Word w INNER JOIN "
                        + "Alternative alt on alt.word_id = w.word_id "
                        + "where w.word_id = ?");
        stm.setInt(1, word_id);
        ResultSet resSet = stm.executeQuery();
        boolean result = true;
        if (resSet.getInt("size") == 0 && expected.isEmpty()) {
            result = true;
        } else if (expected.size() != resSet.getInt("size")) {
            result = false;
        } else {
            while (resSet.next()) {
                result = result && expected.contains(resSet.getString("alternative"));
            }
        }

        return result;
    }

    private static boolean verifyCorrectValue(int word_id, String column,
            String expected) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String
                .format("SELECT %s FROM Word w WHERE w.word_id = ?", column));
        stm.setInt(1, word_id);
        ResultSet resSet = stm.executeQuery();
        String colVal = resSet.getString(column);
        return (colVal == null ? expected == null : colVal.equals(expected));
    }

    private static boolean verifyCorrectDefs(ArrayList<String> expected, int word_id) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT d.def AS definition, "
                        + "COUNT(d.def) AS size FROM Word w INNER JOIN "
                        + "Definition d on d.word_id = w.word_id "
                        + "where w.word_id = ?");
        stm.setInt(1, word_id);
        ResultSet resSet = stm.executeQuery();
        boolean result = true;
        if (resSet.getInt("size") == 0 && expected.isEmpty()) {
            result = true;
        } else if (expected.size() != resSet.getInt("size")) {
            result = false;
        } else {
            while (resSet.next()) {
                result = result && expected.contains(resSet.getString("definition"));
            }
        }

        return result;
    }
}
