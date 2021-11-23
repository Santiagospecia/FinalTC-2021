package compiladores.TablaSimbolos;

public class Function extends Id {
    private boolean declared, implemented;

  public Function(String name, String dataType, boolean declared, boolean impl, boolean used) {
    super(name, dataType, used);
    this.declared = declared;
    this.implemented = impl;
  }

  @Override
  public boolean isImplemented() {
    return implemented;
  }

  @Override
  public void setImplemented(boolean implemented) {
    this.implemented = implemented;
  }

  public boolean isDeclared() {
    return declared;
  }

  public void setDeclared(boolean declared) {
    this.declared = declared;
  }

  public String toString() {
    return "name: " + this.name + ", dataType: " + this.dataType + ", impl: " + this.implemented + ", used: " + this.used
        + ", declared : " + this.declared;
  }
}