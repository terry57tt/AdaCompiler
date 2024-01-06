package org.pcl.grammaire;

import org.pcl.ColorAnsiCode;
import org.pcl.Token;
import org.pcl.ig.PClWindows;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.Collections;

public class Grammar_ast {
    public Boolean error = false;

    private int numberErrors = 0;

    public Token currentToken = null;
    public SyntaxTree syntaxTree = null;
    public SyntaxTree syntaxTree2 = null;
    public SyntaxTree ast = null;
    private Boolean indicateur_acces = false;
    public int tokensIndex = 0;
    ArrayList<Token> tokens;



/*    public Node reduceNodesChildren(Node node){
        Node lastTerminalNode = node; //last terminal Node without numbers and identifiers
        Node newNode = new Node(node.getValue());
        for (Node child : node.getChildren()){
            if (child.isFinal() && child.getToken().getType() != TokenType.NUMBER
                    && !child.getToken().getValue().equals("(")
                    && !child.getToken().getValue().equals(")")
                    && !child.getToken().getValue().equals(";")
                    && !child.getToken().getValue().equals(":")
                    && (child.getToken().getType() != TokenType.IDENTIFIER
                        || !tokens.get(tokens.indexOf(child.getToken()) + 1).getValue().equals("("))) {
                lastTerminalNode = child;
                newNode.addChild(child);
            }
            else if(!child.isFinal()) {
                Node newChild = new Node();
                newChild = lastTerminalNode;
                newChild.addChild(child);
                newNode.getChildren().remove(lastTerminalNode);
                newNode.getChildren().add(newChild);

            }
        }
        return newNode;
    }*/
    public Node reduceNodesChildren(Node node) {
        Node lastTerminalNode = node; //last terminal Node without numbers and identifiers
        Node newNode = new Node(node); //création d'un nouveau noeud égal à node sans ses enfants

        ArrayList<Node> nodeChildren = node.getChildren(); //enfants de node

        for (Node child : nodeChildren) {
            //pour tout child, enfant de node

            if (child.isFinal() && child.getToken().getType() != TokenType.NUMBER
                    && child.getToken().getType() != TokenType.IDENTIFIER
                    && !child.getToken().getValue().equals("(")
                    && !child.getToken().getValue().equals(")")
                    && !child.getToken().getValue().equals(";")
                    && !child.getToken().getValue().equals(":")) {
                /*si child est terminal (n'est pas un nombre, (, ), :, ;,
                ou n'est pas un identificateur sauf un identificateur de fonction)
                alors on le note comment dernier non terminal et on l'ajoute comme fils du nouveau noeud
                 */
                lastTerminalNode = child;
                newNode.addChild(child);
                child.setParent(lastTerminalNode);
                child.setParent(newNode);

            }else if (child.isFinal() && child.getToken().getType() == TokenType.IDENTIFIER){
                if (tokens.get(tokens.indexOf(child.getToken()) + 1).getValue().equals("(")) {
                    lastTerminalNode = child;
                    newNode.addChild(child);
//                    child.setParent(lastTerminalNode);
                    child.setParent(newNode);
                }
                else {
                    if (child.getToken().getLineNumber() != 1){
                    newNode.addChild(child);
//                    child.setParent(lastTerminalNode);
                    child.setParent(newNode);}
                }
            }
            else if (!child.isFinal()) {
                //si child est non terminal
                for (Node childChild : child.getChildren()) {
                    /* pour tout childChild, enfant de child*/
                    if (lastTerminalNode == node) {
                        //s'il n'a pas eu d'enfants terminal avant child
                        //on ajoute les enfants de l'enfant aux enfants du nouveau noeud
                        //le noeud non terminal est donc supprimé
                        childChild.setParent(newNode);
                        newNode.addChild(childChild);

                    } else {
                        //sinon on ajoute les enfants de l'enfant non terminal au dernier terminal rencontré (en dehors des cas particuliers)
                        lastTerminalNode.addChild(childChild);
                        childChild.setParent(lastTerminalNode);
                        newNode.addChild(lastTerminalNode);
                        lastTerminalNode.setParent(newNode);
                    }
                    newNode.getChildren().remove(lastTerminalNode);
                }
                //on réinitialise le lastTerminalNode entre chaque branche
                lastTerminalNode = node;

            }
        }
        return newNode;
    }


    public void createAST(){
        if(this.syntaxTree == null){
            System.out.println("Il n'y a pas encore de parse tree.");
            return;
        }
        this.ast = this.syntaxTree;
        ArrayList<Node> nodes_to_visit = new ArrayList<>();
        Node currentNode = new Node();
        currentNode = ast.getRootNode();



        //cas du noeud racine : en fin de boucle, le noeud racine n'a plus d'enfants non terminaux
        while (currentNode.nonTerminalInDirectChildren()) {
            currentNode = reduceNodesChildren(currentNode);
            this.ast = new SyntaxTree(currentNode);
        }


        //on ajoute les enfants du nouveau noeud racine à la liste de noeud à visiter
        nodes_to_visit.addAll(currentNode.getChildren());

        //cas des autres noeuds
        while (!nodes_to_visit.isEmpty()){
        //on étudie le prochain noeud à visiter (parcours en profondeur), on l'enlève des noeuds à visiter
            currentNode = nodes_to_visit.get(0); // c'est un noeud de l'ast
            nodes_to_visit.remove(0);
            while (currentNode.nonTerminalInDirectChildren()) {
                //tant que le noeud courant a encore des enfants non terminaux
                //on réduit le noeud et on remplace le noeud dans l'arbre précédent
                Node nodeToBeAdded = reduceNodesChildren(currentNode);
                currentNode.getParent().replaceChild(currentNode, nodeToBeAdded);
                currentNode = nodeToBeAdded;
            }

            //ajout des enfants du noeud courant au début de la liste des noeuds à visiter
            int i = 0;
            for (Node child : currentNode.getChildren()){
                nodes_to_visit.add(i, child);
                i ++;
            }
        }

        //l'ast est presque fini, il reste à arranger les opérations : on fait un parcours en largeur
        arangeAST();

    }

    public void arangeAST(){
        ArrayList<Node> nodes_to_visit = new ArrayList<>();
        Node lastNode = ast.getRootNode();
        Node currentNode = ast.getRootNode();
        nodes_to_visit.add(ast.getRootNode());


        while (!nodes_to_visit.isEmpty()){
            currentNode = nodes_to_visit.get(0); // c'est un noeud de l'ast
            nodes_to_visit.remove(0);
            if(currentNode.getToken() != null){
                // on enlève les noeuds qui ne servent pas parmi les enfants du root node
                if (currentNode.getToken().getValue().equalsIgnoreCase("begin") && currentNode.getToken().getType().equals(TokenType.KEYWORD) && currentNode.getParent().equals(ast.getRootNode())){
                    currentNode.setValue("BODY");
                    int indexBegin = currentNode.getParent().getChildren().indexOf(currentNode);
                    for (int i = 0; i < indexBegin; i++) {
                        if (!ast.getRootNode().getChildren().get(0).getValue().equals("procedure")){
                            ast.getRootNode().getChildren().remove(0);
                        }
                    }
                }
                if (currentNode.getToken().getValue().equalsIgnoreCase("is") && currentNode.getToken().getType().equals(TokenType.KEYWORD) && currentNode.getParent().equals(ast.getRootNode())){
                    ast.getRootNode().getChildren().remove(currentNode);
                }

                //on arrange les opérations, affectations
                if (currentNode.getToken().getValue().equals(":=")
                        || currentNode.getToken().getValue().equals("/=")
                        || currentNode.getToken().getValue().equals(">")
                        || currentNode.getToken().getValue().equals(">=")
                        || currentNode.getToken().getValue().equals("<")
                        || currentNode.getToken().getValue().equals("<=")
                        || currentNode.getToken().getValue().equals("+")
                        || currentNode.getToken().getValue().equals("-")
                        || currentNode.getToken().getValue().equals("*")
                        || currentNode.getToken().getValue().equals("/")
                        || currentNode.getToken().getValue().equals("=")
                        || currentNode.getToken().getValue().equalsIgnoreCase("rem")
                        || currentNode.getToken().getValue().equals(".")){
                    currentNode.getChildren().add(0, lastNode); //ajout de lastNode comme 1er enfant
                    lastNode.getParent().getChildren().remove(lastNode);//suppression de lastNode de son parent
                    lastNode.setParent(currentNode);//ajout de currentNode comme parent de lastNode
                }
                // on arrange les boucles if
                if (currentNode.getToken().getValue().equalsIgnoreCase("then")){
                    lastNode.getChildren().add(currentNode); //ajout de currentNode comme enfant de last Node
                    currentNode.getParent().getChildren().remove(currentNode);//suppression de currentNode de son parent
                    currentNode.setParent(lastNode);//ajout de lastNode comme parent de currentNode
                }

                //on arrange les boucles while
                if (!lastNode.equals(ast.getRootNode())){
                    if (lastNode.getToken().getValue().equalsIgnoreCase("while")){
                        lastNode.getChildren().add(currentNode); //ajout de currentNode comme enfant de last Node
                        currentNode.getParent().getChildren().remove(currentNode);//suppression de currentNode de son parent
                        currentNode.setParent(lastNode);//ajout de lastNode comme parent de currentNode
                    }
                }

                //on enlève les end
                if (currentNode.getValue().equalsIgnoreCase("end")){
                    if(currentNode.getChildren().size() == 0) {
                        currentNode.getParent().getChildren().remove(currentNode);
                    }
                }
                if (currentNode.getValue().equalsIgnoreCase("if")
                        || currentNode.getValue().equalsIgnoreCase("loop")){
                    if(currentNode.getChildren().size() == 0){
                        currentNode.getParent().getChildren().remove(currentNode);
                    }
                }


            lastNode = currentNode;
            }
            nodes_to_visit.addAll(currentNode.getChildren());
        }
    }






    public Grammar_ast(ArrayList<Token> tokens){
        this.tokens = tokens;
    }

    private void printError(String expectedMessage, Token currentToken){
        boolean multiples = expectedMessage.contains(" ");
        numberErrors++;
        System.out.println("line " + currentToken.getLineNumber() + ":" + ColorAnsiCode.ANSI_RED + " error:" + ColorAnsiCode.ANSI_RESET +
                " expected " + (multiples ? " one of them": "") + " \"" + expectedMessage + "\" got [value=" + currentToken.getValue() + " type=" + currentToken.getType() + "]");
        System.out.println(getLineToken(currentToken.getLineNumber()) + "\n");
    }

    private String getLineToken(long line) {
        StringBuilder lineToken = new StringBuilder();
        for (Token token: tokens) {
            if (token.getLineNumber() == line) {
                lineToken.append(token.getValue()).append(" ");
            }
        }
        return lineToken.toString();
    }

    public int getNumberErrors() {
        return numberErrors;
    }

    public SyntaxTree getSyntaxTree() {
        this.currentToken = tokens.get(tokensIndex);
        fichier();
        return this.syntaxTree;
    }





    // terminals procedures

    public void terminalAnalyse(String terminal, Node node){
        if(!this.error){
            if(currentToken.getValue().equalsIgnoreCase(terminal)){
                Node terminalNode = new Node(currentToken);
                node.addChild(terminalNode);
                if (tokensIndex != tokens.size() - 1) {
                    this.tokensIndex++;
                    currentToken = this.tokens.get(this.tokensIndex);
                }
            }
            else{
                error = true;
                printError(terminal, currentToken);
                //System.out.println("Erreur syntaxique dans l'analyse de terminal : terminal attendu : " + terminal + " != " + currentToken.getValue() + " = current token" + " ligne " + currentToken.getLineNumber());
            }
        }
    }

    // non-terminals procedures

    //axiom
    void fichier(){
        if(!error){
            if(currentToken.getValue().equals("with")) {
                Node nodeFichier = new Node("Fichier");
                this.syntaxTree = new SyntaxTree(nodeFichier);
                terminalAnalyse("with", nodeFichier);
                terminalAnalyse("Ada", nodeFichier);
                terminalAnalyse(".", nodeFichier);
                terminalAnalyse("Text_IO", nodeFichier);
                terminalAnalyse(";", nodeFichier);
                terminalAnalyse("use", nodeFichier);
                terminalAnalyse("Ada", nodeFichier);
                terminalAnalyse(".", nodeFichier);
                terminalAnalyse("Text_IO", nodeFichier);
                terminalAnalyse(";", nodeFichier);
                terminalAnalyse("procedure", nodeFichier);
                ident(nodeFichier);
                terminalAnalyse("is", nodeFichier);
                declstar(nodeFichier);
                terminalAnalyse("begin", nodeFichier);
                instr(nodeFichier);
                instrstar(nodeFichier);
                terminalAnalyse("end", nodeFichier);
                identinterro(nodeFichier);
                terminalAnalyse(";", nodeFichier);
                if (this.tokensIndex != this.tokens.size() - 1) {
                    error = true;
                    printError("null", currentToken);
                    //System.out.println("Erreur syntaxique : terminal attendu : null");
                }
            }
            else error = true;
            if (error) {
                printError("with", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : with" + " != " + currentToken.getValue() + " = current token" + " ligne " + currentToken.getLineNumber());
            }
        }
    }


    void declstar(Node node) {
        if (!error) {
            if (currentToken.getValue().equals("begin")) {
                return;
            }
            else if (currentToken.getValue().equals("procedure")
                    || currentToken.getValue().equals("type")
                    || currentToken.getValue().equals("function")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeDeclstar = new Node("nodeDeclstar");
                node.addChild(nodeDeclstar);
                decl(nodeDeclstar);
                declstar(nodeDeclstar);
            }
            else error = true;
            if (error) {
                printError("begin procedure type function", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : begin ou procedure ou type ou function" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }


    void instrstar(Node node) {
        if(!error){
            if(currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("else")
                    || currentToken.getValue().equals("end")
                    || currentToken.getValue().equals("elsif")){ return; }
            else if(currentToken.getValue().equals("begin")
                    || currentToken.getValue().equals("return")
                    || currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("moins")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getValue().equals("if")
                    || currentToken.getValue().equals("for")
                    || currentToken.getValue().equals("while")
                    || currentToken.getType() == TokenType.IDENTIFIER){
                Node nodeInstrstar = new Node("nodeInstrstar");
                node.addChild(nodeInstrstar);
                instr(nodeInstrstar);
                instrstar(nodeInstrstar);
            }
            else error = true;
            if (error) {
                printError("begin return ( - number character true false null new character if for while ident ) else elsif", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : begin ou return ou ( ou moins ou number ou character ou true ou false ou null ou new ou character ou if ou for ou while ou ident ou ) ou else ou elsif" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void identinterro(Node node) {
        if(!error){
            if(currentToken.getValue().equals(";")){return; }
            else if(currentToken.getType() == TokenType.IDENTIFIER){
                Node nodeIdentinterro = new Node("nodeIdentinterro");
                node.addChild(nodeIdentinterro);
                ident(nodeIdentinterro);
            }
            else error = true;
            if (error) {
                printError("; ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou ident" + " != " + currentToken.getValue() + " = current token" + " Type : " + currentToken.getType());
            }
        }
    }

    void identstar_virgule(Node node) {
        if(!error){
            if(currentToken.getValue().equals(":")){return; }
            else if (currentToken.getValue().equals(",")){
                Node nodeIdentstar = new Node("nodeIdentstar");
                node.addChild(nodeIdentstar);
                terminalAnalyse(",", nodeIdentstar);
                ident(nodeIdentstar);
                identstar_virgule(nodeIdentstar);
            }
            else error = true;
            if (error) {
                printError(", ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : , ou ident" + " != " + currentToken.getValue() + " = current token" );
            }
        }
    }

    void decl(Node node) {
        if(!error){
            if (currentToken.getValue().equals("type")){
                Node nodeDecl = new Node("nodeDecl");
                node.addChild(nodeDecl);
                terminalAnalyse("type", nodeDecl);
                ident(nodeDecl);
                decl2(nodeDecl);
            }
            else if(currentToken.getValue().equals("procedure")){
                Node nodeDecl = new Node("nodeDecl");
                node.addChild(nodeDecl);
                terminalAnalyse("procedure", nodeDecl);
                ident(nodeDecl);
                paramsinterro(nodeDecl);
                terminalAnalyse("is", nodeDecl);
                declstar(nodeDecl);
                terminalAnalyse("begin", nodeDecl);
                instr(nodeDecl);
                instrstar(nodeDecl);
                terminalAnalyse("end", nodeDecl);
                identinterro(nodeDecl);
                terminalAnalyse(";", nodeDecl);
            } else if (currentToken.getValue().equals("function")) {
                Node nodeDecl = new Node("nodeDecl");
                node.addChild(nodeDecl);
                terminalAnalyse("function", nodeDecl);
                ident(nodeDecl);
                paramsinterro(nodeDecl);
                terminalAnalyse("return", nodeDecl);
                type(nodeDecl);
                terminalAnalyse("is", nodeDecl);
                declstar(nodeDecl);
                terminalAnalyse("begin", nodeDecl);
                instr(nodeDecl);
                instrstar(nodeDecl);
                terminalAnalyse("end", nodeDecl);
                identinterro(nodeDecl);
                terminalAnalyse(";", nodeDecl);
            } else if (currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeDecl = new Node("nodeDecl");
                node.addChild(nodeDecl);
                ident(nodeDecl);
                identstar_virgule(nodeDecl);
                terminalAnalyse(":", nodeDecl);
                type(nodeDecl);
                exprinterro(nodeDecl);
                terminalAnalyse(";", nodeDecl);
            }
            else error = true;
            if (error) {
                printError("type procedure function ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : type ou procedure ou function ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void decl2(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")){
                Node nodeDecl2 = new Node("nodeDecl2");
                node.addChild(nodeDecl2);
                terminalAnalyse(";", nodeDecl2);
            } else if (currentToken.getValue().equals("is")) {
                Node nodeDecl2 = new Node("nodeDecl2");
                node.addChild(nodeDecl2);
                terminalAnalyse("is", nodeDecl2);
                decl3(nodeDecl2);
            }
            else error = true;
            if (error) {
                printError("; is", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou is" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void decl3(Node node) {
        if(!error){
            if (currentToken.getValue().equals("access")){
                Node nodeDecl3 = new Node("nodeDecl3");
                node.addChild(nodeDecl3);
                terminalAnalyse("access", nodeDecl3);
                ident(nodeDecl3);
                terminalAnalyse(";", nodeDecl3);
            } else if (currentToken.getValue().equals("record")) {
                Node nodeDecl3 = new Node("nodeDecl3");
                node.addChild(nodeDecl3);
                terminalAnalyse("record", nodeDecl3);
                champs(nodeDecl3);
                champstar(nodeDecl3);
                terminalAnalyse("end", nodeDecl3);
                terminalAnalyse("record", nodeDecl3);
                terminalAnalyse(";", nodeDecl3);
            }
            else error = true;
            if (error) {
                printError("access record", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : access ou record" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void champs(Node node) {
        if(!error){
            if(currentToken.getType() == TokenType.IDENTIFIER){
                Node nodeChamps = new Node("nodeChamps");
                node.addChild(nodeChamps);
                ident(nodeChamps);
                identstar_virgule(nodeChamps);
                terminalAnalyse(":", nodeChamps);
                type(nodeChamps);
                terminalAnalyse(";", nodeChamps);
            }
            else error = true;
            if (error) {
                printError("ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void exprinterro(Node node) {
        if(!error){
            if(currentToken.getValue().equals(";")) return;
            else if (currentToken.getValue().equals(":=")) {
                Node nodeExprinterro = new Node("nodeExprinterro");
                node.addChild(nodeExprinterro);
                terminalAnalyse(":=", nodeExprinterro);
                expr(nodeExprinterro);
            }
            else error = true;
            if (error) {
                printError("; := :", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu := ; ou :" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void exprinterro2(Node node){
        if(!error){
            if (currentToken.getValue().equals(";")) return;
            else if(currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("moins")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER){
                expr(node);
            }
            else error = true;
        }
    }



    void champstar(Node node) {
        if(!error){
            if(currentToken.getValue().equals("end"))return;
            else if(currentToken.getType() == TokenType.IDENTIFIER){
                Node nodeChampstar = new Node("nodeChampstar");
                node.addChild(nodeChampstar);
                champs(nodeChampstar);
                champstar(nodeChampstar);
            }
            else error = true;
            if (error) {
                printError("end ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : end ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void paramsinterro(Node node) {
        if(!error){
            if (currentToken.getValue().equals("is") || currentToken.getValue().equals("return"))return;
            else if (currentToken.getValue().equals("(")) {
                Node nodeParamsinterro = new Node("nodeParamsinterro");
                node.addChild(nodeParamsinterro);
                params(nodeParamsinterro);
            }
            else error = true;
            if (error) {
                printError("is return (", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : is ou return ou (" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void type(Node node) {
        if(!error){
            if (currentToken.getType() == TokenType.IDENTIFIER ){
                Node nodeType = new Node("nodeType");
                node.addChild(nodeType);
                ident(nodeType);
            }
            else if (currentToken.getValue().equals("access")){
                Node nodeType = new Node("nodeType");
                node.addChild(nodeType);
                terminalAnalyse("access", nodeType);
                ident(nodeType);
                terminalAnalyse(";", nodeType);
            }
            else error = true;
            if (error) {
                printError("ident access", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ident ou access" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void params(Node node) {
        if(!error){
            if(currentToken.getValue().equals("(")){
                Node nodeParams = new Node("nodeParams");
                node.addChild(nodeParams);
                terminalAnalyse("(", nodeParams);
                param(nodeParams);
                paramstar_virgule(nodeParams);
                terminalAnalyse(")", nodeParams);
            }
            else error = true;
            if (error) {
                printError("(", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : (" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }


    void paramstar_virgule(Node node) {
        if(!error){
            if(currentToken.getValue().equals(")"))return;
            else if (currentToken.getValue().equals(";")) {
                Node nodeParamstar = new Node("nodeParamstar");
                node.addChild(nodeParamstar);
                terminalAnalyse(";", nodeParamstar);
                param(nodeParamstar);
                paramstar_virgule(nodeParamstar);
            }
            else error = true;
            if (error) {
                printError(") ;", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou )" + " != " + currentToken.getValue() + " = current token" + " Type : " + currentToken.getType());
            }
        }
    }

    void param(Node node) {
        if(!error){
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeParam = new Node("nodeParam");
                node.addChild(nodeParam);
                ident(nodeParam);
                identstar_virgule(nodeParam);
                terminalAnalyse(":", nodeParam);
                modeinterro(nodeParam);
                type(nodeParam);
            }
            else error = true;
            if (error) {
                printError("ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void mode(Node node) {
        if(!error){
            if (currentToken.getValue().equals("in")){
                Node nodeMode = new Node("nodeMode");
                node.addChild(nodeMode);
                if (tokensIndex != tokens.size() - 1){
                    if (this.tokens.get(this.tokensIndex + 1).getValue().equals("out")) {
                        terminalAnalyse("in", nodeMode);
                        terminalAnalyse("out", nodeMode);
                        terminalAnalyse(";", nodeMode);
                    }
                    else {
                        terminalAnalyse("in", nodeMode);
                    }
                }
                else {
                    terminalAnalyse("in", nodeMode);
                }
            }
            else error = true;
            if (error) {
                printError("in", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : in" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void modeinterro(Node node) {
        if(!error){
            if(currentToken.getValue().equals("in")) {
                Node nodeModeinterro = new Node("nodeModeinterro");
                node.addChild(nodeModeinterro);
                mode(nodeModeinterro);
            } else if (currentToken.getType() == TokenType.IDENTIFIER || currentToken.getValue().equals("access")) return;
            else error = true;
            if (error) {
                printError("in ident access", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : in ou ident ou access" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void expr(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeExpr = new Node("nodeExpr");
                node.addChild(nodeExpr);
                terme_1(nodeExpr);
                priorite_or(nodeExpr);
            } else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_or(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals("or")) {
                Node nodePrioriteOr = new Node("nodePrioriteOr");
                node.addChild(nodePrioriteOr);
                terminalAnalyse("or", nodePrioriteOr);
                priorite_or_2(nodePrioriteOr);
            }
            else error = true;
            if (error) {
                printError("; , ) then . loop or", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou ) ou or ou then ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_or_2(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodePrioriteOr2 = new Node("nodePrioriteOr2");
                node.addChild(nodePrioriteOr2);
                terme_1(nodePrioriteOr2);
                priorite_or(nodePrioriteOr2);
            }
            else if (currentToken.getValue().equals("else")) {
                Node nodePrioriteOr2 = new Node("nodePrioriteOr2");
                node.addChild(nodePrioriteOr2);
                terminalAnalyse("else", nodePrioriteOr2);
                terme_1(nodePrioriteOr2);
                priorite_or(nodePrioriteOr2);
            } else error = true;
            if (error) {
                printError("( - number character true false null new character ident else", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident ou else" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_1(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme1 = new Node("nodeTerme1");
                node.addChild(nodeTerme1);
                terme_2(nodeTerme1);
                priorite_and(nodeTerme1);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_and(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals("and")) {
                Node nodePrioriteAnd = new Node("nodePrioriteAnd");
                node.addChild(nodePrioriteAnd);
                terminalAnalyse("and", nodePrioriteAnd);
                priorite_and_2(nodePrioriteAnd);
            }
            else error = true;
            if (error) {
                printError("; , ) or then . loop", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou ) ou or ou then ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_and_2(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodePrioriteAnd2 = new Node("nodePrioriteAnd2");
                node.addChild(nodePrioriteAnd2);
                terme_2(nodePrioriteAnd2);
                priorite_and(nodePrioriteAnd2);
            } else if (currentToken.getValue().equals("then")) {
                Node nodePrioriteAnd2 = new Node("nodePrioriteAnd2");
                node.addChild(nodePrioriteAnd2);
                terminalAnalyse("then", nodePrioriteAnd2);
                terme_2(nodePrioriteAnd2);
                priorite_and(nodePrioriteAnd2);
            } else error = true;
            if (error) {
                printError("( - number character true false null new character ident then", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident ou then" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_2(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme2 = new Node("nodeTerme2");
                node.addChild(nodeTerme2);
                terme_3(nodeTerme2);
                priorite_not(nodeTerme2);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_not(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("and")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals("not")) {
                Node nodePrioriteNot = new Node("nodePrioriteNot");
                node.addChild(nodePrioriteNot);
                terminalAnalyse("not", nodePrioriteNot);
                terme_3(nodePrioriteNot);
                priorite_not(nodePrioriteNot);
            }
            else error = true;
            if (error) {
                printError("; , ) or and then . loop", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou ) ou or ou and ou then ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_3(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme3 = new Node("nodeTerme3");
                node.addChild(nodeTerme3);
                terme_4(nodeTerme3);
                priorite_egal(nodeTerme3);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_egal(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("and")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("not")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals("=")) {
                Node nodePrioriteEgal = new Node("nodePrioriteEgal");
                node.addChild(nodePrioriteEgal);
                terminalAnalyse("=", nodePrioriteEgal);
                terme_4(nodePrioriteEgal);
                priorite_egal(nodePrioriteEgal);
            }
            else if (currentToken.getValue().equals("/=")) {
                Node nodePrioriteEgal = new Node("nodePrioriteEgal");
                node.addChild(nodePrioriteEgal);
                terminalAnalyse("/=", nodePrioriteEgal);
                terme_4(nodePrioriteEgal);
                priorite_egal(nodePrioriteEgal);
            }
            else error = true;
            if (error) {
                printError("; , ) or and then not . loop", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou ) ou or ou and ou then ou not ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_4(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme4 = new Node("nodeTerme4");
                node.addChild(nodeTerme4);
                terme_5(nodeTerme4);
                priorite_inferieur(nodeTerme4);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_inferieur(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals("=")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("and")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("not")
                    || currentToken.getValue().equals("/=")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals("<")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse("<", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else if (currentToken.getValue().equals("<=")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse("<=", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else if (currentToken.getValue().equals(">")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse(">", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else if (currentToken.getValue().equals(">=")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse(">=", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else error = true;
            if (error) {
                printError("; , = ) or and then not /= . loop", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_5(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme5 = new Node("nodeTerme5");
                node.addChild(nodeTerme5);
                terme_6(nodeTerme5);
                priorite_addition(nodeTerme5);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_addition(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals("=")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("and")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("not")
                    || currentToken.getValue().equals("/=")
                    || currentToken.getValue().equals("<")
                    || currentToken.getValue().equals("<=")
                    || currentToken.getValue().equals(">")
                    || currentToken.getValue().equals(">=")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals("+")) {
                Node nodePrioriteAddition = new Node("nodePrioriteAddition");
                node.addChild(nodePrioriteAddition);
                terminalAnalyse("+", nodePrioriteAddition);
                terme_6(nodePrioriteAddition);
                priorite_addition(nodePrioriteAddition);
            }
            else if (currentToken.getValue().equals("-")) {
                Node nodePrioriteAddition = new Node("nodePrioriteAddition");
                node.addChild(nodePrioriteAddition);
                terminalAnalyse("-", nodePrioriteAddition);
                terme_6(nodePrioriteAddition);
                priorite_addition(nodePrioriteAddition);
            }
            else error = true;
            if (error) {
                printError("; , = ) or and then not /= < <= > >= .. loop", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_6(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme6 = new Node("nodeTerme6");
                node.addChild(nodeTerme6);
                terme_7(nodeTerme6);
                priorite_multiplication(nodeTerme6);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_multiplication(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals("=")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("and")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("not")
                    || currentToken.getValue().equals("/=")
                    || currentToken.getValue().equals("<")
                    || currentToken.getValue().equals("<=")
                    || currentToken.getValue().equals(">")
                    || currentToken.getValue().equals(">=")
                    || currentToken.getValue().equals("+")
                    || currentToken.getValue().equals("-")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals("*")) {
                Node nodePrioriteMultiplication = new Node("nodePrioriteMultiplication");
                node.addChild(nodePrioriteMultiplication);
                terminalAnalyse("*", nodePrioriteMultiplication);
                terme_7(nodePrioriteMultiplication);
                priorite_multiplication(nodePrioriteMultiplication);
            }
            else if (currentToken.getValue().equals("/")) {
                Node nodePrioriteMultiplication = new Node("nodePrioriteMultiplication");
                node.addChild(nodePrioriteMultiplication);
                terminalAnalyse("/", nodePrioriteMultiplication);
                terme_7(nodePrioriteMultiplication);
                priorite_multiplication(nodePrioriteMultiplication);
            }
            else if (currentToken.getValue().equals("rem")) {
                Node nodePrioriteMultiplication = new Node("nodePrioriteMultiplication");
                node.addChild(nodePrioriteMultiplication);
                terminalAnalyse("rem", nodePrioriteMultiplication);
                terme_7(nodePrioriteMultiplication);
                priorite_multiplication(nodePrioriteMultiplication);
            }
            else error = true;
            if (error) {
                printError("; , = ) or and then not /= < <= > >= + - .. loop", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou + ou - ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_7(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme7 = new Node("nodeTerme7");
                node.addChild(nodeTerme7);
                facteur(nodeTerme7);
                priorite_point(nodeTerme7);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_point(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals("=")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("and")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("not")
                    || currentToken.getValue().equals("/=")
                    || currentToken.getValue().equals("<")
                    || currentToken.getValue().equals("<=")
                    || currentToken.getValue().equals(">")
                    || currentToken.getValue().equals(">=")
                    || currentToken.getValue().equals("+")
                    || currentToken.getValue().equals("-")
                    || currentToken.getValue().equals("*")
                    || currentToken.getValue().equals("/")
                    || currentToken.getValue().equals("rem")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals(":=")
                    || currentToken.getValue().equals("loop")) return;
            else if (currentToken.getValue().equals(".")) {
                Node nodePrioritePoint = new Node("nodePrioritePoint");
                node.addChild(nodePrioritePoint);
                terminalAnalyse(".", nodePrioritePoint);
                ident(nodePrioritePoint);
                priorite_point(nodePrioritePoint);
            }
            else {
                if (this.indicateur_acces == true){
                    this.indicateur_acces = false;
                    Node nodePrioritePoint = new Node("nodePrioritePoint");
                    node.addChild(nodePrioritePoint);
                    terminalAnalyse(".", nodePrioritePoint);
                    ident(nodePrioritePoint);
                }
                else error = true;
                if (error) {
                    printError("; , = ) or and then not /= < <= > >= + - * / rem .. := loop", currentToken);
                    //System.out.println("ici Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou + ou - ou * ou / ou rem ou .. ou loop" + " != " + currentToken.getValue() + " = current token");
                }
            }
        }
    }

    void facteur(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("(", nodeFacteur);
                expr(nodeFacteur);
                terminalAnalyse(")", nodeFacteur);
            }
            else if (currentToken.getType() == TokenType.NUMBER) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse(currentToken.getValue(), nodeFacteur);
            }
            else if (currentToken.getType() == TokenType.CHARACTER) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse(currentToken.getValue(), nodeFacteur);
            }
            else if (currentToken.getValue().equals("true")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("true", nodeFacteur);
            }
            else if (currentToken.getValue().equals("false")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("false", nodeFacteur);
            }
            else if (currentToken.getValue().equals("null")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("null", nodeFacteur);
            }
            else if (currentToken.getValue().equals("new")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("new", nodeFacteur);
                ident(nodeFacteur);
            }
            else if (currentToken.getValue().equals("character")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("character", nodeFacteur);
                terminalAnalyse("'", nodeFacteur);
                terminalAnalyse("val", nodeFacteur);
                terminalAnalyse("(", nodeFacteur);
                expr(nodeFacteur);
                terminalAnalyse(")", nodeFacteur);
            }
            else if (currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                ident(nodeFacteur);
                facteur2(nodeFacteur);
            }
            else error = true;
            if (error) {
                printError("( number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void facteur2(Node node){
        if(!error){
            if (currentToken.getValue().equals("(")) {
                terminalAnalyse("(", node);
                expr(node);
                exprstar_virgule(node);
                terminalAnalyse(")", node);
                if (currentToken.getValue().equals(";")) {
                    // terminalAnalyse(";", node);
                    this.indicateur_acces = false;
                }
            }
            else if (currentToken.getValue().equals(";")
                    || currentToken.getValue().equals(",")
                    || currentToken.getValue().equals("=")
                    || currentToken.getValue().equals(")")
                    || currentToken.getValue().equals("or")
                    || currentToken.getValue().equals("and")
                    || currentToken.getValue().equals("then")
                    || currentToken.getValue().equals("not")
                    || currentToken.getValue().equals("/=")
                    || currentToken.getValue().equals("<")
                    || currentToken.getValue().equals("<=")
                    || currentToken.getValue().equals(">")
                    || currentToken.getValue().equals(">=")
                    || currentToken.getValue().equals("+")
                    || currentToken.getValue().equals("-")
                    || currentToken.getValue().equals("*")
                    || currentToken.getValue().equals("/")
                    || currentToken.getValue().equals("rem")
                    || currentToken.getValue().equals(".")
                    || currentToken.getValue().equals(":=")
                    || currentToken.getValue().equals("..")
                    || currentToken.getValue().equals("loop")) return;
        }
        else error = true;
        if (error) {
            printError("( ; , = ) or and then not /= < <= > >= + - * / rem . := .. loop", currentToken);
            // System.out.println("Erreur syntaxique : terminal attendu : ( ou ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou + ou - ou * ou / ou rem ou . ou := ou .. ou loop" + " != " + currentToken.getValue() + " = current token");
        }
    }

    void acces(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER){
                expr(node);
            } else error = true;
            if (error) {
                printError("( - number character true false null new character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    //On met de coté le cas où on a un ident suivi d'un point pour l'instant
    void instr(Node node) {
        if(!error) {
            if (currentToken.getValue().equals("begin")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                terminalAnalyse("begin", nodeIntr1);
                instr(nodeIntr1);
                instrstar(nodeIntr1);
                terminalAnalyse("end", nodeIntr1);
                terminalAnalyse(";", nodeIntr1);
            }
            else if (currentToken.getValue().equals("return")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                terminalAnalyse("return", nodeIntr1);
                exprinterro2(nodeIntr1);
                terminalAnalyse(";", nodeIntr1);
            }
            else if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                this.indicateur_acces = true;
                acces(nodeIntr1);
                instr2_prime(nodeIntr1);
            }
            else if (currentToken.getValue().equals("if")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                terminalAnalyse("if", nodeIntr1);
                expr(nodeIntr1);
                terminalAnalyse("then", nodeIntr1);
                instr(nodeIntr1);
                instrstar(nodeIntr1);
                elsifstar(nodeIntr1);
                terminalAnalyse("end", nodeIntr1);
                terminalAnalyse("if", nodeIntr1);
                terminalAnalyse(";", nodeIntr1);
            }
            else if (currentToken.getValue().equals("while")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                terminalAnalyse("while", nodeIntr1);
                expr(nodeIntr1);
                terminalAnalyse("loop", nodeIntr1);
                instr(nodeIntr1);
                instrstar(nodeIntr1);
                terminalAnalyse("end", nodeIntr1);
                terminalAnalyse("loop", nodeIntr1);
                terminalAnalyse(";", nodeIntr1);
            }
            else if (currentToken.getValue().equals("for")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                terminalAnalyse("for", nodeIntr1);
                ident(nodeIntr1);
                terminalAnalyse("in", nodeIntr1);
                reverseinterro(nodeIntr1);
                expr(nodeIntr1);
                terminalAnalyse("..", nodeIntr1);
                expr(nodeIntr1);
                terminalAnalyse("loop", nodeIntr1);
                instr(nodeIntr1);
                instrstar(nodeIntr1);
                terminalAnalyse("end", nodeIntr1);
                terminalAnalyse("loop", nodeIntr1);
                terminalAnalyse(";", nodeIntr1);
            }
            else if (currentToken.getType() == TokenType.IDENTIFIER){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                //Si on voit := ou un ; ou une ( après le ident, on appelle instr2
                if (this.tokens.get(this.tokensIndex + 1).getValue().equals(":=") || this.tokens.get(this.tokensIndex + 1).getValue().equals(";") || this.tokens.get(this.tokensIndex + 1).getValue().equals("(")){
                    ident(nodeIntr1);
                    instr2(nodeIntr1);
                }
                else {
                    this.indicateur_acces = true;
                    acces(nodeIntr1);
                    instr2_prime(nodeIntr1);
                }
            }
            else error = true;
            if (error) {
                printError("begin return ( - number character true false null new character if while for", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : begin ou return ou ( ou - ou number ou character ou true ou false ou null ou new ou character ou if ou while ou for" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void instr2(Node node) {
        if(!error){
            if (currentToken.getValue().equals(";")) {
                Node nodeIntr2 = new Node("nodeIntr2");
                node.addChild(nodeIntr2);
                terminalAnalyse(";", nodeIntr2);
            }
            else if (currentToken.getValue().equals("(")) {
                Node nodeIntr2 = new Node("nodeIntr2");
                node.addChild(nodeIntr2);
                terminalAnalyse("(", nodeIntr2);
                expr(nodeIntr2);
                exprstar_virgule(nodeIntr2);
                terminalAnalyse(")", nodeIntr2);
                terminalAnalyse(";", nodeIntr2);
            }
            else if (currentToken.getValue().equals(":=")) {
                Node nodeIntr2 = new Node("nodeIntr2");
                node.addChild(nodeIntr2);
                instr2_prime(nodeIntr2);
            }
            else error = true;
            if (error) {
                printError("; ( :=", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou ( ou :=" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void instr2_prime(Node node) {
        if(!error){
            if (currentToken.getValue().equals(":=")) {
                Node nodeIntr2prime = new Node("nodeIntr2prime");
                node.addChild(nodeIntr2prime);
                terminalAnalyse(":=", nodeIntr2prime);
                expr(nodeIntr2prime);
                terminalAnalyse(";", nodeIntr2prime);
            }
            else error = true;
            if (error) {
                printError(":=", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : :=" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void reverseinterro(Node node) {
        if(!error){
            if (currentToken.getValue().equals("(")
                    || currentToken.getValue().equals("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equals("true")
                    || currentToken.getValue().equals("false")
                    || currentToken.getValue().equals("null")
                    || currentToken.getValue().equals("new")
                    || currentToken.getValue().equals("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) return;
            else if (currentToken.getValue().equals("reverse")) {
                Node nodeReverseinterro = new Node("nodeReverseinterro");
                node.addChild(nodeReverseinterro);
                terminalAnalyse("reverse", nodeReverseinterro);
            } else error = true;
            if (error) {
                printError("( - number character true false null new character ident reverse", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident ou reverse" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void elsifstar(Node node) {
        if(!error){
            if (currentToken.getValue().equals("end")) return;
            else if (currentToken.getValue().equals("else")) {
                Node nodeElsifstar = new Node("nodeElsifstar");
                node.addChild(nodeElsifstar);
                terminalAnalyse("else", nodeElsifstar);
                instr(nodeElsifstar);
                instrstar(node);
            }
            else if (currentToken.getValue().equals("elsif")) {
                Node nodeElsifstar = new Node("nodeElsifstar");
                node.addChild(nodeElsifstar);
                terminalAnalyse("elsif", nodeElsifstar);
                expr(nodeElsifstar);
                terminalAnalyse("then", nodeElsifstar);
                instr(nodeElsifstar);
                instrstar(node);
                elsifstar(nodeElsifstar);
            }
            else error = true;
            if (error) {
                printError("end else elsif", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : and ou else ou elsif" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void exprstar_virgule(Node node) {
        if(!error){
            if (currentToken.getValue().equals(",")) {
                Node nodeExprstarVirgule = new Node("nodeExprstarVirgule");
                node.addChild(nodeExprstarVirgule);
                terminalAnalyse(",", nodeExprstarVirgule);
                expr(nodeExprstarVirgule);
                exprstar_virgule(nodeExprstarVirgule);
            }
            else if (currentToken.getValue().equals(")"))return;
            else error = true;
            if (error) {
                printError(", )", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : , ou )" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void ident(Node node) {
        if(!error) {
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeIdent = new Node("nodeIdent");
                node.addChild(nodeIdent);
                terminalAnalyse(currentToken.getValue(), nodeIdent);
            } else error = true;
            if (error) {
                printError("ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }
}
