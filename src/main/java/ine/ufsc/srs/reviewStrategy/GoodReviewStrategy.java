/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs.reviewStrategy;

import ine.ufsc.srs.Card;
import java.time.LocalDate;

/**
 *
 * @author Gabriel
 */
public class GoodReviewStrategy extends CalcReviewStrategy {

    public GoodReviewStrategy(Card.CardProficiency proficiency, double ease) {
        super(proficiency, ease);
    }

    @Override
    public LocalDate calcNextReview() {
        switch (proficiency) {
            case toLearn:
                return LocalDate.now();
            case learning:
                return LocalDate.now().plusDays(1);
            case comfortable:
                return LocalDate.now().plusDays(3);
            case mastered:
                return LocalDate.now().plusDays(5);
            case acquired:
                return LocalDate.now().plusWeeks(1);
            default:
                return LocalDate.now();
        }
    }

    @Override
    public Card.CardProficiency updateProficiency() {
        switch (proficiency) {
            case toLearn:
                return Card.CardProficiency.learning;
            case learning:
                return Card.CardProficiency.comfortable;
            case comfortable:
                return Card.CardProficiency.mastered;
            case mastered:
            case acquired:
                return Card.CardProficiency.acquired;
            default:
                return Card.CardProficiency.toLearn;
        }
    }

    @Override
    public double updateEase() {
        switch (proficiency) {
            case mastered:
                return ease + 0.20;
            case acquired:
                return ease + 0.25;
            default:
                return ease;
        }
    }

}
