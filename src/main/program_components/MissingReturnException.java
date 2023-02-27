package oop.ex6.main.program_components;

/**
 * Exception for when missing return at the end of a method declaration
 */
public class MissingReturnException extends Exception {
    public MissingReturnException() {
        super();
    }

    public MissingReturnException(String message) {
        super(message);
    }
}
