/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries.parsers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class YomichanParserTest {

    static YomichanParser instance;
    static Connection con;
    static ArrayList<File> files;

    public YomichanParserTest() {
    }

    private static void setUpDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:testDict"+File.separator+"dbYomichanTest.db");
        Statement stm = con.createStatement();
        //create tables
    }
    
    private static File setUpFile() throws IOException {
        File resFile = new File("testDict"+File.separator+"dbYomichanTest"+File.separator+"yomichanTest.json");
        return resFile;
    }
    
    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws ClassNotFoundException, SQLException, IOException {
        setUpDB();
        File file = setUpFile();
        files.add(file);
        instance = new YomichanParser(con);
        instance.doParsing(files);
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws IOException, SQLException {
        con.close();
        File dbFile = new File("testDict"+File.separator+"dbYomichanTest.db");
        dbFile.delete();
    }

    /**
     * Test of doParsing method, of class YomichanParser.
     */
    @org.junit.jupiter.api.Test
    public void testDoParsing() throws Exception {
        System.out.println("doParsing");
        ArrayList<File> files = null;
        instance.doParsing(files);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
