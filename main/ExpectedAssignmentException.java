package oop.ex6.main;

/**
 * Exception if no assignment when assignment is needed
 */
public class ExpectedAssignmentException extends Exception {
    public ExpectedAssignmentException() {
        super();
    }

    public ExpectedAssignmentException(String message) {
        super(message);
    }
}
