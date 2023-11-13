package org.pcl.structure.tree;

/** Create the syntax tree */
public class SyntaxTree {

    /** Root node of the syntax tree. */
    private final Node rootNode;

    public SyntaxTree(){ this.rootNode = null; }
    public SyntaxTree(Node rootNode) {
        this.rootNode = rootNode;
    }

    /** Return the root node of the syntax tree. */
    public Node getRootNode() {
        return this.rootNode;
    }

    /** Navigate the syntax tree. */
    public void navigate() {
        depthFirstNavigation(this.rootNode);
    }

    /** Navigate the syntax tree in depth first. */
    private void depthFirstNavigation(Node node) {
        if (node.isFinal()) {
            System.out.println(node.getToken());
        } else {
            for (Node child : node.getChildren()) {
                depthFirstNavigation(child);
            }
        }
    }

    /** Return the syntax tree as a string. */
    @Override
    public String toString() {
        return "SyntaxTree{" +
                "rootNode=" + rootNode +
                '}';
    }
}
