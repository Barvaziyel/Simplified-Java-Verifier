package oop.ex6.main;

/**
 * Exception for when bad name of variable
 */
public class IllegalNameException extends Exception {
    public IllegalNameException() {
        super();
    }

    public IllegalNameException(String message) {
        super(message);
    }
}
