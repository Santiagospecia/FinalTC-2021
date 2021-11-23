package compiladores.ThreeAddressCode;

public class BinaryTree {
  private TreeNode root;

  public BinaryTree(String value) {
    this.root = new TreeNode(value);
  }

  public TreeNode addLeftNode(String value) {
    return new TreeNode(value);
  }

  public TreeNode addRightNode(String value) {
    return new TreeNode(value);
  }

  public TreeNode getRoot() {
    return root;
  }

  public void setRoot(TreeNode root) {
    this.root = root;
  }

  @Override
  public String toString() {
    return "BinaryTree = {" +
        "root=" + root +
        '}';
  }

  public String toStringBeauty() {
    return "BinaryTree = {\n" +
        "root=" + root + "\n" +
        '}';
  }
}
