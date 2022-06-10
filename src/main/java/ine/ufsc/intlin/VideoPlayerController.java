/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import globalExceptions.SRSNotLoadedException;
import ine.ufsc.controller.Controller;
import ine.ufsc.model.subtitle.BadlyFomattedSubtitleFileException;
import ine.ufsc.model.subtitle.Subtitle;
import ine.ufsc.srs.Card;
import ine.ufsc.srs.CardContent;
import ine.ufsc.srs.Content;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class VideoPlayerController implements Initializable {

    public static int TYPE_3BYTE_BGR = 5;

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
    @FXML
    private Label curSubLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView cameraIcon;
    @FXML
    private VBox transcriptionListView;

    private Timer timer;
    private TimerTask timerTask;

    private double loopStartMilliSeconds = 0;
    private double loopEndMilliSeconds = 0;

    private LinkedList<Subtitle> subtitles;
    private Subtitle curSubtitle;
    private double closeCurSubMilisec;

    private static boolean takeScreenshot=false;
    private static double screenShotStamp;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setMedia(String mediaPath, boolean isAudio) {
        this.media = new Media(mediaPath);

        imageView.setVisible(isAudio);
        cameraIcon.setVisible(!isAudio);

        IMediaReader mediaReader = ToolFactory.makeReader(mediaPath.replaceFirst("file:/", "").replaceAll("%20", " "));
        mediaReader.setBufferedImageTypeToGenerate(TYPE_3BYTE_BGR);
        mediaReader.addListener(new snapListener());

        MediaPlayer mp = new MediaPlayer(media);
        mp.setOnEndOfMedia(() -> {
            onEndVideo();
        });
        mp.setOnPlaying(() -> {
            while (mediaReader.readPacket() != null) {
            }
        });
        mediaView.setMediaPlayer(mp);
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(mediaPane.widthProperty());
        mediaView.fitHeightProperty().bind(mediaPane.heightProperty());

        progressBar.setOnMouseClicked((evt) -> {
            if (playButton.getText().equals("↻")) {
                return;
            }
            double x = evt.getX();
            double width = progressBar.getWidth();
            double progression = (x / width);
            double target = (progression * mediaView.getMediaPlayer().getTotalDuration().toMillis());
            Duration duration = new Duration(target);
            progressBar.setProgress(target / media.getDuration().toMillis());
            seekTo(duration);
        });
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
        seekTo(Duration.ZERO);
        playVideo();
    }

    public void startLoop() {
        loopIconButton.setText("∞");
        loopIconButton.setStyle("-fx-text-fill: #8776c6");
        loopStartMilliSeconds = mediaView.getMediaPlayer().getCurrentTime().toMillis();
        loopIconButton.setOnMouseClicked((t) -> {
            closeLoop();
        });
    }

    public void closeLoop() {
        loopIconButton.setText("👍");
        loopIconButton.setStyle("-fx-text-fill: #76c699");
        loopEndMilliSeconds = mediaView.getMediaPlayer().getCurrentTime().toMillis();
        loopIconButton.setOnMouseClicked((t) -> {
            dismissLoop();
        });

        seekTo(Duration.millis(loopStartMilliSeconds));

        mediaView.getMediaPlayer().setStartTime(Duration.millis(loopStartMilliSeconds));
        mediaView.getMediaPlayer().setStopTime(Duration.millis(loopEndMilliSeconds));

        mediaView.getMediaPlayer().setOnEndOfMedia(() -> {
            seekTo(Duration.millis(loopStartMilliSeconds));
            mediaView.getMediaPlayer().play();
        });
    }

    public void loadSubtitle() {
        if (timer != null) {
            pauseVideo();
        }
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your subtitle", "*.srt");
        filechooser.getExtensionFilters().add(filter);
        File srtFile = filechooser.showOpenDialog(null);

        try {
            subtitles = Controller.instance.parseSubs(srtFile);
            buildSubtitleList();
        } catch (IOException | BadlyFomattedSubtitleFileException ex) {
            Dialog dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Could not load subtitles");
            dialog.setContentText("An error has occurred while trying load the subtitles");
            dialog.show();
        }
        if (timer != null) {
            playVideo();
        }
    }

    public void dismissLoop() {
        loopIconButton.setText("∞");
        loopEndMilliSeconds = 0;
        loopStartMilliSeconds = 0;
        loopIconButton.setOnMouseClicked((t) -> {
            startLoop();
        });

        mediaView.getMediaPlayer().setStartTime(Duration.ZERO);
        mediaView.getMediaPlayer().setStopTime(Duration.seconds(media.getDuration().toMillis()));
        mediaView.getMediaPlayer().setOnEndOfMedia(() -> {
            onEndVideo();
        });
    }

    public void startTimerCount() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                double now = mediaView.getMediaPlayer().getCurrentTime().toMillis();
                double end = media.getDuration().toMillis();

                progressBar.setProgress(now / end);

                if (subtitles != null) {
                    final double actualStamp = now;
                    curSubtitle = findCurrentSub(actualStamp);
                    if (curSubtitle != null) {
                        closeCurSubMilisec = curSubtitle.getEnd().toMillis();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                curSubLabel.setText(curSubtitle.getLine());
                            }
                        });
                    }
                    if (closeCurSubMilisec <= actualStamp) {
                        closeCurSubMilisec = Double.MAX_VALUE;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                curSubLabel.setText("");
                            }
                        });
                    }
                }
                if (now / end == 1) {
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1, 1);
    }

    public void saveSelectedSubtitles() {
        CardContent front = new CardContent();
        final var linesToSave = Controller.instance.getLinesToSave();
        String[] selectedList = linesToSave.values().toArray(new String[linesToSave.size()]);
        int pos = 0;
        for (var selected : selectedList) {
            front.addContent(new Content(++pos, (pos + ": " + selected), Content.Type.text));
        }
        Card card = new Card(front, new CardContent());
        try {
            Controller.instance.tryAndCreateDeck("Sentences");
            Controller.instance.addCardToDeck("Sentences", card);
        } catch (SQLException ex) {
            Dialog dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Could not create the flashcard");
            dialog.setContentText("An error has occurred while trying to create the flashcard");
            dialog.show();
        } catch (SRSNotLoadedException ex) {
            Dialog dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Language not loaded");
            dialog.setContentText(ex.getMessage());
            dialog.show();
        }
    }

    public void onHoverCamera() {
        cameraIcon.setOpacity(1);
        DropShadow shadow = new DropShadow();
        shadow.setWidth(11);
        shadow.setHeight(10);
        shadow.setRadius(5);
        cameraIcon.setEffect(shadow);
    }

    public void unfocusCameraIcon() {
        cameraIcon.setOpacity(0.4);
        cameraIcon.setEffect(null);
    }

    public void saveFrameToFlashcard() {
        if (timer != null) {
            pauseVideo();
        }
        takeScreenshot = true;
        screenShotStamp = mediaView.getMediaPlayer().getCurrentTime().toSeconds();
        if (timer != null) {
            playVideo();
        }
    }

    private Subtitle findCurrentSub(double videoTime) {
        for (Subtitle subtitle : subtitles) {
            if (videoTime >= subtitle.getStart().toMillis() && videoTime < subtitle.getEnd().toMillis()) {
                return subtitle;
            }
        }
        return null;
    }

    private void onEndVideo() {
        playButton.setOnAction((t) -> {
            restartVideo();
        });
        playButton.setText("↻");
    }

    private void buildSubtitleList() {
        int id = 1;
        for (Subtitle subtitle : subtitles) {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("SubtitleTile.fxml"));
            try {
                Node subtitleTile = fxmlLoader.load();
                SubtitleTileController tileController = fxmlLoader.getController();

                tileController.setLine(subtitle.getLine(), id++);

                transcriptionListView.getChildren().add(subtitleTile);
            } catch (IOException ex) {
                Logger.getLogger(VideoPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                // tratar
            }
        }
        transcriptionListView.getChildren().forEach(child -> VBox.setVgrow(child, Priority.ALWAYS));
    }

    private void seekTo(Duration duration) {
        mediaView.getMediaPlayer().seek(duration);
        curSubLabel.setText("");
    }

    private static class snapListener extends MediaListenerAdapter {

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
            if (takeScreenshot) {
                System.out.println("timer: " + event.getTimeStamp() + ";\n calc: " + screenShotStamp * Global.DEFAULT_PTS_PER_SECOND);
                takeScreenshot = false;
            }
        }
    }
}
