package com.bunjlabs.classificator.tools;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class WindowBuilder {

    public static void alert(AlertType atype, String head, String body) {
        Alert alert = new Alert(atype);
        alert.setTitle(head);
        alert.setContentText(body);
        alert.show();
    }
}
