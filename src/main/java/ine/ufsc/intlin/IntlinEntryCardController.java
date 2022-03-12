/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import ine.ufsc.srs.Card;
import ine.ufsc.srs.CardContent;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class IntlinEntryCardController implements Initializable {

    @FXML
    private VBox defsRegion;
    @FXML
    private Label wordLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label wordClassLabel;
    @FXML
    private Label altLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setDefinitions(List<Node> defFragments) {
        defFragments.forEach(defFragment -> {
            defsRegion.getChildren().add(defFragment);
        });
    }

    public void setWord(String word) {
        wordLabel.setText(word);
    }

    public void setAlternatives(String alts) {
        if (alts.equals("[]")) {
            return;
        }
        altLabel.setText(alts);
    }

    public void setGender(String gender) {
        if (gender == null) {
            return;
        }
        genderLabel.setText(String.format("(%s)", gender));
    }

    public void setWordClass(String wordClass) {
        wordClassLabel.setText(wordClass);
    }

    public void saveEntryToFlashCard() {
        CardContent frontContent = new CardContent();
        frontContent.addText(wordLabel.getText());
        CardContent backContent = new CardContent();
        String firstLine = wordLabel.getText();
        if (!genderLabel.getText().equals("()")) {
            firstLine += " " + genderLabel.getText();
        }
        firstLine += ": " + wordClassLabel.getText();
        backContent.addText(firstLine);

        defsRegion.getChildren().forEach((child) -> {
            Label def = (Label) child.getScene().lookup("#definitionLabel");
            backContent.addText(def.getText());
        });
        
        Card card = new Card(frontContent, backContent);
        
        try {
            Controller.instance.tryAndCreateDeck("Vocabulary");
            Controller.instance.addCardToDeck("Vocabulary", card);
        } catch (SQLException ex) {
            Logger.getLogger(IntlinEntryCardController.class.getName()).log(Level.SEVERE, null, ex);
            //tratar
        }
    }
}
