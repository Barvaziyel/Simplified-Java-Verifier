package oop.ex6.main;

/**
 * Exception for when scopes are bad
 */
public class ScopeException extends Exception {
    public ScopeException() {
        super();
    }

    public ScopeException(String message) {
        super(message);
    }
}
