/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gabriel
 */
public class CCCedictParserTest {

    static CCCedictParser instance;
    static Connection con;
    static ArrayList<File> files = new ArrayList<>();

    public CCCedictParserTest() {
    }

    private static void setUpDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:testDict/dbtestCCCedict.db");
        Statement stm = con.createStatement();
        // Word table 
        stm.execute("CREATE TABLE IF NOT EXISTS Word("
                + "word_id INTEGER PRIMARY KEY,"
                + "traditional TEXT NOT NULL,"
                + "simplified TEXT NOT NULL,"
                + "reading TEXT NOT NULL)");
        stm = con.createStatement();
        // Definition table
        stm.execute("CREATE TABLE IF NOT EXISTS Definition("
                + "def_id INTEGER PRIMARY KEY,"
                + "definition TEXT NOT NULL,"
                + "word_id INTEGER NOT NULL,"
                + "FOREIGN KEY(word_id) REFERENCES Word(word_id))");
    }

    private static File setUpFile() throws IOException {
        String content = "完璧 完璧 [wan2 bi4] /flawless piece of jade/(fig.) perfect person or thing/virgin/to return sth intact/\n"
                + "完璧之身 完璧之身 [wan2 bi4 zhi1 shen1] /undefiled (girl)/virgin/(of computer system) clean/uncorrupted/\n"
                + "完畢 完毕 [wan2 bi4] /to finish/to end/to complete/\n"
                + "完縣 完县 [Wan2 xian4] /Wan former county, now Shunping county 順平縣|顺平县[Shun4 ping2 xian4] in Baoding 保定[Bao3 ding4], Hebei/\n"
                + "完美 完美 [wan2 mei3] /perfect/perfection/perfectly/\n"
                + "笓 笓 [pi2] /to comb/fine-toothed comb/trap for prawns/\n"
                + "笙 笙 [sheng1] /sheng, a free reed wind instrument with vertical bamboo pipes/\n"
                + "笙歌 笙歌 [sheng1 ge1] /music and song (formal writing)/\n"
                + "笙管 笙管 [sheng1 guan3] /pipes of a panpipe/";
        File resFile = new File("testDict/test_1.u8");
        resFile.createNewFile();
        FileWriter fw = new FileWriter(resFile.getPath());
        fw.write(new String(content.getBytes(StandardCharsets.UTF_16)));
        fw.close();
        return resFile;
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
        setUpDB();
        File file = setUpFile();
        files.add(file);
        instance = new CCCedictParser(con);
        instance.doParsing(files);
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
        con.close();
        File dbFile = new File("testDict/dbtestCCCedict.db");
        dbFile.delete();
        files.forEach(file -> {
            file.delete();
        });
    }

    /**
     * Test of doParsing method, of class CCCedictParser.
     */
    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectAmount() {
        try {
            int expectedAmount = 9;
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) as total FROM Word");
            int actualAmount = rs.getInt("total");
            assertEquals(actualAmount, expectedAmount);
        } catch (SQLException ex) {
            fail("Exception thrown: " + ex.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectTraditional() {
        try {
            String expected = "完縣";
            Statement stm = con.createStatement();
            //System.out.println();
            String sql = "SELECT w.traditional FROM Word w where w.simplified = \'完县\'";
            ResultSet rs = stm.executeQuery(sql);
            String actual = rs.getString("traditional");
            assertEquals(actual, expected);
        } catch (SQLException ex) {
            fail("Exception thrown: " + ex.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectDefinitions() {
        try {
            var expected = new ArrayList<String>();
            expected.add("undefiled (girl)");
            expected.add("virgin");
            expected.add("(of computer system) clean");
            expected.add("uncorrupted");
            Statement stm = con.createStatement();
            //System.out.println();
            String sql = "SELECT d.definition FROM Word w join Definition "
                    + "d on w.word_id == d.word_id where w.simplified = \'完璧之身\'";
            ResultSet rs = stm.executeQuery(sql);
            var actual = new ArrayList<String>();
            while (rs.next()) {
                actual.add(rs.getString("definition"));
            }
            assertEquals(actual, expected);
        } catch (SQLException ex) {
            fail("Exception thrown: " + ex.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    public void testDoParsingYieldsCorrectPinyin() {
        try {
            var expected = "sheng1 guan3";
            Statement stm = con.createStatement();
            //System.out.println();
            String sql = "SELECT w.reading FROM Word w where w.traditional= \'笙管\'";
            ResultSet rs = stm.executeQuery(sql);
            var actual = rs.getString("reading");
            assertEquals(actual, expected);
        } catch (SQLException ex) {
            fail("Exception thrown: " + ex.getMessage());
        }
    }
}
