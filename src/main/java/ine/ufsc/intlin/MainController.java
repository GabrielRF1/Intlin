/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import globalExceptions.SRSNotLoadedException;
import ine.ufsc.controller.Controller;
import ine.ufsc.intlin.utils.ModalDialog;
import ine.ufsc.model.dictionaries.IntlinDictionary;
import ine.ufsc.utils.Message;
import ine.ufsc.utils.Observer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class MainController implements Initializable, Observer {

    @FXML
    private StackPane mediaTabPane;
    @FXML
    private VBox srsTableVBox;
    @FXML
    private VBox definitionsPane;
    @FXML
    private ImageView loadMediaButton;
    @FXML
    private ChoiceBox languageSelectionBox;
    @FXML
    private TextField searchTextBox;
    @FXML
    private Label SRSTitleLabel;
    @FXML
    private ImageView createDeckIconButton;
    @FXML
    private Button searchButton;
    @FXML
    private MenuItem loadMediaMenuButton;
    @FXML
    private Pane gettingStartedPane;
    @FXML 
    private Pane gettingStartedSRSPane;

    private Set<Controller.SupportedLanguage> languages;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languages = new HashSet<>();
        loadSelectionBox();
        languageSelectionBox.setOnAction((t) -> {
            String selected = languageSelectionBox.getSelectionModel().getSelectedItem().toString();
            try {
                Controller.instance.selectLanguage(Controller.instance.StringLanguageToEnum(selected));
            } catch (ClassNotFoundException | SQLException | IOException ex) {
                Dialog<String> dialog = new Dialog<>();
                ButtonType ok = new ButtonType("Ok");
                dialog.setContentText("An error has occurred while loading the dictionary\n"
                        + "Verify if the appropriate files are in the resources directory");
                dialog.setTitle("error");
                dialog.getDialogPane().getButtonTypes().add(ok);
                dialog.show();
                System.out.println("ine.ufsc.intlin.MainController.initialize() " + ex.getMessage());
            }
        });
        Controller.instance.attach(this);
        Controller.instance.checkAndLoadLastStudiedLanguage();
    }

    public void openMedia() throws IOException {
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your media", "*.mp4", "*.mp3", "*.pdf");
        filechooser.getExtensionFilters().add(filter);

        File file = filechooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        String fileExtension = "";
        if (i > 0) {
            fileExtension = fileName.substring(i + 1);
        }

        switch (fileExtension) {
            case "pdf":
                openPDF(file);
                break;
            case "mp3":
                openMedia(file, true);
                break;
            case "mp4":
                openMedia(file, false);
                break;
        }

    }

    private void openPDF(File file) throws IOException {
        disableLoadButton();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("pdfWebView.fxml"));

        Node pdfPane = fxmlLoader.load();

        PdfWebViewController pdfController = fxmlLoader.getController();
        pdfController.setPDF(file);
        mediaTabPane.getChildren().add(pdfPane);
        loadMediaMenuButton.setText("Close content");
        loadMediaMenuButton.setOnAction((t) -> {
            pdfController.closePdfViewer();
            mediaTabPane.getChildren().remove(pdfPane);
            enableLoadButton();
        });
    }

    private void openMedia(File file, boolean isAudio) throws IOException {
        String filepath = file.toURI().toString();
        if (filepath != null) {
            disableLoadButton();

            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("videoPlayer.fxml"));

            Node playerPane = fxmlLoader.load();

            VideoPlayerController videoPlayer = fxmlLoader.getController();
            videoPlayer.setMedia(filepath, isAudio);
            mediaTabPane.getChildren().add(playerPane);
            loadMediaMenuButton.setText("Close content");
            loadMediaMenuButton.setOnAction((t) -> {
                videoPlayer.closeMediaPlayer();
                mediaTabPane.getChildren().remove(playerPane);
                enableLoadButton();
            });
        }
    }

    private void disableLoadButton() {
        loadMediaButton.setVisible(false);
        gettingStartedPane.setVisible(false);
        loadMediaButton.setDisable(true);
    }

    private void enableLoadButton() {
        loadMediaButton.setVisible(true);
        gettingStartedPane.setVisible(true);
        loadMediaButton.setDisable(false);
        loadMediaMenuButton.setText("Open content");
        loadMediaMenuButton.setOnAction((t) -> {
            try {
                openMedia();
            } catch (IOException ex) {
                ModalDialog dialog = new ModalDialog(Alert.AlertType.ERROR, "Could not open load media", "An unexpected error has occurred");
                dialog.show();
            }
        });
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

    public void openLanguageLoadPrompt() {
        try {
            FXMLLoader languageSelectionLoader = new FXMLLoader(App.class.getResource("LoadLanguagePrompt.fxml"));
            Parent selectLanguageNode = languageSelectionLoader.load();

            LoadLanguagePromptController loadLanguageControl = languageSelectionLoader.getController();

            loadLanguageControl.setOptions(Controller.instance.getSupportedlangsList());

            Scene srsScene = new Scene(selectLanguageNode);
            Stage newWindow = new Stage();
            newWindow.setTitle("Select a language to study");
            newWindow.setScene(srsScene);
            newWindow.setResizable(false);
            newWindow.showAndWait();
        } catch (IOException ex) {
            ModalDialog dialog = new ModalDialog(Alert.AlertType.ERROR, "Could not open load language menu", "An unexpected error has occurred");
            dialog.show();
        }
    }

    public void buildSRSTab() throws IOException {

        Controller.SupportedLanguage lang = Controller.instance.getSelectedLanguage();
        if (lang == null) {
            return;
        }

        SRSTitleLabel.setText(Controller.instance.supportedLanguageToString(lang));
        srsTableVBox.getChildren().removeIf((t) -> {
            return true;
        });
        FXMLLoader srsLoader = new FXMLLoader(App.class.getResource("SRSTabDecksTable.fxml"));
        Node srsTabNode = srsLoader.load();
        SRSTabDecksTableController srsController = srsLoader.getController();

        Set<String> decks = Controller.instance.getDecks();

        ArrayList<Node> deckFragments = new ArrayList<>();
        for (String deck : decks) {
            try {
                FXMLLoader srsInfoLoader = new FXMLLoader(App.class.getResource("deckFragment.fxml"));
                Node deckInfoNode = srsInfoLoader.load();
                DeckFragmentController deckInfoController = srsInfoLoader.getController();
                deckInfoController.setData(deck, Controller.instance.getDecksReview(deck).size());
                deckFragments.add(deckInfoNode);
            } catch (SQLException ex) {
                Label notFound = new Label();
                notFound.setText("Error Loading SRS");
                notFound.setFont(Font.font(16));
                srsTableVBox.getChildren().add(notFound);
                return;
            }
        }

        srsController.setDeck(deckFragments);

        srsTableVBox.getChildren().add(srsTabNode);
    }

    public void createDeck() throws IOException {
        try {
            TextInputDialog dialog = new TextInputDialog("Deck Name");

            dialog.setTitle("Create Deck");
            dialog.setHeaderText("Enter a deck name:");
            dialog.setContentText("Deck name:");

            Optional<String> ans = dialog.showAndWait();
            Controller.instance.tryAndCreateDeck(ans.get());
            buildSRSTab();
        } catch (SQLException ex) {
            Dialog dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Could not create deck");
            dialog.setContentText("An error has occurred while trying to create the deck");
            dialog.show();
        } catch (SRSNotLoadedException ex) {
            Dialog dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Language not loaded");
            dialog.setContentText(ex.getMessage());
            dialog.show();
        }
    }

    public void searchWord() throws IOException {
        Controller.SupportedLanguage selected = Controller.instance.getSelectedLanguage();
        if (Controller.instance.getSelectedLanguage() == null) {
            return;
        }
        
        if (!definitionsPane.getChildren().isEmpty()) {
            definitionsPane.getChildren().removeIf((t) -> {
                return true; //delete all
            });
        }

        if (Controller.instance.isIntlin(selected)) {
            try {
                String word = searchTextBox.getText();
                ArrayList<IntlinDictionary.IntlinInfo> entries = Controller.instance.searchIntlinWord(selected, word);
                if (entries == null) {
                    Label notFound = new Label();
                    notFound.setText("Could not find definitions");
                    notFound.setFont(Font.font(16));
                    definitionsPane.getChildren().add(notFound);
                    return;
                }
                buildDictResultSection(entries);
            } catch (SQLException ex) {
                Label notFound = new Label();
                notFound.setText("An error has occurred while trying to find definitions");
                notFound.setFont(Font.font(16));
                definitionsPane.getChildren().add(notFound);
            }
        }

    }

    public void handleOnKeyPressed(KeyEvent event) throws IOException {
        if(event.getCode().equals(KeyCode.ENTER)) {
            searchWord();
        }
    }
    
    private void buildDictResultSection(ArrayList<IntlinDictionary.IntlinInfo> entries) throws IOException {
        ArrayList<Node> definitionsNodes = new ArrayList<>();
        int last = -1;
        String lastWord = null;
        String lastGender = null;
        String lastAlts = null;
        String lastWordClass = null;
        int count = 1;
        for (var entry : entries) {

            int wordId = entry.wordId;
            boolean reachedBuildCondition = (wordId != last && definitionsNodes.size() == 1)
                    || (definitionsNodes.size() != 1 && wordId != last && last != -1);

            if (reachedBuildCondition) {
                buildDefCard(entry.word, lastGender, lastAlts, lastWordClass, definitionsNodes);
                definitionsNodes = new ArrayList<>();
                count = 1;
            }

            definitionsNodes.add(buildDefinitionNode(entry, count++));

            last = wordId;
            lastWord = entry.word;
            lastAlts = entry.alts.toString();
            lastGender = entry.gender;
            lastWordClass = entry.wordClass;
        }
        //LAST CARD
        buildDefCard(lastWord, lastGender, lastAlts, lastWordClass, definitionsNodes);
    }

    private Node buildDefinitionNode(IntlinDictionary.IntlinInfo entry, int count) throws IOException {
        FXMLLoader defLoader = new FXMLLoader(App.class.getResource("intlinDefinitionFragment.fxml"));
        Node defNode = defLoader.load();
        IntlinDefinitionFragmentController defController = defLoader.getController();
        defController.setDefinition(count, entry.def);

        defController.setSynonyms(entry.syns);
        defController.setAntonyms(entry.ants);
        defController.setExtras(entry.extras);

        return defNode;
    }

    private void buildDefCard(String word, String gender, String alts,
            String wordClass, ArrayList<Node> definitionsNodes) throws IOException {
        FXMLLoader entryLoader = new FXMLLoader(App.class.getResource("intlinEntryCard.fxml"));
        Node entryNode = entryLoader.load();
        IntlinEntryCardController entryController = entryLoader.getController();
        entryController.setDefinitions(definitionsNodes);
        entryController.setWord(word);
        entryController.setGender(gender);
        entryController.setAlternatives(alts);
        entryController.setWordClass(wordClass);
        definitionsPane.getChildren().add(entryNode);
    }

    @Override
    public void update(Message message) {
        var types = message.getTypes();
        for (Message.MessageType type : types) {
            switch (type) {
                case UPDATE_SRS: {
                    try {
                        buildSRSTab();
                    } catch (IOException ex) {
                        ModalDialog dialog = new ModalDialog(Alert.AlertType.ERROR, "Error Building SRS TAB", "An unexpected error has occurred while building the SRS TAB");
                        dialog.show();
                    }
                }
                case SRS_HAS_BEEN_LOADED:
                    String selectString = Controller.instance.supportedLanguageToString(Controller.instance.getSelectedLanguage());
                    languageSelectionBox.getSelectionModel().select(selectString);
                    createDeckIconButton.setVisible(true);
                    srsTableVBox.getChildren().remove(gettingStartedSRSPane);
                    searchTextBox.setDisable(false);
                    searchButton.setDisable(false);
                    break;
                default:
            }
        }
    }

}
