package com.bunjlabs.classificator.editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CharacteristicRow {

    private StringProperty name;
    private StringProperty type;
    private StringProperty range;

    public CharacteristicRow(String name, String type, String range) {
        this.name = new SimpleStringProperty(name);
        this.range = new SimpleStringProperty(range);
        this.type = new SimpleStringProperty(type);
    }

    public String getName() {
        return name.get();
    }

    public String getType() {
        return type.get();
    }

    public String getRange() {
        return range.get();
    }

}
