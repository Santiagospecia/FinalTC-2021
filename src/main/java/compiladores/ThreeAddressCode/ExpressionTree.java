package compiladores.ThreeAddressCode;

import compiladores.StringUtils;
import java.util.LinkedList;
import java.util.Stack;

public class ExpressionTree {
  private BinaryTree tree;
  private LinkedList<String[]> code;
  private int id;

  public void setId(int id) {
    this.id = id;
  }

  public ExpressionTree(String operation) {
    this.tree = new BinaryTree(operation.replaceAll("[\\[\\,\\]\\ ]", "")); //reemplazo las coincidencias por ""
    this.code = new LinkedList<>();
    this.id = 0;
  }

  public void evaluateArithmeticOperation(TreeNode node) { //evaluamos las operaciones aritmeticas
    
    String[] operators = new String[]{"+", "-", "/", "*", "%"};
    node.setValue(removeParenthesis(node.getValue()));
    if(StringUtils.isStringInArray(node.getValue(), operators)) { // OPERADORES ARITMETICOS ?
      divideNodeByOperators(node, operators);
      evaluateArithmeticOperation(node.getLeft());
      evaluateArithmeticOperation(node.getRight());
    }
  }

  public boolean balancedParenthensies(String s) {
    Stack<Character> stack  = new Stack<>();
    for(char c : s.toCharArray()) { 
      if(c == '(') {
        stack.push(c);
      } else if(c == ')') {
        if(stack.isEmpty() || stack.pop() != '(') {
          return false;
        }
      }
    }
    return stack.isEmpty();
  }

  public void evaluateLogicalOperation(TreeNode node) { //evaluamos las operaciones logicas
    String[] operators = new String[]{"||", "&&", "==", "!=", "<", "<=", ">", ">="};
    node.setValue(removeParenthesis(node.getValue()));
    if(StringUtils.isStringInArray(node.getValue(), operators)) { // OPERADORES LOGICOS ?
      divideNodeByOperators(node, operators);
      evaluateLogicalOperation(node.getLeft());
      evaluateLogicalOperation(node.getRight());
    }
  }

  private void divideNodeByOperators(TreeNode node, String[] operators) {
    String op = node.getValue();
    String[] splitedOp = this.splitOperationByOperators(op, operators);
    if(splitedOp != null) {
      
      node.setLeft(new TreeNode(splitedOp[0]));
      node.setValue(splitedOp[1]);
      node.setRight(new TreeNode(splitedOp[2]));
    }
  }

  private String[] splitOperationByOperators(String op, String[] operators) { //dividimos por operador
    for (String operator : operators) {
      int opLen = operator.length();
      for (int i = 0; i < op.length() + 1 - opLen; i++) {
        if(op.substring(i, i + opLen).equals(operator)) { //lo encuentro en la operacion
          String leftSide = op.substring(0, i);
          if (StringUtils.countMatches(leftSide, '(') ==
              StringUtils.countMatches(leftSide, ')')) { //NO esta entre parentisis
            return new String[]{leftSide, op.substring(i, i + opLen), op.substring(i + opLen)};
          }
        }
      }
    }
    return null; 
  }

  private String removeParenthesis(String op) {
    if(op.length() > 2) {
      String substring = op.substring(1, op.length()-1); //extraemos caracteres
      if((op.charAt(0) == '(' && op.charAt(op.length()-1) == ')') &&
          this.balancedParenthensies(substring)) {
        return substring;
      }
    }
    return op;
  }

  public TreeNode getRoot() {
    return this.tree.getRoot();
  }

  public BinaryTree getTree() {
    return this.tree;
  }

  public LinkedList<String[]> getCode() {
    return this.code;
  }

  public void generateCode(TreeNode node) {
    //System.out.print(node);
    if(node.getLeft() != null) {
      generateCode(node.getLeft());
    }
    if(node.getRight() != null) {
      generateCode(node.getRight());
      this.code.add(new String[] {
          "$" + id,
          (node.getLeft() == null ? "" : node.getLeft().getValue()),
          (node.getValue()),
          (node.getRight() == null ? "" : node.getRight().getValue())});
      node.setValue("$" + id);
      id += 1;
    }
  }

  public String getPrintableCode() {
    String res = "";
    for (String[] elem : code) {
      res += elem[0] + ": " + elem[1] + " " + elem[2] + " " + elem[3] + "\n";
    }
    return res;
  }
}
