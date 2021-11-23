package compiladores;

import java.util.*;
import compiladores.TablaSimbolos.ContextList;
import compiladores.TablaSimbolos.Function;
import compiladores.TablaSimbolos.Variable;
import compiladores.TablaSimbolos.Context;
import compiladores.errors.CustomErrors;
import org.antlr.v4.runtime.tree.TerminalNode;
import compiladores.ThreeAddressCode.ThreeAddressCode;

public class MiListener extends idBaseListener {

  private boolean guardarDeclaracionesUtil, guardarAsignacionUtil, nuevoContexto;
  public boolean usoFuncion, functionImplem, condicional_if, ifData, forTipo;
  private boolean declaracionFuncion, idUtil;
  private boolean implementacionFuncionEnc, cicloFor, whileData;
  private LinkedList<String> declaracionesTL, asignacionTL, idUtilTL;
  private LinkedList<String> condicional_ifTL, ifDataTL, whileDataTL;
  private LinkedList<String> forTipoTL, cicloForTL;
  private LinkedList<String> usoFuncionTL, declaracionFuncionTL, implementacionFuncionEncTL;
  private ContextList ctxLista, contextoDescartado;
  private ThreeAddressCode code;

  public MiListener() {
    super();
    this.nuevoContexto = true;
    this.guardarDeclaracionesUtil = false;
    this.guardarAsignacionUtil = false;
    this.functionImplem = false;
    this.usoFuncion = false;
    this.declaracionFuncion = false;
    this.idUtil = false;
    this.implementacionFuncionEnc = false;
    this.condicional_if = false;
    this.ifData = false;
    this.whileData = false;
    this.cicloFor = false;
    this.forTipo = false;
    this.declaracionesTL = new LinkedList<>();
    this.asignacionTL = new LinkedList<>();
    this.implementacionFuncionEncTL = new LinkedList<>();
    this.usoFuncionTL = new LinkedList<>();
    this.idUtilTL = new LinkedList<>();
    this.condicional_ifTL = new LinkedList<>();
    this.ifDataTL = new LinkedList<>();
    this.whileDataTL = new LinkedList<>();
    this.cicloForTL = new LinkedList<>();
    this.forTipoTL = new LinkedList<>();
    this.ctxLista = new ContextList();
    this.contextoDescartado = new ContextList();
    this.declaracionFuncionTL = new LinkedList<>();
    this.code = new ThreeAddressCode();

  }

  @Override
  public void enterProg(idParser.ProgContext ctx) {
    this.ctxLista.addFirst(new Context("[ Contexto global ] "));
  }

  @Override
  public void exitProg(idParser.ProgContext ctx) {
    this.contextoDescartado.add(this.ctxLista.removeFirst()); //elimina y devuelve el primer elemento
    System.out.println(this.ctxLista.size() + ": Tamaño lista contextos. ");
    System.out.println(this.contextoDescartado.size() + ": Tamaño lista contextos descartados" );

    System.out.println("\n\nFin del programa: Lista de contextos");
    System.out.println(this.contextoDescartado);
    for (Context context : this.contextoDescartado) {
      context.forEach((key, val) -> {
        if (!val.isUsed() && !key.equals("main")) {
          CustomErrors.symbolNotUsed(key, context.getContextId());
        }
      });
    }
    //codigo de tres direcciones al salir del contexto global
    System.out.println("*** Código de tres direcciones sin optimizar  --->");
    this.code.printCode();
    this.code.optimizeCode(this.contextoDescartado);
    System.out.println("*** Código de tres direcciones optimizado --->");
    this.code.printCode();
    CustomErrors.printErrors();
  }

  @Override
  public void enterDeclaraciones(idParser.DeclaracionesContext ctx) {
    this.guardarDeclaracionesUtil = true;
    this.declaracionesTL.clear();
  }

  @Override
  public void exitDeclaraciones(idParser.DeclaracionesContext ctx) {
    String data_type = this.declaracionesTL.pop();
    while (this.declaracionesTL.size() > 1) {
      String id;
      if (this.declaracionesTL.get(1).equals(";")) { //verifica que sea ; 
        id = this.declaracionesTL.pop(); // sacamos id
        System.out.print(id);
        this.addVariableToActualContext(id, data_type, false, false);
        this.code.declareVariable(id, this.ctxLista.getFirst().getContextId());
        this.declaracionesTL.clear();
      } else if (this.declaracionesTL.get(1).equals(",")) {
        id = this.declaracionesTL.pop(); // sacamos id
        this.addVariableToActualContext(id, data_type, false, false);
        this.code.declareVariable(id, this.ctxLista.getFirst().getContextId());
        this.declaracionesTL.pop(); // sacamos coma
      } else if (this.declaracionesTL.get(3).equals(";")) {
        id = this.declaracionesTL.pop(); // sacamos id
        this.addVariableToActualContext(id, data_type, true, false);
        this.code.declareAndAssignVariable(id, this.declaracionesTL.get(1),this.ctxLista.getFirst().getContextId());
        this.declaracionesTL.clear();
      } else if (this.declaracionesTL.get(3).equals(",")) {
        id = this.declaracionesTL.pop(); // sacamos id
        this.addVariableToActualContext(id, data_type, true, false);
        this.declaracionesTL.pop(); // sacamos igual
        this.code.declareAndAssignVariable(id, this.declaracionesTL.pop(), this.ctxLista.getFirst().getContextId()); //sacamos valor
        this.declaracionesTL.pop(); // sacamos coma
      } else if (this.declaracionesTL.get(1).equals("=")) {
        id = this.declaracionesTL.pop(); // sacamos id
        this.addVariableToActualContext(id, data_type, true, false);
        this.declaracionesTL.pop(); // sacamos igual
        StringBuilder op = new StringBuilder();
        while (!this.declaracionesTL.get(0).equals(";") && !this.declaracionesTL.get(0).equals(",")) {
          op.append(this.declaracionesTL.pop()); // sacamos lo que sea que haya despues del igual
        }
        this.code.assignArithmeticOperation(id, op.toString(),this.ctxLista.getFirst().getContextId());
      } else {
        CustomErrors.unhandledDeclaration();
        this.declaracionesTL.clear();
      }
    }
    this.asignacionTL.clear();
    this.guardarDeclaracionesUtil = false;
  }

  private void addVariableToActualContext(String name, String type, boolean used, boolean initialized) {
    // el objeto no existe en el contexto actual, se puede agregar
    if (this.ctxLista.getFirst().get(name) == null) {
      this.ctxLista.getFirst().put(name, new Variable(name, type, used, initialized));
    } else { // el objeto ya existe.
      CustomErrors.variableAlreadyDeclared(name, this.ctxLista.getFirst().getContextId());
    }
  }

  @Override
  public void enterBloque(idParser.BloqueContext ctx) {
    if (this.nuevoContexto) {
      this.ctxLista.addFirst(new Context());
    } else {
      this.nuevoContexto = true;
    }
  }

  @Override
  public void exitBloque(idParser.BloqueContext ctx) {
    this.contextoDescartado.add(this.ctxLista.removeFirst());
  }

  @Override
  public void enterAsignacion(idParser.AsignacionContext ctx) {
    this.guardarAsignacionUtil = true;
    this.asignacionTL.clear();
  }

  @Override
  public void exitAsignacion(idParser.AsignacionContext ctx) {
    String varId = this.asignacionTL.pop();
    Variable var = this.ctxLista.getVariable(varId);
    if (var != null) {
      var.setInitialized(true); 
      LinkedList<String> atl = this.asignacionTL;
      if (atl.size() == 2) {
        this.code.assignVariable(varId, atl.get(1), this.ctxLista.getFirst().getContextId());
      } else {
        atl.pop();
        this.code.assignArithmeticOperation(varId, atl.toString(), this.ctxLista.getFirst().getContextId());
      }
      this.guardarAsignacionUtil = false;
      this.asignacionTL.clear();
    } else {
      CustomErrors.variableNotDeclared(varId);
    }
  }

  @Override
  public void enterImplementacion_funcion(idParser.Implementacion_funcionContext ctx) {
    this.nuevoContexto = false;
  }

  @Override
  public void exitImplementacion_funcion(idParser.Implementacion_funcionContext ctx) {
    this.code.endFunction();
  }

  @Override
  public void enterImplementacion_funcion_encabezado(idParser.Implementacion_funcion_encabezadoContext ctx) {
    this.implementacionFuncionEnc = true;
    this.implementacionFuncionEncTL.clear();
  }

  @Override
  public void exitImplementacion_funcion_encabezado(idParser.Implementacion_funcion_encabezadoContext ctx) {
    String functionDatatype = this.implementacionFuncionEncTL.pop();
    String functionId = this.implementacionFuncionEncTL.pop();
    Function function = this.ctxLista.getFunction(functionId);
    if (function == null) {
      // pone used en true si la funcion es main, sino false
      this.ctxLista.getGlobal().put(functionId, new Function(functionId, functionDatatype, true, true, functionId.equals("main")));
      this.code.implementFunction(functionId);
    } else if (!function.isImplemented()) {
      function.setImplemented(true);
    } else {
      CustomErrors.functionAreadyImplemented(functionId);
      return;
    }
    Context funcContext = new Context(functionId);
    this.ctxLista.addFirst(funcContext);
    this.implementacionFuncionEncTL.pop(); // sacamos )
    while (this.implementacionFuncionEncTL.size() > 2) { // tiene parámetros, el 2 son los ()
      String varType = this.implementacionFuncionEncTL.pop();
      String varId = this.implementacionFuncionEncTL.pop();
      funcContext.put(varId, new Variable(varId, varType, false, false));
      this.implementacionFuncionEncTL.pop(); // sacamos ,
      this.code.declareVariable(varId, this.ctxLista.getFirst().getContextId());
    }
  }

  @Override
  public void enterUso_funcion(idParser.Uso_funcionContext ctx) {
    this.usoFuncion = true;
    this.usoFuncionTL.clear();
  }

  @Override
  public void exitUso_funcion(idParser.Uso_funcionContext ctx) {
    if (!this.usoFuncionTL.isEmpty()) {
      String functionId = this.usoFuncionTL.pop();
      Function symbol = this.ctxLista.getFunction(functionId);
      if (symbol != null) {
        if (symbol.isImplemented()) {
          symbol.setUsed(true);
          this.code.callFunction(functionId);
          this.usoFuncionTL.pop(); // sacamos (
          LinkedList<String> valueList = new LinkedList<>();
          while (this.usoFuncionTL.size() > 2) { // hay parámetros, el 2 es de );
            valueList.add(this.usoFuncionTL.pop());
            this.usoFuncionTL.pop();
          }
          this.code.assignValueToVariable(functionId, valueList);
        } else {
          CustomErrors.functionNotImplemented(functionId);
        }
      } else {
        CustomErrors.functionNotDeclared(functionId);
      }
    }
    this.usoFuncion = false;
  }

  @Override
  public void enterIf_data(idParser.If_dataContext ctx) {
    this.ifData = true;
    this.ifDataTL.clear();
  }

  @Override
  public void exitIf_data(idParser.If_dataContext ctx) {
    this.ifDataTL.pop();// sacamos if
    this.ifDataTL.pop();// sacamos (
    String op = "";
    while (this.ifDataTL.size() > 1) { // 1 es por )
      op += ifDataTL.pop();
    }
    this.code.evaluateLogicalOperation(op);
    this.ifDataTL.clear();
  }

  @Override
  public void enterCondicional_if(idParser.Condicional_ifContext ctx) {
    this.nuevoContexto = false;
    this.ctxLista.addFirst(new Context("if-context"));
  }

  @Override
  public void exitCondicional_if(idParser.Condicional_ifContext ctx) {
    this.code.completeIfStructure();
  }

  @Override
  public void enterWhile_data(idParser.While_dataContext ctx) {
    this.whileData = true;
    this.whileDataTL.clear();
  }

  @Override
  public void exitWhile_data(idParser.While_dataContext ctx) {
    this.whileDataTL.pop();// sacamos while
    this.whileDataTL.pop();// sacamos (
    String op = "";
    while (this.whileDataTL.size() > 1) { // 1 es por )
      op += whileDataTL.pop();
    }
    this.code.evaluateLogicalOperation(op);
    this.whileDataTL.clear();
  }

  @Override
  public void enterBucle_while(idParser.Bucle_whileContext ctx) {
    this.nuevoContexto = false;
    this.ctxLista.addFirst(new Context("While-context"));
  }

  @Override
  public void exitBucle_while(idParser.Bucle_whileContext ctx) {
    this.code.completeWhileStructure();
  }

  @Override
  public void enterFor_tipo(idParser.For_tipoContext ctx) {
    this.forTipo = true;
    this.forTipoTL.clear();
  }

  @Override
  public void exitFor_tipo(idParser.For_tipoContext ctx) {
    while (!this.forTipoTL.peek().equals(";")) {
      this.forTipoTL.pop();
    }
    this.forTipoTL.pop();
    String operation = "", incrementInstruction = "";
    while (!this.forTipoTL.peek().equals(";")) {
      operation += this.forTipoTL.pop();
    }
    this.code.evaluateLogicalOperation(operation);
    this.forTipoTL.pop();
    while (!this.forTipoTL.peek().equals(")")) {
      incrementInstruction += this.forTipoTL.pop();
    }
    this.code.setForIncrementInstruction(incrementInstruction);
  }

  @Override
  public void enterCiclo_for(idParser.Ciclo_forContext ctx) {
    this.nuevoContexto = false;
    this.ctxLista.addFirst(new Context("For-context"));
  }

  @Override
  public void exitCiclo_for(idParser.Ciclo_forContext ctx) {
    this.code.completeForStructure();
  }

  @Override
  public void enterDeclaracion_funcion(idParser.Declaracion_funcionContext ctx) {
    this.declaracionFuncion = true;
    this.declaracionFuncionTL.clear();
  }

  @Override
  public void exitDeclaracion_funcion(idParser.Declaracion_funcionContext ctx) {
    String functionDataType = this.declaracionFuncionTL.pop();
    String functionId = this.declaracionFuncionTL.pop();
    Function symbol = this.ctxLista.getFunction(functionId);
    if (symbol == null) {
      this.ctxLista.getGlobal().put(functionId, new Function(functionId, functionDataType, true, false, false));
    } else {
      CustomErrors.functionAlreadyDeclared(functionId);
    }
  }

  @Override
  public void enterId_utilizado(idParser.Id_utilizadoContext ctx) {
    this.idUtil = true;
    this.idUtilTL.clear();
  }

  @Override
  public void exitId_utilizado(idParser.Id_utilizadoContext ctx) {
    //buscamos si esa variable usada existe, y seteamos su estado en usada
    if (this.idUtilTL.size() == 1) {
      String usedId = this.idUtilTL.getFirst();
      Variable symbol = this.ctxLista.getVariable(usedId);
      if (symbol != null) { //existe en el contexto que estoy iterando con el primer for
        symbol.setUsed(true);
      } else {
        CustomErrors.variableNotDeclared(usedId);
      }
    }
    this.idUtilTL.clear();
    this.idUtil = false;
  }

  
  @Override
  public void enterExpresion_aritmetica(idParser.Expresion_aritmeticaContext ctx) {
    this.guardarAsignacionUtil = true;
  }

  @Override
  public void exitExpresion_aritmetica(idParser.Expresion_aritmeticaContext ctx) {

  }

  @Override
  public void visitTerminal(TerminalNode node) {
    String nodeText = node.getSymbol().getText();
    if (this.guardarDeclaracionesUtil) this.declaracionesTL.add(nodeText);
    if (this.guardarAsignacionUtil) this.asignacionTL.add(nodeText);
    if (this.usoFuncion) this.usoFuncionTL.add(nodeText);
    if (this.declaracionFuncion) this.declaracionFuncionTL.add(nodeText);
    if (this.idUtil) this.idUtilTL.add(nodeText);
    if (this.implementacionFuncionEnc) this.implementacionFuncionEncTL.add(nodeText);
    if (this.ifData) this.ifDataTL.add(nodeText);
    if (this.whileData) this.whileDataTL.add(nodeText);
    if (this.cicloFor) this.cicloForTL.add(nodeText);
    if (this.forTipo) this.forTipoTL.add(nodeText);
  }
}
