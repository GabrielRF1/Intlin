/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries;

import java.sql.ResultSet;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class IntlinDictionaryTest {
    
    static String dbFileName = "es_enDict";
    
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
        System.out.println("searchDefinition");
        String word = "";
        IntlinDictionary instance = null;
        ResultSet expResult = null;
        ResultSet result = instance.searchDefinition(word);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchAlternativeForm method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchAlternativeForm() {
        System.out.println("searchAlternativeForm");
        String word = "";
        IntlinDictionary instance = null;
        ResultSet expResult = null;
        ResultSet result = instance.searchAlternativeForm(word);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchExtra method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testSearchExtra() {
        System.out.println("searchExtra");
        String extraOf = "";
        IntlinDictionary instance = null;
        ResultSet expResult = null;
        ResultSet result = instance.searchExtra(extraOf);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testAddDefinition() {
        System.out.println("addDefinition");
        ArrayList<String> contents = null;
        IntlinDictionary instance = null;
        boolean expResult = false;
        boolean result = instance.addDefinition(contents);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeDefinition method, of class IntlinDictionary.
     */
    @org.junit.jupiter.api.Test
    public void testRemoveDefinition() {
        System.out.println("removeDefinition");
        int definitionId = 0;
        IntlinDictionary instance = null;
        boolean expResult = false;
        boolean result = instance.removeDefinition(definitionId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
