/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import ine.ufsc.srs.Card;
import ine.ufsc.srs.CardContent;
import ine.ufsc.srs.Content;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class CreateCardViewController implements Initializable {

    private String deckName;
    @FXML
    private Button cancelButton;
    @FXML
    private TextArea cardFrontText;
    @FXML
    private TextArea cardBackText;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setDeckName(String name) {
        deckName = name;
    }

    public void dispose() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        
        stage.close();
    }

    public void createCard() {        
        CardContent frontContent = new CardContent();
        frontContent.addText(cardFrontText.getText());
        CardContent backContent = new CardContent();
        backContent.addText(cardBackText.getText());
        
        Card Card = new Card(frontContent, backContent);
        
        try {
            Controller.instance.addCardToDeck(deckName, Card);
            dispose();
        } catch (SQLException ex) {
            Logger.getLogger(CreateCardViewController.class.getName()).log(Level.SEVERE, null, ex);
            //tratar
        }
        
    }
}
