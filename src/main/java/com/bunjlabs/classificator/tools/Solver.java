package com.bunjlabs.classificator.tools;

import com.bunjlabs.classificator.db.ClassDAO;
import com.bunjlabs.classificator.db.Database;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Solver {

    public static String solve(ClassDAO object) {
        List<ClassDAO> list = (List<ClassDAO>) Database.getInstance().getList();
        List<ClassDAO> matches = new LinkedList<>();
        for (ClassDAO clazz : list) {
            boolean mached = true;
            for (Map.Entry<String, Characteristic> entry : object.getCharacteristics().entrySet()) {
                if (!clazz.getCharacteristics().containsKey(entry.getKey())
                        || !isMatches(entry.getValue(), clazz.getCharacteristics().get(entry.getKey()))) {
                    mached = false;
                    break;
                }
            }
            if (mached) {
                matches.add(clazz);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Classification of object '");
        sb.append(object.getName());
        sb.append("' with characteristics:\n\n");
        sb.append(object.getCharacteristics().toString());
        sb.append("\n\n");
        if (matches.isEmpty()) {
            sb.append("No classes was found for current object");
        } else {
            sb.append("Classes that matches the current object:\n");

            matches.forEach((clazz) -> {
                sb.append("Class '").append(clazz.getName());
                sb.append("' with characteristics:\n");
                sb.append("\t\t").append(clazz.getCharacteristics().toString()).append("\n");
            });

        }
        sb.append("\n\n");

        return sb.toString();
    }

    private static boolean isMatches(Characteristic object, Characteristic clazz) {
        if (object.getType() != clazz.getType()) {
            throw new RuntimeException("Types of characteristics mismatch");
        }
        switch (object.getType()) {
            case NAME:
                return object.getName().equals(clazz.getName());
            case NAME_SET:
                for (String objName : object.getNameSet()) {
                    if (!clazz.getNameSet().contains(objName)) {
                        return false;
                    }
                }
                return true;
            case NUMBER:
                return object.getNumber() == clazz.getNumber();
            case NUMBER_RANGE:
                return object.getNumberFrom() >= clazz.getNumberFrom() && object.getNumberTo() <= clazz.getNumberTo();
        }
        return false;
    }
}