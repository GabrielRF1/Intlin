/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.time.LocalDate;
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
        CardContent front = new CardContent();
        CardContent back = new CardContent();
        Card instance = new Card(front, back);
        CardContent expResult = front;
        CardContent result = instance.getFront();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBack method, of class Card.
     */
    @Test
    public void testGetBack() {
        System.out.println("getBack");
        CardContent front = new CardContent();
        CardContent back = new CardContent();
        Card instance = new Card(front, back);
        CardContent expResult = back;
        CardContent result = instance.getBack();
        assertEquals(expResult, result);
    }

    /**
     * Test of getState method, of class Card.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        Card instance = new Card(new CardContent(), new CardContent());
        Card.CardState expResult = Card.CardState.active;
        Card.CardState result = instance.getState();
        assertEquals(expResult, result);
    }

    /**
     * Test of suspend method, of class Card.
     */
    @Test
    public void testSuspend() {
        System.out.println("suspend");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.suspend();
        assertEquals(instance.getState(), Card.CardState.suspended);
    }

    /**
     * Test of activate method, of class Card.
     */
    @Test
    public void testActivate() {
        System.out.println("activate");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.activate();
        assertEquals(instance.getState(), Card.CardState.active);
    }

    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewWrongAnswer() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.fail); 
        if(instance.getLevel() != Card.CardProficiency.toLearn)
            fail("Card level must return to \"tolearn\"");
        
        assertEquals(LocalDate.now(), nextReview);
    }
    
     /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewHardAnswerAtToLearnLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.toLearn);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.hard); 
        if(instance.getLevel() != Card.CardProficiency.toLearn)
            fail("Card level was not supposed to change");
        
        assertEquals(LocalDate.now(), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewHardAnswerAtLearningLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.learning);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.hard); 
        if(instance.getLevel() != Card.CardProficiency.learning)
            fail("Card level was not supposed to change");
        
        assertEquals(LocalDate.now().plusDays(1), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewHardAnswerAtComfortableLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.comfortable);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.hard); 
        if(instance.getLevel() != Card.CardProficiency.comfortable)
            fail("Card level was not supposed to change");
        
        assertEquals(LocalDate.now().plusDays(2), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewHardAnswerAtMasteredLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.mastered);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.hard); 
        if(instance.getLevel() != Card.CardProficiency.mastered)
            fail("Card level was not supposed to change");
        
        assertEquals(LocalDate.now().plusDays(3), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewHardAnswerAtAcquiredLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.acquired);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.hard); 
        if(instance.getLevel() != Card.CardProficiency.acquired)
            fail("Card level was not supposed to change");
        
        assertEquals(LocalDate.now().plusDays(5), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtToLearnLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.toLearn);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.good); 
        if(instance.getLevel() != Card.CardProficiency.learning)
            fail("Card level was supposed to upgrade by one");
        
        assertEquals(LocalDate.now(), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtLearningLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.learning);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.good); 
        if(instance.getLevel() != Card.CardProficiency.comfortable)
            fail("Card level was supposed to upgrade by one");
        
        assertEquals(LocalDate.now().plusDays(1), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtComfortableLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.comfortable);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.good); 
        if(instance.getLevel() != Card.CardProficiency.mastered)
            fail("Card level was supposed to upgrade by one");
        
        assertEquals(LocalDate.now().plusDays(3), nextReview);
    }
    
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtMasteredLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.mastered);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.good); 
        if(instance.getLevel() != Card.CardProficiency.acquired)
            fail("Card level was supposed to upgrade by one");
        
        assertEquals(LocalDate.now().plusDays(5), nextReview);
    }
    
        /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtAcquiredLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.acquired);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.good); 
        if(instance.getLevel() != Card.CardProficiency.acquired)
            fail("Card level was supposed to upgrade by one");
        
        assertEquals(LocalDate.now().plusWeeks(1), nextReview);
    }
    
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewEasyAnswerAtToLearnLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.toLearn);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.easy); 
        if(instance.getLevel() != Card.CardProficiency.comfortable)
            fail("Card level was supposed to upgrade by two");
        
        assertEquals(LocalDate.now().plusDays(1), nextReview);
    }
    
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewEasyAnswerAtLearningLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.learning);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.easy); 
        if(instance.getLevel() != Card.CardProficiency.mastered)
            fail("Card level was supposed to upgrade by two");
        
        assertEquals(LocalDate.now().plusDays(3), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewEasyAnswerAtComfortableLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.comfortable);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.easy); 
        if(instance.getLevel() != Card.CardProficiency.acquired)
            fail("Card level was supposed to upgrade by two");
        
        assertEquals(LocalDate.now().plusDays(5), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewEasyAnswerAtMasteredLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.mastered);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.easy); 
        if(instance.getLevel() != Card.CardProficiency.acquired)
            fail("Card level was supposed to upgrade by two");
        
        assertEquals(LocalDate.now().plusWeeks(1), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewEasyAnswerAtAcquiredLevel() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setLevel(Card.CardProficiency.acquired);
        LocalDate nextReview = instance.calcNextReview(Card.Difficulty.easy); 
        if(instance.getLevel() != Card.CardProficiency.acquired)
            fail("Card level was supposed to upgrade by two");
        
        assertEquals(LocalDate.now().plusWeeks(2), nextReview);
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewHardAnswerDecreasesEase() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setEase(5.0f);
        instance.calcNextReview(Card.Difficulty.hard); 
        
        assertEquals(5.0 - 0.25, instance.getEase());
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewEasyAnswerIncreasesEase() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setEase(5.0f);
        instance.calcNextReview(Card.Difficulty.easy); 
        
        assertEquals(5.0 + 0.25, instance.getEase());
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewWrongAnswerDecreasesEase() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setEase(5.0f);
        instance.calcNextReview(Card.Difficulty.fail); 
        
        assertEquals(5.0 - 0.8, instance.getEase());
    }
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtMasteredLevelIncreasesEase() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setEase(5.0f);
        instance.setLevel(Card.CardProficiency.mastered);
        instance.calcNextReview(Card.Difficulty.good); 
        
        assertEquals(5.0 + 0.20, instance.getEase());
    }  
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtAcquiredLevelIncreasesEase() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setEase(5.0f);
        instance.setLevel(Card.CardProficiency.acquired);
        instance.calcNextReview(Card.Difficulty.good); 
        
        assertEquals(5.0 + 0.25, instance.getEase());
    } 
    
    /**
     * Test of calcNextReview method, of class Card.
     */
    @Test
    public void testCalcNextReviewGoodAnswerAtLearningIsInfluencedByEase() {
        System.out.println("calcNextReview");
        Card instance = new Card(new CardContent(), new CardContent());
        instance.setEase(4.5f);
        instance.setLevel(Card.CardProficiency.learning);
        
        assertEquals(LocalDate.now().plusDays(1).plusDays(Math.round(5)), instance.calcNextReview(Card.Difficulty.good));
    } 
}
