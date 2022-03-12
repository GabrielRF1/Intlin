/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.srs;

import java.io.File;

/**
 *
 * @author Gabriel
 */
public class Content {

    public static enum Type {
        audio,
        image,
        text,
    }

    private int position;
    private Integer id;
    private Object element;
    private final Type type;

    public Content(int position, Object element, Type type) {
        this.position = position;
        this.element = element;
        this.type = type;
    }

    public Content(int position, int id, String element, Type type) {
        this.position = position;
        this.id = id;
        this.element = type == Type.text ? element : new File(element);
        this.type = type;
    }

    public Content(Object element, Type type) {
        this.position = 0;
        this.element = element;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }

}
