/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class IntlinDefinitionFragmentController implements Initializable {

    @FXML
    private Label synonymsLabel;
    @FXML
    private Label antonymsLabel;
    @FXML
    private Label extrasLabel;
    @FXML
    private Label extrasButton;
    @FXML
    private Label definitionLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setDefinition(int defNum, String def) {
        definitionLabel.setText(String.format("%d. %s", defNum, def));
        Tooltip tp = new Tooltip(definitionLabel.getText());
        tp.setShowDelay(Duration.seconds(1));
        definitionLabel.setTooltip(tp);
    }

    public void setSynonyms(List<String> syns) {
        synonymsLabel.setText(syns.toString());
        Tooltip tp = new Tooltip(synonymsLabel.getText());
        tp.setShowDelay(Duration.seconds(1));
        synonymsLabel.setTooltip(tp);
    }

    public void setAntonyms(List<String> ants) {
        antonymsLabel.setText(ants.toString());
        Tooltip tp = new Tooltip(antonymsLabel.getText());
        tp.setShowDelay(Duration.seconds(1));
        antonymsLabel.setTooltip(tp);
    }

    public void setExtras(List<String> extras) {
        extrasLabel.setText(extras.toString());
    }

    public void showExtrasAction() {
        extrasButton.setText("Extras v");
        extrasLabel.setVisible(true);
        Tooltip tp = new Tooltip(extrasLabel.getText());
        tp.setShowDelay(Duration.seconds(1));
        extrasLabel.setTooltip(tp);
        extrasButton.setOnMouseClicked((t) -> {
            hideExtrasAction();
        });
    }

    public void hideExtrasAction() {
        extrasButton.setText("Extras >");
        extrasLabel.setVisible(false);
        extrasLabel.setTooltip(null);
        extrasButton.setOnMouseClicked((t) -> {
            showExtrasAction();
        });
    }
}
