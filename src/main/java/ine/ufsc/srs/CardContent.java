/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Gabriel
 */
public class CardContent {
    private ArrayList<Object> contents = new ArrayList<>();
    
    public Object getContent(int index) {
        return contents.get(index);
    }
    
    public void addText(String text) {
        contents.add(text);
    }
    
    public void addImage(File img) {
        contents.add(img);
    }
    
    public void addAudio(File audio) {
        contents.add(audio);
    }
}
