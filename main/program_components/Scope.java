package oop.ex6.main.program_components;

import oop.ex6.main.databases.LinesDatabase;
import oop.ex6.main.*;

import java.util.ArrayList;
import java.util.HashMap;

import static oop.ex6.main.Driver.BAD_SYNTAX_ERR_MSG;

/**
 * Class of a scope of code
 */
public class Scope {
    private final HashMap<String, Variable> vars = new HashMap<>();
    final private ArrayList<Scope> superScopes;
    final private int lineNum;

    /**
     * constructor
     *
     * @param superScopes list of scopes that this scope is inside, in ascending order
     * @param lineNum     line number this scope starts at
     */
    public Scope(ArrayList<Scope> superScopes, int lineNum) {
        this.superScopes = superScopes;
        this.lineNum = lineNum;
    }

    /**
     * adds variable to this scope's variable list
     *
     * @param var
     */
    public void addVar(Variable var) {
        vars.put(var.getName(), var);
    }

    /**
     * checks if variable exists in scope
     *
     * @param name name of variable to find
     * @return true if found, else false
     */
    public boolean contains(String name) {
        return vars.containsKey(name);
    }

    /**
     * gets variable in this scope.
     *
     * @param name name of variable to find
     * @return variable found, else null
     */
    public Variable getVar(String name) {
        return vars.get(name);
    }

    /**
     * checks the validity of the scope
     *
     * @return returns the number of the last line of the scope
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     * @throws MethodNotFoundException     if calls method that isn't found
     * @throws MissingReturnException      if missing return statement
     */
    public int checkValidity() throws IllegalAssignmentException, ExpectedAssignmentException,
            SyntaxException, VariableNameTakenException, IllegalNameException, MethodNotFoundException,
            MissingReturnException {
        String line;
        int curLineNum = lineNum;
        while (curLineNum < LinesDatabase.getLinesNum()) {
            curLineNum++;
            line = LinesDatabase.getLine(curLineNum);
            //is statement?
            if (handledStatement(line)) continue;
            //is opening scope?
            if (Driver.isScopeOpeningSuffix(line)) {
                Driver.handleIfWhileStatement(line, vars, superScopes);
                curLineNum = openScope(curLineNum);
                continue;
            }
            //is close scope?
            if (Driver.closeScope.matcher(line).matches()) { // closing the scope
                break;
            }
            throw new SyntaxException(BAD_SYNTAX_ERR_MSG); // can't be anything else
        }
        return curLineNum;
    }

    /**
     * handles statements
     *
     * @return if statement, checks validity. if valid returns true. if invalid, throws exception.
     * if not statement, returns false.
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     * @throws MethodNotFoundException     if calls method that isn't found
     */
    private boolean handledStatement(String line) throws SyntaxException, IllegalAssignmentException,
            ExpectedAssignmentException, VariableNameTakenException, IllegalNameException,
            MethodNotFoundException {
        if (Driver.isStatementSuffix(line)) {
            if (Driver.returnPattern.matcher(line).matches()) { // is return statement?
                return true;
            } else if (Driver.handleDeclaration(line, vars, superScopes)) { //is declaration?
                return true;
            } else if (Driver.handleMethodCall(line, vars, superScopes)) {  // is method call?
                return true;
            } else if (Driver.handleAssignment(line, vars, superScopes)) { //is assignment?
                return true;
            }
            throw new SyntaxException(BAD_SYNTAX_ERR_MSG); //if not any of the above, is error
        }
        return false;
    }

    /**
     * opens a new scope and checks its validity
     *
     * @param curLineNum line number where new scope opens
     * @return returns ending line of new scope
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     * @throws MethodNotFoundException     if calls method that isn't found
     * @throws MissingReturnException      if missing return statement
     */
    private int openScope(int curLineNum) throws IllegalAssignmentException,
            ExpectedAssignmentException, SyntaxException, VariableNameTakenException, IllegalNameException,
            MethodNotFoundException, MissingReturnException {
        ArrayList<Scope> newSuperScopes = new ArrayList<>();
        for (Scope scope : superScopes) {
            newSuperScopes.add(scope);
        }
        newSuperScopes.add(0, this);
        Scope innerScope = new Scope(newSuperScopes, curLineNum);
        return innerScope.checkValidity();
    }
}
