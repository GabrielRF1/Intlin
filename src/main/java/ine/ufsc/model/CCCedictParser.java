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
import java.util.Scanner;

/**
 *
 * @author Gabriel
 */
public class CCCedictParser implements DictParser {

    private final Connection con;
    private final PreparedStatement stmWord;
    private final PreparedStatement stmDefinition;
    private int bufferedCount = 0;

    public CCCedictParser(Connection con) throws SQLException {
        this.con = con;
        stmWord = con.prepareStatement("INSERT OR IGNORE INTO "
                + "Word(traditional, simplified, reading) values(?,?,?)");
        stmDefinition = con.prepareStatement("INSERT OR IGNORE INTO "
                + "Definition(definition, word_id) values(?,?)");
    }

    @Override
    public void doParsing(ArrayList<File> files) throws IOException, SQLException {
        con.setAutoCommit(false);
        for (File file : files) {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
            Scanner sc = new Scanner(content);
            sc.useDelimiter(" ");
            while (sc.hasNext()) {
                String trad = sc.next().strip();
                String simp = sc.next().strip();
                stmWord.setString(1, trad);
                stmWord.setString(2, simp);

                sc.useDelimiter("]");
                stmWord.setString(3, sc.next().substring(2));

                sc.useDelimiter("\n");
                String definitions = sc.next().substring(3);
                Scanner defSc = new Scanner(definitions);
                defSc.useDelimiter("/");
                while (defSc.hasNext()) {
                    String def = defSc.next().stripTrailing().stripLeading();
                    if (def.length() != 0) {
                        //System.out.println(def.length());
                        stmDefinition.setString(1, def);
                        stmDefinition.setInt(2, bufferedCount + 1);
                        stmDefinition.addBatch();
                    }
                }
                stmWord.addBatch();
                bufferedCount++;
                if (bufferedCount % 10000 == 0) {
                    stmWord.executeBatch();
                    stmDefinition.executeBatch();
                    con.commit();
                }
                sc.useDelimiter(" ");
            }
            stmWord.executeBatch();
            stmDefinition.executeBatch();
            con.commit();
        }
    }

}
