package com.bunjlabs.classificator.editor;

import com.bunjlabs.classificator.MainController;
import com.bunjlabs.classificator.db.PossibleCharacteristics;
import com.bunjlabs.classificator.tools.Characteristic;
import com.bunjlabs.classificator.tools.WindowBuilder;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CharacteristicEditorController implements Initializable {

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
        Characteristic.Type charType = (Characteristic.Type) characteristicTypeCombo.getSelectionModel().getSelectedItem();
        String charRange = characteristicRangeArea.getText();

        if (charName == null || charName.isEmpty() || charType == null || charRange == null || charRange.isEmpty()) {
            WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot save instance", "Field(-s) are empty");
            return;
        }

        Characteristic.Range range;

        if (charType == Characteristic.Type.NAME || charType == Characteristic.Type.NAME_SET) {

            String rangeNames[] = charRange.split(",");

            if (rangeNames.length <= 0) {
                WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot save instance", "Name set Range cannot be empty");
                return;
            }

            for (int i = 0; i < rangeNames.length; i++) {
                rangeNames[i] = rangeNames[i].trim();
            }

            range = new Characteristic.Range(Arrays.asList(rangeNames));
        } else {
            if (!charRange.contains("-")) {
                WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot save instance", "Number range must contain two numbers with '-' separator character");
                return;
            }

            String numbersString[] = charRange.split("-");

            try {
                range = new Characteristic.Range(Double.parseDouble(numbersString[0]), Double.parseDouble(numbersString[1]));
            } catch (NumberFormatException ex) {
                WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot save instance", "Not a number in TextArea");
                return;
            }

        }

        PossibleCharacteristics.getInstance().add(charName, new PossibleCharacteristics.TypeRange(range, charType));
        mainController.addToCharTable(new CharacteristicRow(charName, charType.toString(),
                charType == Characteristic.Type.NAME || charType == Characteristic.Type.NAME_SET ? range.names.toString() : range.toString()));

        mainController.refreshSolverParamsChoiceBox();
        PossibleCharacteristics.getInstance().flush();

        Stage stage = (Stage) characteristicNameField.getScene().getWindow();
        stage.close();
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
