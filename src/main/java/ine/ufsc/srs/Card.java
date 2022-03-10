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
        this.nextReview = LocalDate.now();
    }

    public Card(CardContent front, CardContent back, Integer id, String nextReview, double ease, CardProficiency level, boolean isSuspended) {
        this.front = front;
        this.back = back;
        this.state = isSuspended ? CardState.suspended : CardState.active;
        this.level = level;
        this.ease = ease;
        this.nextReview = (nextReview == null) ? LocalDate.now() : LocalDate.parse(nextReview);
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

        long bonusDay = level != CardProficiency.toLearn ? Math.round(ease) : 0;
        
        return nextReviewBase.plusDays(bonusDay);
    }

    private LocalDate calcBaseReview(Difficulty difficulty) {
        switch (difficulty) {
            case fail:
                if (ease > 0) {
                    ease -= 0.80;
                }
                level = CardProficiency.toLearn;
                nextReview = LocalDate.now();
                break;
            case hard:
                if (ease > 0) {
                    ease -= 0.25;
                }
                switch (level) {
                    case toLearn:
                        nextReview = LocalDate.now();
                        break;
                    case learning:
                        nextReview = LocalDate.now().plusDays(1);
                        break;
                    case comfortable:
                        nextReview = LocalDate.now().plusDays(2);
                        break;
                    case mastered:
                        nextReview = LocalDate.now().plusDays(3);
                        break;
                    case acquired:
                        nextReview = LocalDate.now().plusDays(5);
                        break;
                }
                break;
            case good:
                switch (level) {
                    case toLearn:
                        level = CardProficiency.learning;
                        nextReview = LocalDate.now();
                        break;
                    case learning:
                        level = CardProficiency.comfortable;
                        nextReview = LocalDate.now().plusDays(1);
                        break;
                    case comfortable:
                        level = CardProficiency.mastered;
                        nextReview = LocalDate.now().plusDays(3);
                        break;
                    case mastered:
                        ease += 0.20;
                        level = CardProficiency.acquired;
                        nextReview = LocalDate.now().plusDays(5);
                        break;
                    case acquired:
                        ease += 0.25;
                        level = CardProficiency.acquired;
                        nextReview = LocalDate.now().plusWeeks(1);
                        break;
                }
                break;
            case easy:
                ease += 0.25;
                switch (level) {
                    case toLearn:
                        level = CardProficiency.comfortable;
                        nextReview = LocalDate.now().plusDays(1);
                        break;
                    case learning:
                        level = CardProficiency.mastered;
                        nextReview = LocalDate.now().plusDays(3);
                        break;
                    case comfortable:
                        level = CardProficiency.acquired;
                        nextReview = LocalDate.now().plusDays(5);
                        break;
                    case mastered:
                        level = CardProficiency.acquired;
                        nextReview = LocalDate.now().plusWeeks(1);
                        break;
                    case acquired:
                        level = CardProficiency.acquired;
                        nextReview = LocalDate.now().plusWeeks(2);
                        break;
                }
                break;
            default: // should not reach
                nextReview = LocalDate.now();
                break;
        }
        return nextReview;
    }

    // used in tests
    protected void setLevel(CardProficiency level) {
        this.level = level;
    }

    protected void setEase(float ease) {
        this.ease = ease;
    }

}
