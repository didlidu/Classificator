package com.bunjlabs.classificator;

import com.bunjlabs.classificator.db.Database;
import com.bunjlabs.classificator.editor.ClassEditorController;
import com.bunjlabs.classificator.editor.ClassRow;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainController implements Initializable {
    
    @FXML private TableColumn<ClassRow, String> classNameColumn;
    @FXML private TableColumn<ClassRow, String> classDescriptionColumn;
    @FXML private TableView<ClassRow> classesTable;
    
    public ObservableList<ClassRow> data = Database.getInstance().getRowsForTableView();
    
    public void refreshTable() {
        classesTable.refresh();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        classNameColumn.setCellValueFactory(new PropertyValueFactory<ClassRow, String>("classProperty"));
        classDescriptionColumn.setCellValueFactory(new PropertyValueFactory<ClassRow, String>("description"));
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
}
