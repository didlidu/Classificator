package com.bunjlabs.classificator.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Control;


public class PossibleCharacteristics {
    
    public static class CharacteristicField {

        public Control control;
        public String name;

        public CharacteristicField(Control control, String name) {
            this.control = control;
            this.name = name;
        }
    }
    
    private static Map<String, Characteristic.Range> characteristicsRange = new HashMap<>();
    private static Map<String, Characteristic.Type> characteristicsType = new HashMap<>();

    static {
        List<String> list = Arrays.asList("a", "b", "c");

        characteristicsType.put("param a", Characteristic.Type.NAME_SET);
        characteristicsRange.put("param a", new Characteristic.Range(list));
        characteristicsType.put("param b", Characteristic.Type.NUMBER_RANGE);
        characteristicsRange.put("param b", new Characteristic.Range(0, 100));
    }
    
    public static Map<String, Characteristic.Range> getCharacteristicsRange() {
        return characteristicsRange;
    }
    
    public static Map<String, Characteristic.Type> getCharacteristicsType() {
        return characteristicsType;
    }
}
