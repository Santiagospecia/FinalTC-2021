package compiladores.ThreeAddressCode;

import compiladores.TablaSimbolos.Context;
import compiladores.TablaSimbolos.ContextList;
import compiladores.TablaSimbolos.Function;
import compiladores.TablaSimbolos.Variable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ThreeAddressCode extends LinkedList<Instruction[]> {
  private int id = 1;
  private String forIncrementInstruction = "";
  public ThreeAddressCode() { }

  public void declareAndAssignVariable(String variableId, String value, String ctxId) { 
    this.add(new Instruction[] {
        new Instruction(""),
       // new Instruction(variableId + " - " + ctxId),
        new Instruction(variableId ),
        new Instruction("="),
        new Instruction(value),
        new Instruction()
    });
  }

  public void declareAndAssignVariable(String variableId, Instruction value, String ctxId) {
    this.add(new Instruction[] {
        new Instruction(""),
        //new Instruction(variableId + " - " + ctxId),
        new Instruction(variableId ),
        new Instruction("="),
        value,
        new Instruction()
    });
  }

  public void assignVariable(String variableId, String value, String ctxId) {
    this.add(new Instruction[] {
        new Instruction(""),
       // new Instruction(variableId + " - " + ctxId),
        new Instruction(variableId),
        new Instruction("="),
        new Instruction(value),
        new Instruction()
    });
  }

  public void assignVariable(String variableId, Instruction value) {
    this.add(new Instruction[] {
        new Instruction(""),
        new Instruction(variableId),
        new Instruction("="),
        value,
        new Instruction()
    });
  }

  public void implementFunction(String functionId) {
    this.add(new Instruction[] {
        new Instruction(""),
        new Instruction("func"),
        new Instruction(functionId),
        new Instruction(),
        new Instruction()
    });
  }

  public void declareVariable(String variableId, String ctxId) {
    this.add(new Instruction[] {
        new Instruction(""),
      //  new Instruction(variableId + " - " + ctxId),
        new Instruction(variableId),
        new Instruction(),
        new Instruction(),
        new Instruction()
    });
  }

  public void callFunction(String functionId) {
    this.add(new Instruction[] {
        new Instruction(""),
        new Instruction("goto"),
        this.searchFunctionDeclaration(functionId),
        new Instruction(),
        new Instruction()
    });
  }

  public void assignValueToVariable(String functionId, LinkedList<String> valueList) {
    int i = 0;
    for(; i < this.size(); i++) {
      if(this.get(i)[1].getText().equals("func") && this.get(i)[2].getText().equals(functionId)) {
        i++;
        break;
      }
    }
    for(String val : valueList) {
      this.get(i)[2].setText("=");
      this.get(i)[3].setText(val);
      i++;
    }
  }

  private Instruction searchFunctionDeclaration(String functionId) {
    for(Instruction[] item : this) {
      if(item[1].getText().equals("func") && item[2].getText().equals(functionId))
        return item[0];
    }
    return null;
  }

  public void endFunction() {
    this.add(new Instruction[] {
        new Instruction(""),
        new Instruction("return"),
        new Instruction(),
        new Instruction(),
        new Instruction()
    });
  }

  public void assignArithmeticOperation(String varId, String operation, String ctxId) { //operaciones aritmeticas
    ExpressionTree tree = new ExpressionTree(operation);
    tree.evaluateArithmeticOperation(tree.getRoot());
    tree.setId(this.id);
    tree.generateCode(tree.getRoot());
    int codeLength = this.size();
    
    for(String[] elem : tree.getCode()) {
      this.add(new Instruction[] {
        new Instruction(""),
        this.convertInstructionIdToReference(elem[1], codeLength),
        new Instruction(elem[2]),
        this.convertInstructionIdToReference(elem[3], codeLength),
        new Instruction()
      }); 
    }
    
    this.declareAndAssignVariable(varId, this.get(codeLength + tree.getCode().size() - 1)[0], ctxId);
  }

  public void evaluateLogicalOperation(String operation) { //operaciones logicas
    ExpressionTree tree = new ExpressionTree(operation);
    tree.evaluateLogicalOperation(tree.getRoot());
    tree.setId(this.id);
    tree.generateCode(tree.getRoot());
    int codeLength = tree.getCode().size();
    Instruction finalLogicalCondition;
    if(tree.getCode().size() > 0) {
      for (String[] elem : tree.getCode()) {
        this.add(new Instruction[] {
          new Instruction(""),
          this.convertInstructionIdToReference(elem[1], codeLength-1),
          new Instruction(elem[2]),
          this.convertInstructionIdToReference(elem[3], codeLength-1),
          new Instruction()
        });
      }
      finalLogicalCondition = this.getLast()[0];
    } else {
      finalLogicalCondition = new Instruction(operation);
    }
    this.addLogicalCondition(finalLogicalCondition);
  }

  /*toma como parametro un string de instruccion
     devuelve la referencia a la instruccion que apunta
  / sino devuelve una instruccion con el texto del numero involucrado */
  private Instruction convertInstructionIdToReference(String id, int length) {
    if(id.startsWith("$")) {
      int index = Integer.parseInt(id.substring(1));
      return this.get(length + index - 1)[0];
    } else {
      return new Instruction(id);
    }
  }

  public void addLogicalCondition(Instruction op) {
    this.add(new Instruction[] {
      new Instruction(""),
      new Instruction("if not"),
      op,
      new Instruction("goto"),
      new Instruction()
    });
  }

  public void completeIfStructure() {
    for(int i=this.size()-1; i>=0; i--) {
      if(this.get(i)[1].getText().equals("if not") &&
         this.get(i)[3].getText().equals("goto")) {
        this.addEmptyInstruction();
        this.get(i)[4] = this.getLast()[0];
        return;
      }
    }
  }

  public void completeWhileStructure() {
    for(int i=this.size()-1; i>=0; i--) {
      //obtenemos el indice de la instruccion comparadora del while
      if(this.get(i)[1].getText().equals("if not") &&
          this.get(i)[3].getText().equals("goto") &&
          this.get(i)[4].isEmpty()) {
        //agregamos el goto a dicha instruccion
        this.add(new Instruction[]{
            new Instruction(""),
            new Instruction("goto"),
            this.get(i)[0],
            new Instruction(),
            new Instruction()
        });
        //Agregamos la nueva linea a la que va a apuntar esa instruccion
        this.addEmptyInstruction();
        this.get(i)[4] = this.getLast()[0];
        return;
      }
    }
  }

  public void setForIncrementInstruction(String forIncrementInstruction) {
    this.forIncrementInstruction = forIncrementInstruction;
  }

  public void completeForStructure() {
    String id = this.forIncrementInstruction.substring(0,1), op = "";
    if(this.forIncrementInstruction.contains("++"))
      op = "+";
    else if(this.forIncrementInstruction.contains("--"))
      op = "-";

    this.add(new Instruction[] {
        new Instruction(""),
        new Instruction(id),
        new Instruction(op),
        new Instruction("1"),
        new Instruction()
    });

    this.assignVariable(id, this.getLast()[0]);

    for(int i = this.size()-1; i >= 0; i--) {
      if(this.get(i)[1].getText().equals("if not") &&
          this.get(i)[3].getText().equals("goto") &&
          this.get(i)[4].isEmpty()) {

        this.add(new Instruction[] {
            new Instruction(""),
            new Instruction("goto"),
            this.get(i)[0],
            new Instruction(),
            new Instruction()
        });
        this.addEmptyInstruction();
        this.get(i)[4] = this.getLast()[0];
        return;
      }
    }
  }

  public void printCode() {
    int id = this.id;
    for (int i = 0; i < this.size(); i++) {
      this.get(i)[0].setText("t" + id++);
    }

    String res = "\nt0: goto " + this.searchFunctionDeclaration("main").getText() + "\n";

    for (Instruction[] array : this) {
      res += array[0].getText()  + ": " + array[1].getText() + " " + array[2].getText() + " " + array[3].getText() + " " + array[4].getText() + "\n";
    }

    System.out.println(res);
    for(int i = 0; i < this.size(); i++)
      this.get(i)[0].setText("");
  }

  @Override
  public boolean add(Instruction[] insert) {
    try {
      Instruction[] last = this.getLast();
      if(last[0].isEmpty() && last[1].isEmpty() &&
         last[2].isEmpty() && last[3].isEmpty() &&
         last[3].isEmpty()) {
        last[0].setText(insert[0].getText());
        last[1].setText(insert[1].getText());
        last[2].setText(insert[2].getText());
        last[3].setText(insert[3].getText());
        last[4].setText(insert[4].getText());
        return false;
      } else
        return super.add(insert);
    } catch (Exception e) {
      return super.add(insert);
    }
  }

  private void addEmptyInstruction() {
    this.add(new Instruction[] {
        new Instruction(),
        new Instruction(),
        new Instruction(),
        new Instruction(),
        new Instruction()
    });
  }

  public void optimizeCode(ContextList discardedContexts) { //eliminamos lo que no se utiliza
    this.removeUninitializedVariables();
    this.replaceOperationsWithConstants();
    this.removeUnusedVariables(discardedContexts);
    this.removeUnusedFunctions(discardedContexts);
    this.removeEmptyFunctions();
  }

  
  private void removeEmptyFunctions() { //para eliminar las funciones vacias
    for(int i = 0; i < this.size()-1; i++) {
      if(this.get(i)[1].getText().equals("func") && !this.get(i)[2].getText().equals("main") && this.get(i+1)[1].getText().equals("return")) {
        //buscamos el goto a esa funcion
        for(int j = 0; j < this.size(); j++) {
          if(this.get(j)[1].getText().equals("goto") && this.get(j)[2].equals(this.get(i)[0])) {
            this.remove(j);
            this.remove(i);
            this.remove(i); //mismo i que el anterior, pq cuando borramos el primero, el siguiente baja un indice de posicion
            i--;
          }
        }
      }
    }
  }

  private void replaceOperationsWithConstants() { //reemplazamos las operaciones aritmeticas por constantes
    for(int i = 0; i < this.size(); i++) {
      Instruction[] inst = this.get(i);

      try {
        float val1 = Float.valueOf(inst[1].getText());
        float val2 = Float.valueOf(inst[3].getText());
        if(!(inst[2].getText().equals("+") || inst[2].getText().equals("-") || inst[2].getText().equals("*") || inst[2].getText().equals("/") || inst[2].getText().equals("%")))
          throw new Exception("");

        String res = "";

          if(inst[2].getText().equals("+"))
            res = String.valueOf(val1 + val2);
          else if(inst[2].getText().equals("-"))
            res = String.valueOf(val1 - val2);
          else if(inst[2].getText().equals("*"))
            res = String.valueOf(val1 * val2);
          else if(inst[2].getText().equals("/"))
            res = String.valueOf(val1 / val2);
          else if(inst[2].getText().equals("%"))
            res = String.valueOf(val1 % val2);

        for(int j = i; j < this.size(); j++) {
          if(this.get(j)[1].equals(inst[0]))
            this.get(j)[1].setText(res);
          else if(this.get(j)[2].equals(inst[0]))
            this.get(j)[2].setText(res);
          else if(this.get(j)[3].equals(inst[0]))
            this.get(j)[3].setText(res);
          else if(this.get(j)[4].equals(inst[0]))
            this.get(j)[4].setText(res);
        }

        this.remove(i);
        i--;
      } catch (Exception e) {
      }
    }
  }

  private void removeUnusedFunctions(ContextList discardedContexts) { //eliminamos funciones sin uso 
    for(Context ctx : discardedContexts) {
      ctx.forEach((key, Id) -> {
        if(Id instanceof Function && !Id.isUsed()) { //verificamos que no se use
          for(int i = 0; i < this.size(); i++) {
            if(this.get(i)[1].getText().equals("func") && this.get(i)[2].getText().equals(Id.getName())) {
              while(!this.get(i)[1].getText().equals("return")) {
                this.remove(i);
              }
              this.remove(i);
              return;
            }
          }
        }
      });
    }
  }

  private void removeUnusedVariables(ContextList discardedContexts) { //eliminamos las variables sin uso
    for(Context ctx : discardedContexts) {
      ctx.forEach((key, Id) -> {
        if(Id instanceof Variable && !Id.isUsed()) { //verificamos que no sean usadas.
          for(int i = 0; i < this.size(); i++) {
            for(int j = 1; j < this.get(i).length; j++) {
              if(this.get(i)[j].getText().equals(Id.getName() + " - " + ctx.getContextId())) {
                this.remove(i);
                break;
              }
            }
          }
        }
      });
    }
  }

  private void removeUninitializedVariables() { //remover variables no inicializadas
    for(int i = 0; i < this.size(); i++) {
      if(!this.get(i)[1].getText().equals("") && !this.get(i)[1].getText().equals("return") && !this.get(i)[1].getText().equals("goto") && this.get(i)[2].getText().equals("") && this.get(i)[3].getText().equals("") && this.get(i)[4].getText() .equals("")) {
        this.remove(i);
        return;
      }
    }
  }
}