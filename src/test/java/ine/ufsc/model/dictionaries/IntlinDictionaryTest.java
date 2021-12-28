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
import java.sql.PreparedStatement;
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
     * Test of searchExtras method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchExtras() {
        try {

            String extraOfDefiniton = "compost, fertilizer, manure";
            Set<String> expResult = new HashSet<>();
            expResult.add("2002, Clara Inés Ríos Katto, Guía para el cultivo y aprovechamiento del botón de oro: Tithoni diversifolia (Hemsl.) Gray, Concenio Andrés Bello, page 22.");
            expResult.add("En menor medida se utiliza abono de vaca, vermicompost, mantillo, abono de caballo[,] restos de cultivos, restos de cocina.To a lesser extent, cow manure, vermicompost, humus, horse manure, crop residues, and kitchen scraps are used.");
            expResult.add("En campos de cultivos de arroz por inundación, los agricultores cosechan el botón de oro[. L]o incorporan al suelo como abono verde y mejorador de suelos.In rice paddies watered by flooding, farmers harvest buttercups. They incorporate it into the soil as a green fertilizer and soil improver.");
            expResult.add("2009, Alfredo Tolón Becerra &amp; Xavier B. Lastra Bravo (eds.), Actas del III Seminario Internacional de Cooperación y Desarrollo en Espacios Rurales Iberoamericanos, Editorial Universidad de  Almería, page 205.");

            Set<String> actual = new HashSet<>();
            ResultSet result = instance.searchExtras(extraOfDefiniton);
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

            ResultSet defs = instance.searchDefinition("estampido");
            boolean foundDef = false;
            while (defs.next()) {
                if (defs.getString("definition").equals("shot")) {
                    foundDef = true;
                    break;
                }
            }
            result &= foundDef;
            result &= wasSynonymProperlyAdded(contents.syns, "shot");

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

            ResultSet defs = instance.searchDefinition("estampido");
            boolean foundDef = false;
            while (defs.next()) {
                if (defs.getString("definition").equals("shot2")) {
                    foundDef = true;
                    break;
                }
            }
            result &= foundDef;
            result &= wasAntonymProperlyAdded(contents.ants, "shot2");

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

            ResultSet defs = instance.searchDefinition("estampido");
            boolean foundDef = false;
            while (defs.next()) {
                if (defs.getString("definition").equals("shot3")) {
                    foundDef = true;
                    break;
                }
            }
            result &= foundDef;
            result &= wasExtraProperlyAdded(contents.extras, "shot3");

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
            
            ResultSet defs = instance.searchDefinition("nevera");
            boolean foundDef = false;
            while (defs.next()) {
                if (defs.getString("definition").equals("refrigerator")
                        && defs.getString("word_class").equals("Noun")
                        && defs.getString("gender").equals("f")) {
                    foundDef = true;
                    break;
                }
            }
            result &= foundDef;
            result &= wasSynonymProperlyAdded(contents.syns, "refrigerator");
            
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
            
            ResultSet defs = instance.searchDefinition("estampido");
            boolean foundDef = false;
            while (defs.next()) {
                if (defs.getString("definition").equals("shot4")) {
                    foundDef = true;
                    break;
                }
            }
            result &= foundDef;
            result &= wasSynonymProperlyAdded(contents.syns, "shot4");
            result &= wasAntonymProperlyAdded(contents.ants, "shot4");
            result &= wasExtraProperlyAdded(contents.extras, "shot4");
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
            PreparedStatement stm = instance.con.prepareStatement("SELECT * FROM Definition WHERE def_id = ?");
            stm.setInt(1, definitionId);
            ResultSet RS = stm.executeQuery();
            assertTrue(result && RS.isClosed());
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
            PreparedStatement stmDef = instance.con.prepareStatement("SELECT * FROM Definition WHERE def_id = ?");
            stmDef.setInt(1, definitionId);
            ResultSet RSdef = stmDef.executeQuery();
            
            PreparedStatement stmSyn = instance.con.prepareStatement("SELECT * FROM Synonym WHERE def_id = ?");
            stmSyn.setInt(1, definitionId);
            ResultSet RSsyn = stmSyn.executeQuery();
            
            PreparedStatement stmAnt = instance.con.prepareStatement("SELECT * FROM Antonym WHERE def_id = ?");
            stmAnt.setInt(1, definitionId);
            ResultSet RSant = stmAnt.executeQuery();
            
            PreparedStatement stmExtra = instance.con.prepareStatement("SELECT * FROM Extra WHERE def_id = ?");
            stmExtra.setInt(1, definitionId);
            ResultSet RSextra = stmExtra.executeQuery();
            
            assertTrue(result && RSdef.isClosed() && RSsyn.isClosed()
            && RSant.isClosed() && RSextra.isClosed());
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of searchAntonyms method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchAntonyms() {
        try {
            String definition = "there (away from the speaker and the listener)";
            Set<String> expResult = new HashSet<>();
            expResult.add("aquí");
            expResult.add("acá");
            Set<String> actual = new HashSet<>();

            ResultSet result = instance.searchAntonyms(definition);
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
     * Test of searchSynonyms method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchSynonyms() {
        try {
            String definition = "compost, fertilizer, manure";
            Set<String> expResult = new HashSet<>();
            expResult.add("fertilizante");
            Set<String> actual = new HashSet<>();

            ResultSet result = instance.searchSynonyms(definition);
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
            PreparedStatement stm = instance.con.prepareStatement("SELECT * FROM Word WHERE word_id = ?");
            stm.setInt(1, wordId);
            ResultSet RS = stm.executeQuery();
            assertTrue(result && RS.isClosed());
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
            PreparedStatement stm = instance.con.prepareStatement("SELECT * FROM Word WHERE word_id = ?");
            stm.setInt(1, wordId);
            ResultSet RS = stm.executeQuery();
            
            PreparedStatement stmDef = instance.con.prepareStatement("SELECT * FROM Definition WHERE word_id = ?");
            stmDef.setInt(1, wordId);
            ResultSet RSdef = stmDef.executeQuery();
            assertTrue(result && RS.isClosed() && RSdef.isClosed());
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
            String syn = "crisálide";
            int defId = 4;
            boolean result = instance.addSynonym(defId, syn);
            ArrayList<String> syns = new ArrayList<>();
            assertTrue(result && wasSynonymProperlyAdded(syns, syn));
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addAntonym method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddAntonym() {
        try {
            String ant = "Noncrisálide";
            int defId = 4;
            boolean result = instance.addAntonym(defId, ant);
            ArrayList<String> ants = new ArrayList<>();
            assertTrue(result && wasAntonymProperlyAdded(ants, ant));
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    /**
     * Test of addExtra method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddExtra() {
        try {
            String extra = "Doce días en la crisálida y estás destinado a ser lo que realmente eres.";
            int defId = 4;
            boolean result = instance.addExtra(defId, extra);
            ArrayList<String> extras = new ArrayList<>();
            assertTrue(result && wasExtraProperlyAdded(extras, extra));
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }
    
    /**
     * Test of updateWord method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testUpdateWord() {
        try {
            //String newText = "directamenteee\' WHERE 1==1; DROP TABLE Word; --"; // Does not work, fortunately
            String newText = "directamenteee";
            int wordId = 2;
            boolean result = instance.updateWord(wordId, newText);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }
    
    /**
     * Test of updateAlt method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testUpdateAlt() {
        try {
            String newAltText = "ahueonao, aweonado, aweonao";
            int id = 1;
            boolean result = instance.updateAlt(id, newAltText);
            instance.updateAlt(id, "ahueonao, aweonado, aweonao (eye dialect)"); //back to normal, so we don't mess up testSearchAlternativeForm
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }
    
    /**
     * Test of updateAlt method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testUpdateDef() {
        try {
            String newText = "crack, bang";
            int defId = 1;
            boolean result = instance.updateAlt(defId, newText);
            assertTrue(result);
        } catch (SQLException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

    private boolean wasSynonymProperlyAdded(ArrayList<String> colection, String def) throws SQLException {
        ResultSet syns = instance.searchSynonyms(def);
        ArrayList<String> synsStrings = new ArrayList<>();
        while (syns.next()) {
            synsStrings.add(syns.getString("syn"));
        }
        return synsStrings.containsAll(colection);
    }
    
    private boolean wasAntonymProperlyAdded(ArrayList<String> colection, String def) throws SQLException {
        ResultSet ants = instance.searchAntonyms(def);
        ArrayList<String> antsStrings = new ArrayList<>();
        while (ants.next()) {
            antsStrings.add(ants.getString("ant"));
        }
        return antsStrings.containsAll(colection);
    }
    
    private boolean wasExtraProperlyAdded(ArrayList<String> colection, String def) throws SQLException {
        ResultSet extras = instance.searchExtras(def);
        ArrayList<String> extrasStrings = new ArrayList<>();
        while (extras.next()) {
            extrasStrings.add(extras.getString("extra"));
        }
        return extrasStrings.containsAll(colection);
    }
}
