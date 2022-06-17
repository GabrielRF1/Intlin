/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import ine.ufsc.intlin.utils.ModalDialog;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class LoadLanguagePromptController implements Initializable {

    @FXML
    ComboBox languageSelectionBox;

    String selected;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setOptions(Set<Controller.SupportedLanguage> supportedlangsList) {
        supportedlangsList.forEach((lang) -> {
            languageSelectionBox.getItems().add(Controller.instance.supportedLanguageToString(lang));
        });
    }

    public void onComboBoxChange() {
        selected = languageSelectionBox.getSelectionModel().getSelectedItem().toString();
    }

    public void dispose() {
        Stage stage = (Stage) languageSelectionBox.getScene().getWindow();

        stage.close();
    }

    public void onSelected() {
        try {
            Controller.instance.selectLanguage(Controller.instance.StringLanguageToEnum(selected));
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            ModalDialog dialog = new ModalDialog(Alert.AlertType.ERROR, "Could not open load language", "An unexpected error has occurred while trying to load the selected language");
            dialog.show();
        }
        dispose();
    }

}
