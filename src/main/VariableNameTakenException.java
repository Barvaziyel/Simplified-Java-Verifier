package oop.ex6.main;

/**
 * Exception for when trying to declare variable with name that is already taken by previously declared
 * variable
 */
public class VariableNameTakenException extends Exception {
    public VariableNameTakenException() {
        super();
    }

    public VariableNameTakenException(String message) {
        super(message);
    }
}
