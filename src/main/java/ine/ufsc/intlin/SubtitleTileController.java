/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class SubtitleTileController implements Initializable {
    @FXML
    private CheckBox checkbox;
    
    @FXML
    private Label lineLabel;
    
    private int id = -1;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void setLine(String line, int id) {
        lineLabel.setText(line);
        this.id = id;
    }
    
    public void onCheckBoxClicked() {
        if(checkbox.isSelected()) {
             Controller.instance.saveLine(lineLabel.getText(), id);
        } else {
             Controller.instance.removeLineIfSaved(id);
        }
    }
    
}


















