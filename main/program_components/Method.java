package oop.ex6.main.program_components;

import oop.ex6.main.databases.LinesDatabase;
import oop.ex6.main.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing a method in the code. Extends Scope class
 */
public class Method extends Scope {
    public static final String METHOD_MISSING_RETURN_ERR_MSG = "Method missing return statement!";
    private final int argsNum;
    private final ArrayList<Type> paramTypes = new ArrayList<>();
    private final HashMap<String, Variable> globalVars = new HashMap<>();
    private final Scope scope;
    private final String name;

    /**
     * Constructor
     *
     * @param name    name of method
     * @param params  list of var parameters function requires
     * @param lineNum number of line method is declared at
     */
    public Method(String name, List<Variable> params, int lineNum) {
        super(new ArrayList<>(), lineNum);
        this.name = name;
        argsNum = params.size();
        ArrayList<Scope> globalSuperScopes = new ArrayList<>();
        globalSuperScopes.add(this);
        scope = new Scope(globalSuperScopes, lineNum);
        for (Variable param : params) {
            paramTypes.add(param.getType());
            scope.addVar(param);
        }
    }

    /**
     * adds global variables to method
     *
     * @param globalVars map of global variables
     */
    public void addGlobalVars(HashMap<String, Variable> globalVars) {
        for (String varName : globalVars.keySet()) {
            this.globalVars.put(varName, new Variable(globalVars.get(varName)));
        }
    }

    /**
     * name getter
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * how many arguments are expected by method getter
     *
     * @return number of args expected by method
     */
    public int getArgsNum() {
        return argsNum;
    }

    /**
     * checks if variable exists in scope
     *
     * @param name name of variable to find
     * @return true if found, else false
     */
    @Override
    public boolean contains(String name) {
        return globalVars.containsKey(name);
    }

    /**
     * gets variable in this scope. Since we are a method, checks for variable in global variables.
     *
     * @param name name of variable to find
     * @return variable found, else null
     */
    @Override
    public Variable getVar(String name) {
        return globalVars.get(name);
    }

    /**
     * gets the type of the parameter number received
     *
     * @param paramNum parameter numebr to get type of
     * @return type of parameter
     */
    public Type getParamType(int paramNum) {
        return paramTypes.get(paramNum);
    }

    /**
     * checks the validity of the method
     *
     * @return returns the number of the last line of the method
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     * @throws MethodNotFoundException     if calls method that isn't found
     * @throws MissingReturnException      if missing return statement
     */
    @Override
    public int checkValidity() throws IllegalAssignmentException, ExpectedAssignmentException,
            SyntaxException, VariableNameTakenException, IllegalNameException, MethodNotFoundException,
            MissingReturnException {
        int endLine = scope.checkValidity();
        if (!Driver.returnPattern.matcher(LinesDatabase.getLine(endLine - 1)).matches()) {
            throw new MissingReturnException(METHOD_MISSING_RETURN_ERR_MSG); // if no return statement
        }
        return endLine;
    }
}
