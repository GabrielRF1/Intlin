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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Gabriel
 */
public class IntlinParser implements DictParser {

    @Override
    public void doParsing(ArrayList<File> files, Connection con) throws IOException, SQLException {
        for (File file : files) {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())),
                    StandardCharsets.UTF_8);
            JSONArray json = new JSONArray(content);
            PreparedStatement stm = con.prepareStatement("INSERT OR "
                    + "IGNORE INTO Word(word, word_class, gender) "
                    + "VALUES(?, ?, ?)");
            con.setAutoCommit(false);
            for (int i = 0; i < json.length(); i++) {
                System.out.println(i);
                parseLine(json, con, i, stm);
                if (i % 1000 == 0) {
                    stm.executeBatch();
                    con.commit();
                }
            }
            stm.executeBatch();
            con.commit();
        }
    }

    private void parseLine(JSONArray json, Connection con, int index,
            PreparedStatement stm) throws SQLException {
        JSONObject jsonObject = json.getJSONObject(index);
//        JSONArray alternatives = null;
//        JSONArray definitions = null;
        for (String keyStr : jsonObject.keySet()) {
            Object keyValue = jsonObject.get(keyStr);
            switch (keyStr) {
                case "word":
                case "word_class":
                case "gender":
                    int pos = keyStr.equals("word") ? 1
                            : keyStr.equals("word_class") ? 2 : 3;
                    stm.setString(pos, (String) keyValue);
                    break;
                case "alt":
                    //alternatives = (JSONArray) keyValue;
                    break;
                case "defs":
                    //definitions = (JSONArray) keyValue;
                    break;
                default:
            }
        }
        stm.addBatch();
//        Statement stmLast = con.createStatement();
//        ResultSet resSet = stmLast.executeQuery("select last_insert_rowid() as word_id");
//        int word_id = resSet.getInt("word_id");
//        if (alternatives != null) {
//            parseAlternatives(alternatives, con, word_id);
//        }
//        parseDefinitons(definitions, con, word_id);
    }

//    private void parseAlternatives(JSONArray alternatives, Connection con, int word_id) throws SQLException {
//        for (Object alternative : alternatives) {
//            PreparedStatement prepStem = con
//                    .prepareStatement("INSERT OR IGNORE INTO Extension VALUES (?)");
//            prepStem.setString(1, (String) alternative);
//            prepStem.execute();
//            PreparedStatement prepStem2 = con
//                    .prepareStatement("INSERT OR IGNORE INTO Alternative VALUES (?, ?)");
//            prepStem2.setInt(1, word_id);
//            prepStem2.setString(2, (String) alternative);
//            prepStem2.execute();
//        }
//    }
//
//    private void parseDefinitons(JSONArray definitions, Connection con, int wordId) throws SQLException {
//        for (Iterator<Object> it = definitions.iterator(); it.hasNext();) {
//            JSONObject definition = (JSONObject) it.next();
//            JSONArray synonyms = null;
//            JSONArray antonyms = null;
//            JSONArray extras = null;
//            for (String keyStr : definition.keySet()) {
//                Object keyValue = definition.get(keyStr);
//                switch (keyStr) {
//                    case "_def":
//                        PreparedStatement prepStem = con.prepareStatement("INSERT OR IGNORE INTO "
//                                + "Definition(def, word_id) VALUES (?, ?)");
//                        prepStem.setString(1, (String) keyValue);
//                        prepStem.setInt(2, wordId);
//                        prepStem.execute();
//                        break;
//                    case "_synonyms":
//                        synonyms = (JSONArray) keyValue;
//                        break;
//                    case "antonyms":
//                        antonyms = (JSONArray) keyValue;
//                        break;
//                    case "extras":
//                        extras = (JSONArray) keyValue;
//                        break;
//                    default:
//                }
//            }
//            Statement stmLast = con.createStatement();
//            ResultSet resDefId = stmLast.executeQuery("select last_insert_rowid() as def_id");
//            int defId = resDefId.getInt("def_id");
//            if (synonyms != null) {
//                parseInterest(synonyms, con, defId, "Synonym");
//            }
//            if (antonyms != null) {
//                parseInterest(antonyms, con, defId, "Antonym");
//            }
//            if (extras != null) {
//                parseInterest(extras, con, defId, "Extra");
//            }
//        }
//    }
//
//    private void parseInterest(JSONArray interest, Connection con, int defId,
//            String interestString) throws SQLException {
//        for (Object in : interest) {
//            PreparedStatement prepStem2 = con
//                    .prepareStatement("INSERT OR IGNORE INTO Extension VALUES (?)");
//            prepStem2.setString(1, (String) in);
//            prepStem2.execute();
//            PreparedStatement prepStem3 = con
//                    .prepareStatement(String
//                            .format("INSERT OR IGNORE INTO %s VALUES (?, ?)", interestString));
//            prepStem3.setInt(1, defId);
//            prepStem3.setString(2, (String) in);
//            prepStem3.execute();
//        }
//    }
}
