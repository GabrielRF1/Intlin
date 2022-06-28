/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class PdfWebViewController implements Initializable {

    private Media audio;
    @FXML
    private WebView webView;
    @FXML
    private Button loadAudioButton;
    @FXML
    private Button playButton;
    @FXML
    private MediaView audioPlayer;
    @FXML
    private AnchorPane progressBarPane;
    @FXML
    private ProgressBar progressBar;

    private Timer timer;
    private TimerTask timerTask;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setPDF(File file) {
        WebEngine engine = webView.getEngine();
        String url = getClass().getResource("web/viewer.html").toExternalForm();

        engine.setUserStyleSheetLocation(getClass().getResource("web/viewer.css").toExternalForm());

        engine.setJavaScriptEnabled(true);

        engine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        try {
                            byte[] data;
                            URI uri = file.toURI();
                            data = Files.readAllBytes(Path.of(uri));
                            String base64 = Base64.getEncoder().encodeToString(data);
                            engine.executeScript("openBase64File('" + base64 + "')");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        engine.load(url);
    }

    public void playAudio() {
        startTimerCount();
        audioPlayer.getMediaPlayer().play();
        playButton.setText("⏸");
        playButton.setOnAction((t) -> {
            pauseAudio();
        });
    }

    public void pauseAudio() {
        timer.cancel();
        audioPlayer.getMediaPlayer().pause();
        playButton.setText("▶");
        playButton.setOnAction((t) -> {
            playAudio();
        });
    }

    private void onEndAudio() {
        playButton.setOnAction((t) -> {
            audioPlayer.getMediaPlayer().seek(Duration.ZERO);
            playAudio();
        });
        playButton.setText("↻");
    }

    public void loadAudio() {
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your media", "*.mp3", "*.aif", "*.aiff", "*.wav");
        filechooser.getExtensionFilters().add(filter);
        File file = filechooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        loadAudioButton.setVisible(false);

        String filepath = file.toURI().toString();
        if (filepath != null) {
            audio = new Media(filepath);
            progressBarPane.setVisible(true);
            MediaPlayer mp = new MediaPlayer(audio);

            audioPlayer.setMediaPlayer(mp);
            mp.setOnEndOfMedia(() -> {
                onEndAudio();
            });
            progressBar.setOnMouseClicked((evt) -> {
                if (playButton.getText().equals("↻")) {
                    return;
                }
                double x = evt.getX();
                double width = progressBar.getWidth();
                double progression = (x / width);
                double target = (progression * audioPlayer.getMediaPlayer().getTotalDuration().toMillis());
                Duration duration = new Duration(target);
                progressBar.setProgress(target / audio.getDuration().toMillis());
                audioPlayer.getMediaPlayer().seek(duration);
            });
        }
    }

    public void startTimerCount() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                double now = audioPlayer.getMediaPlayer().getCurrentTime().toMillis();
                double end = audio.getDuration().toMillis();

                progressBar.setProgress(now / end);

                if (now / end == 1) {
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1, 1);
    }

    public void closePdfViewer() {
        webView.getEngine().load(null);
        audioPlayer.getMediaPlayer().dispose();
    }
}
