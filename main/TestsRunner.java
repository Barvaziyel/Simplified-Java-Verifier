package oop.ex6.main;

import oop.ex6.main.program_components.MissingReturnException;

import java.io.IOException;
import java.util.ArrayList;

import static oop.ex6.main.Sjavac.*;

public class TestsRunner {

    private static final String BASIC_DEC_TEST = "tests/var_test_basic.sjava";
    private static final String LEGAL_TEST = "tests/legal.sjava";
    private static final String ILLEGAL_TEST = "tests/illegal.sjava";
    private static final String LEGAL_FULL_TEST = "tests/test full.sjava";

    private static final int TESTS_AMOUNT = 47;



    private static final ArrayList<String> PATHS = new ArrayList<>();

    public static void initTests(){
//        PATHS.add(BASIC_DEC_TEST);
//        PATHS.add(LEGAL_TEST);
//        PATHS.add(LEGAL_FULL_TEST);
//        PATHS.add(ILLEGAL_TEST);

        for (int i = 1; i <= TESTS_AMOUNT; i++) {
            PATHS.add("tests/tests for submission/" + i + ".sjava");
        }
    }

    public static void runTests()  {
        for (var path: PATHS){
            try {
                Driver.checkValidity(path);
                System.out.println(path + ": ");
                System.out.println(SUCCESS_EXIT_CODE+"\n");
            } catch (ScopeException | IllegalNameException | IllegalAssignmentException |
                     ExpectedAssignmentException | VariableNameTakenException | MethodNameTakenException |
                     MethodNotFoundException | MissingReturnException | SyntaxException e) {
                System.out.println(path + ": ");
                System.out.println(EXCEPTION_ERR_CODE);
                System.out.println(e+"\n");
            } catch (IOException e) {
                System.out.println(path + ": ");
                System.out.println(IOEXCEPTION_EXIT_CODE);
                System.out.println(e+"\n");
            }
        }
    }
}
