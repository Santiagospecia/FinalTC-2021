package compiladores.TablaSimbolos;


import java.util.*;

public class Context extends HashMap<String, Id>{
    private String contextId;
    
    public Context() {
        super();
        contextId = UUID.randomUUID().toString();
    }

    public Context(String contextId){
        super();
        this.contextId = contextId;
    }
    
    public String getContextId(){
        return contextId;
    }

    public void setContextId(String contextId){
        this.contextId = contextId;
    }

    @Override
    public String toString() {
        String ret = "\n Contexto: " + this.contextId;
        ret += "\n________________\n";
        for (String key : this.keySet()) {
          ret += this.get(key).toString() + "\n";
        }
        if (this.keySet().isEmpty()) {
          ret += " Contexto vacio  \n";
        }
        ret += "________________\n";
    
        return ret;
      }
}
