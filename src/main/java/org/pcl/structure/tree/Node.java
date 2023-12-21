package org.pcl.structure.tree;

import org.pcl.Token;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/** Represent a node in the syntax tree. */
public class Node {

    /* Semantic action to execute when the node is reached. */
    private NodeType type;

    /* Children of the node. */
    private final ArrayList<Node> children;

    /* If the node is a leaf */
    private final boolean isFinal;

    /* Associated token */
    private Token token;
    private String value;

    public Node() {
        this.children = new ArrayList<>();
        this.isFinal = false;
    }

    /* Create a node with a semantic action. */
    public Node(NodeType type, ArrayList<Node> children) {
        this.type = type;
        this.children = children;
        this.isFinal = false;
    }

    /* Create final node with a token. */
    public Node(Token token) {
        this.token = token;
        this.children = new ArrayList<>();
        this.isFinal = true;
        this.value = token.getValue();
    }

    /* Create intermediate node with a non_terminal */

    public Node(String value){
        this.children = new ArrayList<>();
        this.isFinal = false;
        this.value = value;
    }

    /* Add a child to the node. */
    public void addChild(Node node) {
        this.children.add(node);
    }

    /* Add a list of children to the node. */
    public void addChildren(List<Node> nodeList) {
        for (Node node : nodeList) {
            this.addChild(node);
        }
    }

    /** Return the semantic action of the node. */
    public NodeType getType() {
        return type;
    }
    
    /** Return the children of the node. */
    public ArrayList<Node> getChildren() {
        return children;
    }
    /** Return if the node is final. */
    public boolean isFinal() {
        return isFinal;
    }
    
    /** Return the token associated with the node. */
    public Token getToken() {
        return token;
    }

    /** Return the value associated with the node. */
    public String getValue() {return this.value;}
    /** Return the node as a string. */
    @Override
    public String toString() {
        return value;
    }

}
