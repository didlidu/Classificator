package com.bunjlabs.classificator.editor;

import com.bunjlabs.classificator.MainController;
import com.bunjlabs.classificator.db.Characteristic;
import com.bunjlabs.classificator.db.ClassDAO;
import com.bunjlabs.classificator.db.Database;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

public class ClassEditorController implements Initializable {

    @FXML
    private TextField classNameField;
    @FXML
    private ListView characteristicsListView;
    @FXML
    private ChoiceBox characteristicsChoiseBox;
    @FXML
    private Accordion accordion;

    private String editingClass = "";
    private boolean isEditing = false;
    
    public MainController mainController;

    public void setEditingClass(String className) {
        this.editingClass = className;
        this.isEditing = true;
        classNameField.setText(editingClass);
    }

    private List<CharacteristicField> characteristicsFields = new ArrayList<>();

    private Map<String, Characteristic.Range> characteristicsRange = new HashMap<>();
    private Map<String, Characteristic.Type> characteristicsType = new HashMap<>();

    {
        List<String> list = Arrays.asList("a", "b", "c");

        characteristicsType.put("param a", Characteristic.Type.NAME_SET);
        characteristicsRange.put("param a", new Characteristic.Range(list));
        characteristicsType.put("param b", Characteristic.Type.NUMBER_RANGE);
        characteristicsRange.put("param b", new Characteristic.Range(0, 100));
    }

    private ObservableList<String> characteristicsData
            = FXCollections.observableArrayList(characteristicsType.keySet());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        characteristicsChoiseBox.setItems(characteristicsData);
        characteristicsChoiseBox.setTooltip(new Tooltip("Select characteristic"));
    }

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        String characteristicName = (String) characteristicsChoiseBox.getSelectionModel().getSelectedItem();
        if (characteristicName == null) {
            return;
        }
        Characteristic.Type type = characteristicsType.get(characteristicName);
        characteristicsData.remove(characteristicName);
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        Control control = null;
        switch (type) {
            case NAME:
                control = new ChoiceBox<String>();
                ((ChoiceBox<String>) control).setItems(
                        FXCollections.observableArrayList(characteristicsRange.get(characteristicName).names));
                grid.add(control, 0, 0);
                break;
            case NAME_SET:
                control = new CheckComboBox<String>(FXCollections.observableArrayList(
                        characteristicsRange.get(characteristicName).names));
                grid.add(control, 0, 0);
                break;
            case NUMBER:
                control = new TextField();
                grid.add(control, 0, 0);
                break;
            case NUMBER_RANGE:
                control = new TextField();
                grid.add(control, 0, 0);
                break;
        }
        characteristicsFields.add(new CharacteristicField(control, characteristicName));
        gridTitlePane.setText(characteristicName);
        gridTitlePane.setContent(grid);
        accordion.getPanes().add(gridTitlePane);
    }

    @FXML
    public void handleCancelButtonAction(ActionEvent event) {
        Stage stage = (Stage) characteristicsChoiseBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSaveButtonAction(ActionEvent event) {
        String className = classNameField.getText();
        if (className.isEmpty()) {
            //TODO error
            return;
        }
        Map<String, Characteristic> map = new HashMap<>();
        ClassDAO classDAO = new ClassDAO(className, map);
        for (CharacteristicField field : characteristicsFields) {
            Characteristic.Type type = characteristicsType.get(field.name);
            Characteristic.Range range = characteristicsRange.get(field.name);
            switch (type) {
                case NAME:
                    String selected = (String) ((ChoiceBox<String>) field.control).getSelectionModel().getSelectedItem();
                    if (selected == null) {
                        //TODO error
                        return;
                    }
                    map.put(field.name, new Characteristic(selected));
                    break;
                case NAME_SET:
                    List<String> checked = ((CheckComboBox<String>) field.control).getCheckModel().getCheckedItems();
                    if (checked == null || checked.isEmpty()) {
                        //TODO error
                        return;
                    }
                    map.put(field.name, new Characteristic(checked));
                    break;
                case NUMBER:
                    double number;
                    try {
                        number = Double.parseDouble(((TextField) field.control).getText());
                    } catch (NumberFormatException ex) {
                        //TODO error
                        return;
                    }
                    map.put(field.name, new Characteristic(number));
                    break;
                case NUMBER_RANGE:
                    String[] numbers = ((TextField) field.control).getText().split("-");
                    try {
                        map.put(field.name, new Characteristic(
                                Double.parseDouble(numbers[0]),
                                Double.parseDouble(numbers[1])
                        ));
                    } catch (NumberFormatException ex) {
                        //TODO error
                        return;
                    }
                    break;
            }
        }
        classDAO.flush();
        mainController.data.add(new ClassRow(className, map.toString()));
        //mainController.refreshTable();
        Database.getInstance().flush();
        Stage stage = (Stage) characteristicsChoiseBox.getScene().getWindow();
        stage.close();
    }

    private static class CharacteristicField {

        public Control control;
        public String name;

        public CharacteristicField(Control control, String name) {
            this.control = control;
            this.name = name;
        }
    }

}
