/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Gabriel
 */
public abstract class Dictionary {
    private Connection con;
    private String dbFileName;

    public Dictionary(String dbFileName) {
        this.dbFileName = dbFileName;
    }
    
    private void connect() {
    }
    
    public abstract ResultSet searchDefinition(String word);
    
    public abstract ResultSet searchAlternativeForm(String word);
    
    public abstract ResultSet searchExtra(String extraOf);
    
    public abstract boolean addDefinition(ArrayList<String> contents);
    
    public abstract boolean removeDefinition(int definitionId);
    
    protected abstract void build();
    
    protected abstract boolean bdExists();
}
