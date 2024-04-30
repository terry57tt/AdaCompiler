package org.pcl.structure.codeGeneration;

import org.pcl.OutputGenerator;
import org.pcl.structure.tds.Tds;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import java.io.IOException;

import static org.pcl.OutputGenerator.*;
import static org.pcl.structure.tree.NodeType.SUBSTRACTION;
import static org.pcl.structure.tree.NodeType.SUPERIOR;

public class CodeGenerator {
    SyntaxTree ast;
    Tds tds;

    private final String whileLabel = "While";
    private final String endLabel = "End";
    private int whileCounter = 0;
    private int shift = 0;


    public CodeGenerator(SyntaxTree ast, Tds tds) throws IOException {
        if (ast == null || tds == null) {
            throw new IllegalArgumentException("ast et tds ne doivent pas être null");
        }

        this.ast = ast;
        this.tds = tds;
        OutputGenerator.resetFile();
        OutputGenerator.resetTabulation();
        generateCode(ast.getRootNode());
    }

    public void generateCode(Node node) throws IOException {
        if (node == null) {
            return;
        }
        if(node.getType() != null) {
            switch (node.getType()) {
                case DECL_PROC:
                    generateDeclProcedure(node);
                    break;
                case DECL_FUNC:
                    generateDeclFunction(node);
                    break;
                case IF:
                    generateIf(node);
                    break;
                case FOR:
                    generateFor(node);
                    break;
                case WHILE:
                    generateWhile(node);
                    return;
                case AFFECTATION:
                    generateAffectationVar(node);
                    break;
                case DECLARATION:
                    generateDeclVar(node);
                    break;
                case CALL:
                    generateCallFunctionProcedure(node);
                    break;
                case COMPARATOR:
                    // pour l'instant, résultat à la base de la pile
                    write("; ---  BOOLEAN evaluation ---");
                    generateBoolean(node);
                    write("; --- END BOOLEAN evaluation ---");
                    return;
                case EXPRESSION, ELSIF, REVERSE, BEGIN, RETURN, CHAR_VAL, NEW, NULL, FALSE, TRUE, CHARACTER, INTEGER, POINT,
                     NEGATIVE_SIGN, REM, DIVIDE, MULTIPLY, SUBSTRACTION, ADDITION, SUPERIOR_EQUAL, SUPERIOR, INFERIOR_EQUAL, INFERIOR, EQUAL, SLASH_EQUAL, NOT, THEN, AND, ELSE, OR, INOUT, IN, MODE, MULTIPLE_PARAM, PARAMETERS, INITIALIZATION, FIELD, DECL_VAR, RECORD, ACCESS, IS, TYPE, VIRGULE, BODY, FILE, IDENTIFIER, PROGRAM:
                    // NO ACTION
                    break;
                default:
                    throw new IllegalArgumentException("NodeType inconnu : " + node.getType());
            }
        }

        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                generateCode(child);
            }
        }
    }

    private void generateBoolean(Node node) throws IOException {
        Node left = node.getChildren().get(0);
        Node right = node.getChildren().get(1);

        switch (node.getValue()){
            case "=" :
                write("MOV R1, #" + left.getValue()); // On met les valeurs a comparer dans les registres
                write("MOV R2, #" + right.getValue());
                write("CMP R1, R2"); // On compare les valeurs
                write("SUB R13, R13, #4"); // On décrémente le pointeur de pile
                write("MOVEQ   R3, #1"); // 1 si égalité
                write("MOVNE   R3, #0"); // 0 sinon
                shift = shift-4;
                write("STR   R3, [R11, #"+ shift +"]"); // On stocke le résultat de la comparaison en pile

                return;
            case "<" :
                break;
            case "<=" :
                break;
            case ">" :
                break;
            case ">=" :
                break;
            case "and" :
                generateBoolean(left);
                generateBoolean(right);
                write("LDR R1, [R11, #"+shift+"]"); // On récupère les valeurs de la pile
                shift = shift+4;
                write("LDR R2, [R11, #"+shift+"]");

                write("AND R3, R1, R2"); // On fait le ET logique
                write("STR R3, [R11, #" + shift + "]"); // On stocke le résultat en pile (on décale de 8 car on a déjà stocké le résultat de la première comparaison)
                write("ADD R13, R13, #4"); // On incrémente le pointeur de pile
                return;
            case "or" :
                generateBoolean(left);
                generateBoolean(right);
                write("LDR R1, [R11, #"+shift+"]"); // On récupère les valeurs de la pile
                shift = shift+4;
                write("LDR R2, [R11, #"+shift+"]");

                write("ORR R3, R1, R2"); // On fait le OU logique
                write("STR R3, [R11, #" + shift + "]"); // On stocke le résultat en pile (on décale de 8 car on a déjà stocké le résultat de la première comparaison)
                write("ADD R13, R13, #4"); // On incrémente le pointeur de pile
                return;
            default:
                break;
        }
    }

    private void generateArithmetic(Node node) throws IOException {
        //TODO
    }

    private void generateWhile(Node node) throws IOException {
        int number = whileCounter;
        whileCounter++;
        Node comparator = node.getChild("comparator");
        Node body = node.getChild("body");

        if (comparator == null) {
            throw new IllegalArgumentException("No comparator found in while node");
        }
        if (body == null) {
            throw new IllegalArgumentException("No body found in while node");
        }

        write("; ---  WHILE generation for " + whileLabel + number + " ---");
        write(whileLabel + number);
        incrementTabulation();
        write("; condition");

        generateCode(comparator);

        write("BEQ " + whileLabel + endLabel + number + " ; exit while if condition is false");
        write("");
        write("; body of while");

        generateCode(body);

        write("BL " + whileLabel + number + " ; continue iteration in while");
        decrementTabulation();
        write(whileLabel + endLabel + number);
        write("; --- END WHILE generation for " + whileLabel + number + " ---");

    }

    private void generateIf(Node node) throws IOException {
        //TODO
    }

    private void generateFor(Node node) throws IOException {
        //TODO
    }

    private void generateMultiply(Node node) throws IOException {
        //TODO
    }

    private void generateDivide(Node node) throws IOException {
        //TODO
    }

    private void generateDeclFunction(Node node) throws IOException {
        //TODO
    }

    private void generateDeclProcedure(Node node) throws IOException {
        //TODO
    }

    private void generateCallFunctionProcedure(Node node) throws IOException {
        //TODO
    }



    private void generateDeclVar(Node node) throws IOException {
        //TODO
    }

    private void generateAffectationVar(Node node) throws IOException {
        //TODO
    }

    private void generateAccessVariable(Node node) throws IOException {
        //TODO
    }
}
