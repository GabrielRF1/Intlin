/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.usfs.srs;

import ine.ufsc.util.Interval;
import java.util.Date;

/**
 *
 * @author Gabriel
 */
public class Card {
    
    static enum cardState {
        active,
        suspended,
    }
    
    private cardState state;
    private CardContent front;
    private CardContent back;
    private Interval<Date> nextReview;
    
    public Card() {
    
    }

    public CardContent getFront() {
        return front;
    }

    public CardContent getBack() {
        return back;
    }

    public Interval<Date> getNextReview() {
        return nextReview;
    }

    public cardState getState() {
        return state;
    }
    
    public void suspend() {
    }
    
    public void activate() {
    
    }
    
    public void calcNextReview() {
    
    }
}
