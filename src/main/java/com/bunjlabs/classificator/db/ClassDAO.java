package com.bunjlabs.classificator.db;

import com.bunjlabs.classificator.tools.Characteristic;
import java.util.HashMap;
import java.util.Map;


public class ClassDAO implements Flushable {
    
    private String name;
    private Map<String, Characteristic> characteristics;
    
    public ClassDAO() {
        this.name = "class";
        this.characteristics = new HashMap<>();
    }
    
    public ClassDAO(String name, Map<String, Characteristic> characteristics) {
        this.name = name;
        this.characteristics = characteristics;
    }

    @Override
    public void flush() {
        Database.getInstance().add(this);
    }
    
    public void replaceWith(ClassDAO c) {
        this.name = c.name;
        this.characteristics = c.characteristics;
    }

    public String getName() {
        return name;
    }

    public Map<String, Characteristic> getCharacteristics() {
        return characteristics;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCharacteristics(Map<String, Characteristic> characteristics) {
        this.characteristics = characteristics;
    }
    
}
