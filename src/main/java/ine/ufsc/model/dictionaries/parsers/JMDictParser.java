/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries.parsers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.sql.PreparedStatement;
import org.xml.sax.SAXException;

/**
 *
 * @author Gabriel
 */
public class JMDictParser implements DictParser {

    private final Connection con;
    private final DocumentBuilder builder;
    private final PreparedStatement wordStm;
    private final PreparedStatement KElementStm;
    private final int commitMark = 1000;

    JMDictParser(Connection con) throws SQLException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.builder = factory.newDocumentBuilder();
        this.con = con;
        this.wordStm = con.prepareStatement("INSERT OR IGNORE INTO Word(word_id)"
                + "VALUES(?)");
        this.KElementStm = con.prepareStatement("INSERT OR IGNORE INTO KElement"
                + "(priority, kanji, word_id)"
                + "VALUES(?, ?, ?)");
    }

    @Override
    public void doParsing(ArrayList<File> files) throws IOException, SQLException {
        con.setAutoCommit(false);
        for (File file : files) {
            try {
                Document doc = builder.parse(file);
                NodeList nodes = doc.getChildNodes();
                Element JMDict = (Element) nodes.item(nodes.getLength() - 1);
                NodeList entries = JMDict.getElementsByTagName("entry");
                for (int i = 0; i < entries.getLength(); i++) {
                    Element entry = (Element) entries.item(i);
                    parseEntry(entry);
                    if (i % commitMark == 0) {
                        executeBatches();
                    }
                }
                executeBatches();
            } catch (SAXException ex) {
                throw new IOException("Could not parse file");
            }
        }
    }

    private void parseEntry(Element entry) throws SQLException {
        Node entSeq = entry.getElementsByTagName("ent_seq").item(0);
        int wordId = Integer.parseInt(entSeq.getTextContent());
        this.wordStm.setInt(1, wordId);
        this.wordStm.addBatch();

        NodeList kElements = entry.getElementsByTagName("k_ele");
        for (int i = 0; i < kElements.getLength(); i++) {
            Node kEle = kElements.item(i);
            parseKElement(kEle, wordId);
        }
    }

    private void parseKElement(Node kEle, int wordId) throws SQLException {
        NodeList kEleChildren = kEle.getChildNodes();
        int priority = 0;
        for (int j = 0; j < kEleChildren.getLength(); j++) {
            Node kEleChild = kEleChildren.item(j);
            switch (kEleChild.getNodeName()) {
                case "keb":
                    String keb = kEleChild.getTextContent();
                    KElementStm.setString(2, keb);
                    break;
                default:
                    continue;
            }
            KElementStm.setInt(1, priority);
            KElementStm.setInt(3, wordId);
            KElementStm.addBatch();
        }
    }

    private void executeBatches() throws SQLException {
        wordStm.executeBatch();
        KElementStm.executeBatch();
        con.commit();
    }
}
