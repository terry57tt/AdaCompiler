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

    public Node() {
        this.children = new ArrayList<>();
        this.isFinal = false;
        this.parent = null;
    }

    /* Create the same node as node without children */
    public Node(Node node) {
        this.children = new ArrayList<>();
        this.isFinal = node.isFinal();
        this.parent = node.getParent();
        this.token = node.getToken();
        this.value = node.getValue();
        this.type = node.getType();
    }

    /* Create a node with a semantic action. */
    public Node(NodeType type, ArrayList<Node> children) {
        this.type = type;
        this.children = children;
        this.isFinal = false;
        this.parent = null;
    }

    /* Create final node with a token. */
    public Node(Token token) {
        this.token = token;
        this.children = new ArrayList<>();
        this.isFinal = true;
        this.value = token.getValue();
        this.parent = null;
    }

    /* Create intermediate node with a non_terminal */

    public Node(String value){
        this.children = new ArrayList<>();
        this.isFinal = false;
        this.value = value;
        this.parent = null;
    }

    /* Add a child to the node. */
    public void addChild(Node node) {
        this.children.add(node);
        node.parent = this;
    }

    /* Add a list of children to the node. */
    public void addChildren(List<Node> nodeList) {
        for (Node node : nodeList) {
            this.addChild(node);
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
        if(type != null){
            return type.name();
        }
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

    public void setType(NodeType type) {
        this.type = type;
    }

    public void defineName() {
        switch (this.value) {
            case "PROGRAM":
                this.type = NodeType.PROGRAM;
                break;
            case "IDENTIFIER":
                this.type = NodeType.IDENTIFIER;
                break;
            case "Fichier":
                this.type = NodeType.FILE;
                break;
            case "BODY":
                this.type = NodeType.BODY;
                break;
            case "VIRGULE":
                this.type = NodeType.VIRGULE;
                break;
            case "TYPE":
                this.type = NodeType.TYPE;
                break;
            case "IS":
                this.type = NodeType.IS;
                break;
            case "ACCESS":
                this.type = NodeType.ACCESS;
                break;
            case "RECORD":
                this.type = NodeType.RECORD;
                break;
            case "DECL_VAR":
                this.type = NodeType.DECL_VAR;
                break;
            case "DECL_PROC":
                this.type = NodeType.DECL_PROC;
                break;
            case "DECL_FUNC":
                this.type = NodeType.DECL_FUNC;
                break;
            case "FIELD":
                this.type = NodeType.FIELD;
                break;
            case "INITIALIZATION":
                this.type = NodeType.INITIALIZATION;
                break;
            case "PARAMETERS":
                this.type = NodeType.PARAMETERS;
                break;
            case "MULTIPLE_PARAM":
                this.type = NodeType.MULTIPLE_PARAM;
                break;
            case "MODE":
                this.type = NodeType.MODE;
                break;
            case "IN":
                this.type = NodeType.IN;
                break;
            case "INOUT":
                this.type = NodeType.INOUT;
                break;
            case "OR":
                this.type = NodeType.OR;
                break;
            case "ELSE":
                this.type = NodeType.ELSE;
                break;
            case "AND":
                this.type = NodeType.AND;
                break;
            case "THEN":
                this.type = NodeType.THEN;
                break;
            case "NOT":
                this.type = NodeType.NOT;
                break;
            case "SLASH_EQUAL":
                this.type = NodeType.SLASH_EQUAL;
                break;
            case "EQUAL":
                this.type = NodeType.EQUAL;
                break;
            case "INFERIOR":
                this.type = NodeType.INFERIOR;
                break;
            case "INFERIOR_EQUAL":
                this.type = NodeType.INFERIOR_EQUAL;
                break;
            case "SUPERIOR":
                this.type = NodeType.SUPERIOR;
                break;
            case "SUPERIOR_EQUAL":
                this.type = NodeType.SUPERIOR_EQUAL;
                break;
            case "+":
                this.type = NodeType.ADDITION;
                break;
            case "-":
                this.type = NodeType.SUBSTRACTION;
                break;
            case "*":
                this.type = NodeType.MULTIPLY;
                break;
            case "/":
                this.type = NodeType.DIVIDE;
                break;
            case "REM":
                this.type = NodeType.REM;
                break;
            case "NEGATIVE_SIGN":
                this.type = NodeType.NEGATIVE_SIGN;
                break;
            case "POINT":
                this.type = NodeType.POINT;
                break;
            case "INTEGER":
                this.type = NodeType.INTEGER;
                break;
            case "CHARACTER":
                this.type = NodeType.CHARACTER;
                break;
            case "TRUE":
                this.type = NodeType.TRUE;
                break;
            case "FALSE":
                this.type = NodeType.FALSE;
                break;
            case "NULL":
                this.type = NodeType.NULL;
                break;
            case "NEW":
                this.type = NodeType.NEW;
                break;
            case "CHAR_VAL":
                this.type = NodeType.CHAR_VAL;
                break;
            case "RETURN":
                this.type = NodeType.RETURN;
                break;
            case "BEGIN":
                this.type = NodeType.BEGIN;
                break;
            case "IF":
                this.type = NodeType.IF;
                break;
            case "FOR":
                this.type = NodeType.FOR;
                break;
            case "WHILE":
                this.type = NodeType.WHILE;
                break;
            case "AFFECTATION":
                this.type = NodeType.AFFECTATION;
                break;
            case "REVERSE":
                this.type = NodeType.REVERSE;
                break;
            case "ELSIF":
                this.type = NodeType.ELSIF;
                break;
            case "EXPRESSION":
                this.type = NodeType.EXPRESSION;
                break;
            default:
                break;
        }
    }
}
