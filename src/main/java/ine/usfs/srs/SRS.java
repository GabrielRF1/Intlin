/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.usfs.srs;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Gabriel
 */
public class SRS {
    
    private String srsDBName;
    private String language;
    private Connection con;
    
    public SRS() {
    
    }
    
    public ResultSet getDeck() {
        return null;
    }
    
    public boolean createDeck(String deckName) {
        return false;
    }
    
    public boolean createDeck(String deckName, int parentDeckId) {
        return false;
    }
    
    public boolean addToDeck(int deckId) {
        return false;
    }
    
    public ResultSet getTodaysReview() {
        return null;
    }   
    
}
