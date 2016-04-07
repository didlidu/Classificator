package com.bunjlabs.classificator.editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClassRow {

    private StringProperty classProperty;
    private StringProperty description;

    public ClassRow() {
        classProperty = new SimpleStringProperty("class");
        description = new SimpleStringProperty("description");
    }

    public ClassRow(String classProperty, String description) {
        this.classProperty = new SimpleStringProperty(classProperty);
        this.description = new SimpleStringProperty(description);
    }

    public String getClassProperty() {
        return classProperty.get();
    }

    public String getDescription() {
        return description.get();
    }

    public void setClassProperty(String classProperty) {
        this.classProperty.set(classProperty);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

}
