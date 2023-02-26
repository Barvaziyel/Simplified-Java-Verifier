package oop.ex6.main;

/**
 * Exception for when syntax of code is bad
 */
public class SyntaxException extends Exception {
    public SyntaxException() {
        super();
    }

    public SyntaxException(String message) {
        super(message);
    }
}
