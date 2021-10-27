/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            JSONArray json = new JSONArray(content);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                PreparedStatement stm = con.prepareStatement("INSERT OR "
                        + "IGNORE INTO Word(word, word_class, gender) "
                        + "VALUES(?, ?, ?)");
                JSONArray alternatives = null;
                JSONArray definitions = null;
                for (String keyStr : jsonObject.keySet()) {
                    Object keyvalue = jsonObject.get(keyStr);
                    switch (keyStr) {
                        case "word":
                        case "word_class":
                        case "gender":
                            int index = keyStr.equals("word") ? 1
                                    : keyStr.equals("word_class") ? 2 : 3;
                            stm.setString(index, (String) keyvalue);
                            break;
                        case "alt":
                            alternatives = (JSONArray) keyvalue;
                            break;
                        case "defs":
                            definitions = (JSONArray) keyvalue;
                            break;
                        default:
                    }
                }
                stm.execute();
                if (alternatives != null) {
                    parseAlternatives(alternatives, con);
                }
            }
        }
    }

    private void parseAlternatives(JSONArray alternatives, Connection con) throws SQLException {
        Statement stm = con.createStatement();
        ResultSet resSet = stm.executeQuery("select last_insert_rowid() as word_id");
        int word_id = resSet.getInt("word_id");
        System.out.println("WORD_ID: " + word_id);
        for (Object alternative : alternatives) {
            PreparedStatement prepStem = con
                    .prepareStatement("INSERT OR IGNORE INTO Extension VALUES (?)");
            prepStem.setString(1, (String) alternative);
            prepStem.execute();
            PreparedStatement prepStem2 = con
                    .prepareStatement("INSERT OR IGNORE INTO Alternative VALUES (?, ?)");
            prepStem2.setInt(1, word_id);
            prepStem2.setString(2, (String) alternative);
            prepStem2.execute();
        }
    }
}
