package com.bunjlabs.classificator.db;

import com.bunjlabs.classificator.editor.ClassRow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Database {

    private static Database instance;

    private Gson gson = new Gson();
    private Collection<ClassDAO> list = new LinkedList<>();
    private String filePath = "knowlages.json";

    private Database() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("[]");
                writer.flush();
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));

            Type collectionType = new TypeToken<LinkedList<ClassDAO>>() {
            }.getType();
            list = gson.fromJson(reader, collectionType);
            if (list == null) {
                list = gson.fromJson("[]", collectionType);
            }

        } catch (IOException ex) {
            System.out.println("Error while opening file " + filePath);
            System.exit(0);
        }
    }

    synchronized public void add(ClassDAO c) {
        ClassDAO conflictClass = findByName(c.getName());
        if (conflictClass == null) {
            getList().add(c);
        } else {
            conflictClass.replaceWith(c);
        }
    }

    synchronized public void removeByName(String name) {
        getList().removeIf((el) -> {
            return el.getName().equals(name);
        });
    }

    synchronized public ClassDAO findByName(String name) {
        for (ClassDAO el : getList()) {
            if (el.getName().equals(name)) {
                return el;
            }
        }
        return null;
    }

    synchronized public boolean flush() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(gson.toJson(getList()));
            writer.flush();
        } catch (IOException ex) {
            System.out.println("Error while writing to file " + filePath);
            return false;
        }
        return true;
    }

    public ObservableList<ClassRow> getRowsForTableView() {
        ObservableList<ClassRow> data = FXCollections.observableArrayList();
        getList().forEach((el) -> {
            data.add(
                    new ClassRow(
                            el.getName(),
                            el.getCharacteristics().toString()
                    )
            );
        });
        return data;
    }

    synchronized public static Database getInstance() {
        return (instance == null) ? instance = new Database() : instance;
    }

    public Collection<ClassDAO> getList() {
        return list;
    }

    public void removeByCharacteristicName(String name) {
        getList().removeIf((el) -> {
            return el.getCharacteristics().containsKey(name);
        });
    }
}
