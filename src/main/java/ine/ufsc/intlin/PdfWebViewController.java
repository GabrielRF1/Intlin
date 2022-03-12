/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;

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

    public void setPDF(String path) {
        WebEngine engine = webView.getEngine();
        String url = getClass().getResource("/web/viewer.html").toExternalForm();

        // connect CSS styles to customize pdf.js appearance
        engine.setUserStyleSheetLocation(getClass().getResource("/web.css").toExternalForm());

        engine.setJavaScriptEnabled(true);
        engine.load(url);

        engine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    try {
                        byte[] data = FileUtils.readFileToByteArray(new File(path));
                        String base64 = Base64.getEncoder().encodeToString(data);
                        engine.executeScript("openFileFromBase64('" + base64 + "')");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
