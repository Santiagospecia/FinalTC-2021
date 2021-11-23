package compiladores.ThreeAddressCode;

public class Instruction {
    private String text;
  
    public Instruction(String text) {
      this.text = text;
    }
  
    public Instruction() {
      this.text = "";
    }
  
    public String getText() {
      return text;
    }
  
    public void setText(String text) {
      this.text = text;
    }
  
    public boolean isEmpty() {
      return this.text.equals("");
    }
  }