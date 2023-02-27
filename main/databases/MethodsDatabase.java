package oop.ex6.main.databases;

import oop.ex6.main.program_components.Method;

import java.util.ArrayList;

/**
 * class of static fields and methods. Database of methods in the code.
 */
public class MethodsDatabase {
    static ArrayList<Method> methods = new ArrayList<>();

    /**
     * Add new method to the database
     *
     * @param method method to add to database
     */
    public static void addMethod(Method method) {
        methods.add(method);
    }

    /**
     * gets method list
     *
     * @return method list
     */
    public static ArrayList<Method> getMethods() {
        return methods;
    }

    /**
     * gets method based on method name
     *
     * @param name name of method to get
     * @return method if found, else null
     */
    public static Method getMethod(String name) {
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    /**
     * clears database
     */
    public static void clear() {
        methods.clear();
    }
}
