/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class IntlinDictionaryTest {

    static String dbFileName = "intlinTest";
    static String dbFilePath = "testDict/IntlinTest";

    public IntlinDictionaryTest() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() {
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() {
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
            ArrayList<String> expectedDefinitions = new ArrayList<>();
            expectedDefinitions.add("plural of albóndiga");
            expectedDefinitions.add("A soup made with albóndigas (meatballs)");
            expected.add(expectedGender);
            expected.add(expectedWordClass);
            expected.addAll(expectedDefinitions);

            IntlinDictionary instance = new IntlinDictionary(dbFileName, dbFilePath);
            ArrayList<String> actual = new ArrayList<>();
            ResultSet result = instance.searchDefinition(word);
            actual.add(result.getString("gender"));
            actual.add(result.getString("word_class"));
            while (result.next()) {
                actual.add(result.getString("definition"));
            }
            assertEquals(expected, actual);
            // TODO review the generated test code and remove the default call to fail.
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            fail("\nException thrown: " + ex.toString());
        }
    }

//    /**
//     * Test of searchAlternativeForm method, of class IntlinDictionary.
//     */
//    @org.junit.jupiter.api.Test
//    public void testSearchAlternativeForm() {
//        System.out.println("searchAlternativeForm");
//        String word = "";
//        IntlinDictionary instance = null;
//        ResultSet expResult = null;
//        ResultSet result = instance.searchAlternativeForm(word);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of searchExtra method, of class IntlinDictionary.
//     */
//    @org.junit.jupiter.api.Test
//    public void testSearchExtra() {
//        System.out.println("searchExtra");
//        String extraOf = "";
//        IntlinDictionary instance = null;
//        ResultSet expResult = null;
//        ResultSet result = instance.searchExtra(extraOf);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addDefinition method, of class IntlinDictionary.
//     */
//    @org.junit.jupiter.api.Test
//    public void testAddDefinition() {
//        System.out.println("addDefinition");
//        ArrayList<String> contents = null;
//        IntlinDictionary instance = null;
//        boolean expResult = false;
//        boolean result = instance.addDefinition(contents);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeDefinition method, of class IntlinDictionary.
//     */
//    @org.junit.jupiter.api.Test
//    public void testRemoveDefinition() {
//        System.out.println("removeDefinition");
//        int definitionId = 0;
//        IntlinDictionary instance = null;
//        boolean expResult = false;
//        boolean result = instance.removeDefinition(definitionId);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
