package com.bunjlabs.classificator;

import com.bunjlabs.classificator.editor.ClassEditorController;
import com.bunjlabs.classificator.tools.Loginer;
import com.bunjlabs.classificator.tools.WindowBuilder;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginSceneController implements Initializable {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        Parent root;
        try {
            String login = loginField.getText();
            String password = passwordField.getText();
            Loginer.AccessLevel level = Loginer.getInstance().whoIs(login, password);
            if (level == null || level == Loginer.AccessLevel.NOBODY) {
                WindowBuilder.alert(Alert.AlertType.ERROR, "Unable to login", "Login or password is incorrect");
                return;
            }
            FXMLLoader fXMLLoader = new FXMLLoader();
            root = fXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml").openStream());
            MainController controller = fXMLLoader.getController();
            controller.setAccessLevel(level);
            Stage stage = new Stage();
            stage.setTitle("Bacteria Classificator");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            Stage ourStage = (Stage) loginField.getScene().getWindow();
            ourStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
