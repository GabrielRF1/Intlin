/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import ine.ufsc.intlin.utils.ModalDialog;
import ine.ufsc.srs.Card;
import ine.ufsc.srs.CardContent;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
        
        Card card = new Card(frontContent, backContent);
        
        try {
            Controller.instance.addCardToDeck(deckName, card);
            dispose();
        } catch (SQLException ex) {
            ModalDialog dialog = new ModalDialog(Alert.AlertType.ERROR, "Could not create the flashcard", "An error has occurred while trying to create the flashcard");
            dialog.show();
        }
    }
}
