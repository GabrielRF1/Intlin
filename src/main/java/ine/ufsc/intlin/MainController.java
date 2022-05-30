/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import globalExceptions.SRSNotLoadedException;
import ine.ufsc.controller.Controller;
import ine.ufsc.model.dictionaries.IntlinDictionary;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class MainController implements Initializable {

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

    private File chosenMedia;

    private Set<Controller.SupportedLanguage> languages;

    private boolean isSRSTabBuilt = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chosenMedia = null;
        languages = new HashSet<>();
        languageSelectionBox.setOnAction((t) -> {
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
                System.out.println("ine.ufsc.intlin.MainController.initialize() " + ex.getMessage());
            }
        });
    }

    public void openMedia() throws IOException {
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your media", "*.mp4", "*.mp3", "*.pdf");
        filechooser.getExtensionFilters().add(filter);
        filechooser.setInitialDirectory(chosenMedia);

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
            case "mp4":
                openVideo(file);
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
    }

    private void openVideo(File file) throws IOException {
        String filepath = file.toURI().toString();
        if (filepath != null) {
            Media media = new Media(filepath);
            disableLoadButton();

            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("videoPlayer.fxml"));

            Node playerPane = fxmlLoader.load();

            VideoPlayerController videoPlayer = fxmlLoader.getController();
            videoPlayer.setMedia(media);
            mediaTabPane.getChildren().add(playerPane);
        }

        chosenMedia = file.getParentFile();
    }

    private void disableLoadButton() {
        loadMediaButton.setVisible(false);
        loadMediaButton.setDisable(true);
    }

    private void enableLoadButton() {
        loadMediaButton.setVisible(true);
        loadMediaButton.setDisable(false);
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

    public void buildSRSTab() throws IOException {
        if (isSRSTabBuilt && !Controller.instance.isSrsDirty()) {
            return;
        }
        Controller.instance.setSrsIsDirty(false);
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
        isSRSTabBuilt = true;
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
                buildDictResultSection(entries, word);
            } catch (SQLException ex) {
                Label notFound = new Label();
                notFound.setText("An error has occurred while trying to find definitions");
                notFound.setFont(Font.font(16));
                definitionsPane.getChildren().add(notFound);
            }
        }

    }

    private void buildDictResultSection(ArrayList<IntlinDictionary.IntlinInfo> entries, String word) throws IOException {
        ArrayList<Node> definitionsNodes = new ArrayList<>();
        int last = -1;
        String lastGender = null;
        String lastAlts = null;
        String lastWordClass = null;
        int count = 1;
        for (var entry : entries) {

            int wordId = entry.wordId;
            boolean reachedBuildCondition = (wordId != last && definitionsNodes.size() == 1)
                    || (definitionsNodes.size() != 1 && wordId != last && last != -1);

            if (reachedBuildCondition) {
                buildDefCard(word, lastGender, lastAlts, lastWordClass, definitionsNodes);
                definitionsNodes = new ArrayList<>();
                count = 1;
            }

            definitionsNodes.add(buildDefinitionNode(entry, count++));

            last = wordId;
            lastAlts = entry.alts.toString();
            lastGender = entry.gender;
            lastWordClass = entry.wordClass;
        }
        //LAST CARD
        buildDefCard(word, lastGender, lastAlts, lastWordClass, definitionsNodes);
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

}
