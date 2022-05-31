/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Gabriel
 */
public class CardContent {

    private ArrayList<Content> contents = new ArrayList<>();

    public Content getContent(int index) {
        return contents.get(index);
    }

    public void addContent(Content content) {
        contents.add(content);
    }

    public void addText(String text) {
        Content cont = new Content(text, Content.Type.text);
        contents.add(cont);
    }

    public void addImage(File img) {
        Content cont = new Content(img, Content.Type.image);
        contents.add(cont);
    }

    public void addAudio(File audio) {
        Content cont = new Content(audio, Content.Type.audio);
        contents.add(cont);
    }

    public void addText(String text, int pos) {
        Content cont = new Content(pos, text, Content.Type.text);
        contents.add(cont);
    }

    public void addImage(File img, int pos) {
        Content cont = new Content(pos, img, Content.Type.image);
        contents.add(cont);
    }

    public void addAudio(File audio, int pos) {
        Content cont = new Content(pos, audio, Content.Type.audio);
        contents.add(cont);
    }

    public int size() {
        return contents.size();
    }

    public void sortContents() {
        Collections.sort(contents, (Content content2, Content content1) -> {
            Integer pos1 = content1.getPosition();
            Integer pos2 = content2.getPosition();
            return  pos2.compareTo(pos1);
        });
    }
}
