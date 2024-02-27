package org.pcl.structure.tree;

import org.pcl.Token;

import java.util.ArrayList;
import java.util.List;


/** Represent a node in the syntax tree. */
public class Node {

    /* Semantic action to execute when the node is reached. */
    private NodeType type;

    /* Children of the node. */
    private ArrayList<Node> children;

    /* Parent of the node. */
    private Node parent;

    /* If the node is a leaf */
    private final boolean isFinal;

    /* Associated token */
    private Token token;
    private String value;
    private boolean meaningful;

    public Node() {
        this.children = new ArrayList<>();
        this.isFinal = false;
        this.parent = null;
        this.meaningful = false;
    }

    /* Create the same node as node without children */
    public Node(Node node) {
        this.children = new ArrayList<>();
        this.isFinal = node.isFinal();
        this.parent = node.getParent();
        this.token = node.getToken();
        this.value = node.getValue();
        this.type = node.getType();
        this.meaningful = false;
    }

    /* Create a node with a semantic action. */
    public Node(NodeType type, ArrayList<Node> children) {
        this.type = type;
        this.children = children;
        this.isFinal = false;
        this.parent = null;
        this.meaningful = false;
    }

    /* Create final node with a token. */
    public Node(Token token) {
        this.token = token;
        this.children = new ArrayList<>();
        this.isFinal = true;
        this.value = token.getValue();
        this.parent = null;
        this.meaningful = false;
    }

    /* Create intermediate node with a non_terminal */

    public Node(String value){
        this.children = new ArrayList<>();
        this.isFinal = false;
        this.value = value;
        this.parent = null;
        this.meaningful = false;
    }

    /* Add a child to the node. */
    public void addChild(Node node) {
        this.children.add(node);
        node.parent = this;
    }

    public void addChild(int index, Node node){
        this.children.add(index, node);
        node.parent = this;
    }

    /* Add a list of children to the node. */
    public void addChildren(List<Node> nodeList) {
        for (Node node : nodeList) {
            this.addChild(node);
        }
    }

    public void addChildren(int index, List<Node> nodeList){
        for (Node node : nodeList) {
            this.addChild(index, node);
            index++;
        }
    }

    public void replaceChild(Node childToBeReplaced, Node remplacement){
        int indexChild = children.indexOf(childToBeReplaced);
        children.set(indexChild, remplacement);
    }

    public void deleteChildren(){
        this.children = new ArrayList<>();
    }

    /** Return the semantic action of the node. */
    public NodeType getType() {
        return type;
    }
    
    /** Return the children of the node. */
    public ArrayList<Node> getChildren() {
        return children;
    }
    public Node getChild(int index){
        return children.get(index);
    }
    public void setChildren(ArrayList<Node> children){
        this.children = children;
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

    public Boolean nonTerminalInChildren(){
        //return true if there is a non-terminal Node in Node's children and children's children...
        ArrayList<Node> nodesToVisit = new ArrayList<>();
        Node currentNode = this; //current Node of parse Tree
        nodesToVisit.add(currentNode);

        while (!nodesToVisit.isEmpty()) {
            currentNode = nodesToVisit.get(0);
            nodesToVisit.remove(0);
            int i = 0;
            for (Node child : currentNode.getChildren()) {
                nodesToVisit.add(i, child);
                i++;
            }
            if (!currentNode.isFinal()) {
                return true;
            }
        }
        return false;
    }

    public Boolean nonTerminalInDirectChildren(){
        for(Node child : this.children){
            if (!child.isFinal){
                return true;
            }
        }
        return false;
    }

    public Node getParent(){
        return parent;
    }
    public void setParent(Node parent){
        this.parent = parent;
    }

    public void setValue(String value){
        this.value = value;
    }
    public void getToken(Token token){
        this.token = token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setMeaningful(boolean meaningful){
        this.meaningful = meaningful;
    }
    public Boolean isMeaningful(){
        return this.meaningful;
    }

    public Node firstChild(){
        return this.children.get(0);
    }

    public void deleteFromParent(){
        this.parent.children.remove(this);
    }

    public void deleteFromParentTransferringChildTokenToParent(){
        this.parent.children.remove(this);
        this.parent.token = this.token;
    }
    public void deleteFromParentTransferringChildTokenTo(Node node){
        this.parent.children.remove(this);
        node.token = this.token;
    }

    public int indexInBrothers(){
        return this.parent.children.indexOf(this);
    }


}
