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

    static enum CardState {
        active,
        suspended,
    }

    static enum CardProficiency {
        toLearn,
        learning,
        comfortable,
        mastered,
    }
    
    private CardState state;
    private CardProficiency level;
    private CardContent front;
    private CardContent back;
    private Interval<Date> nextReview;

    public Card(CardContent front, CardContent back) {
        this.front = front;
        this.back = back;
        this.state = CardState.active;
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

    public CardState getState() {
        return state;
    }

    public void suspend() {
        this.state = CardState.suspended;
    }

    public void activate() {
        this.state = CardState.active;
    }

    public CardProficiency getLevel() {
        return level;
    }

    public Date calcNextReview(int difficulty) {
        switch(difficulty){
            case 0: //fail
                break;
            case 1: //hard
                break;
            case 3: //good
                break;
            case 4: //easy
                break;
        }
        return null;
    }
}
