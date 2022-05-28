/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.subtitle;

import ine.ufsc.utils.TimeStamp;

/**
 *
 * @author Gabriel
 */
public class Subtitle {
    final int index;
    final TimeStamp start;
    final TimeStamp end;
    final String line;

    public Subtitle(int index, TimeStamp start, TimeStamp end, String line) {
        this.index = index;
        this.start = start;
        this.end = end;
        this.line = line;
    }

    public int getIndex() {
        return index;
    }

    public TimeStamp getStart() {
        return start;
    }

    public TimeStamp getEnd() {
        return end;
    }

    public String getLine() {
        return line;
    }
    
    @Override
    public String toString() {
        return String.format("%d\n%s --> %s\n%s", index, start.toString(), end.toString(), line);
    }
}
