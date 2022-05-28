/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.subtitle;

/**
 *
 * @author Gabriel
 */
public class BadlyFomattedSubtitleFileException extends Exception{

    public BadlyFomattedSubtitleFileException(String errorMessage) {
        super(errorMessage);
    }
        
}
