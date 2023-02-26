package oop.ex6.main.program_components;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * enum of types
 */
public enum Type {
    INT, DOUBLE, BOOLEAN, CHAR, STRING;

    public static final String INT_NAME = "int";
    public static final String DOUBLE_NAME = "double";
    public static final String CHAR_NAME = "char";
    public static final String BOOLEAN_NAME = "boolean";
    public static final String STRING_NAME = "String";
    public static final Pattern stringPattern = Pattern.compile("^\\s*\"[^\"]*\"\\s*");
    public static final Pattern charPattern = Pattern.compile("^\\s*'[^']'\\s*");
    public static final Pattern intPattern = Pattern.compile("^\\s*[-+]?\\d+\\s*");
    public static final Pattern doublePattern =
            Pattern.compile("^\\s*[+-]?(\\d+\\.?\\d*|\\d*\\.\\d+)\\s*");
    public static final Pattern booleanPattern =
            Pattern.compile("^\\s*(true|false|[+-]?(\\d+\\.?\\d*|\\d*\\.\\d+))\\s*");

    /**
     * gets a list of patterns of the accepted assignment types of the input type
     *
     * @param type input type
     * @return list of patterns of the accepted assignment types of the input type
     */
    public static List<Pattern> getTypePatterns(Type type) {
        ArrayList<Pattern> acceptedPatterns = new ArrayList<>();
        switch (type) {
            case INT:
                acceptedPatterns.add(intPattern);
                return acceptedPatterns;
            case DOUBLE:
                acceptedPatterns.add(doublePattern);
                acceptedPatterns.add(intPattern);
                return acceptedPatterns;
            case CHAR:
                acceptedPatterns.add(charPattern);
                return acceptedPatterns;
            case BOOLEAN:
                acceptedPatterns.add(booleanPattern);
                acceptedPatterns.add(doublePattern);
                acceptedPatterns.add(intPattern);
                return acceptedPatterns;
            case STRING:
                acceptedPatterns.add(stringPattern);
                return acceptedPatterns;
            default:
                return null;
        }
    }

    /**
     * checks if receiver can accept the type of giver
     *
     * @param receiver receiver type
     * @param giver    giver type
     * @return true if acceptable, else false
     */
    public static boolean equals(Type receiver, Type giver) {
        switch (receiver) {
            case INT:
                return giver == Type.INT;
            case DOUBLE:
                return giver == Type.DOUBLE || giver == Type.INT;
            case CHAR:
                return giver == Type.CHAR;
            case BOOLEAN:
                return giver == Type.BOOLEAN || giver == Type.DOUBLE || giver == Type.INT;
            case STRING:
                return giver == Type.STRING;
            default:
                return false;
        }
    }

    /**
     * type getter from string
     *
     * @param name type name
     * @return matching Type
     */
    public static Type getType(String name) {
        switch (name) {
            case INT_NAME:
                return Type.INT;
            case DOUBLE_NAME:
                return Type.DOUBLE;
            case CHAR_NAME:
                return Type.CHAR;
            case BOOLEAN_NAME:
                return Type.BOOLEAN;
            case STRING_NAME:
                return Type.STRING;
            default:
                return null;
        }
    }
}
