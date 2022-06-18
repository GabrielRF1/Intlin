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
public class FailReviewStrategy extends CalcReviewStrategy {

    public FailReviewStrategy(Card.CardProficiency proficiency, double ease) {
        super(proficiency, ease);
    }

    @Override
    public LocalDate calcNextReview() {
        return LocalDate.now();
    }

    @Override
    public Card.CardProficiency updateProficiency() {
        return Card.CardProficiency.toLearn;
    }

    @Override
    public double updateEase() {
        return ease >= 0 ? ease - 0.80 : ease;
    }

}
