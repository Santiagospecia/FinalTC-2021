package compiladores.ThreeAddressCode;

public class TreeNode {
    private String value;
    private TreeNode left, right;
    public TreeNode(String value) {
      this.value = value;
      this.left = null;
      this.right = null;
    }
  
    public String getValue() {
      return value;
    }
  
    public void setValue(String value) {
      this.value = value;
    }
  
    public TreeNode getLeft() {
      return left;
    }
  
    public void setLeft(TreeNode left) {
      this.left = left;
    }
  
    public TreeNode getRight() {
      return right;
    }
  
    public void setRight(TreeNode right) {
      this.right = right;
    }
  
    @Override
    public String toString() {
      return "TreeNode = {" + "\n" +
          "left=" + left + "\n" +
          ", value='" + value + '\'' + "\n" +
          ", right=" + right + "\n" +
          '}';
    }
  
    public String toStringBeauty() {
      return "TreeNode = {" + "\n" +
          (left == null ? "" : "left=" + left + ",\n") +
          "value='" + value + '\'' + ",\n" +
          (right == null ? "" : "right=" + right + "\n") +
          '}';
    }
  }
  