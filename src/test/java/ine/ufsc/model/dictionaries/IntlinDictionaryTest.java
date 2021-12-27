/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class IntlinDictionaryTest {

    static String dbFileName = "intlinTest";
    static String dbFilePath = "testDict" + File.separator + "IntlinTest";
    static Path srcPath;
    static Path dstPath;
    static IntlinDictionary instance;

    public IntlinDictionaryTest() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws ClassNotFoundException, SQLException, IOException {
        instance = new IntlinDictionary(dbFileName, dbFilePath);
        srcPath = new File(dbFilePath + File.separator + dbFileName + ".db").toPath();
        dstPath = new File(dbFilePath + File.separator + dbFileName + "(1).db").toPath();
        Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws ClassNotFoundException, SQLException, IOException {
        instance.closeConnection();
        Files.delete(srcPath);
        dstPath.toFile().renameTo(new File(dbFilePath + File.separator + dbFileName + ".db"));
    }

    /**
     * Test of searchDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchDefinition() {
        try {
            System.out.println("searchDefinition");
            String word = "albóndigas";
            ArrayList<String> expected = new ArrayList<>();
            String expectedGender = "f pl";
            String expectedWordClass = "Noun";
            String expectedId = "7";
            ArrayList<String> expectedDefinitions = new ArrayList<>();
            expectedDefinitions.add("plural of albóndiga");
            expectedDefinitions.add("A soup made with albóndigas (meatballs)");
            expected.add(expectedId);
            expected.add(expectedGender);
            expected.add(expectedWordClass);
            expected.addAll(expectedDefinitions);

            ArrayList<String> actual = new ArrayList<>();
            ResultSet result = instance.searchDefinition(word);
            actual.add(result.getString("word_id"));
            actual.add(result.getString("gender"));
            actual.add(result.getString("word_class"));
            while (result.next()) {
                actual.add(result.getString("definition"));
            }
            assertEquals(expected, actual);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of searchAlternativeForm method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchAlternativeForm() {
        try {
            String word = "ahuevonado";
            Set<String> expResult = new HashSet<>();
            expResult.add("ahueonao, aweonado, aweonao (eye dialect)");
            expResult.add("ahueonado (eye dialect, rare)");

            Set<String> actual = new HashSet<>();
            ResultSet result = instance.searchAlternativeForm(word);
            while (result.next()) {
                String nextAlt = result.getString("alternative");
                actual.add(nextAlt);
            }
            assertEquals(expResult, actual);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of searchExtra method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchExtra() {
        try {

            String extraOfDefiniton = "compost, fertilizer, manure";
            Set<String> expResult = new HashSet<>();
            expResult.add("2002, Clara Inés Ríos Katto, Guía para el cultivo y aprovechamiento del botón de oro: Tithoni diversifolia (Hemsl.) Gray, Concenio Andrés Bello, page 22.");
            expResult.add("En menor medida se utiliza abono de vaca, vermicompost, mantillo, abono de caballo[,] restos de cultivos, restos de cocina.To a lesser extent, cow manure, vermicompost, humus, horse manure, crop residues, and kitchen scraps are used.");
            expResult.add("En campos de cultivos de arroz por inundación, los agricultores cosechan el botón de oro[. L]o incorporan al suelo como abono verde y mejorador de suelos.In rice paddies watered by flooding, farmers harvest buttercups. They incorporate it into the soil as a green fertilizer and soil improver.");
            expResult.add("2009, Alfredo Tolón Becerra &amp; Xavier B. Lastra Bravo (eds.), Actas del III Seminario Internacional de Cooperación y Desarrollo en Espacios Rurales Iberoamericanos, Editorial Universidad de  Almería, page 205.");

            Set<String> actual = new HashSet<>();
            ResultSet result = instance.searchExtra(extraOfDefiniton);
            while (result.next()) {
                String nextExt = result.getString("extra");
                actual.add(nextExt);
            }
            assertEquals(expResult, actual);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddDefinitionWSyn() {
        try {
            IntlinDictionary.IntlinInfo contents = new IntlinDictionary.IntlinInfo();
            contents.word = "estampido";
            contents.def = "shot";
            contents.syns.add("disparo");
            contents.syns.add("tiro");

            boolean result = instance.addDefinition(contents);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddDefinitionWAnt() {
        try {
            IntlinDictionary.IntlinInfo contents = new IntlinDictionary.IntlinInfo();
            contents.word = "estampido";
            contents.def = "shot2";
            contents.ants.add("Nondisparo");
            contents.ants.add("Nontiro");

            boolean result = instance.addDefinition(contents);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddDefinitionWExtra() {
        try {
            IntlinDictionary.IntlinInfo contents = new IntlinDictionary.IntlinInfo();
            contents.word = "estampido";
            contents.def = "shot3";
            contents.extras.add("disparó y murrió");

            boolean result = instance.addDefinition(contents);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddDefinitionToNewWord() {
        try {
            IntlinDictionary.IntlinInfo contents = new IntlinDictionary.IntlinInfo();
            contents.word = "nevera";
            contents.wordClass = "Noun";
            contents.gender = "f";
            contents.def = "refrigerator";
            contents.syns.add("frigorífico");
            contents.syns.add("refri");
            contents.syns.add("refrigeradora");
            contents.syns.add("refrigerador");

            boolean result = instance.addDefinition(contents);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddFullDefinition() {
        try {
            IntlinDictionary.IntlinInfo contents = new IntlinDictionary.IntlinInfo();
            contents.word = "estampido";
            contents.def = "shot4";
            contents.syns.add("disparo");
            contents.syns.add("tiro");
            contents.ants.add("Nondisparo");
            contents.ants.add("Nontiro");
            contents.extras.add("disparó y murrió");

            boolean result = instance.addDefinition(contents);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of removeDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testRemoveBasicDefinition() {
        try {
            int definitionId = 20;
            boolean result = instance.removeDefinition(definitionId);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of removeDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testRemoveDefinitionWSynAntExt() {
        try {
            int definitionId = 7;
            boolean result = instance.removeDefinition(definitionId);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of searchAntonym method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchAntonym() {
        try {
            String definition = "there (away from the speaker and the listener)";
            Set<String> expResult = new HashSet<>();
            expResult.add("aquí");
            expResult.add("acá");
            Set<String> actual = new HashSet<>();

            ResultSet result = instance.searchAntonym(definition);
            while (result.next()) {
                String nextAnt = result.getString("ant");
                actual.add(nextAnt);
            }
            assertEquals(expResult, actual);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of searchSynonym method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchSynonym() {
        try {
            String definition = "compost, fertilizer, manure";
            Set<String> expResult = new HashSet<>();
            expResult.add("fertilizante");
            Set<String> actual = new HashSet<>();

            ResultSet result = instance.searchSynonym(definition);
            while (result.next()) {
                String nextSyn = result.getString("syn");
                actual.add(nextSyn);
            }
            assertEquals(expResult, actual);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of removeWord method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testRemoveSimpleWord() {
        try {
            int wordId = 9;
            boolean result = instance.removeWord(wordId);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of removeWord method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testRemoveComplexWord() {
        try {
            int wordId = 8;
            boolean result = instance.removeWord(wordId);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addSynonym method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddSynonym() {
        try {
            int defId = 4;
            boolean result = instance.addSynonym(defId, "crisálide");
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }
}
