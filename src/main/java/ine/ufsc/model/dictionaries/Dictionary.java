/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries;

import ine.ufsc.model.dictionaries.parsers.DictParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Gabriel
 */
public abstract class Dictionary {
    protected final String dbFileName;
    protected final String filesPath;
    protected Connection con;
    protected DictParser parser;

    public Dictionary(String dbFileName, String filesPath) throws ClassNotFoundException, SQLException {
        this.dbFileName = dbFileName;
        this.filesPath = filesPath;
        connect();
    }
    
    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:"+filesPath+"/"+dbFileName+".db");
    }
    
    public abstract ResultSet searchDefinition(String word) throws SQLException;
    
    public abstract ResultSet searchAlternativeForm(String word) throws SQLException;
    
    public abstract ResultSet searchExtra(String extraOf) throws SQLException;
    
    public abstract boolean addDefinition(ArrayList<String> contents);
    
    public abstract boolean removeDefinition(int definitionId);
}
