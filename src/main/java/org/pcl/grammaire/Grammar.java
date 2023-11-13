package org.pcl.grammaire;

import org.pcl.Token;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class Grammar {

    public Boolean error = false;
    public Token currentToken = null;
    public SyntaxTree syntaxTree = null;
    public int tokensIndex = 0;
    ArrayList<Token> tokens;

    public Grammar(ArrayList<Token> tokens){
        this.tokens = tokens;
    }
    public SyntaxTree getSyntaxTree() {
        this.currentToken = tokens.get(tokensIndex);
        fichier();
        if(error){
            System.out.println("erreur ici");
        }
        else {
            if(currentToken == this.tokens.get(this.tokens.size()-1)){
                System.out.println("ok");
            }
            else {
                System.out.println("erreur là");
            }
        }
        return this.syntaxTree;
    }


    // terminals procedures

    public void terminalAnalyse(String terminal, Node node){
        if(!this.error){
            if(currentToken.getValue().equals(terminal)){
                Node terminalNode = new Node(currentToken);
                node.addChild(terminalNode);
                if (tokensIndex != tokens.size() - 1){
                    this.tokensIndex++;
                    currentToken = this.tokens.get(this.tokensIndex);
                }
            }
            else{
                error = true;
            }
        }
    }

    // non-terminals procedures

    //axiom
    void fichier(){
        if(!error){
            Node nodeFichier = new Node("nodeFichier");
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
        }
    }

    void ident(Node node) {
        if(!error){
            if(currentToken.getValue().equals("idf")) {
                Node nodeIdent = new Node("nodeB");
                node.addChild(nodeIdent);
                terminalAnalyse("idf", nodeIdent);
            }
        }
    }

    /*
       premiers de declstar :
       suivants de declstar : begin
       DECLSTAR -> DECL DECLSTAR .
       DECLSTAR -> .
     */
    void declstar(Node node) {
        if(!error){
            if(currentToken.getValue().equals("begin")){ return; }
            else {
                Node nodeDeclstar = new Node("nodeDeclstar");
                node.addChild(nodeDeclstar);
                decl(nodeDeclstar);
                declstar(nodeDeclstar);
            }
        }
    }


    void instrstar(Node node) {
        if(!error){
            if(currentToken.getValue().equals("(") || currentToken.getValue().equals(")") || currentToken.getValue().equals("end")){ return; }
            else {
                Node nodeInstrstar = new Node("nodeInstrstar");
                node.addChild(nodeInstrstar);
                instr(nodeInstrstar);
                instrstar(nodeInstrstar);
            }
        }
    }
    void identinterro(Node node) {
        if(!error){
            if(currentToken.getValue().equals("(") || currentToken.getValue().equals(";")
                    || currentToken.getValue().equals("is") || currentToken.getValue().equals("return")){return; }
            else {
                Node nodeIdentinterro = new Node("nodeIdentinterro");
                node.addChild(nodeIdentinterro);
                ident(nodeIdentinterro);
            }
        }
    }
    void identstar(Node node) {
        if(!error){

        }
    }

    void decl(Node node) {
        if(!error){

        }
    }
    void decl2(Node node) {
        if(!error){

        }
    }
    void decl3(Node node) {
        if(!error){

        }
    }
    void champs(Node node) {
        if(!error){

        }
    }
    void exprinterro(Node node) {
        if(!error){

        }
    }
    void champstar(Node node) {
        if(!error){

        }
    }
    void paramsinterro(Node node) {
        if(!error){

        }
    }
    void type(Node node) {
        if(!error){

        }
    }
    void params(Node node) {
        if(!error){

        }
    }
    void paramstar(Node node) {
        if(!error){

        }
    }
    void param(Node node) {
        if(!error){

        }
    }
    void mode(Node node) {
        if(!error){

        }
    }
    void modeinterro(Node node) {
        if(!error){

        }
    }
    void expr(Node node) {
        if(!error){

        }
    }
    void expr2(Node node) {
        if(!error){

        }
    }
    void expr_prime(Node node) {
        if(!error){

        }
    }
    void terme(Node node) {
        if(!error){

        }
    }
    void terme_prime(Node node) {
        if(!error){

        }
    }
    void facteur(Node node) {
        if(!error){

        }
    }
    void instr(Node node) {
        if(!error){

        }
    }
    void instr2(Node node) {
        if(!error){

        }
    }
    void reverseinterro(Node node) {
        if(!error){

        }
    }
    void elsifstar(Node node) {
        if(!error){

        }
    }
    void elsifstar2(Node node) {
        if(!error){

        }
    }
    void exprstar(Node node) {
        if(!error){

        }
    }
    void operateur_comparaison(Node node) {
        if(!error){

        }
    }
    void operateur_addition(Node node) {
        if(!error){

        }
    }
    void operateur_multiplication(Node node) {
        if(!error){

        }
    }
    void operateur2(Node node) {
        if(!error){

        }
    }
}
