package compiladores.TablaSimbolos;

import java.util.*;

public class ContextList extends LinkedList<Context>{
  public ContextList() { }

  public Variable getVariable(String varId) {
    for(Context ctx : this) {
      if(ctx.get(varId) != null) {
        return (Variable)ctx.get(varId);
      }
    }
    return null;
  }

  public Function getFunction(String functionId) {
    return (Function)this.getLast().get(functionId);
  }

  public Context getGlobal() {
    return this.getLast();
  }

  public Context getContext(String contextId) {
    for(Context ctx : this) {
      if(ctx.getContextId().equals(contextId))
        return ctx;
    }
    return null;
  }
}
