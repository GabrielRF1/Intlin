/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.srs.CardContent;
import ine.ufsc.srs.Content;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class CardFaceController implements Initializable {

    @FXML
    private VBox mainFace;
    @FXML
    private Label ContentLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setCardContent(CardContent cardContent, Pos pos) {
        String face = "";
        for (int i = 0; i < cardContent.size(); i++) {
            Content content = cardContent.getContent(i);
            switch (content.getType()) {
                case text:
                    face += (String) content.getElement() + "\n";
                    break;
                case audio:
                    break;
                case image:
                    break;
            }
        }
        System.out.println("ine.ufsc.intlin.CardFaceController.setCardContent(): "+face);
        ContentLabel.setText(face);
        ContentLabel.setAlignment(pos);
    }

}
