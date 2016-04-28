package com.bunjlabs.classificator;

import com.bunjlabs.classificator.db.ClassDAO;
import com.bunjlabs.classificator.db.Database;
import com.bunjlabs.classificator.db.PossibleCharacteristics;
import com.bunjlabs.classificator.editor.CharacteristicEditorController;
import com.bunjlabs.classificator.editor.CharacteristicRow;
import com.bunjlabs.classificator.editor.ClassEditorController;
import com.bunjlabs.classificator.editor.ClassRow;
import com.bunjlabs.classificator.tools.Characteristic;
import com.bunjlabs.classificator.tools.Loginer;
import com.bunjlabs.classificator.tools.Solver;
import com.bunjlabs.classificator.tools.WindowBuilder;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

public class MainController implements Initializable {

    @FXML
    private TableColumn<ClassRow, String> classNameColumn;
    @FXML
    private TableColumn<ClassRow, String> classDescriptionColumn;
    @FXML
    private TableView<ClassRow> classesTable;
    @FXML
    private Tab answerTab;

    @FXML
    public Tab knowlagesTab;
    @FXML
    public Tab characteristicsTab;

    @FXML
    private TableView<CharacteristicRow> characteristicsTable;
    @FXML
    private TableColumn<CharacteristicRow, String> characteristicNameColumn;
    @FXML
    private TableColumn<CharacteristicRow, String> characteristiDescriptionColumn;
    @FXML
    private TableColumn<CharacteristicRow, String> characteristiRangeColumn;

    public ObservableList<CharacteristicRow> charData = PossibleCharacteristics.getInstance().getRowsForTableView();

    public ObservableList<ClassRow> data = Database.getInstance().getRowsForTableView();

    public void setAccessLevel(Loginer.AccessLevel accessLevel) {
        if (accessLevel == Loginer.AccessLevel.ADMIN) {
            knowlagesTab.setDisable(false);
            characteristicsTab.setDisable(false);
        }
    }

    public void refreshTable() {
        classesTable.refresh();
    }

    public void refreshCharTable() {
        characteristicsTable.refresh();
    }

    public void refreshSolverParamsChoiceBox() {
        solverParamsChoiceBox.setItems(
                characteristicsData = FXCollections.observableArrayList(PossibleCharacteristics.getInstance().getMap().keySet()));
    }

    public void addToTable(ClassRow row) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getClassProperty().equals(row.getClassProperty())) {
                data.set(i, row);
                return;
            }
        }
        data.add(row);
    }

    public void addToCharTable(CharacteristicRow row) {
        for (int i = 0; i < charData.size(); i++) {
            if (charData.get(i).getName().equals(row.getName())) {
                charData.set(i, row);
                return;
            }
        }
        charData.add(row);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        classNameColumn.setCellValueFactory(new PropertyValueFactory<ClassRow, String>("classProperty"));
        classDescriptionColumn.setCellValueFactory(new PropertyValueFactory<ClassRow, String>("description"));
        classesTable.setItems(data);
        solverParamsChoiceBox.setItems(characteristicsData);

        characteristicNameColumn.setCellValueFactory(new PropertyValueFactory<CharacteristicRow, String>("name"));
        characteristiDescriptionColumn.setCellValueFactory(new PropertyValueFactory<CharacteristicRow, String>("type"));
        characteristiRangeColumn.setCellValueFactory(new PropertyValueFactory<CharacteristicRow, String>("range"));
        characteristicsTable.setItems(charData);
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        Parent root;
        try {
            FXMLLoader fXMLLoader = new FXMLLoader();
            root = fXMLLoader.load(getClass().getResource("/fxml/ClassEditorScene.fxml").openStream());
            ClassEditorController controller = fXMLLoader.getController();
            controller.mainController = this;
            Stage stage = new Stage();
            stage.setTitle("New Class");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditButtonAction(ActionEvent event) {
        ClassRow row = classesTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            return;
        }
        Parent root;
        try {
            FXMLLoader fXMLLoader = new FXMLLoader();
            root = fXMLLoader.load(getClass().getResource("/fxml/ClassEditorScene.fxml").openStream());
            ClassEditorController controller = fXMLLoader.getController();
            controller.mainController = this;
            controller.setEditingClass(row.getClassProperty());
            Stage stage = new Stage();
            stage.setTitle("Edit Class " + row.getClassProperty());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonAction(ActionEvent event) {
        ClassRow row = classesTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            return;
        }
        data.remove(row);
        Database.getInstance().removeByName(row.getClassProperty());
        Database.getInstance().flush();
    }

    @FXML
    public TextField objectNameField;
    @FXML
    public Accordion solverAccordion;
    @FXML
    public ChoiceBox solverParamsChoiceBox;
    @FXML
    public TextArea answerTextArea;

    private List<PossibleCharacteristics.CharacteristicField> characteristicsFields = new ArrayList<>();

    private ObservableList<String> characteristicsData
            = FXCollections.observableArrayList(PossibleCharacteristics.getInstance().getMap().keySet());

    @FXML
    private void handleSolveButtonAction(ActionEvent event) {
        String objectName = objectNameField.getText();
        if (objectName.isEmpty()) {
            WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve", "Object name is empty");
            return;
        }
        if (characteristicsFields.isEmpty()) {
            WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve", "Object has no characterisrics");
            return;
        }
        Map<String, Characteristic> map = new HashMap<>();
        ClassDAO object = new ClassDAO(objectName, map);
        for (PossibleCharacteristics.CharacteristicField field : characteristicsFields) {
            Characteristic.Type type = PossibleCharacteristics.getInstance().findByName(field.name).type;
            Characteristic.Range range = PossibleCharacteristics.getInstance().findByName(field.name).range;
            switch (type) {
                case NAME:
                    String selected = (String) ((ChoiceBox<String>) field.control).getSelectionModel().getSelectedItem();
                    if (selected == null) {
                        WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve", "ChoiseBox item is not selected");
                        return;
                    }
                    map.put(field.name, new Characteristic(selected));
                    break;
                case NAME_SET:
                    List<String> checked = ((CheckComboBox<String>) field.control).getCheckModel().getCheckedItems();
                    if (checked == null || checked.isEmpty()) {
                        WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve", "CheckComboBox items are not selected");
                        return;
                    }
                    map.put(field.name, new Characteristic(checked));
                    break;
                case NUMBER:
                    double number;
                    try {
                        number = Double.parseDouble(((TextField) field.control).getText());
                    } catch (NumberFormatException ex) {
                        WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve", "Not a number in TextField");
                        return;
                    }
                    if (number < range.from || number > range.to) {
                        WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve",
                                "Number of parameter '" + field.name + "' is not in range " + range);
                        return;
                    }
                    map.put(field.name, new Characteristic(number));
                    break;
                case NUMBER_RANGE:
                    try {
                        double from = Double.parseDouble(((TextField) field.control).getText());
                        double to = from;
                        map.put(field.name, new Characteristic(from, to));
                    } catch (NumberFormatException ex) {
                        String[] numbers = ((TextField) field.control).getText().split("-");
                        try {
                            double from = Double.parseDouble(numbers[0]);
                            double to = Double.parseDouble(numbers[1]);
                            if (from > to || from < range.from || to > range.to) {
                                WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve",
                                        "Range of parameter '" + field.name + "' is not in range " + range + " or invalid");
                                return;
                            }
                            map.put(field.name, new Characteristic(
                                    from,
                                    to
                            ));
                        } catch (NumberFormatException e) {
                            WindowBuilder.alert(Alert.AlertType.WARNING, "Cannot solve", "Not a range in TextField");
                            return;
                        }
                    }
                    break;
            }
        }
        solve(object);
    }

    private Control addPaneToAccordion(Characteristic.Type type, String characteristicName) {
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        Control control = null;
        Characteristic.Range range = PossibleCharacteristics.getInstance().findByName(characteristicName).range;
        switch (type) {
            case NAME:
                control = new ChoiceBox<String>();
                ((ChoiceBox<String>) control).setItems(
                        FXCollections.observableArrayList(PossibleCharacteristics.getInstance().findByName(characteristicName).range.names));
                grid.add(control, 0, 0);
                gridTitlePane.setText(characteristicName + " Name");
                break;
            case NAME_SET:
                control = new CheckComboBox<String>(FXCollections.observableArrayList(
                        PossibleCharacteristics.getInstance().findByName(characteristicName).range.names));
                grid.add(control, 0, 0);
                gridTitlePane.setText(characteristicName + " Name set");
                break;
            case NUMBER:
                control = new TextField();
                grid.add(control, 0, 0);
                gridTitlePane.setText(characteristicName + " Number: [" + range.from + ", " + range.to + "]");
                break;
            case NUMBER_RANGE:
                control = new TextField();
                grid.add(control, 0, 0);
                gridTitlePane.setText(characteristicName + " Number Range [" + range.from + ", " + range.to + "]");
                break;
        }
        characteristicsFields.add(new PossibleCharacteristics.CharacteristicField(control, characteristicName));
        gridTitlePane.setContent(grid);
        solverAccordion.getPanes().add(gridTitlePane);
        return control;
    }

    @FXML
    private void handleAddParameterButtonAction(ActionEvent event) {
        String characteristicName = (String) solverParamsChoiceBox.getSelectionModel().getSelectedItem();
        if (characteristicName == null) {
            return;
        }
        Characteristic.Type type = PossibleCharacteristics.getInstance().findByName(characteristicName).type;
        characteristicsData.remove(characteristicName);
        addPaneToAccordion(type, characteristicName);
    }

    private void solve(ClassDAO object) {
        solverAccordion.getPanes().clear();
        characteristicsFields.clear();
        objectNameField.clear();
        characteristicsData = FXCollections.observableArrayList(PossibleCharacteristics.getInstance().getMap().keySet());
        solverParamsChoiceBox.setItems(characteristicsData);
        answerTab.setDisable(false);
        String answer = Solver.solve(object);
        answerTextArea.setText(answer);
    }

    @FXML
    public void handleAddChButtonAction(ActionEvent event) {
        Parent root;
        try {
            FXMLLoader fXMLLoader = new FXMLLoader();
            root = fXMLLoader.load(getClass().getResource("/fxml/CharacteristicEditorScene.fxml").openStream());
            CharacteristicEditorController controller = fXMLLoader.getController();
            controller.mainController = this;
            Stage stage = new Stage();
            stage.setTitle("New Characteristic");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteChButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation dialog");
        alert.setHeaderText("Are you shure want to delete this characteristic?");
        alert.setContentText("That action will remove all knowlages that uses this characteristic.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            CharacteristicRow row = characteristicsTable.getSelectionModel().getSelectedItem();
            if (row == null) {
                return;
            }
            this.charData.remove(row);
            PossibleCharacteristics.getInstance().removeByName(row.getName());
            PossibleCharacteristics.getInstance().flush();
            Database.getInstance().removeByCharacteristicName(row.getName());
            Database.getInstance().flush();
            classesTable.setItems(data = Database.getInstance().getRowsForTableView());
            this.refreshSolverParamsChoiceBox();
        }
    }

}
