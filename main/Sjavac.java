package oop.ex6.main;

import oop.ex6.main.program_components.MissingReturnException;

import java.io.IOException;

/**
 * class containing main method
 */
public class Sjavac {

    public static final int PATH_ARG = 0;
    public static final int SUCCESS_EXIT_CODE = 0;
    public static final int EXCEPTION_ERR_CODE = 1;
    public static final int IOEXCEPTION_EXIT_CODE = 2;

    /**
     * main method
     *
     * @param args input args, should have path of sjavac file
     */
    public static void main(String[] args) {
        String path = args[PATH_ARG];
        try {
            Driver.checkValidity(path);
            System.out.println(SUCCESS_EXIT_CODE); //success! code is valid
        } catch (ScopeException | IllegalNameException | IllegalAssignmentException |
                 ExpectedAssignmentException | VariableNameTakenException | MethodNameTakenException |
                 MethodNotFoundException | MissingReturnException | SyntaxException e) {
            System.out.println(EXCEPTION_ERR_CODE); // invalid code
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(IOEXCEPTION_EXIT_CODE); //IO exception, bad file reading
            System.err.println(e.getMessage());
        }
    }
}