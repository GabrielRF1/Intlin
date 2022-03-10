/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.controller.Controller;
import ine.ufsc.model.dictionaries.Dictionary;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.scene.media.Media;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class MainController implements Initializable {

    @FXML
    private StackPane mediaTabPane;
    @FXML
    private ImageView loadMediaButton;
    @FXML
    private ChoiceBox languageSelectionBox;
    @FXML
    private Label SRSTitleLabel;
    @FXML
    private Label tempText1;
    @FXML
    private Label tempText2;
    @FXML
    private Label tempText3;

    private File chosenMedia;

    private Set<Controller.SupportedLanguage> languages;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chosenMedia = null;
        languages = new HashSet<>();
        languageSelectionBox.setOnAction((t) -> {
            int selectedIndex = languageSelectionBox.getSelectionModel().getSelectedIndex();
            String selected = languageSelectionBox.getSelectionModel().getSelectedItem().toString();
            try {
                Controller.instance.selectLanguage(Controller.instance.StringLanguageToEnum(selected));
                buildSRSTab();
            } catch (ClassNotFoundException | SQLException | IOException ex) {
                Dialog<String> dialog = new Dialog<>();
                ButtonType ok = new ButtonType("Ok");
                dialog.setContentText("An error has occurred while loading the dictionary\n"
                        + "Verify if the appropriate files are in the resources directory");
                dialog.setTitle("error");
                dialog.getDialogPane().getButtonTypes().add(ok);
                dialog.show();
                System.out.println("ine.ufsc.intlin.MainController.initialize()" + ex.getMessage());
            }
        });
    }

    public void openMedia() throws IOException {
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your media", "*.mp4");
        filechooser.getExtensionFilters().add(filter);
        filechooser.setInitialDirectory(chosenMedia);

        FXMLLoader fxmlLoader;
        File file = filechooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        String filepath = file.toURI().toString();

        if (filepath != null) {
            Media media = new Media(filepath);
            loadMediaButton.setVisible(false);
            loadMediaButton.setDisable(true);

            fxmlLoader = new FXMLLoader(App.class.getResource("videoPlayer.fxml"));

            Node playerPane = fxmlLoader.load();

            VideoPlayerController videoPlayer = fxmlLoader.getController();
            videoPlayer.setMedia(media);
            mediaTabPane.getChildren().add(playerPane);
        }

        chosenMedia = file.getParentFile();
    }

    public void removeShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setWidth(0);
        shadow.setHeight(0);
        shadow.setRadius(0);
        loadMediaButton.setEffect(shadow);
    }

    public void createShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setWidth(22.97);
        shadow.setHeight(19.02);
        shadow.setRadius(10);
        loadMediaButton.setEffect(shadow);
    }

    public void loadSelectionBox() {
        Controller.instance.getSupportedlangsList().forEach((t) -> {
            String lang = Controller.instance.supportedLanguageToString(t);
            if (!languageSelectionBox.getItems().contains(lang)) {
                languageSelectionBox.getItems().add(lang);
            }
        });
    }

    public void buildSRSTab() {
        Controller.SupportedLanguage lang = Controller.instance.getSelectedLanguage();
        if (lang != null) {
            SRSTitleLabel.setText(Controller.instance.supportedLanguageToString(lang));
            tempText1.setVisible(false);
            tempText2.setVisible(false);
            tempText3.setVisible(false);
        }
    }
}
