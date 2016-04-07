package com.bunjlabs.classificator.db;

import com.bunjlabs.classificator.editor.CharacteristicRow;
import com.bunjlabs.classificator.tools.Characteristic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

public class PossibleCharacteristics {

    public static class TypeRange {

        public Characteristic.Range range;
        public Characteristic.Type type;

        public TypeRange(Characteristic.Range range, Characteristic.Type type) {
            this.range = range;
            this.type = type;
        }
    }

    private static PossibleCharacteristics instance;
    private Gson gson = new Gson();
    private Map<String, TypeRange> list = new HashMap<>();
    private String filePath = "characteristics.json";

    private PossibleCharacteristics() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("{}");
                writer.flush();
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));

            Type collectionType = new TypeToken<Map<String, TypeRange>>() {
            }.getType();
            list = gson.fromJson(reader, collectionType);
            if (list == null) {
                list = gson.fromJson("{}", collectionType);
            }

        } catch (IOException ex) {
            System.out.println("Error while opening file " + filePath);
            System.exit(0);
        }
    }

    public static class CharacteristicField {

        public Control control;
        public String name;

        public CharacteristicField(Control control, String name) {
            this.control = control;
            this.name = name;
        }
    }

    synchronized public void add(String s, TypeRange tr) {
        list.put(s, tr);
    }

    synchronized public void removeByName(String name) {
        list.remove(name);
    }

    synchronized public TypeRange findByName(String name) {
        return list.get(name);
    }

    synchronized public boolean flush() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(gson.toJson(list));
            writer.flush();
        } catch (IOException ex) {
            System.out.println("Error while writing to file " + filePath);
            return false;
        }
        return true;
    }

    public ObservableList<CharacteristicRow> getRowsForTableView() {
        ObservableList<CharacteristicRow> data = FXCollections.observableArrayList();
        list.forEach((s, rt) -> {
            data.add(
                    new CharacteristicRow(
                            s,
                            rt.type.name(),
                            rt.type == Characteristic.Type.NAME || rt.type == Characteristic.Type.NAME_SET ? rt.range.names.toString() : rt.range.toString()
                    )
            );
        });
        return data;
    }

    public Map<String, TypeRange> getMap() {
        return this.list;
    }

    /*static {
     List<String> list = Arrays.asList("a", "b", "c");

     characteristicsType.put("param a", Characteristic.Type.NAME_SET);
     characteristicsRange.put("param a", new Characteristic.Range(list));
     characteristicsType.put("param b", Characteristic.Type.NUMBER_RANGE);
     characteristicsRange.put("param b", new Characteristic.Range(0, 100));
     }*/
    public static PossibleCharacteristics getInstance() {
        return (instance == null) ? instance = new PossibleCharacteristics() : instance;
    }
}
