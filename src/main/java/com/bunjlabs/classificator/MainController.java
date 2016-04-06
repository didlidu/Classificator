package com.bunjlabs.classificator;

import com.bunjlabs.classificator.db.Database;
import com.bunjlabs.classificator.editor.ClassEditorController;
import com.bunjlabs.classificator.editor.ClassRow;
import com.bunjlabs.classificator.tools.Characteristic;
import com.bunjlabs.classificator.tools.PossibleCharacteristics;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    private TableColumn<ClassRow, String> classNameColumn;
    @FXML
    private TableColumn<ClassRow, String> classDescriptionColumn;
    @FXML
    private TableView<ClassRow> classesTable;

    public ObservableList<ClassRow> data = Database.getInstance().getRowsForTableView();

    public void refreshTable() {
        classesTable.refresh();
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        classNameColumn.setCellValueFactory(new PropertyValueFactory<ClassRow, String>("classProperty"));
        classDescriptionColumn.setCellValueFactory(new PropertyValueFactory<ClassRow, String>("description"));
        classesTable.setItems(data);
        solverParamsChoiceBox.setItems(characteristicsData);
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
    }

    @FXML
    public TextField objectNameField;
    @FXML
    public Accordion solverAccordion;
    @FXML
    public ChoiceBox solverParamsChoiceBox;
    
    private List<PossibleCharacteristics.CharacteristicField> characteristicsFields = new ArrayList<>();

    private Map<String, Characteristic.Range> characteristicsRange = PossibleCharacteristics.getCharacteristicsRange();
    private Map<String, Characteristic.Type> characteristicsType = PossibleCharacteristics.getCharacteristicsType();

    private ObservableList<String> characteristicsData
            = FXCollections.observableArrayList(characteristicsType.keySet());
    
    @FXML
    private void handleSolveButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void handleAddParameterButtonAction(ActionEvent event) {
        
    }
}
