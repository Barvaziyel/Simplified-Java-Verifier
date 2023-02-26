package oop.ex6.main;

/**
 * Exception for when calling method that hasn't been declared
 */
public class MethodNotFoundException extends Exception {
    public MethodNotFoundException() {
        super();
    }

    public MethodNotFoundException(String message) {
        super(message);
    }
}
