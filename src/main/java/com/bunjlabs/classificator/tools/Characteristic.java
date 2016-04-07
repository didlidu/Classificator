package com.bunjlabs.classificator.tools;

import java.util.List;

public class Characteristic {

    public enum Type {

        NAME,
        NAME_SET,
        NUMBER,
        NUMBER_RANGE
    }

    public static class Range {

        public double from;
        public double to;
        public List<String> names;

        public Range(double from, double to) {
            this.from = from;
            this.to = to;
        }

        public Range(List<String> names) {
            this.names = names;
        }

        @Override
        public String toString() {
            return "(" + from + ", " + to + ")";
        }

    }

    private Type type;
    private String name;
    private List<String> nameSet;
    private double numberFrom;
    private double numberTo;

    public Characteristic(String name) {
        this.type = Type.NAME;
        this.name = name;
    }

    public Characteristic(List<String> nameSet) {
        this.type = Type.NAME_SET;
        this.nameSet = nameSet;
    }

    public Characteristic(double number) {
        this.type = Type.NUMBER;
        this.numberFrom = this.numberTo = number;
    }

    public Characteristic(double numberFrom, double numberTwo) {
        this.type = Type.NUMBER_RANGE;
        this.numberFrom = numberFrom;
        this.numberTo = numberTwo;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        if (type != Type.NAME) {
            throw new ClassCastException("Characteristic type is not NAME");
        }
        return name;
    }

    public List<String> getNameSet() {
        if (type != Type.NAME_SET) {
            throw new ClassCastException("Characteristic type is not NAME_SET");
        }
        return nameSet;
    }

    public double getNumber() {
        if (type != Type.NUMBER) {
            throw new ClassCastException("Characteristic type is not NUMBER");
        }
        return numberTo;
    }

    public double getNumberFrom() {
        if (type != Type.NUMBER_RANGE) {
            throw new ClassCastException("Characteristic type is not NUMBER_RANGE");
        }
        return numberFrom;
    }

    public double getNumberTo() {
        if (type != Type.NUMBER_RANGE) {
            throw new ClassCastException("Characteristic type is not NUMBER_RANGE");
        }
        return numberTo;
    }

    @Override
    public String toString() {
        switch (type) {
            case NAME:
                return this.name;
            case NAME_SET:
                String str = "|";
                for (String el : this.nameSet) {
                    str += el + "|";
                }
                return str;
            case NUMBER:
                return "" + this.numberFrom;
            case NUMBER_RANGE:
                return "" + this.numberFrom + "-" + this.numberTo;
        }
        return "";
    }

}
