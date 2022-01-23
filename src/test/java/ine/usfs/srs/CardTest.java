/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.usfs.srs;

import ine.ufsc.util.Interval;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class CardTest {
    
    public CardTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }

    /**
     * Test of getFront method, of class Card.
     */
    @Test
    public void testGetFront() {
        System.out.println("getFront");
        Card instance = null;
        CardContent expResult = null;
        CardContent result = instance.getFront();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBack method, of class Card.
     */
    @Test
    public void testGetBack() {
        System.out.println("getBack");
        Card instance = null;
        CardContent expResult = null;
        CardContent result = instance.getBack();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextReview method, of class Card.
     */
    @Test
    public void testGetNextReview() {
        System.out.println("getNextReview");
        Card instance = null;
        Interval<Date> expResult = null;
        Interval<Date> result = instance.getNextReview();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getState method, of class Card.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        Card instance = null;
        Card.cardState expResult = null;
        Card.cardState result = instance.getState();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of suspend method, of class Card.
     */
    @Test
    public void testSuspend() {
        System.out.println("suspend");
        Card instance = null;
        instance.suspend();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of activate method, of class Card.
     */
    @Test
    public void testActivate() {
        System.out.println("activate");
        Card instance = null;
        instance.activate();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReview() {
        System.out.println("calcNextReview");
        Card instance = null;
        instance.calcNextReview();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
