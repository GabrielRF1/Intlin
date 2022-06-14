/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

/**
 *
 * @author Gabriel
 */
public class ModalDialog {
    final Dialog dialog;
    public ModalDialog(Alert.AlertType alertType, String title, String content) {
        dialog = new Alert(alertType);
        dialog.setTitle(title);
        dialog.setContentText(content);
    }
    
    public void show() {
        dialog.show();
    }

}
