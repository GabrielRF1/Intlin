/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import ine.ufsc.srs.reviewStrategy.CalcReviewStrategy;
import ine.ufsc.srs.reviewStrategy.EasyReviewStrategy;
import ine.ufsc.srs.reviewStrategy.FailReviewStrategy;
import ine.ufsc.srs.reviewStrategy.GoodReviewStrategy;
import ine.ufsc.srs.reviewStrategy.HardReviewStrategy;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author Gabriel
 */
public class Card {

    public static enum CardState {
        active,
        suspended,
    }

    public static enum CardProficiency {
        toLearn,
        learning,
        comfortable,
        mastered,
        acquired,
    }

    public static enum Difficulty {
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
        nextReview = nextReviewBase.plusDays(bonusDay);
        return nextReview;
    }

    private LocalDate calcBaseReview(Difficulty difficulty) {
        CalcReviewStrategy reviewCalculator = selectStrategy(difficulty);
        ease = reviewCalculator.updateEase();
        level = reviewCalculator.updateProficiency();
        nextReview = reviewCalculator.calcNextReview();

        return nextReview;
    }

    private CalcReviewStrategy selectStrategy(Difficulty answer) {
        switch (answer) {
            case easy:
                return new EasyReviewStrategy(level, ease);
            case good:
                return new GoodReviewStrategy(level, ease);
            case hard:
                return new HardReviewStrategy(level, ease);
            default:
                return new FailReviewStrategy(level, ease);
        }
    }

    // used in tests
    protected void setLevel(CardProficiency level) {
        this.level = level;
    }

    protected void setEase(float ease) {
        this.ease = ease;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            return Objects.equals(this.hashCode(), ((Card) o).hashCode());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
