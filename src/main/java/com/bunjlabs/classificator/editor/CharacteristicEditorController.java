package com.bunjlabs.classificator.editor;

import com.bunjlabs.classificator.MainController;
import com.bunjlabs.classificator.tools.Characteristic;
import com.bunjlabs.classificator.tools.WindowBuilder;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CharacteristicEditorController implements Initializable{

    @FXML
    public TextField characteristicNameField;
    @FXML
    public ComboBox characteristicTypeCombo;
    @FXML
    public TextArea characteristicRangeArea;
    
    public MainController mainController;

    @FXML
    public void handleSaveButtonAction(ActionEvent ae) {
        String charName = characteristicNameField.getText();
        String charType = (String) characteristicTypeCombo.getSelectionModel().getSelectedItem();
        String charRange = characteristicRangeArea.getText();
        
        if (charName == null || charName.isEmpty() || charType == null || charType.isEmpty()) {
            WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot save instance", "Field(-s) are empty");
            return;
        }
        
        
    }

    @FXML
    public void handleCancelButtonAction(ActionEvent ae) {
        Stage stage = (Stage) characteristicNameField.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        characteristicTypeCombo.setItems(FXCollections.observableArrayList(
                Characteristic.Type.NAME,
                Characteristic.Type.NAME_SET,
                Characteristic.Type.NUMBER,
                Characteristic.Type.NUMBER_RANGE
        ));
    }
}
