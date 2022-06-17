/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.utils;

/**
 *
 * @author Gabriel
 */
public class Message {

    public static enum MessageType {
        UPDATE_SRS,
        SRS_HAS_BEEN_LOADED,
        UPDATE_LAST_DATE,
    }

    private final MessageType[] types;

    public Message(MessageType[] types) {
        this.types = types;
    }
    
    public Message(MessageType type) {
        types = new MessageType[1];
        this.types[0] = type;
    }

    public MessageType[] getTypes() {
        return types;
    }
    
    
}
