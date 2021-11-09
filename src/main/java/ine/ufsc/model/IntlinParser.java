/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Gabriel
 */
public class IntlinParser implements DictParser {

    private int curId = 1;
    private int curDefId = 0;
    private final Connection con;
    private final PreparedStatement stmWord;
    private final PreparedStatement stmExtension;
    private final PreparedStatement stmAlt;
    private final PreparedStatement stmDef;
    private final PreparedStatement stmSyn;
    private final PreparedStatement stmAnt;
    private final PreparedStatement stmExt;

    public IntlinParser(Connection con) throws SQLException {
        this.con = con;
        stmWord = con.prepareStatement("INSERT OR "
                + "IGNORE INTO Word(word, word_class, gender) "
                + "VALUES(?, ?, ?)");
        stmExtension = con.prepareStatement("INSERT OR IGNORE INTO Extension "
                + "VALUES (?)");
        stmAlt = con.prepareStatement("INSERT OR IGNORE INTO Alternative "
                + "VALUES (?, ?)");
        stmDef = con.prepareStatement("INSERT OR IGNORE INTO "
                + "Definition(def, word_id) VALUES (?, ?)");
        stmSyn = con.prepareStatement("INSERT OR IGNORE INTO Synonym VALUES (?, ?)");
        stmAnt = con.prepareStatement("INSERT OR IGNORE INTO Antonym VALUES (?, ?)");
        stmExt = con.prepareStatement("INSERT OR IGNORE INTO Extra VALUES (?, ?)");
    }

    @Override
    public void doParsing(ArrayList<File> files) throws IOException, SQLException {
        for (File file : files) {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())),
                    StandardCharsets.UTF_8);
            JSONArray json = new JSONArray(content);
            con.setAutoCommit(false);
            for (int i = 0; i < json.length(); i++) {
                parseLine(json, i);
                curId++;
                if (i % 1000 == 0) {
                    execBatches();
                }
            }
            execBatches();
        }
    }

    private void execBatches() throws SQLException {
        stmWord.executeBatch();
        stmExtension.executeBatch();
        stmAlt.executeBatch();
        stmDef.executeBatch();
        stmSyn.executeBatch();
        stmAnt.executeBatch();
        stmExt.executeBatch();
        con.commit();
    }

    private void parseLine(JSONArray json, int index) throws SQLException {
        JSONObject jsonObject = json.getJSONObject(index);
        JSONArray alternatives = null;
        JSONArray definitions = null;
        for (String keyStr : jsonObject.keySet()) {
            Object keyValue = jsonObject.get(keyStr);
            switch (keyStr) {
                case "word":
                case "word_class":
                case "gender":
                    int pos = keyStr.equals("word") ? 1
                            : keyStr.equals("word_class") ? 2 : 3;
                    stmWord.setString(pos, (String) keyValue);
                    break;
                case "alt":
                    alternatives = (JSONArray) keyValue;
                    break;
                case "defs":
                    definitions = (JSONArray) keyValue;
                    break;
                default:
            }
        }
        stmWord.addBatch();
        if (alternatives != null) {
            parseAlternatives(alternatives, curId);
        }
        parseDefinitons(definitions, curId);
    }

    private void parseAlternatives(JSONArray alternatives, int wordId) throws SQLException {
        for (Object alternative : alternatives) {
            stmExtension.setString(1, (String) alternative);
            stmExtension.addBatch();
            stmAlt.setInt(1, wordId);
            stmAlt.setString(2, (String) alternative);
            stmAlt.addBatch();
        }
    }

    private void parseDefinitons(JSONArray definitions, int wordId) throws SQLException {
        for (Iterator<Object> it = definitions.iterator(); it.hasNext();) {
            JSONObject definition = (JSONObject) it.next();
            JSONArray synonyms = null;
            JSONArray antonyms = null;
            JSONArray extras = null;
            for (String keyStr : definition.keySet()) {
                Object keyValue = definition.get(keyStr);
                switch (keyStr) {
                    case "_def":
                        stmDef.setString(1, (String) keyValue);
                        stmDef.setInt(2, wordId);
                        stmDef.addBatch();
                        curDefId++;
                        break;
                    case "_synonyms":
                        synonyms = (JSONArray) keyValue;
                        break;
                    case "antonyms":
                        antonyms = (JSONArray) keyValue;
                        break;
                    case "extras":
                        extras = (JSONArray) keyValue;
                        break;
                    default:
                }
            }
            if (synonyms != null) {
                parseInterest(synonyms, curDefId, 0);
            }
            if (antonyms != null) {
                parseInterest(antonyms, curDefId, 1);
            }
            if (extras != null) {
                parseInterest(extras, curDefId, 2);
            }
        }
    }

    private void parseInterest(JSONArray interest, int defId,
            int interestCode) throws SQLException {
        for (Object in : interest) {
            stmExtension.setString(1, (String) in);
            stmExtension.addBatch();
            switch (interestCode) {
                case 0:
                    stmSyn.setInt(1, defId);
                    stmSyn.setString(2, (String) in);
                    stmSyn.addBatch();
                    break;
                case 1:
                    stmAnt.setInt(1, defId);
                    stmAnt.setString(2, (String) in);
                    stmAnt.addBatch();
                    break;
                case 2:
                    stmExt.setInt(1, defId);
                    stmExt.setString(2, (String) in);
                    stmExt.addBatch();
                    break;
            }
        }
    }
}
