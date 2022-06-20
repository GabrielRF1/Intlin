/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import ine.ufsc.intlin.utils.ModalDialog;
import ine.ufsc.srs.Card;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class ReviewScreenController implements Initializable {

    @FXML
    private ScrollPane cardFrontScrollRegion;
    @FXML
    private ScrollPane cardBackScrollRegion;
    @FXML
    private HBox buttonRegionHbox;
    @FXML
    private Button easyB;
    @FXML
    private Button goodB;
    @FXML
    private Button failB;
    @FXML
    private Button hardB;

    ArrayList<Card> reviewCards;
    HashSet<Card> reviewedCards;
    Card cur;
    int curIndex;

    String deckName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        curIndex = 0;
    }

    public void setCards(Set<Card> cards, String deck) throws IOException {
        reviewCards = new ArrayList<>(cards);
        reviewedCards = new HashSet<>();
        deckName = deck;
        if (reviewCards.isEmpty()) {
            buildEndCard();
            return;
        }
        nextCard();
    }

    public void buildEndCard() {
        cardFrontScrollRegion.setVisible(false);
        hardB.setVisible(false);
        failB.setVisible(false);
        easyB.setVisible(false);
        hardB.setDisable(true);
        failB.setDisable(true);
        easyB.setDisable(true);
        goodB.setText("No more reviews left");
        goodB.setOnAction((event) -> {
            Stage stage = (Stage) goodB.getScene().getWindow();
            stage.close();
        });
    }

    public void saveCards() {
        reviewedCards.forEach((card) -> {
            System.out.println("ine.ufsc.intlin.ReviewScreenController.saveCards()");
            try {
                Controller.instance.updateCards(card);
                Controller.instance.setAsReviewed(deckName, card);
            } catch (SQLException ex) {
                ModalDialog dialog = new ModalDialog(Alert.AlertType.ERROR, "Could not update flashcards", "An error has occurred while trying to save your reviews. You might have to review your cards again. We apologise for the inconvinience");
                dialog.show();
            }
        });
    }

    public void reviewFinished() {
        buildEndCard();
    }

    public void nextCard() throws IOException {
        if (curIndex >= reviewCards.size()) {
            reviewFinished();
            return;
        }
        cur = reviewCards.get(curIndex);
        FXMLLoader front = new FXMLLoader(App.class.getResource("cardFace.fxml"));
        FXMLLoader back = new FXMLLoader(App.class.getResource("cardFace.fxml"));

        Node cardFront = front.load();
        Node cardBack = back.load();

        CardFaceController frontControl = front.getController();
        CardFaceController backControl = back.getController();

        frontControl.setCardContent(cur.getFront(), Pos.TOP_CENTER);
        backControl.setCardContent(cur.getBack(), Pos.TOP_CENTER);

        cardFrontScrollRegion.setContent(cardFront);
        cardBackScrollRegion.setContent(cardBack);

        AnchorPane.setTopAnchor(cardFront, 0.0);
        AnchorPane.setRightAnchor(cardFront, 0.0);
        AnchorPane.setLeftAnchor(cardFront, 0.0);
        AnchorPane.setBottomAnchor(cardFront, 0.0);

        AnchorPane.setTopAnchor(cardBack, 0.0);
        AnchorPane.setRightAnchor(cardBack, 0.0);
        AnchorPane.setLeftAnchor(cardBack, 0.0);
        AnchorPane.setBottomAnchor(cardBack, 0.0);

        curIndex++;
    }

    public void onEasyPressed() throws IOException {
        LocalDate day = Controller.instance.answerCard(cur, Card.Difficulty.easy);
        onAnswerComplete(day);
    }

    public void onGoodPressed() throws IOException {
        LocalDate day = Controller.instance.answerCard(cur, Card.Difficulty.good);
        onAnswerComplete(day);
    }

    public void onHardPressed() throws IOException {
        LocalDate day = Controller.instance.answerCard(cur, Card.Difficulty.hard);
        onAnswerComplete(day);
    }

    public void onFailPressed() throws IOException {
        LocalDate day = Controller.instance.answerCard(cur, Card.Difficulty.fail);
        onAnswerComplete(day);
    }

    private void onAnswerComplete(LocalDate day) {
        if (day.equals(LocalDate.now())) {
            reviewCards.add(cur);
        }
        reviewedCards.add(cur);
        cardBackScrollRegion.setVisible(true);
        hardB.setVisible(false);
        failB.setVisible(false);
        easyB.setVisible(false);
        hardB.setDisable(true);
        failB.setDisable(true);
        easyB.setDisable(true);
        Label nextReviewDateLabel = new Label("Next review: " + formatDate(day));
        nextReviewDateLabel.setFont(new Font(16));
        buttonRegionHbox.getChildren().add(2, nextReviewDateLabel);

        goodB.setText("Ok");
        goodB.setOnAction((t) -> {
            cardBackScrollRegion.setVisible(false);
            buttonRegionHbox.getChildren().remove(2);
            hardB.setVisible(true);
            failB.setVisible(true);
            easyB.setVisible(true);
            hardB.setDisable(false);
            failB.setDisable(false);
            easyB.setDisable(false);
            goodB.setText("Good");
            goodB.setOnAction((click) -> {
                try {
                    onGoodPressed();
                } catch (IOException ex) {
                    Logger.getLogger(ReviewScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            try {
                nextCard();
            } catch (IOException ex) {
                Logger.getLogger(ReviewScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private String formatDate(LocalDate date) {
        if (date.equals(LocalDate.now())) {
            return "Today";
        } else if (date.equals(LocalDate.now().plusDays(1))) {
            return "Tomorrow";
        } else {
            return date.format(DateTimeFormatter.ofPattern("MM/dd/uuuu"));
        }
    }
}
