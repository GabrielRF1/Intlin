/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class DeckFragmentController implements Initializable {

    @FXML
    private Label deckName;
    @FXML
    private Label reviewCount;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setData(String name, int count) {
        deckName.setText(name);
        reviewCount.setText(String.valueOf(count));
    }

    public void openReviewsWindow() throws IOException {
        try {
            FXMLLoader reviewScreenloader = new FXMLLoader(App.class.getResource("ReviewScreen.fxml"));
            Parent reviewNode = reviewScreenloader.load();
            ReviewScreenController reviewScreenControl = reviewScreenloader.getController();

            reviewScreenControl.setCards(Controller.instance.getDecksReview(deckName.getText()), deckName.getText());

            Scene srsScene = new Scene(reviewNode);
            Stage newWindow = new Stage();
            newWindow.setTitle("Review");
            newWindow.setScene(srsScene);
            newWindow.setResizable(false);
            newWindow.setMaximized(true);
            newWindow.showAndWait();
        } catch (SQLException ex) {
            Logger.getLogger(DeckFragmentController.class.getName()).log(Level.SEVERE, null, ex);
            //tratar
        }
    }

    public void createCard() throws IOException {
        FXMLLoader createCardloader = new FXMLLoader(App.class.getResource("CreateCardView.fxml"));
        Parent createCardNode = createCardloader.load();
        CreateCardViewController CreateCardControl = createCardloader.getController();

        CreateCardControl.setDeckName(deckName.getText());

        Scene srsScene = new Scene(createCardNode);
        Stage newWindow = new Stage();
        newWindow.setTitle("Create Card");
        newWindow.setResizable(false);
        newWindow.setScene(srsScene);
        newWindow.showAndWait();
    }

    public void highlightLabel() {
        deckName.setStyle("-fx-background-color: #76c699;");
    }

    public void removeHighlightLabel() {
        deckName.setStyle("-fx-background-color: invisible;");;
    }
}
