package compiladores.TablaSimbolos;

public abstract class Id {
  String name, dataType;
  Boolean used;

  public Id (String name, String dataType, boolean used) {
    this.name = name;
    this.dataType = dataType;
    this.used = used;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Boolean isUsed() {
    return used;
  }

  public void setUsed(Boolean used) {
    this.used = used;
  }

  protected boolean isImplemented() {
    return false;
  }

  protected void setImplemented(boolean implemented) {
  }

  protected boolean isInitialized() {
    return false;
  }

  protected void setInitialized(boolean initialized) {
  }
}
