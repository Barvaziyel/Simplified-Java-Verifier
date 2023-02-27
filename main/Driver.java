package oop.ex6.main;

import oop.ex6.main.databases.LinesDatabase;
import oop.ex6.main.databases.MethodsDatabase;
import oop.ex6.main.program_components.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class of driver of program
 */
public class Driver {
    //patterns
    public static final String DECLARATION_PREFIX_REGEX =
            "^\\s*(final\\s+)?(int|double|String|char|boolean)\\s+";
    private static final Pattern declarationPrefix = Pattern.compile(DECLARATION_PREFIX_REGEX);
    public static final String METHOD_PREFIX_REGEX = "^\\s*void\\s+([a-zA-Z]\\w*)\\s*[(]";
    private static final Pattern methodPrefix = Pattern.compile(METHOD_PREFIX_REGEX);
    public static final String METHOD_CALL_PREFIX_REGEX = "^\\s*([a-zA-Z]\\w*)\\s*[(]";
    private static final Pattern methodCallPrefix = Pattern.compile(METHOD_CALL_PREFIX_REGEX);
    public static final String VAR_NAME_REGEX = "^\\s*(_\\w+|[a-zA-Z]\\w*)\\s*";
    private static final Pattern varName = Pattern.compile(VAR_NAME_REGEX);
    public static final String STATEMENT_SUFFIX_REGEX = "\\s*;\\s*$";
    private static final Pattern statementSuffix = Pattern.compile(STATEMENT_SUFFIX_REGEX);
    public static final String CLOSE_SCOPE_REGEX = "^\\s*}\\s*$";
    public static final Pattern closeScope = Pattern.compile(CLOSE_SCOPE_REGEX);
    public static final String OPEN_SCOPE_SUFFIX_REGEX = "\\s*[{]\\s*$";
    private static final Pattern openScopeSuffix = Pattern.compile(OPEN_SCOPE_SUFFIX_REGEX);
    public static final String RETURN_REGEX = "^\\s*return\\s*;\\s*$";
    public static final Pattern returnPattern = Pattern.compile(RETURN_REGEX);
    public static final String IF_WHILE_PREFIX_REGEX = "^\\s*(if|while)\\s*[(]";
    public static final Pattern ifWhilePrefix = Pattern.compile(IF_WHILE_PREFIX_REGEX);
    public static final String OR_AND_REGEX = "^\\s*([|]{2}|&{2})\\s*";
    public static final Pattern orAndPattern = Pattern.compile(OR_AND_REGEX);

    //characters
    public static final char COMMA = ',';
    public static final char EQUALS = '=';
    public static final char CLOSE_PARENTHESES = ')';

    //regex groups
    public static final int VAR_NAME_GROUP = 1;
    public static final int METHOD_NAME_GROUP_NUM = 1;
    public static final int TYPE_GROUP_NUM = 2;
    public static final int FINAL_GROUP_NUM = 1;

    //saved words in sjavac
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String RETURN = "return";

    //error messages
    public static final String SCOPE_ERR_MSG = "Expected '}'";
    public static final String BAD_SYNTAX_ERR_MSG = "Bad syntax!";
    public static final String ILLEGAL_METHOD_PREFIX_ERR_MSG = "Illegal method prefix!";
    public static final String METHOD_NAME_TAKEN_ERR_MSG = "Method of this name already exists!";
    public static final String EXPECTED_CLOSE_PARENTHESES_ERR_MSG = "Expected ')'";
    public static final String EXPECTED_OPEN_SCOPE_ERR_MSG = "Expected '{'";
    public static final String ILLEGAL_PARAMETER_TYPE_ERR_MSG = "Illegal parameter type!";
    public static final String ILLEGAL_STATEMENT_END_ERR_MSG = "Statement line must end with ';'!";
    public static final String ASSIGN_TO_FINAL_OR_NO_EXIST_ERR_MSG = "Trying to assign to non-existent or " +
            "final variable!";
    public static final String EXPECTED_EQUALS_ERR_MSG = "Expected '='";
    public static final String VARIABLE_NAME_TAKEN_ERR_MSG = "Variable name already taken!";
    public static final String ASSINGING_ILLEGAL_VAR_ERR_MSG = "Trying to assign illegal variable!";
    public static final String ILLEGAL_VAR_NAME_ERR_MSG = "Illegal variable name!";
    public static final String ILLEGAL_VALUE_ERR_MSG = "illegal value for type!";
    public static final String CALLED_METHOD_NOT_FOUND_ERR_MSG = "Called method not found!";

    /**
     * checks validity of file
     *
     * @param path path of sjavac file to check
     * @throws ScopeException              if bad scoping
     * @throws MethodNameTakenException    if method name already taken
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     * @throws MethodNotFoundException     if calls method that isn't found
     * @throws MissingReturnException      if missing return statement
     * @throws IOException                 if problem with reading file
     */
    public static void checkValidity(String path)
            throws ScopeException, SyntaxException, IllegalNameException, IllegalAssignmentException,
            ExpectedAssignmentException, VariableNameTakenException, MethodNameTakenException,
            MethodNotFoundException, MissingReturnException, IOException {
        MethodsDatabase.clear();
        LinesDatabase.readLines(path); //create database of code line-by-line
        HashMap<String, Variable> globalVars = new HashMap<>();
        readGlobal(globalVars);
        for (Method method : MethodsDatabase.getMethods()) {
            method.addGlobalVars(globalVars);
            method.checkValidity();
        }
    }

    /**
     * reads all global scope lines, and gathers all method signatures
     *
     * @param globalVars map of global variables
     * @throws ScopeException              if bad scoping
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     * @throws MethodNameTakenException    if method name already taken
     */
    private static void readGlobal(HashMap<String, Variable> globalVars)
            throws ScopeException, SyntaxException, IllegalNameException, IllegalAssignmentException,
            ExpectedAssignmentException, VariableNameTakenException, MethodNameTakenException {
        String curLine;
        Matcher closeScopeM;
        for (int i = 0; i < LinesDatabase.getLinesNum(); i++) {
            curLine = LinesDatabase.getLine(i);
            if (isStatementSuffix(curLine)) {
                if (handleStatement(globalVars, curLine)) continue;
            }
            MethodsDatabase.addMethod(handleMethod(curLine, i));
            int scopeDepth = 1;
            while (scopeDepth != 0) { //while in method
                i++; //next line
                if (i == LinesDatabase.getLinesNum()) { //end of document and still in method
                    throw new ScopeException(SCOPE_ERR_MSG);
                }
                curLine = LinesDatabase.getLine(i);
                closeScopeM = closeScope.matcher(curLine);
                if (closeScopeM.matches()) { //close scope
                    scopeDepth--;
                } else if (isScopeOpeningSuffix(curLine)) { //open scope
                    scopeDepth++;
                }
            }
        }
    }

    /**
     * handles statments
     *
     * @param globalVars global variable hashMap
     * @param curLine    statement line
     * @return true if successful, else throws exception
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     */
    private static boolean handleStatement(HashMap<String, Variable> globalVars, String curLine)
            throws SyntaxException, IllegalAssignmentException, ExpectedAssignmentException,
            VariableNameTakenException, IllegalNameException {
        if (handleDeclaration(curLine, globalVars, new ArrayList<>())) {
            return true;
        }
        return handleAssignment(curLine, globalVars, new ArrayList<>());
    }

    /**
     * handles method declaration
     *
     * @param line    method signature
     * @param lineNum line num where method is declared
     * @return returns new Method instance
     * @throws SyntaxException          if bad syntax
     * @throws IllegalNameException     if illegal variable name
     * @throws MethodNameTakenException if method name already taken
     */
    private static Method handleMethod(String line, int lineNum)
            throws SyntaxException, IllegalNameException, MethodNameTakenException {
        if (!isScopeOpeningSuffix(line)) {
            throw new SyntaxException(BAD_SYNTAX_ERR_MSG);
        }
        Matcher m = methodPrefix.matcher(line);
        if (!m.find()) { // does line begin with method prefix?
            throw new SyntaxException(ILLEGAL_METHOD_PREFIX_ERR_MSG);
        }
        String methodName = m.group(METHOD_NAME_GROUP_NUM);
        if (MethodsDatabase.getMethod(methodName) != null) {
            throw new MethodNameTakenException(METHOD_NAME_TAKEN_ERR_MSG);
        }
        ArrayList<Variable> params = new ArrayList<>();
        line = line.substring(m.end()); //trim line
        do {
            line = processParameters(line, params);
        } while (line.charAt(0) == COMMA);
        if (line.charAt(0) != CLOSE_PARENTHESES) {
            throw new SyntaxException(EXPECTED_CLOSE_PARENTHESES_ERR_MSG);
        }
        line = line.substring(1); //remove ')'
        m = openScopeSuffix.matcher(line);
        if (!m.matches()) { // does end with '{' ?
            throw new SyntaxException(EXPECTED_OPEN_SCOPE_ERR_MSG);
        }
        return new Method(methodName, params, lineNum);
    }

    /**
     * processes paratmeters of a method declaration
     *
     * @param line   method declaration line starting at parameters
     * @param params list of parameters
     * @return returns rest of line after parameters
     * @throws SyntaxException      if bad syntax
     * @throws IllegalNameException if illegal variable name
     */
    private static String processParameters(String line, ArrayList<Variable> params)
            throws IllegalNameException, SyntaxException {
        if (line.charAt(0) == CLOSE_PARENTHESES) {
            return line;
        }
        Matcher m;
        Type type;
        String name;
        boolean isFinal;
        line = removeComma(line);
        m = declarationPrefix.matcher(line);
        if (!m.find()) {
            throw new SyntaxException(ILLEGAL_PARAMETER_TYPE_ERR_MSG);
        }
        type = Type.getType(m.group(TYPE_GROUP_NUM)); //get declaration type
        isFinal = m.group(FINAL_GROUP_NUM) != null; //get final status of variable
        line = line.substring(m.end());
        m = isValidVarName(line);
        name = m.group(VAR_NAME_GROUP);
        for (Variable param : params) {
            if (param.getName().equals(name)) {
                throw new IllegalNameException(ILLEGAL_VAR_NAME_ERR_MSG);
            }
        }
        params.add(new Variable(name, type, isFinal, true));
        line = line.substring(m.end()); //trim line
        return line;
    }

    /**
     * handles assignment statement
     *
     * @param line        line with assignment
     * @param vars        list of variables of current scope
     * @param superScopes list of scopes that statement is within
     * @return returns true if successful, else throws exception
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws IllegalNameException        if illegal variable name
     */
    public static boolean handleAssignment(String line, HashMap<String, Variable> vars,
                                           ArrayList<Scope> superScopes)
            throws SyntaxException, IllegalAssignmentException, ExpectedAssignmentException,
            IllegalNameException {
        do {
            line = processAssignment(line, vars, superScopes);
        } while (line.charAt(0) == COMMA);
        if (!statementSuffix.matcher(line).matches()) {
            throw new SyntaxException(ILLEGAL_STATEMENT_END_ERR_MSG);
        }
        return true;
    }

    /**
     * process assignment statement
     *
     * @param line        line with assignment
     * @param vars        variable list of current scope
     * @param superScopes list of scopes this statement is within
     * @return rest of line after processed
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws IllegalNameException        if illegal variable name
     */
    private static String processAssignment(String line, HashMap<String, Variable> vars,
                                            ArrayList<Scope> superScopes)
            throws IllegalNameException, IllegalAssignmentException, ExpectedAssignmentException {
        Matcher m;
        line = removeComma(line);
        m = isValidVarName(line);
        Variable receiver = findVariable(vars, superScopes, m.group(VAR_NAME_GROUP));
        if (receiver == null || receiver.getIsFinal()) {
            throw new IllegalAssignmentException(ASSIGN_TO_FINAL_OR_NO_EXIST_ERR_MSG);
        }
        line = line.substring(m.end());
        if (line.charAt(0) != EQUALS) {
            throw new ExpectedAssignmentException(EXPECTED_EQUALS_ERR_MSG);
        }
        line = line.substring(1);
        line = assign(line, vars, superScopes, receiver.getType());
        receiver.init();
        return line;
    }

    /**
     * checks assignment to variable
     *
     * @param line         line starting at assignment
     * @param vars         variables from current scope
     * @param superScopes  list of scopes this assignment is within
     * @param receiverType type of the receiver variable of this assignment
     * @return returns rest of line after assignment
     * @throws IllegalAssignmentException if bad assignment occurred
     */
    private static String assign(String line, HashMap<String, Variable> vars,
                                 ArrayList<Scope> superScopes, Type receiverType)
            throws IllegalAssignmentException {
        Matcher m = varName.matcher(line);
        if (m.find() && !m.group(VAR_NAME_GROUP).equals(TRUE) &&
                !m.group(VAR_NAME_GROUP).equals(FALSE) &&
                !m.group(VAR_NAME_GROUP).equals(RETURN)) { // assign other variable to it
            line = checkAssignmentValidity(vars, superScopes, line, receiverType, m);
        } else { // assign value
            line = checkValueValidity(line, Type.getTypePatterns(receiverType));
        }
        return line;
    }

    /**
     * handles declarations
     *
     * @param line        line with declaration
     * @param vars        variables of this current scope
     * @param superScopes list of scopes this declaration is within
     * @return returns false if not declaration. If must be declaration - returns true if valid, else throws
     * exception
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws SyntaxException             if bad syntax
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     */
    public static boolean handleDeclaration(String line, HashMap<String, Variable> vars,
                                            ArrayList<Scope> superScopes)
            throws SyntaxException, IllegalAssignmentException, ExpectedAssignmentException,
            VariableNameTakenException, IllegalNameException {
        Matcher m = declarationPrefix.matcher(line);
        if (!m.find()) { // does line begin with declaration prefix?
            return false;
        }
        line = line.substring(m.end()); //trim line
        if (line.charAt(0) == COMMA) { //comma is illegal here
            throw new SyntaxException(BAD_SYNTAX_ERR_MSG);
        }
        Type type = Type.getType(m.group(TYPE_GROUP_NUM)); //get declaration type
        boolean isFinal = m.group(FINAL_GROUP_NUM) != null; //get final status of variable
        do {
            line = addVar(line, vars, type, isFinal, superScopes); //handle single var declaration
        } while (line.charAt(0) == COMMA); // keep going until no more variables
        if (!statementSuffix.matcher(line).matches()) {
            throw new SyntaxException(ILLEGAL_STATEMENT_END_ERR_MSG);
        }
        return true;
    }

    /**
     * checks if line has a statement suffix
     *
     * @param line line of code
     * @return true if found statement suffix, else false
     */
    public static boolean isStatementSuffix(String line) {
        Matcher m = statementSuffix.matcher(line);
        return m.find(); // does line end with ; ?
    }

    /**
     * checks if line has a scope opening suffix
     *
     * @param line line of code
     * @return true if found scope opening suffix, else false
     */
    public static boolean isScopeOpeningSuffix(String line) {
        Matcher m = openScopeSuffix.matcher(line);
        return m.find(); // does line end with { ?
    }

    /**
     * adds global variable to globalVars if legal, else throws exception. returns the remaining line.
     *
     * @param line        line to parse
     * @param vars        variable list to add vars to
     * @param type        type of vars declared in this line
     * @param isFinal     are vars on this line final
     * @param superScopes list of scopes current scope is within
     * @return remaining line
     * @throws IllegalAssignmentException  if bad assignment occurred
     * @throws ExpectedAssignmentException if expected assignment but no
     * @throws VariableNameTakenException  if variable name already taken
     * @throws IllegalNameException        if illegal variable name
     */
    private static String addVar(String line, HashMap<String, Variable> vars, Type type,
                                 boolean isFinal, ArrayList<Scope> superScopes)
            throws IllegalNameException, VariableNameTakenException, ExpectedAssignmentException,
            IllegalAssignmentException {
        String name;
        boolean isInit = false;
        Matcher m;
        line = removeComma(line);
        m = isValidVarName(line);
        name = m.group(VAR_NAME_GROUP);
        if (vars.get(name) != null) { // is var name taken?
            throw new VariableNameTakenException(VARIABLE_NAME_TAKEN_ERR_MSG);
        }
        line = line.substring(m.end()); //trim line
        if (isFinal && line.charAt(0) != EQUALS) { // is final and no assignment?
            throw new ExpectedAssignmentException(EXPECTED_EQUALS_ERR_MSG);
        }
        if (line.charAt(0) == EQUALS) { // is assignment?
            isInit = true;
            line = line.substring(1); //trim =
            line = assign(line, vars, superScopes, type);
        }
        vars.put(name, new Variable(name, type, isFinal, isInit));
        return line;
    }

    /**
     * checks if assignment is valid
     *
     * @param vars        variables list of current scope
     * @param superScopes list of scopes assignment is within
     * @param line        line of code starting at assignment
     * @param type        type of receiver
     * @param m           matcher
     * @return returns rest of line after assignemnt
     * @throws IllegalAssignmentException if bad assignment occurred
     */
    private static String checkAssignmentValidity(HashMap<String, Variable> vars,
                                                  ArrayList<Scope> superScopes, String line, Type type,
                                                  Matcher m) throws IllegalAssignmentException {
        String giverVarName = m.group(VAR_NAME_GROUP);
        Variable giver = findVariable(vars, superScopes, giverVarName);
        if (giver == null || !giver.getIsInit() || !Type.equals(type, giver.getType())) {
            throw new IllegalAssignmentException(ASSINGING_ILLEGAL_VAR_ERR_MSG);
        }
        line = line.substring(m.end());
        return line;
    }

    /**
     * searches scopes in order for variable
     *
     * @param vars        variables of current scope
     * @param superScopes list of scopes current scope is within
     * @param varName     variable name
     * @return returns variable found, or null if not found
     */
    private static Variable findVariable(HashMap<String, Variable> vars, ArrayList<Scope> superScopes,
                                         String varName) {
        Variable giver = vars.get(varName);
        int scopeIndex = 0;
        while (giver == null && scopeIndex < superScopes.size()) {
            giver = superScopes.get(scopeIndex).getVar(varName);
            scopeIndex++;
        }
        return giver;
    }

    /**
     * checks if variable name is valid
     *
     * @param line line of code starting at place where variable name should be
     * @return returns the matcher
     * @throws IllegalNameException if illegal variable name
     */
    private static Matcher isValidVarName(String line) throws IllegalNameException {
        Matcher m = varName.matcher(line);
        if (!m.find() || m.group(VAR_NAME_GROUP).equals(TRUE) ||
                m.group(VAR_NAME_GROUP).equals(FALSE) ||
                m.group(VAR_NAME_GROUP).equals(RETURN)) { // variable name legal?
            throw new IllegalNameException(ILLEGAL_VAR_NAME_ERR_MSG);
        }
        return m;
    }

    /**
     * removes comma from line
     *
     * @param line line to remove comma from
     * @return line after comma has been removed
     */
    private static String removeComma(String line) {
        if (line.charAt(0) == COMMA) { // remove , from beginning if exists (every time but first time)
            line = line.substring(1);
        }
        return line;
    }

    /**
     * checks if value is valid for list of patterns
     *
     * @param line     line to check
     * @param patterns regex patterns of type value
     * @return returns remaining line
     * @throws IllegalAssignmentException if bad assignment occurred
     */
    private static String checkValueValidity(String line, List<Pattern> patterns)
            throws IllegalAssignmentException {
        Matcher m;
        for (Pattern pattern : patterns) {
            m = pattern.matcher(line);
            if (m.find()) { // is legal value assignment for type?
                line = line.substring(m.end());
                return line;
            }
        }
        throw new IllegalAssignmentException(ILLEGAL_VALUE_ERR_MSG);
    }

    /**
     * handles method call statement
     *
     * @param line        line with method call
     * @param vars        variables of current scope
     * @param superScopes list of scopes statement is within
     * @return returns false if not method call. if valid returns true, else throws exception
     * @throws IllegalAssignmentException if bad assignment occurred
     * @throws SyntaxException            if bad syntax
     * @throws MethodNotFoundException    if calls method that isn't found
     */
    public static boolean handleMethodCall(String line, HashMap<String, Variable> vars,
                                           ArrayList<Scope> superScopes)
            throws MethodNotFoundException, IllegalAssignmentException, SyntaxException {
        Matcher m = methodCallPrefix.matcher(line);
        if (!m.find()) {
            return false;
        }
        Method method = MethodsDatabase.getMethod(m.group(METHOD_NAME_GROUP_NUM));
        if (method == null) {
            throw new MethodNotFoundException(CALLED_METHOD_NOT_FOUND_ERR_MSG);
        }
        line = line.substring(m.end());
        line = checkParameters(line, vars, superScopes, method);
        if (line.charAt(0) != CLOSE_PARENTHESES) { // illegal syntax if no ')'
            throw new SyntaxException(EXPECTED_CLOSE_PARENTHESES_ERR_MSG);
        }
        line = line.substring(1);
        if (!statementSuffix.matcher(line).matches()) {
            throw new SyntaxException(ILLEGAL_STATEMENT_END_ERR_MSG);
        }
        return true;
    }

    /**
     * checks parametes in method call
     *
     * @param line        lines of code starting at parameters of method call
     * @param vars        variables of current scope
     * @param superScopes list of scopes statement is within
     * @param method      method being called
     * @return returns rest of line after parameters
     * @throws IllegalAssignmentException if bad assignment occurred
     * @throws SyntaxException            if bad syntax
     */
    private static String checkParameters(String line, HashMap<String, Variable> vars,
                                          ArrayList<Scope> superScopes, Method method)
            throws IllegalAssignmentException, SyntaxException {
        for (int i = 0; i < method.getArgsNum(); i++) {
            line = assign(line, vars, superScopes, method.getParamType(i)); //checks validity of assignment
            if (i + 1 != method.getArgsNum()) { // if not on last arg
                if (line.charAt(0) == COMMA) { // if next char is comma
                    line = line.substring(1); //remove comma
                } else { // illegal syntax
                    throw new SyntaxException(BAD_SYNTAX_ERR_MSG);
                }
            }
        }
        return line;
    }

    /**
     * handles if and while scopes
     *
     * @param line        line of code with if or while scope opening
     * @param vars        variables of current scope
     * @param superScopes list of scopes statement is within
     * @throws IllegalAssignmentException if bad assignment occurred
     * @throws SyntaxException            if bad syntax
     */
    public static void handleIfWhileStatement(String line, HashMap<String, Variable> vars,
                                              ArrayList<Scope> superScopes)
            throws SyntaxException, IllegalAssignmentException {
        Matcher m = ifWhilePrefix.matcher(line);
        if (!m.find()) { // does line begin with declaration prefix?
            throw new SyntaxException(BAD_SYNTAX_ERR_MSG);
        }
        line = line.substring(m.end());
        do {
            if (orAndPattern.matcher(line).find()) { // all times but first
                line = line.substring(2); // remove AND or OR symbols
            }
            line = assign(line, vars, superScopes, Type.BOOLEAN); // check condition is boolean-assignable
        } while (orAndPattern.matcher(line).find()); // while there's more conditions
        if (line.charAt(0) != CLOSE_PARENTHESES) {
            throw new SyntaxException(EXPECTED_CLOSE_PARENTHESES_ERR_MSG);
        }
        line = line.substring(1); // remove ')'
        if (!openScopeSuffix.matcher(line).matches()) {
            throw new SyntaxException(EXPECTED_OPEN_SCOPE_ERR_MSG);
        }
    }
}
