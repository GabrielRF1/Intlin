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
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            JSONArray json = new JSONArray(content);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                PreparedStatement stm = con.prepareStatement("INSERT OR "
                        + "IGNORE INTO Word(word, word_class, gender) "
                        + "VALUES(?, ?, ?)");
                for (String keyStr : jsonObject.keySet()) {
                    Object keyvalue = jsonObject.get(keyStr);
                    switch (keyStr) {
                        case "word":
                        case "word_class":
                        case "gender":
                            int index = keyStr.equals("word") ? 1 : 
                                    keyStr.equals("word_class") ? 2 : 3;
                            stm.setString(index, (String) keyvalue);
                        default:
                            break;
                    }
                }
                stm.execute();
            }
            //System.out.println("");
        }
    }
}
