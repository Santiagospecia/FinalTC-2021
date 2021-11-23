package compiladores.errors;

import java.util.LinkedList;

public class CustomErrors {
  private static LinkedList<String> errors = new LinkedList<>();

  public static void printErrors() {
    for(String msg : errors) {
      System.err.println(msg);
    }
  }

  private static void addErrorMsg(String msg) {
    errors.add("\nError: ");
    errors.add(msg);
  }

  public static void symbolNotUsed(String id, String contextId) {
    addErrorMsg("El simbolo " + id + " del contexto " + contextId + " no es usado en el programa.");
  }

  public static void variableAlreadyDeclared(String variableId, String contextId) {
    addErrorMsg("La variable " + variableId + " ya existe en el contexto de " + contextId + ".");
  }

  public static void variableNotDeclared(String variableId) {
    addErrorMsg("La variable " + variableId + " no esta declarada en ningun contexto del programa.");
  }

  public static void functionAreadyImplemented(String functionId) {
    addErrorMsg("La funcion " + functionId + " ya esta implementada.");
  }

  public static void functionNotDeclared(String functionId) {
    addErrorMsg("La funcion " + functionId + " no fue declarada.");
  }

  public static void functionNotImplemented(String functionId) {
    addErrorMsg("La funcion " + functionId + " no fue implementada");
  }

  public static void functionAlreadyDeclared(String functionId) {
    addErrorMsg("La funcion " + functionId + " ya esta declarada.");
  }

  public static void variableNotInitialized(String variableId) {
    addErrorMsg("La variable " + variableId + " esta siendo usada pero no fue inicializada.");
  }

  public static void dataTypesNotCompatible(String leftVariableId, String rightVariableId) {
    addErrorMsg("Los tipos de datos de " + leftVariableId + " y " + rightVariableId + " no son compatibles.");
  }

  public static void unhandledDeclaration() {
    addErrorMsg("Error de declaración no manejada");
  }
}
