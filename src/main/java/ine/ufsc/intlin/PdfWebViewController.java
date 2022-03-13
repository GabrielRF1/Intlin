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
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class PdfWebViewController implements Initializable {

    @FXML
    private WebView webView;

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
}
