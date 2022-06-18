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
public abstract class CalcReviewStrategy {

    final Card.CardProficiency proficiency;
    final double ease;

    public CalcReviewStrategy(Card.CardProficiency proficiency, double ease) {
        this.proficiency = proficiency;
        this.ease = ease;
    }

    abstract public LocalDate calcNextReview();

    abstract public Card.CardProficiency updateProficiency();
    
    abstract public double updateEase();
    
}
