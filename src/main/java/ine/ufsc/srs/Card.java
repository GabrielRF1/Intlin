/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import ine.ufsc.util.Interval;
import java.time.LocalDate;
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
    private final CardContent front;
    private final CardContent back;
    private Interval<Date> nextReview;
    private float ease;

    public Card(CardContent front, CardContent back) {
        this.front = front;
        this.back = back;
        this.state = CardState.active;
        this.level = CardProficiency.toLearn;
        this.ease = 1.0f;
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

    public float getEase() {
        return ease;
    }

    public LocalDate calcNextReview(int difficulty) {
        switch(difficulty){
            case 0: //fail
                level = CardProficiency.toLearn;
                return LocalDate.now();
            case 1: //hard
                if(ease > 0) {
                    ease -= ease * 0.15;
                }
                switch(level) {
                    case toLearn:
                        return LocalDate.now();
                    case learning:
                        return LocalDate.now().plusDays(1);
                    case comfortable:
                        return LocalDate.now().plusDays(2);
                    case mastered:
                        return LocalDate.now().plusDays(3);
                }
            case 2: //good
                switch(level) {
                    case toLearn:
                        level = CardProficiency.learning;
                        return LocalDate.now();
                    case learning:
                        level = CardProficiency.comfortable;
                        return LocalDate.now().plusDays(1);
                    case comfortable:
                        level = CardProficiency.mastered;
                        return LocalDate.now().plusDays(5);
                    case mastered:
                        level = CardProficiency.mastered;
                        return LocalDate.now().plusWeeks(1);
                }
            case 3: //easy
                 switch(level) {
                    case toLearn:
                        level = CardProficiency.comfortable;
                        return LocalDate.now().plusDays(1);
                    case learning:
                        level = CardProficiency.mastered;
                        return LocalDate.now().plusDays(5);
                    case comfortable:
                        level = CardProficiency.mastered;
                        return LocalDate.now().plusWeeks(1);
                    case mastered:
                        level = CardProficiency.mastered;
                        return LocalDate.now().plusWeeks(2);
                }
        }
        return null;
    }
    
    // used in tests
    protected void setLevel(CardProficiency level) {
        this.level = level;
    }
    
    protected void setEase(float ease) {
        this.ease = ease;
    }

}
