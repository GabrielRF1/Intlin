/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.intlin;

import ine.ufsc.srs.CardContent;
import ine.ufsc.srs.Content;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author Gabriel
 */
public class CardFaceController implements Initializable {

    @FXML
    private VBox mainFace;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setCardContent(CardContent cardContent, Pos pos) {
        cardContent.sortContents();
        for (int i = 0; i < cardContent.size(); i++) {
            Label contentNode;
            AnchorPane anchorPane = new AnchorPane();
            Content content = cardContent.getContent(i);
            switch (content.getType()) {
                case text:
                    String face = (String) content.getElement() + "\n";
                    contentNode = new Label(face);
                    contentNode.setFont(new Font(30.0));
                    break;
                case image:
                    File imageFile = (File) content.getElement();
                    if (imageFile.exists()) {
                        ImageView imageView = new ImageView(imageFile.getAbsolutePath());
                        contentNode = new Label();
                        contentNode.setGraphic(imageView);
                    } else {
                        contentNode = new Label("DELETED: <"+imageFile.getPath()+">");
                        contentNode.setFont(new Font(30.0));
                    }
                    break;
                default:
                    contentNode = new Label("");
            }
            contentNode.setAlignment(pos);
            contentNode.setPadding(new Insets(0, 0, 10, 0));
            anchorPane.getChildren().add(contentNode);
            AnchorPane.setTopAnchor(contentNode, 0.0);
            AnchorPane.setLeftAnchor(contentNode, 0.0);
            AnchorPane.setRightAnchor(contentNode, 0.0);
            mainFace.getChildren().add(anchorPane);
            VBox.setVgrow(anchorPane, Priority.ALWAYS);
        }
    }

}
