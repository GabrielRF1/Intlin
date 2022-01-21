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
import java.util.HashMap;
import java.util.Map;
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
    private final PreparedStatement posStm;
    private final PreparedStatement defPosStm;
    private final PreparedStatement dialStm;
    private final PreparedStatement defDialStm;
    private final PreparedStatement fieldStm;
    private final PreparedStatement defFieldStm;
    private final PreparedStatement kInfStm;
    private final PreparedStatement kInfKeleStm;
    private final PreparedStatement rInfStm;
    private final PreparedStatement rInfReleStm;
    private final int commitMark = 1000;

    private int curDef = 0;
    private int curKele = 0;
    private int curRele = 0;
    private int latestPosId = 0;
    private int latestDialId = 0;
    private int latestFieldId = 0;
    private int latestKIndId = 0;
    private int latestRIndId = 0;
    
    private final Map<String, Integer> posSet = new HashMap<>();
    private final Map<String, Integer> dialSet = new HashMap<>();
    private final Map<String, Integer> fieldSet = new HashMap<>();
    private final Map<String, Integer> kInfSet = new HashMap<>();
    private final Map<String, Integer> rInfSet = new HashMap<>();

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
        this.posStm = con.prepareStatement("INSERT OR IGNORE INTO PartOfSpeech(pos)"
                + "VALUES(?)");
        this.defPosStm = con.prepareStatement("INSERT OR IGNORE INTO DefPos"
                + "(def_id, pos_id)"
                + "VALUES(?, ?)");
        this.dialStm = con.prepareStatement("INSERT OR IGNORE INTO Dialect(dial)"
                + "VALUES(?)");
        this.defDialStm = con.prepareStatement("INSERT OR IGNORE INTO DefDial"
                + "(def_id, dial_id)"
                + "VALUES(?, ?)");
        this.fieldStm = con.prepareStatement("INSERT OR IGNORE INTO Field(field)"
                + "VALUES(?)");
        this.defFieldStm = con.prepareStatement("INSERT OR IGNORE INTO DefField"
                + "(def_id, field_id)"
                + "VALUES(?, ?)");
        this.kInfStm = con.prepareStatement("INSERT OR IGNORE INTO KanjiInfo(info)"
                + "VALUES(?)");
        this.kInfKeleStm = con.prepareStatement("INSERT OR IGNORE INTO KElementInfo"
                + "(k_id, k_info_id)"
                + "VALUES(?, ?)");
        this.rInfStm = con.prepareStatement("INSERT OR IGNORE INTO ReadingInfo(info)"
                + "VALUES(?)");
        this.rInfReleStm = con.prepareStatement("INSERT OR IGNORE INTO RElementInfo"
                + "(r_id, r_info_id)"
                + "VALUES(?, ?)");
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
            curKele++;
            Node kEle = kElements.item(i);
            parseKElement(kEle, wordId);
        }

        NodeList rElements = entry.getElementsByTagName("r_ele");
        for (int i = 0; i < rElements.getLength(); i++) {
            curRele++;
            Node rEle = rElements.item(i);
            parseRElement(rEle, wordId);
        }

        NodeList senseElements = entry.getElementsByTagName("sense");
        for (int i = 0; i < senseElements.getLength(); i++) {
            defStm.setInt(2, wordId);
            curDef++;
            Node sense = senseElements.item(i);
            parseSense(sense);
            defStm.addBatch();
        }
    }

    private void parseKElement(Node kEle, int wordId) throws SQLException {
        NodeList kEleChildren = kEle.getChildNodes();
        int priority = 100000;
        for (int j = 0; j < kEleChildren.getLength(); j++) {
            Node kEleChild = kEleChildren.item(j);
            switch (kEleChild.getNodeName()) {
                case "keb":
                    String keb = kEleChild.getTextContent();
                    kElementStm.setString(2, keb);
                    break;
                case "ke_pri":
                    int prio = parsePriority(kEleChild.getTextContent());
                    if (prio < priority) {
                        priority = prio;
                    }
                    break;
                case "ke_inf":
                    String inf = kEleChild.getTextContent();
                    if(!kInfSet.containsKey(inf)){
                        latestKIndId++;
                        kInfSet.put(inf, latestKIndId);
                        kInfStm.setString(1, inf);
                        kInfStm.addBatch();
                    }
                    kInfKeleStm.setInt(1, curKele);
                    kInfKeleStm.setInt(2, kInfSet.get(inf));
                    kInfKeleStm.addBatch();
                    break;
            }
        }
        kElementStm.setInt(1, priority);
        kElementStm.setInt(3, wordId);
        kElementStm.addBatch();
    }

    private void parseRElement(Node rEle, int wordId) throws SQLException {
        NodeList rEleChildren = rEle.getChildNodes();
        int priority = 100000;
        for (int j = 0; j < rEleChildren.getLength(); j++) {
            Node rEleChild = rEleChildren.item(j);
            switch (rEleChild.getNodeName()) {
                case "reb":
                    String reb = rEleChild.getTextContent();
                    rElementStm.setString(2, reb);
                    break;
                case "re_pri":
                    int prio = parsePriority(rEleChild.getTextContent());
                    if (prio < priority) {
                        priority = prio;
                    }
                    break;
                case "re_inf":
                    String inf = rEleChild.getTextContent();
                    if(!rInfSet.containsKey(inf)){
                        latestRIndId++;
                        rInfSet.put(inf, latestRIndId);
                        rInfStm.setString(1, inf);
                        rInfStm.addBatch();
                    }
                    rInfReleStm.setInt(1, curRele);
                    rInfReleStm.setInt(2, rInfSet.get(inf));
                    rInfReleStm.addBatch();
                    break;
            }
        }
        rElementStm.setInt(1, priority);
        rElementStm.setInt(3, wordId);
        rElementStm.addBatch();
    }

    private int parsePriority(String priority) {
        switch (priority) {
            case "news1":
            case "ichi1":
            case "spec1":
            case "gai1":
                return 1;
            case "news2":
            case "ichi2":
            case "spec2":
            case "gai2":
                return 2;
            default:
                String nf = priority.substring(0, 1);
                if (nf.equals("nf")) {
                    int prio = Integer.parseInt(priority.substring(2));
                    if (prio <= 24) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    return 3;
                }
        }
    }

    private void parseSense(Node sense) throws SQLException {
        NodeList senseChildren = sense.getChildNodes();
        for (int j = 0; j < senseChildren.getLength(); j++) {
            Node senseChild = senseChildren.item(j);
            switch (senseChild.getNodeName()) {
                case "gloss":
                    parseGloss(senseChild);
                    break;
                case "s_inf":
                    String info = senseChild.getTextContent();
                    defStm.setString(1, info);
                    break;
                case "pos":
                    String pos = senseChild.getTextContent();
                    if(!posSet.containsKey(pos)){
                        latestPosId++;
                        posSet.put(pos, latestPosId);
                        posStm.setString(1, pos);
                        posStm.addBatch();
                    }
                    defPosStm.setInt(1, curDef);
                    defPosStm.setInt(2, posSet.get(pos));
                    defPosStm.addBatch();
                    break;
                case "dial":
                    String dial = senseChild.getTextContent();
                    if(!dialSet.containsKey(dial)){
                        latestDialId++;
                        dialSet.put(dial, latestDialId);
                        dialStm.setString(1, dial);
                        dialStm.addBatch();
                    }
                    defDialStm.setInt(1, curDef);
                    defDialStm.setInt(2, dialSet.get(dial));
                    defDialStm.addBatch();
                    break;
                case "field":
                    String field = senseChild.getTextContent();
                    if(!fieldSet.containsKey(field)){
                        latestFieldId++;
                        fieldSet.put(field, latestFieldId);
                        fieldStm.setString(1, field);
                        fieldStm.addBatch();
                    }
                    defFieldStm.setInt(1, curDef);
                    defFieldStm.setInt(2, fieldSet.get(field));
                    defFieldStm.addBatch();
                    break;
            }
        }

    }

    private void parseGloss(Node glossNode) throws SQLException {
        String gloss = glossNode.getTextContent();
        if (glossNode.hasAttributes()
                && glossNode.getAttributes().getNamedItem("g_type") != null) {
            NamedNodeMap attrs = glossNode.getAttributes();
            String type = attrs.getNamedItem("g_type").getTextContent();
            glossStm.setString(2, type);
        }
        glossStm.setString(1, gloss);
        glossStm.setInt(3, curDef);
        glossStm.addBatch();
    }

    private void executeBatches() throws SQLException {
        wordStm.executeBatch();
        kElementStm.executeBatch();
        rElementStm.executeBatch();
        defStm.executeBatch();
        glossStm.executeBatch();
        posStm.executeBatch();
        defPosStm.executeBatch();
        dialStm.executeBatch();
        defDialStm.executeBatch();
        fieldStm.executeBatch();
        defFieldStm.executeBatch();
        kInfStm.executeBatch();
        kInfKeleStm.executeBatch();
        rInfStm.executeBatch();
        rInfReleStm.executeBatch();
        con.commit();
    }

}
