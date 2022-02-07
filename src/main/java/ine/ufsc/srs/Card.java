/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.time.LocalDate;

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
        acquired,
    }

    static enum Difficulty {
        fail,
        hard,
        good,
        easy,
    }

    private CardState state;
    private CardProficiency level;
    private final CardContent front;
    private final CardContent back;
    private LocalDate nextReview;
    private Integer id;
    private double ease;

    public Card(CardContent front, CardContent back) {
        this.front = front;
        this.back = back;
        this.state = CardState.active;
        this.level = CardProficiency.toLearn;
        this.ease = 0.0f;
    }
    
    public Card(CardContent front, CardContent back, Integer id) {
        this.front = front;
        this.back = back;
        this.state = CardState.active;
        this.level = CardProficiency.toLearn;
        this.ease = 0.0f;
        this.id = id;
    }
    

    public CardContent getFront() {
        return front;
    }

    public CardContent getBack() {
        return back;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getNextReview() {
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

    public double getEase() {
        return ease;
    }

    public LocalDate calcNextReview(Difficulty difficulty) {
        LocalDate nextReviewBase = calcBaseReview(difficulty);

        return nextReviewBase.plusDays(Math.round(ease));
    }

    private LocalDate calcBaseReview(Difficulty difficulty) {
        switch (difficulty) {
            case fail:
                if (ease > 0) {
                    ease -= 0.80;
                }
                level = CardProficiency.toLearn;
                return LocalDate.now();
            case hard:
                if (ease > 0) {
                    ease -= 0.25;
                }
                switch (level) {
                    case toLearn:
                        return LocalDate.now();
                    case learning:
                        return LocalDate.now().plusDays(1);
                    case comfortable:
                        return LocalDate.now().plusDays(2);
                    case mastered:
                        return LocalDate.now().plusDays(3);
                    case acquired:
                        return LocalDate.now().plusDays(5);
                }
            case good:
                switch (level) {
                    case toLearn:
                        level = CardProficiency.learning;
                        return LocalDate.now();
                    case learning:
                        level = CardProficiency.comfortable;
                        return LocalDate.now().plusDays(1);
                    case comfortable:
                        level = CardProficiency.mastered;
                        return LocalDate.now().plusDays(3);
                    case mastered:
                        ease += 0.20;
                        level = CardProficiency.acquired;
                        return LocalDate.now().plusDays(5);
                    case acquired:
                        ease += 0.25;
                        level = CardProficiency.acquired;
                        return LocalDate.now().plusWeeks(1);
                }
            case easy:
                ease += 0.25;
                switch (level) {
                    case toLearn:
                        level = CardProficiency.comfortable;
                        return LocalDate.now().plusDays(1);
                    case learning:
                        level = CardProficiency.mastered;
                        return LocalDate.now().plusDays(3);
                    case comfortable:
                        level = CardProficiency.acquired;
                        return LocalDate.now().plusDays(5);
                    case mastered:
                        level = CardProficiency.acquired;
                        return LocalDate.now().plusWeeks(1);
                    case acquired:
                        level = CardProficiency.acquired;
                        return LocalDate.now().plusWeeks(2);
                }
            default: // should not reach
                return LocalDate.now();
        }
    }

    // used in tests
    protected void setLevel(CardProficiency level) {
        this.level = level;
    }

    protected void setEase(float ease) {
        this.ease = ease;
    }

}
