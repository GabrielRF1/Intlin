/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries;

import ine.ufsc.model.dictionaries.parsers.DictParser;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        con = DriverManager.getConnection("jdbc:sqlite:"+filesPath+File.separator+dbFileName+".db");
    }
    
    public void closeConnection() throws SQLException {
        con.close();
    }
    
    public abstract ResultSet searchDefinition(String word) throws SQLException;
    
    public abstract ResultSet searchAlternativeForm(String word) throws SQLException;
        
    public abstract boolean addDefinition(Object contents) throws SQLException;
    
    public abstract boolean removeDefinition(int definitionId) throws SQLException;
    
    public abstract boolean removeWord(int wordId) throws SQLException;
    
    public abstract boolean updateWord(int wordId, String newText) throws SQLException;
    
    public abstract boolean updateAlt(int id, String newAltText) throws SQLException;
    
    public abstract boolean updateDef(int defId, String newText) throws SQLException;
    
    protected abstract boolean addWord(Object contents) throws SQLException;
}
