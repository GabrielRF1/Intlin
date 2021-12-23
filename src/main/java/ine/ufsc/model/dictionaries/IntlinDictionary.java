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

    public static class IntlinInfo {

        public String word;
        public String gender;
        public String wordClass;
        public String def;
        public ArrayList<String> alts = new ArrayList<>();
        public ArrayList<String> syns = new ArrayList<>();
        public ArrayList<String> ants = new ArrayList<>();
        public ArrayList<String> extras = new ArrayList<>();

    }

    public IntlinDictionary(String dbFileName, String filesPath) throws ClassNotFoundException, SQLException, IOException {
        super(dbFileName, filesPath);
        if (!bdExists()) {
            build();
        }
    }

    @Override
    public ResultSet searchDefinition(String word) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT w.word_id, w.gender, w.word_class, d.def AS definition "
                        + "FROM Word w INNER JOIN "
                        + "Definition d on d.word_id = w.word_id "
                        + "where w.word = ?");
        stm.setString(1, word);
        return stm.executeQuery();
    }

    @Override
    public ResultSet searchAlternativeForm(String word) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT a.alt AS alternative "
                        + "FROM Word w INNER JOIN "
                        + "Alternative a on a.word_id = w.word_id "
                        + "where w.word = ?");
        stm.setString(1, word);
        return stm.executeQuery();
    }

    @Override
    public ResultSet searchExtra(String extraOfDefinition) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT e.extra "
                        + "FROM Definition d INNER JOIN "
                        + "Extra e on d.def_id = e.def_id "
                        + "where d.def = ?");
        stm.setString(1, extraOfDefinition);
        return stm.executeQuery();
    }

    @Override
    public boolean addDefinition(Object contents) throws SQLException {
        boolean success = true;
        IntlinInfo info = (IntlinInfo) contents;
        con.setAutoCommit(false);
        PreparedStatement wordStm = con.prepareStatement("SELECT word_id FROM Word WHERE word=?");
        wordStm.setString(1, info.word);
        int wordId = wordStm.executeQuery().getInt("word_id");

        PreparedStatement stmInsertDef = con
                .prepareStatement("INSERT INTO Definition(def, word_id)"
                        + "VALUES(?, ?)");
        stmInsertDef.setString(1, info.def);
        stmInsertDef.setInt(2, wordId);

        success &= !stmInsertDef.execute();

        int defId = con.prepareStatement("SELECT last_insert_rowid() AS id;")
                .executeQuery().getInt("id");

        if (!info.syns.isEmpty()) {
            insertSynAntExt("Synonym", info.syns, defId);
        }
        if (!info.ants.isEmpty()) {
            insertSynAntExt("Antonym", info.ants, defId);
        }
        if (!info.extras.isEmpty()) {
            insertSynAntExt("Extra", info.extras, defId);
        }
        con.commit();
        con.setAutoCommit(true);
        return success;
    }

    @Override
    public boolean removeDefinition(int definitionId) throws SQLException {
        PreparedStatement stm = con.prepareStatement("DELETE FROM Definition WHERE def_id=?");
        stm.setInt(1, definitionId);
        return !stm.execute();
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
        parser = new IntlinParser(con);
        parser.doParsing(filesAr);
    }

    private void createTables() throws SQLException {
        Statement stm = con.createStatement();
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
                + "alt_id INTEGER PRIMARY KEY,"
                + "word_id INTEGER NOT NULL,"
                + "alt TEXT NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))"
        /*+ "PRIMARY KEY(word_id, alt))"*/);
        stm = con.createStatement();
        // Synonym table 
        stm.execute("CREATE TABLE IF NOT EXISTS Synonym("
                + "syn_id INTEGER PRIMARY KEY,"
                + "def_id INTEGER NOT NULL,"
                + "syn TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id))");
        stm = con.createStatement();
        // Antonym table 
        stm.execute("CREATE TABLE IF NOT EXISTS Antonym("
                + "ant_id INTEGER PRIMARY KEY,"
                + "def_id INTEGER NOT NULL,"
                + "ant TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id))");
        stm = con.createStatement();
        // Extra table 
        stm.execute("CREATE TABLE IF NOT EXISTS Extra("
                + "extra_id INTEGER PRIMARY KEY,"
                + "def_id INTEGER NOT NULL,"
                + "extra TEXT NOT NULL,"
                + "FOREIGN KEY(def_id) REFERENCES Definition(def_id))");
    }

    private boolean bdExists() {
        File file = new File(filesPath + "/" + dbFileName + ".db");
        if (file.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean insertSynAntExt(String table, ArrayList<String> values, int defId) throws SQLException {
        boolean success = true;
        String column = table.equals("Extra") ? "extra"
                : table.equals("Synonym") ? "syn"
                : "ant";
        for (String value : values) {
            PreparedStatement stmInsertExtra = con
                    .prepareStatement(String.format("INSERT INTO %s(def_id, %s)"
                            + "VALUES(?, ?)", table, column));
            stmInsertExtra.setInt(1, defId);
            stmInsertExtra.setString(2, value);
            success &= !stmInsertExtra.execute();
        }
        return success;
    }
}
