/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class MainController implements Initializable {

    @FXML
    private StackPane mediaTabPane;

    @FXML
    private Button loadMediaButton;

    private File chosenMedia;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chosenMedia = null;
    }

    public void openMedia() {
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your media", "*.mp4", ".pdf", ".mp3");
        filechooser.getExtensionFilters().add(filter);
        filechooser.setInitialDirectory(chosenMedia);

        MediaPlayer mp;
        MediaView mediaView;
        File file = filechooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        String filepath = file.toURI().toString();

        if (filepath != null) {
            Media media = new Media(filepath);
            mp = new MediaPlayer(media);
            mediaView = new MediaView(mp);
            loadMediaButton.setVisible(false);
            mediaTabPane.getChildren().add(mediaView);
        }

        chosenMedia = file.getParentFile();
    }
}
