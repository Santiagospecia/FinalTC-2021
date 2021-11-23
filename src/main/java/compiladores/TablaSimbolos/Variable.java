package compiladores.TablaSimbolos;

public class Variable extends Id{
  private boolean initialized;

  public Variable(String name, String dataType, boolean init, boolean used) {
    super(name, dataType, used);
    this.initialized = init;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  public String toString() {
    return "Name: " + this.name + ", dataType: " + this.dataType + ", init: " + this.initialized + ", used: " + this.used;
  }
}
