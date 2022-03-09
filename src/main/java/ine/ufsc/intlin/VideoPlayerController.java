/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class VideoPlayerController implements Initializable {

    private Media media;

    @FXML
    private MediaView mediaView;
    @FXML
    private Pane mediaPane;
    @FXML
    private Button playButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label loopIconButton;

    private Timer timer;
    private TimerTask timerTask;

    private double loopStartSeconds;
    private double loopEndSeconds;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setMedia(Media media) {
        this.media = media;
        MediaPlayer mp = new MediaPlayer(media);
        mp.setOnEndOfMedia(() -> {
            onEndVideo();
        });
        mediaView.setMediaPlayer(mp);
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(mediaPane.widthProperty());
        mediaView.fitHeightProperty().bind(mediaPane.heightProperty());

    }

    public void playVideo() {
        startTimerCount();
        mediaView.getMediaPlayer().play();
        playButton.setText("⏸");
        playButton.setOnAction((t) -> {
            pauseVideo();
        });
    }

    public void pauseVideo() {
        timer.cancel();
        mediaView.getMediaPlayer().pause();
        playButton.setText("▶");
        playButton.setOnAction((t) -> {
            playVideo();
        });
    }

    public void restartVideo() {
        mediaView.getMediaPlayer().seek(Duration.ZERO);
        playVideo();
    }

    public void startLoop() {
        loopIconButton.setText("∞");
        loopIconButton.setStyle("-fx-text-fill: #8776c6");
        loopStartSeconds = mediaView.getMediaPlayer().getCurrentTime().toSeconds();
        loopIconButton.setOnMouseClicked((t) -> {
            closeLoop();
        });
    }

    public void closeLoop() {
        loopIconButton.setText("👍");
        loopIconButton.setStyle("-fx-text-fill: #76c699");
        loopEndSeconds = mediaView.getMediaPlayer().getCurrentTime().toSeconds();
        loopIconButton.setOnMouseClicked((t) -> {
            dismissLoop();
        });
        
        mediaView.getMediaPlayer().seek(Duration.seconds(loopStartSeconds));
        
        mediaView.getMediaPlayer().setStartTime(Duration.seconds(loopStartSeconds));
        mediaView.getMediaPlayer().setStopTime(Duration.seconds(loopEndSeconds));
        
        mediaView.getMediaPlayer().setOnEndOfMedia(() -> {
            mediaView.getMediaPlayer().seek(Duration.seconds(loopStartSeconds));
            mediaView.getMediaPlayer().play();
        });
    }
    
    public void dismissLoop() {
        loopIconButton.setText("∞");
        loopEndSeconds = mediaView.getMediaPlayer().getCurrentTime().toSeconds();
        loopIconButton.setOnMouseClicked((t) -> {
            startLoop();
        });
        
         mediaView.getMediaPlayer().setStartTime(Duration.ZERO);
        mediaView.getMediaPlayer().setStopTime(Duration.seconds(media.getDuration().toSeconds()));
    }

    public void startTimerCount() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                double now = mediaView.getMediaPlayer().getCurrentTime().toMillis();
                double end = media.getDuration().toMillis();

                System.out.println(".run(): " + now / end);
                progressBar.setProgress(now / end);

                if (now / end == 1) {
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1, 1);
    }

    private void onEndVideo() {
        playButton.setOnAction((t) -> {
            restartVideo();
        });
        playButton.setText("↻");
    }

}
