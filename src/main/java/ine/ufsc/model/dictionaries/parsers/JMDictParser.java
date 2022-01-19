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
    private final PreparedStatement kElementStm;
    private final PreparedStatement rElementStm;
    private final PreparedStatement defStm;
    private final PreparedStatement glossStm;
    private final int commitMark = 1000;
    
    private int curDef = 0;

    JMDictParser(Connection con) throws SQLException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.builder = factory.newDocumentBuilder();
        this.con = con;
        this.wordStm = con.prepareStatement("INSERT OR IGNORE INTO Word(word_id)"
                + "VALUES(?)");
        this.kElementStm = con.prepareStatement("INSERT OR IGNORE INTO KElement"
                + "(priority, kanji, word_id)"
                + "VALUES(?, ?, ?)");
        this.rElementStm = con.prepareStatement("INSERT OR IGNORE INTO RElement"
                + "(priority, reading, word_id)"
                + "VALUES(?, ?, ?)");
        this.defStm = con.prepareStatement("INSERT OR IGNORE INTO Definition"
                + "(additional_info, word_id)"
                + "VALUES(?, ?)");
        this.glossStm = con.prepareStatement("INSERT OR IGNORE INTO Gloss"
                + "(gloss, type, def_id)"
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
        
        NodeList rElements = entry.getElementsByTagName("r_ele");
        for (int i = 0; i < rElements.getLength(); i++) {
            Node rEle = rElements.item(i);
            parseRElement(rEle, wordId);
        }
        
        NodeList senseElements = entry.getElementsByTagName("sense");
        for (int i = 0; i < senseElements.getLength(); i++) {
            defStm.setInt(2, wordId);
            curDef++;
            Node sense = senseElements.item(i);
            NodeList senseChildren = sense.getChildNodes();
            for (int j = 0; j < senseChildren.getLength(); j++) {
                Node senseChild = senseChildren.item(j);
                System.out.println("hmm: "+senseChild.getNodeName());
                switch(senseChild.getNodeName()) {
                    case "gloss":
                        String gloss = senseChild.getTextContent();
                        System.out.println("\thmm: "+gloss);
                        glossStm.setString(1, gloss);
                        glossStm.setInt(3, curDef);
                        glossStm.addBatch();
                        break;
                }
            }
            defStm.addBatch();
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
                    kElementStm.setString(2, keb);
                    break;
                default:
                    continue;
            }
            kElementStm.setInt(1, priority);
            kElementStm.setInt(3, wordId);
            kElementStm.addBatch();
        }
    }

    private void parseRElement(Node rEle, int wordId) throws SQLException {
        NodeList rEleChildren = rEle.getChildNodes();
        int priority = 0;
        for (int j = 0; j < rEleChildren.getLength(); j++) {
            Node rEleChild = rEleChildren.item(j);
            switch (rEleChild.getNodeName()) {
                case "reb":
                    String reb = rEleChild.getTextContent();
                    rElementStm.setString(2, reb);
                    break;
                default:
                    continue;
            }
            rElementStm.setInt(1, priority);
            rElementStm.setInt(3, wordId);
            rElementStm.addBatch();
        }
    }
    
    private void executeBatches() throws SQLException {
        wordStm.executeBatch();
        kElementStm.executeBatch();
        rElementStm.executeBatch();
        defStm.executeBatch();
        glossStm.executeBatch();
        con.commit();
    }

}
