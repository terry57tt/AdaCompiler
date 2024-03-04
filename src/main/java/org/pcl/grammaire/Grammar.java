package org.pcl.grammaire;

import org.pcl.ColorAnsiCode;
import org.pcl.Token;
import org.pcl.ig.PCLWindows;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Grammar {

    public Boolean error = false;

    private int numberErrors = 0;

    public Boolean firstTime = true;

    private int indexLastError = -1;


    public Token currentToken = null;
    public SyntaxTree syntaxTree = null;
    public SyntaxTree ast = null;
    private Boolean indicateur_acces = false;
    public int tokensIndex = 0;
    private String file;
    ArrayList<Token> tokens;

    public Grammar(ArrayList<Token> tokens, String file){
        this.tokens = tokens;
        this.file = file;
    }

    public Grammar(ArrayList<Token> tokens){
        this(tokens, "test");
    }

    private void printError(String expectedMessage, Token currentToken){
        if (!firstTime) return;

        firstTime = false;
        boolean multiples = expectedMessage.contains(" ");
        numberErrors++;
        System.out.println( file + ":" + currentToken.getLineNumber() + ":" + ColorAnsiCode.ANSI_RED + " error:" + ColorAnsiCode.ANSI_RESET +
                " expected " + (multiples ? " one of them": "") + " \"" + expectedMessage + "\" got [value=" + currentToken.getValue() + " type=" + currentToken.getType() + "]");
        if (expectedMessage.equals(";"))
            System.out.println(getLineToken(tokens.get(tokensIndex - 1).getLineNumber(), currentToken) + "\n");
        else
            System.out.println(getLineToken(currentToken.getLineNumber(), currentToken) + "\n");
        if (tokensIndex > indexLastError + 1 || indexLastError == -1) {
            GrammarErrorUtility.ProceedAnalysis(expectedMessage, this, currentToken.getLineNumber(), file);
        }
        error = true;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public int getIndexLastError() {
        return indexLastError;
    }

    public static  Grammar createGrammarError(Grammar grammar, int decal, Token newToken, String file) {
        Grammar grammarError = new Grammar(GrammarErrorUtility.deepClone(grammar.tokens), file);

        grammarError.indexLastError = grammar.tokensIndex;
        grammarError.tokens.add(grammar.tokensIndex + decal, newToken);

        return grammarError;
    }

    private String getLineToken(long line, Token currentToken) {
        StringBuilder lineToken = new StringBuilder();
        for (Token token: tokens) {
            if (token.getLineNumber() == line) {
                if (token == currentToken) {
                    lineToken.append(ColorAnsiCode.ANSI_RED).append(token.getValue()).append(ColorAnsiCode.ANSI_RESET).append(" ");
                } else {
                    lineToken.append(token.getValue()).append(" ");
                }
            }
        }
        return lineToken.toString();
    }

    public int getNumberErrors() {
        return numberErrors;
    }

    public void setNumberErrors(int numberErrors) {
        this.numberErrors = numberErrors;
    }

    public SyntaxTree getSyntaxTree() {
        if (this.tokens.size() == 0) {
            return null;
        }
        this.currentToken = tokens.get(tokensIndex);
        fichier();
        return this.syntaxTree;
    }

    public int getTokensIndex() {
        return tokensIndex;
    }

    // terminals procedures

    public void terminalAnalyse(String terminal, Node node){
        if(!this.error){
            if(currentToken.getValue().equalsIgnoreCase(terminal)){
                Node terminalNode = new Node(currentToken);
                node.addChild(terminalNode);
                if (tokensIndex != tokens.size() - 1){
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
            if(currentToken.getValue().equalsIgnoreCase("with")) {
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
            if (currentToken.getValue().equalsIgnoreCase("begin")) {
                return;
            }
            else if (currentToken.getValue().equalsIgnoreCase("procedure")
                    || currentToken.getValue().equalsIgnoreCase("type")
                    || currentToken.getValue().equalsIgnoreCase("function")
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
            if(currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("else")
                    || currentToken.getValue().equalsIgnoreCase("end")
                    || currentToken.getValue().equalsIgnoreCase("elsif")){ return; }
            else if(currentToken.getValue().equalsIgnoreCase("begin")
                    || currentToken.getValue().equalsIgnoreCase("return")
                    || (currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getValue().equalsIgnoreCase("if")
                    || currentToken.getValue().equalsIgnoreCase("for")
                    || currentToken.getValue().equalsIgnoreCase("while")
                    || currentToken.getType() == TokenType.IDENTIFIER){
                Node nodeInstrstar = new Node("nodeInstrstar");
                node.addChild(nodeInstrstar);
                instr(nodeInstrstar);
                instrstar(nodeInstrstar);
            }
            else error = true;
            if (error) {
                printError("begin return ( - number character not true false null new character if for while ident ) else elsif end", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : begin ou return ou ( ou moins ou number ou character ou true ou false ou null ou new ou character ou if ou for ou while ou ident ou ) ou else ou elsif" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void identinterro(Node node) {
        if(!error){
            if(currentToken.getValue().equalsIgnoreCase(";")){return; }
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
            if(currentToken.getValue().equalsIgnoreCase(":")){return; }
            else if (currentToken.getValue().equalsIgnoreCase(",")){
                Node nodeIdentstar = new Node("nodeIdentstar");
                node.addChild(nodeIdentstar);
                terminalAnalyse(",", nodeIdentstar);
                ident(nodeIdentstar);
                identstar_virgule(nodeIdentstar);
            }
            else error = true;
            if (error) {
                printError(": ,", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : , ou ident" + " != " + currentToken.getValue() + " = current token" );
            }
        }
    }

    void decl(Node node) {
        if(!error){
            if (currentToken.getValue().equalsIgnoreCase("type")){
                Node nodeDecl = new Node("nodeDecl");
                node.addChild(nodeDecl);
                terminalAnalyse("type", nodeDecl);
                ident(nodeDecl);
                decl2(nodeDecl);
            }
            else if(currentToken.getValue().equalsIgnoreCase("procedure")){
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
            } else if (currentToken.getValue().equalsIgnoreCase("function")) {
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
            } else if ((currentToken.getType() == TokenType.IDENTIFIER)) {
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
            if (currentToken.getValue().equalsIgnoreCase(";")){
                Node nodeDecl2 = new Node("nodeDecl2");
                node.addChild(nodeDecl2);
                terminalAnalyse(";", nodeDecl2);
            } else if (currentToken.getValue().equalsIgnoreCase("is")) {
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
            if (currentToken.getValue().equalsIgnoreCase("access")){
                Node nodeDecl3 = new Node("nodeDecl3");
                node.addChild(nodeDecl3);
                terminalAnalyse("access", nodeDecl3);
                ident(nodeDecl3);
                terminalAnalyse(";", nodeDecl3);
            } else if (currentToken.getValue().equalsIgnoreCase("record")) {
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
            if(currentToken.getValue().equalsIgnoreCase(";")) return;
            else if (currentToken.getValue().equalsIgnoreCase(":=")) {
                Node nodeExprinterro = new Node("nodeExprinterro");
                node.addChild(nodeExprinterro);
                terminalAnalyse(":=", nodeExprinterro);
                expr(nodeExprinterro);
            }
            else error = true;
            if (error) {
                printError("; :=", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu := ; ou :" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void exprinterro2(Node node){
        if(!error){
            if (currentToken.getValue().equalsIgnoreCase(";")) return;
            else if((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getType() == TokenType.IDENTIFIER){
                expr(node);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new character ident ;", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident ou ;" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }



    void champstar(Node node) {
        if(!error){
            if(currentToken.getValue().equalsIgnoreCase("end"))return;
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
            if (currentToken.getValue().equalsIgnoreCase("is") || currentToken.getValue().equalsIgnoreCase("return"))return;
            else if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)) {
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
            if ((currentToken.getType() == TokenType.IDENTIFIER) || currentToken.getValue().equalsIgnoreCase("character")) {
                Node nodeType = new Node("nodeType");
                node.addChild(nodeType);
                if (currentToken.getValue().equalsIgnoreCase("character")) {
                    terminalAnalyse("character", nodeType);
                } else {
                    ident(nodeType);
                }
            }
            else if (currentToken.getValue().equalsIgnoreCase("access")){
                Node nodeType = new Node("nodeType");
                node.addChild(nodeType);
                terminalAnalyse("access", nodeType);
                ident(nodeType);
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
            if((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)){
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
            if(currentToken.getValue().equalsIgnoreCase(")"))return;
            else if (currentToken.getValue().equalsIgnoreCase(";")) {
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
            if (currentToken.getType() == TokenType.IDENTIFIER || currentToken.getValue().equalsIgnoreCase("character")) {
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
            if (currentToken.getValue().equalsIgnoreCase("in")){
                Node nodeMode = new Node("nodeMode");
                node.addChild(nodeMode);
                if (tokensIndex != tokens.size() - 1){
                    if (this.tokens.get(this.tokensIndex + 1).getValue().equalsIgnoreCase("out")) {
                        terminalAnalyse("in", nodeMode);
                        terminalAnalyse("out", nodeMode);
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
            if(currentToken.getValue().equalsIgnoreCase("in")) {
                Node nodeModeinterro = new Node("nodeModeinterro");
                node.addChild(nodeModeinterro);
                mode(nodeModeinterro);
            } else if (currentToken.getType() == TokenType.IDENTIFIER || currentToken.getValue().equalsIgnoreCase("access") || currentToken.getValue().equalsIgnoreCase("character")) return;
            else error = true;
            if (error) {
                printError("in ident access", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : in ou ident ou access" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void expr(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("character")
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
            if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
            else if (currentToken.getValue().equalsIgnoreCase("or")) {
                Node nodePrioriteOr = new Node("nodePrioriteOr");
                node.addChild(nodePrioriteOr);
                terminalAnalyse("or", nodePrioriteOr);
                priorite_or_2(nodePrioriteOr);
            }
            else error = true;
            if (error) {
                printError("; , ) then .. loop or :=", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou ) ou or ou then ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_or_2(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodePrioriteOr2 = new Node("nodePrioriteOr2");
                node.addChild(nodePrioriteOr2);
                terme_1(nodePrioriteOr2);
                priorite_or(nodePrioriteOr2);
            }
            else if (currentToken.getValue().equalsIgnoreCase("else")) {
                Node nodePrioriteOr2 = new Node("nodePrioriteOr2");
                node.addChild(nodePrioriteOr2);
                terminalAnalyse("else", nodePrioriteOr2);
                terme_1(nodePrioriteOr2);
                priorite_or(nodePrioriteOr2);
            } else error = true;
            if (error) {
                printError("( - number character true false null new not character ident else", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident ou else" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_1(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme1 = new Node("nodeTerme1");
                node.addChild(nodeTerme1);
                terme_2(nodeTerme1);
                priorite_and(nodeTerme1);
            }
            else error = true;
            if (error) {
                printError("( - number character true false null new not character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_and(Node node) {
        if(!error){
            if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("or")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
            else if (currentToken.getValue().equalsIgnoreCase("and")) {
                Node nodePrioriteAnd = new Node("nodePrioriteAnd");
                node.addChild(nodePrioriteAnd);
                terminalAnalyse("and", nodePrioriteAnd);
                priorite_and_2(nodePrioriteAnd);
            }
            else error = true;
            if (error) {
                printError("; , ) or then .. loop := and", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou ) ou or ou then ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void priorite_and_2(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodePrioriteAnd2 = new Node("nodePrioriteAnd2");
                node.addChild(nodePrioriteAnd2);
                terme_2(nodePrioriteAnd2);
                priorite_and(nodePrioriteAnd2);
            } else if (currentToken.getValue().equalsIgnoreCase("then")) {
                Node nodePrioriteAnd2 = new Node("nodePrioriteAnd2");
                node.addChild(nodePrioriteAnd2);
                terminalAnalyse("then", nodePrioriteAnd2);
                terme_2(nodePrioriteAnd2);
                priorite_and(nodePrioriteAnd2);
            } else error = true;
            if (error) {
                printError("( - number character true false null new not character ident then", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident ou then" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_2(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme2 = new Node("nodeTerme2");
                node.addChild(nodeTerme2);
                terme_3(nodeTerme2);
            } else if (currentToken.getValue().equalsIgnoreCase("not")) {
                Node nodeTerme2 = new Node("nodeTerme2");
                node.addChild(nodeTerme2);
                terminalAnalyse("not", nodeTerme2);
                terme_3(nodeTerme2);
            } else error = true;
            if (error) {
                printError("( - number character true false not null new not character ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_3(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
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
            if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("or")
                    || currentToken.getValue().equalsIgnoreCase("and")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
            else if (currentToken.getValue().equalsIgnoreCase("=")) {
                Node nodePrioriteEgal = new Node("nodePrioriteEgal");
                node.addChild(nodePrioriteEgal);
                terminalAnalyse("=", nodePrioriteEgal);
                terme_4(nodePrioriteEgal);
                priorite_egal(nodePrioriteEgal);
            }
            else if (currentToken.getValue().equalsIgnoreCase("/=")) {
                Node nodePrioriteEgal = new Node("nodePrioriteEgal");
                node.addChild(nodePrioriteEgal);
                terminalAnalyse("/=", nodePrioriteEgal);
                terme_4(nodePrioriteEgal);
                priorite_egal(nodePrioriteEgal);
            }
            else error = true;
            if (error) {
                printError("; , ) or and then not .. loop := = /=", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou ) ou or ou and ou then ou not ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_4(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
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
            if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase("=")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("or")
                    || currentToken.getValue().equalsIgnoreCase("and")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("/=")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
            else if (currentToken.getValue().equalsIgnoreCase("<")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse("<", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else if (currentToken.getValue().equalsIgnoreCase("<=")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse("<=", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else if (currentToken.getValue().equalsIgnoreCase(">")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse(">", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else if (currentToken.getValue().equalsIgnoreCase(">=")) {
                Node nodePrioriteInferieur = new Node("nodePrioriteInferieur");
                node.addChild(nodePrioriteInferieur);
                terminalAnalyse(">=", nodePrioriteInferieur);
                terme_5(nodePrioriteInferieur);
                priorite_inferieur(nodePrioriteInferieur);
            }
            else error = true;
            if (error) {
                printError("; , = ) or and then not /= .. loop := < <= > >=", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_5(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
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
            if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase("=")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("or")
                    || currentToken.getValue().equalsIgnoreCase("and")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("/=")
                    || currentToken.getValue().equalsIgnoreCase("<")
                    || currentToken.getValue().equalsIgnoreCase("<=")
                    || currentToken.getValue().equalsIgnoreCase(">")
                    || currentToken.getValue().equalsIgnoreCase(">=")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
            else if (currentToken.getValue().equalsIgnoreCase("+")) {
                Node nodePrioriteAddition = new Node("nodePrioriteAddition");
                node.addChild(nodePrioriteAddition);
                terminalAnalyse("+", nodePrioriteAddition);
                terme_6(nodePrioriteAddition);
                priorite_addition(nodePrioriteAddition);
            }
            else if (currentToken.getValue().equalsIgnoreCase("-")) {
                Node nodePrioriteAddition = new Node("nodePrioriteAddition");
                node.addChild(nodePrioriteAddition);
                terminalAnalyse("-", nodePrioriteAddition);
                terme_6(nodePrioriteAddition);
                priorite_addition(nodePrioriteAddition);
            }
            else error = true;
            if (error) {
                printError("; , = ) or and then not /= < <= > >= .. loop := + -", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_6(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
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
            if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase("=")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("or")
                    || currentToken.getValue().equalsIgnoreCase("and")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("/=")
                    || currentToken.getValue().equalsIgnoreCase("<")
                    || currentToken.getValue().equalsIgnoreCase("<=")
                    || currentToken.getValue().equalsIgnoreCase(">")
                    || currentToken.getValue().equalsIgnoreCase(">=")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("+")
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
            else if (currentToken.getValue().equalsIgnoreCase("*")) {
                Node nodePrioriteMultiplication = new Node("nodePrioriteMultiplication");
                node.addChild(nodePrioriteMultiplication);
                terminalAnalyse("*", nodePrioriteMultiplication);
                terme_7(nodePrioriteMultiplication);
                priorite_multiplication(nodePrioriteMultiplication);
            }
            else if (currentToken.getValue().equalsIgnoreCase("/")) {
                Node nodePrioriteMultiplication = new Node("nodePrioriteMultiplication");
                node.addChild(nodePrioriteMultiplication);
                terminalAnalyse("/", nodePrioriteMultiplication);
                terme_7(nodePrioriteMultiplication);
                priorite_multiplication(nodePrioriteMultiplication);
            }
            else if (currentToken.getValue().equalsIgnoreCase("rem")) {
                Node nodePrioriteMultiplication = new Node("nodePrioriteMultiplication");
                node.addChild(nodePrioriteMultiplication);
                terminalAnalyse("rem", nodePrioriteMultiplication);
                terme_7(nodePrioriteMultiplication);
                priorite_multiplication(nodePrioriteMultiplication);
            }
            else error = true;
            if (error) {
                printError("; , = ) or and then not /= < <= > >= + - := .. loop * / rem", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou + ou - ou . ou loop" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void terme_7(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) {
                Node nodeTerme7 = new Node("nodeTerme7");
                node.addChild(nodeTerme7);
                facteur(nodeTerme7);
                priorite_point(nodeTerme7);
            }
            else if (currentToken.getValue().equalsIgnoreCase("-")) {
                Node nodeTerme7 = new Node("nodeTerme7");
                node.addChild(nodeTerme7);
                terminalAnalyse("-", nodeTerme7);
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
            if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase("=")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("or")
                    || currentToken.getValue().equalsIgnoreCase("and")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("/=")
                    || currentToken.getValue().equalsIgnoreCase("<")
                    || currentToken.getValue().equalsIgnoreCase("<=")
                    || currentToken.getValue().equalsIgnoreCase(">")
                    || currentToken.getValue().equalsIgnoreCase(">=")
                    || currentToken.getValue().equalsIgnoreCase("+")
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getValue().equalsIgnoreCase("*")
                    || currentToken.getValue().equalsIgnoreCase("/")
                    || currentToken.getValue().equalsIgnoreCase("rem")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
            else if (currentToken.getValue().equalsIgnoreCase(".")) {
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
                    terminalAnalyse(":=", nodePrioritePoint);
                    terminalAnalyse(".", nodePrioritePoint);
                    ident(nodePrioritePoint);
                }
                else error = true;
                if (error) {
                    printError("; , = ) or and then not /= < <= > >= + - * / rem .. := loop .", currentToken);
                    //System.out.println("ici Erreur syntaxique : terminal attendu : ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou + ou - ou * ou / ou rem ou .. ou loop" + " != " + currentToken.getValue() + " = current token");
                }
            }
        }
    }

    void facteur(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)) {
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
            else if (currentToken.getValue().equalsIgnoreCase("true")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("true", nodeFacteur);
            }
            else if (currentToken.getValue().equalsIgnoreCase("false")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("false", nodeFacteur);
            }
            else if (currentToken.getValue().equalsIgnoreCase("null")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("null", nodeFacteur);
            }
            else if (currentToken.getValue().equalsIgnoreCase("new")) {
                Node nodeFacteur = new Node("nodeFacteur");
                node.addChild(nodeFacteur);
                terminalAnalyse("new", nodeFacteur);
                ident(nodeFacteur);
            }
            else if (currentToken.getValue().equalsIgnoreCase("character")) {
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
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)) {
                terminalAnalyse("(", node);
                expr(node);
                exprstar_virgule(node);
                terminalAnalyse(")", node);
                if (currentToken.getValue().equalsIgnoreCase(";")) {
                    // terminalAnalyse(";", node);
                    this.indicateur_acces = false;
                }
            }
            else if (currentToken.getValue().equalsIgnoreCase(";")
                    || currentToken.getValue().equalsIgnoreCase(",")
                    || currentToken.getValue().equalsIgnoreCase("=")
                    || currentToken.getValue().equalsIgnoreCase(")")
                    || currentToken.getValue().equalsIgnoreCase("or")
                    || currentToken.getValue().equalsIgnoreCase("and")
                    || currentToken.getValue().equalsIgnoreCase("then")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("/=")
                    || currentToken.getValue().equalsIgnoreCase("<")
                    || currentToken.getValue().equalsIgnoreCase("<=")
                    || currentToken.getValue().equalsIgnoreCase(">")
                    || currentToken.getValue().equalsIgnoreCase(">=")
                    || currentToken.getValue().equalsIgnoreCase("+")
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getValue().equalsIgnoreCase("*")
                    || currentToken.getValue().equalsIgnoreCase("/")
                    || currentToken.getValue().equalsIgnoreCase("rem")
                    || currentToken.getValue().equalsIgnoreCase(".")
                    || currentToken.getValue().equalsIgnoreCase(":=")
                    || currentToken.getValue().equalsIgnoreCase("..")
                    || currentToken.getValue().equalsIgnoreCase("loop")) return;
        }
        else error = true;
        if (error) {
            printError("( ; , = ) or and then not /= < <= > >= + - * / rem . := .. loop", currentToken);
            // System.out.println("Erreur syntaxique : terminal attendu : ( ou ; ou , ou = ou ) ou or ou and ou then ou not ou /= ou < ou <= ou > ou >= ou + ou - ou * ou / ou rem ou . ou := ou .. ou loop" + " != " + currentToken.getValue() + " = current token");
        }
    }

    void acces(Node node) {
        if(!error){
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")
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
            if (currentToken.getValue().equalsIgnoreCase("begin")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                terminalAnalyse("begin", nodeIntr1);
                instr(nodeIntr1);
                instrstar(nodeIntr1);
                terminalAnalyse("end", nodeIntr1);
                terminalAnalyse(";", nodeIntr1);
            }
            else if (currentToken.getValue().equalsIgnoreCase("return")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                terminalAnalyse("return", nodeIntr1);
                exprinterro2(nodeIntr1);
                terminalAnalyse(";", nodeIntr1);
            }
            else if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("character")){
                Node nodeIntr1 = new Node("nodeIntr1");
                node.addChild(nodeIntr1);
                this.indicateur_acces = true;
                acces(nodeIntr1);
                instr2_prime(nodeIntr1);
            }
            else if (currentToken.getValue().equalsIgnoreCase("if")){
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
            else if (currentToken.getValue().equalsIgnoreCase("while")){
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
            else if (currentToken.getValue().equalsIgnoreCase("for")){
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
                if (this.tokens.get(this.tokensIndex + 1).getValue().equalsIgnoreCase(":=") || this.tokens.get(this.tokensIndex + 1).getValue().equalsIgnoreCase(";") || this.tokens.get(this.tokensIndex + 1).getValue().equalsIgnoreCase("(")){
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
                printError("begin return ( - number character true false null new character if while for ident", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : begin ou return ou ( ou - ou number ou character ou true ou false ou null ou new ou character ou if ou while ou for" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void instr2(Node node) {
        if(!error){
            if (currentToken.getValue().equalsIgnoreCase(";")) {
                Node nodeIntr2 = new Node("nodeIntr2");
                node.addChild(nodeIntr2);
                terminalAnalyse(";", nodeIntr2);
            }
            else if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)) {
                Node nodeIntr2 = new Node("nodeIntr2");
                node.addChild(nodeIntr2);
                terminalAnalyse("(", nodeIntr2);
                expr(nodeIntr2);
                exprstar_virgule(nodeIntr2);
                terminalAnalyse(")", nodeIntr2);
                terminalAnalyse(";", nodeIntr2);
            }
            else if (currentToken.getValue().equalsIgnoreCase(":=")) {
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
            if (currentToken.getValue().equalsIgnoreCase(":=")) {
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
            if ((currentToken.getValue().equalsIgnoreCase("(") && currentToken.getType() == TokenType.SEPARATOR)
                    || currentToken.getValue().equalsIgnoreCase("-")
                    || currentToken.getType() == TokenType.NUMBER
                    || currentToken.getType() == TokenType.CHARACTER
                    || currentToken.getValue().equalsIgnoreCase("true")
                    || currentToken.getValue().equalsIgnoreCase("false")
                    || currentToken.getValue().equalsIgnoreCase("null")
                    || currentToken.getValue().equalsIgnoreCase("new")
                    || currentToken.getValue().equalsIgnoreCase("not")
                    || currentToken.getValue().equalsIgnoreCase("character")
                    || currentToken.getType() == TokenType.IDENTIFIER) return;
            else if (currentToken.getValue().equalsIgnoreCase("reverse")) {
                Node nodeReverseinterro = new Node("nodeReverseinterro");
                node.addChild(nodeReverseinterro);
                terminalAnalyse("reverse", nodeReverseinterro);
            } else error = true;
            if (error) {
                printError("( - number character true false null new not character ident reverse", currentToken);
                //System.out.println("Erreur syntaxique : terminal attendu : ( ou - ou number ou character ou true ou false ou null ou new ou character ou ident ou reverse" + " != " + currentToken.getValue() + " = current token");
            }
        }
    }

    void elsifstar(Node node) {
        if(!error){
            if (currentToken.getValue().equalsIgnoreCase("end")) return;
            else if (currentToken.getValue().equalsIgnoreCase("else")) {
                Node nodeElsifstar = new Node("nodeElsifstar");
                node.addChild(nodeElsifstar);
                terminalAnalyse("else", nodeElsifstar);
                instr(nodeElsifstar);
                instrstar(nodeElsifstar);
            }
            else if (currentToken.getValue().equalsIgnoreCase("elsif")) {
                Node nodeElsifstar = new Node("nodeElsifstar");
                node.addChild(nodeElsifstar);
                terminalAnalyse("elsif", nodeElsifstar);
                expr(nodeElsifstar);
                terminalAnalyse("then", nodeElsifstar);
                instr(nodeElsifstar);
                instrstar(nodeElsifstar);
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
            if (currentToken.getValue().equalsIgnoreCase(",")) {
                Node nodeExprstarVirgule = new Node("nodeExprstarVirgule");
                node.addChild(nodeExprstarVirgule);
                terminalAnalyse(",", nodeExprstarVirgule);
                expr(nodeExprstarVirgule);
                exprstar_virgule(nodeExprstarVirgule);
            }
            else if (currentToken.getValue().equalsIgnoreCase(")"))return;
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



    //AST
    public void createAST(){
        // create AST from parse tree
        if(this.syntaxTree == null){
            System.out.println("Il n'y a pas encore de parse tree.");
            return;
        }
        this.ast = this.syntaxTree;

        meaningfulNode();
        reduceChains();
        meaningfulNode2();
        renameNodes();
        reduceChains();
    }
    public void meaningfulNode() {
        // browse through tree and convert meaningful non terminal and terminal nodes to meaningful nodes

        ArrayList<Node> nodes_to_visit = new ArrayList<>();
        Node currentNode = ast.getRootNode();
        nodes_to_visit.add(currentNode);

        while (!nodes_to_visit.isEmpty()) {
            //on étudie le prochain noeud à visiter (parcours en profondeur), on l'enlève des noeuds à visiter
            currentNode = nodes_to_visit.get(0); // c'est un noeud de l'ast
            nodes_to_visit.remove(0);

            if (!currentNode.isFinal()) {

                // case currentNode is an operation node
                if (currentNode.getValue().contains("nodePriorite")) {

                    // node addition and soustraction
                    if (currentNode.getValue().equals("nodePrioriteAddition")) {
                        if (currentNode.firstChild().getValue().equals("+") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node +
                        }
                        if (currentNode.firstChild().getValue().equals("-") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node -
                            currentNode.setValue("nodePrioriteSoustraction");
                        }
                    }
                    // node multiplication, division and rem
                    if (currentNode.getValue().equals("nodePrioriteMultiplication")) {
                        if (currentNode.firstChild().getValue().equals("*") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node *
                        }
                        if (currentNode.firstChild().getValue().equals("/") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node /
                            currentNode.setValue("nodePrioriteDivision");
                        }
                        if(currentNode.firstChild().getValue().equals("rem") && currentNode.firstChild().isFinal()){
                            currentNode.getChildren().remove(0); //delete node rem
                            currentNode.setValue("nodePrioriteRem");
                        }
                    }
                    // node lower, lower or equal, greater and greater or equal
                    if (currentNode.getValue().equals("nodePrioriteInferieur")) {
                        if (currentNode.firstChild().getValue().equals("<") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node <
                        }
                        if (currentNode.firstChild().getValue().equals("<=") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node <=
                            currentNode.setValue("nodePrioriteInferieurEgal");
                        }
                        if (currentNode.firstChild().getValue().equals(">") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node >
                            currentNode.setValue("nodePrioriteSuperieur");
                        }
                        if (currentNode.firstChild().getValue().equals(">=") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node >=
                            currentNode.setValue("nodePrioriteSuperieurEgal");
                        }
                    }
                    //node equality and inequality
                    if (currentNode.getValue().equals("nodePrioriteEgal")) {
                        if (currentNode.firstChild().getValue().equals("=") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node =
                        }
                        if (currentNode.firstChild().getValue().equals("/=") && currentNode.firstChild().isFinal()) {
                            currentNode.getChildren().remove(0); //delete node /=
                            currentNode.setValue("nodePrioriteInegal");
                        }
                    }


                    //if parent contains terme, add parent's first child to current node's children (first child)
                    if (currentNode.getParent().getValue().contains("nodeTerme") && !currentNode.isMeaningful()) {
                        currentNode.getParent().getChildren().get(0).setParent(currentNode);
                        currentNode.getChildren().add(0, currentNode.getParent().getChildren().get(0));
                        currentNode.getParent().getChildren().remove(0);
                    } else if (currentNode.getParent().getValue().contains("nodePriorite")
                            && !currentNode.isMeaningful()) {
                        //if parent is a nodePriorite, add parent to current node's children (first child)
                        int indexPapa = currentNode.getParent().getParent().getChildren().indexOf(currentNode.getParent());
                        //add current node to his grandparent's children
                        currentNode.getParent().getChildren().remove(currentNode);
                        currentNode.getParent().getParent().getChildren().add(indexPapa, currentNode); //papa's index = indexPapa+1
                        currentNode.setParent(currentNode.getParent().getParent());
                        //add papa as current node's first child
                        Node papa = currentNode.getParent().getChild(indexPapa + 1);
                        currentNode.getParent().getChildren().remove(papa);
                        papa.setParent(currentNode);
                        currentNode.addChild(0, papa);
                    }
                    currentNode.setMeaningful(true);
                }
                // unary minus
                else if (currentNode.getValue().equals("nodeTerme7")) {
                    if (currentNode.firstChild().getValue().equals("-") && currentNode.firstChild().isFinal()) {
                        currentNode.getChildren().remove(0); //delete node -
                        currentNode.setValue("moinsUnaire");
                        currentNode.setMeaningful(true);
                    }
                }


            } else {
                //on est sur un noeud terminal
                currentNode.setMeaningful(true);

                if ((currentNode.getValue().equals("(") || currentNode.getValue().equals(")") || currentNode.getValue().equals(";") || currentNode.getValue().equals(":"))
                        && currentNode.getToken().getType()==TokenType.SEPARATOR) {
                    currentNode.deleteFromParent();
                }
                //end
                if (currentNode.getValue().equalsIgnoreCase("end") && currentNode.getToken().getType()==TokenType.KEYWORD) {
                    currentNode.deleteFromParent();
                }
                //end if
                if(currentNode.getValue().equalsIgnoreCase("if") && currentNode.getToken().getType()==TokenType.KEYWORD
                        && isNodeNextToken(currentNode, ";") && isNodePreviousToken(currentNode, "end")){
                    currentNode.deleteFromParent();
                }
                //end loop
                if(currentNode.getValue().equalsIgnoreCase("loop") && currentNode.getToken().getType()==TokenType.KEYWORD
                        && isNodeNextToken(currentNode, ";") && isNodePreviousToken(currentNode, "end")){
                    currentNode.deleteFromParent();
                }
                //end record
                if(currentNode.getValue().equalsIgnoreCase("record") && currentNode.getToken().getType()==TokenType.KEYWORD
                        && isNodeNextToken(currentNode, ";") && isNodePreviousToken(currentNode, "end")){
                    currentNode.deleteFromParent();
                }

            }


            //ajout des enfants du noeud courant au début de la liste des noeuds à visiter
            int i = 0;
            for (Node child : currentNode.getChildren()) {
                nodes_to_visit.add(i, child);
                i++;
            }
        }

    }

    public void reduceChains(){
        // delete node with only one child, if this child is not a terminal or a meaningful node
        // currentNode = node to delete

        ArrayList<Node> nodes_to_visit = new ArrayList<>();
        Node currentNode = ast.getRootNode();
        nodes_to_visit.add(currentNode);

        while (!nodes_to_visit.isEmpty()){
            //on étudie le prochain noeud à visiter (parcours en profondeur), on l'enlève des noeuds à visiter
            currentNode = nodes_to_visit.get(0); // c'est un noeud de l'ast
            nodes_to_visit.remove(0);

            if (!currentNode.isMeaningful()){
                if (currentNode.getChildren().size() == 1){
                    //on supprime le noeud courant et on remplace par son enfant
                    int indexCurrentNode = currentNode.getParent().getChildren().indexOf(currentNode);
                    currentNode.getParent().getChildren().set(indexCurrentNode, currentNode.getChildren().get(0));
                    currentNode.getChildren().get(0).setParent(currentNode.getParent());
                }
                if (currentNode.getChildren().isEmpty() && !currentNode.isFinal()){
                    currentNode.deleteFromParent();
                }
            }


            //ajout des enfants du noeud courant au début de la liste des noeuds à visiter
            int i = 0;
            for (Node child : currentNode.getChildren()){
                nodes_to_visit.add(i, child);
                i ++;
            }
        }


    }

    public void renameNodes(){
        // delete node with only one child, if this child is not a terminal or a meaningful node
        // currentNode = node to delete

        ArrayList<Node> nodes_to_visit = new ArrayList<>();
        Node currentNode = ast.getRootNode();
        nodes_to_visit.add(currentNode);

        while (!nodes_to_visit.isEmpty()){
            //on étudie le prochain noeud à visiter (parcours en profondeur), on l'enlève des noeuds à visiter
            currentNode = nodes_to_visit.get(0); // c'est un noeud de l'ast
            nodes_to_visit.remove(0);

            if (currentNode.getValue().equals("nodePrioriteAddition") && !currentNode.isFinal()){
                currentNode.setValue("+");
            }
            if (currentNode.getValue().equals("nodePrioriteSoustraction") && !currentNode.isFinal()){
                currentNode.setValue("-");
            }
            if (currentNode.getValue().equals("nodePrioriteMultiplication") && !currentNode.isFinal()){
                currentNode.setValue("*");
            }
            if (currentNode.getValue().equals("nodePrioriteDivision") && !currentNode.isFinal()){
                currentNode.setValue("/");
            }
            if (currentNode.getValue().equals("nodePrioriteRem") && !currentNode.isFinal()){
                currentNode.setValue("rem");
            }
            if (currentNode.getValue().equals("nodePrioriteInferieur") && !currentNode.isFinal()){
                currentNode.setValue("<");
            }
            if (currentNode.getValue().equals("nodePrioriteInferieurEgal") && !currentNode.isFinal()){
                currentNode.setValue("<=");
            }
            if (currentNode.getValue().equals("nodePrioriteSuperieur") && !currentNode.isFinal()){
                currentNode.setValue(">");
            }
            if (currentNode.getValue().equals("nodePrioriteSuperieurEgal") && !currentNode.isFinal()){
                currentNode.setValue(">=");
            }
            if (currentNode.getValue().equals("nodePrioriteEgal") && !currentNode.isFinal()){
                currentNode.setValue("=");
            }
            if (currentNode.getValue().equals("nodePrioriteInegal") && !currentNode.isFinal()){
                currentNode.setValue("/=");
            }
            if(currentNode.getValue().equals("nodeIntr1If") && !currentNode.isFinal()){
                currentNode.setValue(currentNode.getToken().getValue());
            }
            if(currentNode.getValue().equals("nodeIntr1Elsif") && !currentNode.isFinal()){
                currentNode.setValue(currentNode.getToken().getValue());
            }
            if(currentNode.getValue().equals("nodeIntr1Else") && !currentNode.isFinal()){
                currentNode.setValue(currentNode.getToken().getValue());
            }
            if(currentNode.getValue().equals("nodeIntr1Then") && !currentNode.isFinal()){
                currentNode.setValue(currentNode.getToken().getValue());
            }
            if(currentNode.getValue().equals("nodeIntr1Loop") && !currentNode.isFinal()){
                currentNode.setValue(currentNode.getToken().getValue());
            }
            if (currentNode.getValue().equals("nodeIntr1While") && !currentNode.isFinal()){
                currentNode.setValue(currentNode.getToken().getValue());
            }
            if (currentNode.getValue().equals("nodeIntr1Return") && !currentNode.isFinal()){
                currentNode.setValue(currentNode.getToken().getValue());
            }
            //nodeDeclstar : declaration in procedure
            if(currentNode.getValue().equalsIgnoreCase("nodeDeclstar") && !currentNode.isFinal()){
                currentNode.setValue("declaration");
            }
            if(currentNode.getValue().equalsIgnoreCase("nodeIntr1For") && !currentNode.isFinal()){
                currentNode.setValue("for");
            }
            if(currentNode.getValue().equalsIgnoreCase("moinsUnaire") && !currentNode.isFinal()){
                currentNode.setValue("-");
            }
            if(currentNode.getValue().equals("nodeDecl")
                    && (currentNode.getParent().getValue().equals("declaration")
                            || currentNode.getParent().getValue().equals("function")
                            || currentNode.getParent().getValue().equals("procedure"))){
                currentNode.setValue("variable");
            }


            //ajout des enfants du noeud courant au début de la liste des noeuds à visiter
            int i = 0;
            for (Node child : currentNode.getChildren()){
                nodes_to_visit.add(i, child);
                i ++;
            }
        }


    }

    public void meaningfulNode2() {
        // browse through tree and convert meaningful non terminal and terminal nodes to meaningful nodes

        ArrayList<Node> nodes_to_visit = new ArrayList<>();
        Node currentNode = ast.getRootNode();
        nodes_to_visit.add(currentNode);

        while (!nodes_to_visit.isEmpty()) {
            //on étudie le prochain noeud à visiter (parcours en profondeur), on l'enlève des noeuds à visiter
            currentNode = nodes_to_visit.get(0); // c'est un noeud de l'ast
            nodes_to_visit.remove(0);


            // affectation :=
            if (currentNode.getValue().equals("nodeIntr2prime") && currentNode.getParent().getValue().contains("nodeIntr1") && currentNode.firstChild().getValue().equals(":=")) {
                currentNode.getChildren().remove(0); //delete node :=
                currentNode.setValue(":=");
                currentNode.getParent().firstChild().setParent(currentNode);
                currentNode.getChildren().add(0, currentNode.getParent().firstChild());
                currentNode.getParent().getChildren().remove(0);
                currentNode.setMeaningful(true);
            }

            // . (point)
            if (currentNode.getValue().equals("nodePrioritePoint") && currentNode.getChildIndex(1).getValue().equals(".")) {
                currentNode.getChildIndex(1).deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue(".");
                currentNode.setMeaningful(true);
            }

            // new
            if (currentNode.getValue().equals("nodeFacteur") && currentNode.firstChild().getValue().equals("new")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue("new");
                currentNode.setMeaningful(true);
            }

            //if
            if (currentNode.getValue().contains("nodeIntr1") && currentNode.firstChild().getValue().equalsIgnoreCase("if")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent(); //delete node if
                currentNode.setValue("nodeIntr1If");
                currentNode.setMeaningful(true);
            }
            // else if
            if (currentNode.getValue().equals("nodeElsifstar") && currentNode.firstChild().getValue().equalsIgnoreCase("elsif")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent(); //delete node else
                currentNode.setValue("nodeIntr1Elsif");
                currentNode.setMeaningful(true);
                if (currentNode.getParent().getValue().equals("nodeIntr1Elsif") && currentNode.getParent().isMeaningful()) {
                    // add currentnode to currentNode.getParent().getParent()
                    currentNode.getParent().getChildren().remove(currentNode);
                    currentNode.getParent().getParent().getChildren().add(currentNode);
                    currentNode.setParent(currentNode.getParent().getParent());
                }
            }
            //else
            if (currentNode.getValue().equals("nodeElsifstar") && currentNode.firstChild().getValue().equalsIgnoreCase("else")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent(); //delete node else
                currentNode.setValue("nodeIntr1Else");
                currentNode.setMeaningful(true);
                if (currentNode.getParent().getValue().equals("nodeIntr1Elsif") && currentNode.getParent().isMeaningful()) {
                    // add currentnode to currentNode.getParent().getParent()
                    currentNode.getParent().getChildren().remove(currentNode);
                    currentNode.getParent().getParent().getChildren().add(currentNode);
                    currentNode.setParent(currentNode.getParent().getParent());
                }
            }
            //then
            if (currentNode.getValue().contains("nodeIntr1")
                    && (currentNode.getParent().getValue().equals("nodeIntr1If") || currentNode.getParent().getValue().equals("nodeIntr1Elsif"))) {
                if (currentNode.indexInBrothers()!=0 && currentNode.getParent().getChildren().get(currentNode.indexInBrothers() - 1).getValue().equalsIgnoreCase("then")
                        && currentNode.getParent().getChildren().get(currentNode.indexInBrothers() - 1).isFinal()) {
                    currentNode.getParent().getChildren().get(currentNode.indexInBrothers() - 1).deleteFromParentTransferringChildTokenTo(currentNode); //delete node then
                    currentNode.setValue("nodeIntr1Then");
                    currentNode.setMeaningful(true);
                }
                if (currentNode.indexInBrothers()!=0 && currentNode.getParent().getChild(currentNode.indexInBrothers() - 1).getValue().equalsIgnoreCase("nodeIntr1Then")
                        && !currentNode.getValue().contains("Elsif") && !currentNode.getValue().contains("Else")) {
                    int indexCurrentNode = currentNode.indexInBrothers();
                    currentNode.deleteFromParent();
                    currentNode.getParent().getChild(indexCurrentNode - 1).addChild(currentNode);
                }
            }
            //loop
            if (currentNode.getValue().equalsIgnoreCase("loop") && currentNode.isFinal()) {
                int i = currentNode.indexInBrothers();
                ;
                // move all brother after loop node to loop's children
                while (i < currentNode.getParent().getChildren().size()) {
                    if (!currentNode.getParent().getChildren().get(i).isFinal()) {
                        currentNode.addChild(currentNode.getParent().getChildren().get(i));
                        currentNode.getParent().getChildren().remove(i);
                    } else i++;
                }

            }
            //while
            if (currentNode.getValue().contains("nodeIntr1")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("while")
                    && isNodesTokenType(currentNode.firstChild(), TokenType.KEYWORD)) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent(); //delete node while
                currentNode.setValue("nodeIntr1While");
                currentNode.setMeaningful(true);
            }

            //declaration block

            if (currentNode.getValue().equals("nodeDeclstar") && currentNode.getParent().getValue().equals("nodeDeclstar")) {
                int indexCurrentNode = currentNode.indexInBrothers();
                currentNode.deleteFromParent();
                currentNode.getParent().addChildren(indexCurrentNode, currentNode.getChildren());
            }

            //type
            if (currentNode.getValue().equals("nodeDecl")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("type")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue(currentNode.getToken().getValue());
                currentNode.setMeaningful(true);
            }

            // is in type block
            if (currentNode.getValue().equals("nodeDecl2") && currentNode.getParent().getValue().equalsIgnoreCase("type")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("is")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue("declaration");
                currentNode.setMeaningful(true);
            }
            //access in type block
            if (currentNode.getValue().equals("nodeDecl3") && currentNode.getParent().getValue().equals("declaration")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("access")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue(currentNode.getToken().getValue());
                currentNode.setMeaningful(true);
            }
            //record in type block
            if (currentNode.getValue().equals("nodeDecl3") && currentNode.getParent().getValue().equals("declaration")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("record")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue(currentNode.getToken().getValue());
                currentNode.setMeaningful(true);
            }
            //nodeChamps in record (type block)
            if (currentNode.getValue().equals("nodeChamps") && currentNode.getParent().getValue().equalsIgnoreCase("record")) {
                currentNode.setValue("variable");
                currentNode.setMeaningful(true);
            }

            //function block
            if (currentNode.getValue().equals("nodeDecl")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("function")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue(currentNode.getToken().getValue());
                currentNode.setMeaningful(true);
            }

            //param in function block and procedure block
            if (currentNode.getValue().equals("nodeParams")
                    && (currentNode.getParent().getValue().equalsIgnoreCase("function") || currentNode.getParent().getValue().equalsIgnoreCase("procedure"))) {
                //move currentNode to previous brother's children
                int indexCurrentNode = currentNode.indexInBrothers();
                currentNode.deleteFromParent();
                Node nodeFunctionName = currentNode.getParent().getChildren().get(indexCurrentNode - 1);
                nodeFunctionName.addChildren(currentNode.getChildren());
            }
            if (currentNode.getValue().equals("nodeParam")) {
                if (currentNode.getParent().getValue().equalsIgnoreCase("function")
                        || currentNode.getParent().getValue().equalsIgnoreCase("procedure")) {
                    //move currentNode to previous brother's children (previous brother is nodeFunctionName)
                    int indexCurrentNode = currentNode.indexInBrothers();
                    currentNode.deleteFromParent();
                    Node nodeFunctionName = currentNode.getParent().getChildren().get(indexCurrentNode - 1);
                    nodeFunctionName.addChild(currentNode);
                    currentNode.setMeaningful(true);
                }
                currentNode.setValue("param");
            }
            if (currentNode.getValue().equals("nodeParamstar")) {
                currentNode.deleteFromParent();
                currentNode.getParent().addChildren(currentNode.getChildren());
            }
            if (currentNode.getValue().equals("nodeIdentstar") && currentNode.getParent().getValue().equals("param")) {
                int indexCurrentNode = currentNode.indexInBrothers();
                currentNode.deleteFromParent();
                if (currentNode.firstChild().getValue().equals(",")) {
                    currentNode.firstChild().deleteFromParent();
                }
                currentNode.getParent().addChildren(indexCurrentNode, currentNode.getChildren());
            }
            //return type in function block
            if (currentNode.getValue().equalsIgnoreCase("return") && currentNode.getToken().getType() == TokenType.KEYWORD
                    && currentNode.getParent().getValue().equalsIgnoreCase("function")) {
                int indexCurrentNode = currentNode.indexInBrothers();
                if (currentNode.getParent().getChildren().get(indexCurrentNode + 1).getToken().getType() == TokenType.IDENTIFIER
                        && currentNode.getParent().getChildren().get(indexCurrentNode + 1).isFinal()) {
                    Node nextBrother = currentNode.getParent().getChildren().get(indexCurrentNode + 1);
                    currentNode.getParent().getChildren().get(indexCurrentNode + 1).deleteFromParent();
                    currentNode.addChild(nextBrother);
                }
                currentNode.setMeaningful(true);
            }



            //is in function block and procedure block and fichier
            if (currentNode.getValue().equalsIgnoreCase("is") && currentNode.getToken().getType() == TokenType.KEYWORD
                    && (currentNode.getParent().getValue().equalsIgnoreCase("function") || currentNode.getParent().getValue().equalsIgnoreCase("procedure") || currentNode.getParent().getValue().equalsIgnoreCase("Fichier"))) {
                currentNode.deleteFromParent();
            }
            //begin in function block and procedure block and fichier
            if (currentNode.getValue().equalsIgnoreCase("begin") && currentNode.getToken().getType() == TokenType.KEYWORD
                    && (currentNode.getParent().getValue().equalsIgnoreCase("function") || currentNode.getParent().getValue().equalsIgnoreCase("procedure") || currentNode.getParent().getValue().equalsIgnoreCase("Fichier"))) {
                currentNode.setValue("body");
                int i = currentNode.indexInBrothers();
                ;
                // move all brother after begin node to begin's children (unless it's a terminal, return name of function)
                while (i < currentNode.getParent().getChildren().size()) {
                    if (!currentNode.getParent().getChildren().get(i).isFinal()) {
                        currentNode.addChild(currentNode.getParent().getChildren().get(i));
                        currentNode.getParent().getChildren().remove(i);
                    } else i++;
                }
                currentNode.setMeaningful(true);
            }
            //final return in function block
            if (currentNode.getValue().equals("nodeIntr1")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("return")
                    && !currentNode.getValue().equals("nodeIntr1Then")) {
                currentNode.setValue("nodeIntr1Return");
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setMeaningful(true);
            }

            //function call
            if ((currentNode.getValue().equals("nodeFacteur") || currentNode.getValue().equals("nodeIntr1"))
                    && currentNode.firstChild().isFinal()
                    && currentNode.firstChild().getToken().getType() == TokenType.IDENTIFIER
                    && isNodeNextToken(currentNode.firstChild(), "(")) {
                currentNode.setValue("call");
            }
            if(currentNode.getValue().contains("nodeIntr")
                    && !currentNode.getValue().equals("nodeIntr1")
                    && currentNode.firstChild().isFinal()
                    && currentNode.firstChild().getToken().getType() == TokenType.IDENTIFIER
                    && isNodeNextToken(currentNode.firstChild(), "(")){
                    Node newNode = new Node();
                    newNode.setValue("call");
                    newNode.addChildren(currentNode.getChildren());
                    currentNode.deleteChildren();
                    currentNode.addChild(newNode);
            }


            //function call parameters
            if (currentNode.getValue().contains("nodeIntr") && currentNode.getParent().getValue().equals("call")) {
                int indexCurrentNode = currentNode.indexInBrothers();
                currentNode.deleteFromParent();
                currentNode.getParent().addChildren(indexCurrentNode, currentNode.getChildren());
            }
            if (currentNode.getValue().equals("nodeExprstarVirgule") && currentNode.getParent().getValue().equals("call")
                    && currentNode.firstChild().getValue().equals(",")) {
                int indexCurrentNode = currentNode.indexInBrothers();
                currentNode.deleteFromParent();
                currentNode.getParent().addChildren(indexCurrentNode, currentNode.getChildren());
                currentNode.firstChild().deleteFromParent();
            }


            //procedure block
            if (currentNode.getValue().equals("nodeDecl")
                    && currentNode.firstChild().getValue().equalsIgnoreCase("procedure")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue(currentNode.getToken().getValue());
                currentNode.setMeaningful(true);
            }

            //declaration (types) in procedure block
            if (currentNode.getValue().equals("nodeDeclstar") && currentNode.getParent().getValue().equalsIgnoreCase("procedure")) {
                currentNode.setMeaningful(true);
            }


            //for block
            if (currentNode.getValue().equals("nodeIntr1") && currentNode.firstChild().getValue().equalsIgnoreCase("for")) {
                currentNode.setValue("nodeIntr1For");
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setMeaningful(true);
            }
            if (currentNode.getValue().equals("nodeInstrstar") && !currentNode.isMeaningful()) {
                currentNode.deleteFromParent();
                currentNode.getParent().addChildren(currentNode.getChildren());
                currentNode.setMeaningful(true);
            }
            if(currentNode.getValue().equals("..") && currentNode.getParent().getValue().equals("nodeIntr1For")){
                currentNode.deleteFromParent();
            }

            // variable declaration (ex :    N: Integer := 42)
            if (currentNode.getValue().equals("nodeExprinterro") && currentNode.getParent().getValue().equals("nodeDecl") && currentNode.firstChild().getValue().equals(":=")) {
                currentNode.firstChild().deleteFromParentTransferringChildTokenToParent();
                currentNode.setValue(currentNode.getToken().getValue());
                currentNode.getParent().setValue("variable");
                int indexPapa = currentNode.getParent().indexInBrothers();
                currentNode.deleteFromParent();
                currentNode.getParent().getParent().addChild(indexPapa +1 , currentNode);
                currentNode.addChild(0, currentNode.getParent().getChild(indexPapa));
                currentNode.getParent().getChildren().remove(indexPapa);
            }

            //Fichier (root node)
            if (currentNode.getValue().equals("Fichier")) {
                for (int i = 0; i < 9; i++) {
                    currentNode.getChildren().get(0).deleteFromParent();
                }
                currentNode.setMeaningful(true);
            }

            //Character'Val
            if (currentNode.getValue().equals("nodeFacteur") && currentNode.firstChild().getValue().equalsIgnoreCase("character")
                    && currentNode.getChild(1).getValue().equals("'")
                    && currentNode.getChild(2).getValue().equalsIgnoreCase("val")){
                currentNode.firstChild().deleteFromParent();
                currentNode.firstChild().deleteFromParent();
                currentNode.firstChild().deleteFromParent();
                currentNode.setValue("Character'Val");
                currentNode.setMeaningful(true);
            }

            //coma
            if (currentNode.getValue().equals(",") && currentNode.getToken().getType() == TokenType.SEPARATOR){
                currentNode.deleteFromParent();
            }


            //in out
            if(currentNode.getValue().equals("nodeMode") && currentNode.getChildren().size()==2
                    && currentNode.firstChild().getValue().equals("in") && currentNode.getChild(1).getValue().equals("out")){
                currentNode.setValue("in out");
                currentNode.firstChild().deleteFromParent();
                currentNode.firstChild().deleteFromParent();
                currentNode.setMeaningful(true);
            }


            // if return is under then with child after return
            if (currentNode.getValue().equalsIgnoreCase("return") && currentNode.getToken().getType() == TokenType.KEYWORD
                    && currentNode.getParent().getValue().equals("nodeIntr1Then")) {
                ArrayList<Node> brothers = currentNode.getParent().getChildren();
                currentNode.getParent().deleteChildren();
                brothers.remove(currentNode);
                currentNode.addChildren(brothers);
                currentNode.getParent().addChild(currentNode);
            }

            //if return is under then with nothing to return
            if (currentNode.getValue().equalsIgnoreCase("return") && currentNode.getToken().getType() == TokenType.KEYWORD
                    && isNodePreviousToken(currentNode, "then") && isNodeNextToken(currentNode, ";")
                    && currentNode.getParent().getChildren().size() >= 2) {
                System.out.println(currentNode);
                int index = currentNode.indexInBrothers();
                currentNode.deleteFromParent();
                currentNode.getParent().getChildren().get(index - 1).addChild(currentNode);
            }



            //ajout des enfants du noeud courant au début de la liste des noeuds à visiter
            int i = 0;
            for (Node child : currentNode.getChildren()) {
                nodes_to_visit.add(i, child);
                i++;
            }

        }

    }

    public boolean isNodeNextToken(Node node, String string){
        //return true if the node's token's next token in the token list is string
        return tokens.get(tokens.indexOf(node.getToken()) + 1).getValue().equals(string);
    }

    public boolean isNodePreviousToken(Node node, String string){
        //return true if the node's token's previous token in the token list is string
        return tokens.get(tokens.indexOf(node.getToken()) - 1).getValue().equals(string);
    }

    public boolean isNodesTokenType(Node node, TokenType type){
        //return true if the node's token's type is type
        return node.getToken().getType() == type;
    }
}
