/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries;

import ine.ufsc.model.dictionaries.parsers.IntlinParser;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Gabriel
 */
public class IntlinDictionary extends Dictionary {

    public IntlinDictionary(String dbFileName, String filesPath) throws ClassNotFoundException, SQLException, IOException {
        super(dbFileName, filesPath);
        if (!bdExists()) {
            parser = new IntlinParser(con);
            build();
        }
    }

    @Override
    public ResultSet searchDefinition(String word) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT w.gender, w.word_class, d.def AS definition, "
                        + "FROM Word w INNER JOIN "
                        + "Definition d on d.word_id = w.word_id "
                        + "where w.word = ?");
        stm.setString(1, word);
        return stm.executeQuery();
    }

    @Override
    public ResultSet searchAlternativeForm(String word) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet searchExtra(String extraOf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addDefinition(ArrayList<String> contents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeDefinition(int definitionId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void build() throws IOException, SQLException {
        createTables();
        File files = new File(filesPath);
        var filesAr = new ArrayList<File>();
        for (File file : files.listFiles()) {
            if (file.isFile() && file.getName().contains(".json")
                    && file.getName().contains(dbFileName)) {
                filesAr.add(file);
            }
        }
        parser.doParsing(filesAr);
    }

    private void createTables() throws SQLException {
        Statement stm = con.createStatement();
        // Extension table
        stm.execute("CREATE TABLE IF NOT EXISTS Extension("
                + "extension TEXT NOT NULL PRIMARY KEY)");
        stm = con.createStatement();
        // Word table 
        stm.execute("CREATE TABLE IF NOT EXISTS Word("
                + "word_id INTEGER PRIMARY KEY,"
                + "word TEXT NOT NULL,"
                + "word_class TEXT NOT NULL,"
                + "gender text)");
        stm = con.createStatement();
        // Definition table 
        stm.execute("CREATE TABLE IF NOT EXISTS Definition("
                + "def_id INTEGER PRIMARY KEY,"
                + "def TEXT NOT NULL,"
                + "word_id INTEGER NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))");
        stm = con.createStatement();
        // Alternative table 
        stm.execute("CREATE TABLE IF NOT EXISTS Alternative("
                + "word_id INTEGER NOT NULL,"
                + "extension TEXT NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id),"
                + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                + "PRIMARY KEY(word_id, extension))");
        stm = con.createStatement();
        // Synonym table 
        stm.execute("CREATE TABLE IF NOT EXISTS Synonym("
                + "def_id INTEGER NOT NULL,"
                + "extension TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                + "PRIMARY KEY(def_id, extension));");
        stm = con.createStatement();
        // Antonym table 
        stm.execute("CREATE TABLE IF NOT EXISTS Antonym("
                + "def_id INTEGER NOT NULL,"
                + "extension TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                + "PRIMARY KEY(def_id, extension));");
        stm = con.createStatement();
        // Extra table 
        stm.execute("CREATE TABLE IF NOT EXISTS Extra("
                + "def_id INTEGER NOT NULL,"
                + "extension TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id),"
                + "FOREIGN KEY(extension) REFERENCES Extension(extension),"
                + "PRIMARY KEY(def_id, extension));");
    }

    private boolean bdExists() {
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select count(word) as size from Word");
            return rs.getInt("size") > 0;
        } catch (SQLException ex) {
            return false;
        }
    }

}
