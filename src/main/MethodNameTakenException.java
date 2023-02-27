package oop.ex6.main;

/**
 * Exception for when trying to declare a method with a name that already exists
 */
public class MethodNameTakenException extends Exception {
    public MethodNameTakenException() {
        super();
    }

    public MethodNameTakenException(String message) {
        super(message);
    }
}
