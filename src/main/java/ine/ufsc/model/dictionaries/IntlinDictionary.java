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
        public int wordId;
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
                .prepareStatement("SELECT a.alt_id, a.alt AS alternative "
                        + "FROM Word w INNER JOIN "
                        + "Alternative a on a.word_id = w.word_id "
                        + "where w.word = ?");
        stm.setString(1, word);
        return stm.executeQuery();
    }

    public ResultSet searchExtras(String definition) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT e.extra_id, e.extra "
                        + "FROM Definition d INNER JOIN "
                        + "Extra e on d.def_id = e.def_id "
                        + "where d.def = ?");
        stm.setString(1, definition);
        return stm.executeQuery();
    }

    public ResultSet searchAntonyms(String definition) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT a.ant_id, a.ant "
                        + "FROM Definition d INNER JOIN "
                        + "Antonym a on d.def_id = a.def_id "
                        + "WHERE d.def = ?");
        stm.setString(1, definition);
        return stm.executeQuery();
    }

    public ResultSet searchSynonyms(String definition) throws SQLException {
        PreparedStatement stm = con
                .prepareStatement("SELECT s.syn_id, s.syn "
                        + "FROM Definition d INNER JOIN "
                        + "Synonym s on d.def_id = s.def_id "
                        + "where d.def = ?");
        stm.setString(1, definition);
        return stm.executeQuery();
    }

    public boolean addSynonym(int defId, String syn) throws SQLException {
        ArrayList<String> values = new ArrayList<>();
        values.add(syn);
        return insertSynAntExt("Synonym", values, defId);
    }

    public boolean addAntonym(int defId, String ant) throws SQLException {
        ArrayList<String> values = new ArrayList<>();
        values.add(ant);
        return insertSynAntExt("Antonym", values, defId);
    }

    public boolean addExtra(int defId, String extra) throws SQLException {
        ArrayList<String> values = new ArrayList<>();
        values.add(extra);
        return insertSynAntExt("Extra", values, defId);
    }

    @Override
    public boolean addDefinition(Object contents) throws SQLException {
        boolean success = true;
        IntlinInfo info = (IntlinInfo) contents;
        con.setAutoCommit(false);
        PreparedStatement wordStm = con.prepareStatement("SELECT word_id FROM Word WHERE word=?");
        wordStm.setString(1, info.word);
        ResultSet WordIdRS = wordStm.executeQuery();
        int wordId;
        if (WordIdRS.isClosed()) {
            success &= addWord(contents);
            wordId = con.prepareStatement("SELECT last_insert_rowid() AS id;")
                    .executeQuery().getInt("id");
        } else {
            wordId = WordIdRS.getInt("word_id");
        }

        success &= insertDef(wordId, info);

        int defId = con.prepareStatement("SELECT last_insert_rowid() AS id;")
                .executeQuery().getInt("id");

        success &= insertDefChildren(defId, info);

        con.commit();
        con.setAutoCommit(true);
        return success;
    }

    @Override
    public boolean addAlternative(int wordId, String alt) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO Alternative(word_id, alt) values(?,?)");
        stm.setInt(1, wordId);
        stm.setString(2, alt);
        int res = stm.executeUpdate();
        System.out.println("res: " + res);
        return res != 0;
    }

    @Override
    public boolean removeDefinition(int definitionId) throws SQLException {
        boolean success = true;
        PreparedStatement stmSyn = con.prepareStatement("SELECT syn_id FROM Synonym WHERE def_id=?");
        stmSyn.setInt(1, definitionId);
        ResultSet synS = stmSyn.executeQuery();
        if (!synS.isClosed()) {
            while (synS.next()) {
                success &= removeSynonym(synS.getInt("syn_id"));
            }
        }

        PreparedStatement stmAnt = con.prepareStatement("SELECT ant_id FROM Antonym WHERE def_id=?");
        stmAnt.setInt(1, definitionId);
        ResultSet antS = stmAnt.executeQuery();
        if (!antS.isClosed()) {
            while (antS.next()) {
                success &= removeAntonym(antS.getInt("ant_id"));
            }
        }

        PreparedStatement stmExtra = con.prepareStatement("SELECT extra_id FROM Extra WHERE def_id=?");
        stmExtra.setInt(1, definitionId);
        ResultSet extraS = stmExtra.executeQuery();
        if (!extraS.isClosed()) {
            while (extraS.next()) {
                success &= removeExtra(extraS.getInt("extra_id"));
            }
        }

        PreparedStatement stm = con.prepareStatement("DELETE FROM Definition WHERE def_id=?");
        stm.setInt(1, definitionId);
        success &= (stm.executeUpdate() != 0);
        return success;
    }

    public boolean removeSynonym(int SynId) throws SQLException {
        PreparedStatement stm = con.prepareStatement("DELETE FROM Synonym WHERE syn_id=?");
        stm.setInt(1, SynId);
        return (stm.executeUpdate() != 0);
    }

    public boolean removeAntonym(int antId) throws SQLException {
        PreparedStatement stm = con.prepareStatement("DELETE FROM Antonym WHERE ant_id=?");
        stm.setInt(1, antId);
        return (stm.executeUpdate() != 0);
    }

    public boolean removeExtra(int extraId) throws SQLException {
        PreparedStatement stm = con.prepareStatement("DELETE FROM Extra WHERE extra_id=?");
        stm.setInt(1, extraId);
        return (stm.executeUpdate() != 0);
    }

    @Override
    public boolean removeWord(int wordId) throws SQLException {
        boolean success = true;
        PreparedStatement defsStm = con.prepareStatement("SELECT def_id FROM Definition WHERE word_id=?");
        defsStm.setInt(1, wordId);
        ResultSet defs = defsStm.executeQuery();
        if (defs.isClosed()) {
            return false;
        } else {
            while (defs.next()) {
                success &= removeDefinition(defs.getInt("def_id"));
            }
        }
        PreparedStatement delStm = con.prepareStatement("DELETE FROM Word WHERE word_id=?");
        delStm.setInt(1, wordId);
        success &= (delStm.executeUpdate() != 0);

        return success;
    }

    @Override
    public boolean updateWord(int wordId, String newText) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Word SET word = \'%s\' WHERE word_id=?", newText));
        stm.setInt(1, wordId);
        int res = stm.executeUpdate();
        return res != 0;
    }

    @Override
    public boolean updateAlt(int id, String newAltText) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Alternative SET alt = \'%s\' WHERE alt_id=?", newAltText));
        stm.setInt(1, id);
        int res = stm.executeUpdate();
        return res != 0;
    }

    @Override
    public boolean updateDef(int defId, String newText) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Definition SET def = \'%s\' WHERE def_id=?", newText));
        stm.setInt(1, defId);
        int res = stm.executeUpdate();
        return res != 0;
    }

    public boolean updateGender(int wordId, String newGender) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Word SET gender = \'%s\' WHERE word_id=?", newGender));
        stm.setInt(1, wordId);
        int res = stm.executeUpdate();
        return res != 0;
    }

    public boolean updateWordClass(int wordId, String newClass) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Word SET word_class = \'%s\' WHERE word_id=?", newClass));
        stm.setInt(1, wordId);
        int res = stm.executeUpdate();
        return res != 0;
    }

    public boolean updateSynonym(int synId, String newSyn) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Synonym SET syn = \'%s\' WHERE syn_id=?", newSyn));
        stm.setInt(1, synId);
        int res = stm.executeUpdate();
        return res != 0;
    }

    public boolean updateAntonym(int antId, String newAnt) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Antonym SET ant = \'%s\' WHERE ant_id=?", newAnt));
        stm.setInt(1, antId);
        int res = stm.executeUpdate();
        return res != 0;
    }

    public boolean updateExtra(int extraId, String newExtra) throws SQLException {
        PreparedStatement stm = con.prepareStatement(String.format("UPDATE Extra SET extra = \'%s\' WHERE extra_id=?", newExtra));
        stm.setInt(1, extraId);
        int res = stm.executeUpdate();
        return res != 0;
    }

    @Override
    protected boolean addWord(Object contents) throws SQLException {
        IntlinInfo info = (IntlinInfo) contents;
        PreparedStatement stmInsertWord = con
                .prepareStatement("INSERT INTO Word(word, word_class, gender) "
                        + "VALUES(?, ?, ?)");
        stmInsertWord.setString(1, info.word);
        stmInsertWord.setString(2, info.wordClass);
        stmInsertWord.setString(3, info.gender);

        return (stmInsertWord.executeUpdate() != 0);
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
        File file = new File(filesPath + File.separator + dbFileName + ".db");
        return file.length() > 0;
    }

    private boolean insertDef(int wordId, IntlinInfo info) throws SQLException {
        PreparedStatement stmInsertDef = con
                .prepareStatement("INSERT INTO Definition(def, word_id)"
                        + "VALUES(?, ?)");
        stmInsertDef.setString(1, info.def);
        stmInsertDef.setInt(2, wordId);
        return (stmInsertDef.executeUpdate() != 0);
    }

    private boolean insertDefChildren(int defId, IntlinInfo info) throws SQLException {
        boolean success = true;
        if (!info.syns.isEmpty()) {
            success &= insertSynAntExt("Synonym", info.syns, defId);
        }
        if (!info.ants.isEmpty()) {
            success &= insertSynAntExt("Antonym", info.ants, defId);
        }
        if (!info.extras.isEmpty()) {
            success &= insertSynAntExt("Extra", info.extras, defId);
        }

        return success;
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
            success &= (stmInsertExtra.executeUpdate() != 0);
        }
        return success;
    }
}
