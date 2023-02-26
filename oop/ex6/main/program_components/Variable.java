package oop.ex6.main.program_components;

/**
 * Class representing a variable
 */
public class Variable {
    final private String name;
    final private Type type;
    final private boolean isFinal;
    private boolean isInit;

    /**
     * constructor
     *
     * @param name    name of variable
     * @param type    type of variable
     * @param isFinal is variable final
     * @param isInit  is variable initiated
     */
    public Variable(String name, Type type, boolean isFinal, boolean isInit) {
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        this.isInit = isInit;
    }

    /**
     * copy constructor
     *
     * @param other variable to copy from
     */
    public Variable(Variable other) {
        this.name = other.getName();
        this.type = other.getType();
        this.isFinal = other.getIsFinal();
        this.isInit = other.getIsInit();
    }

    //initialize variable

    /**
     * set variable as initialized
     */
    public void init() {
        isInit = true;
    }

    //getters

    /**
     * get variable name
     *
     * @return variable name
     */
    public String getName() {
        return name;
    }

    /**
     * get variable type
     *
     * @return variable type
     */
    public Type getType() {
        return type;
    }

    /**
     * get final status of variable
     *
     * @return final status of variable
     */
    public boolean getIsFinal() {
        return isFinal;
    }

    /**
     * get initialization status of variable
     *
     * @return initialization status of variable
     */
    public boolean getIsInit() {
        return isInit;
    }
//
//    /**
//     * print variable
//     */
//    public void printVar() { // added for testing
//        System.out.println("Name: " + name + ", Type: " + type +
//                ", Final: " + isFinal + ", isInit: " + isInit);
//    }
}
