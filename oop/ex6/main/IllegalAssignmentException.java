package oop.ex6.main;

/**
 * Exception for when assignement is illegal
 */
public class IllegalAssignmentException extends Exception {
    public IllegalAssignmentException() {
        super();
    }

    public IllegalAssignmentException(String message) {
        super(message);
    }
}
