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
                    generateBoolean(node);
                    break;
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
        //TODO ici de ce que j'avais pensé c'est tu fais les comp etc et le résultat si je m'abuse y'a le système de flag qui le save
    }

    private void generateArithmetic(Node node) throws IOException {
        //TODO
    }

    private void generateWhile(Node node) throws IOException {
        Node comparator = node.getChild("comparator");
        Node body = node.getChild("body");

        write("; ---  WHILE generation for " + whileLabel + whileCounter + " ---");
        write(whileLabel + whileCounter);
        incrementTabulation();
        write("; condition");

        generateCode(comparator);

        write("BEQ " + whileLabel + endLabel + whileCounter + " ; exit while if condition is false");
        write("");
        write("; body of while");

        generateCode(body);

        write("BL " + whileLabel + whileCounter + " ; continue iteration in while");
        decrementTabulation();
        write(whileLabel + endLabel + whileCounter);
        write("; --- END WHILE generation for " + whileLabel + whileCounter + " ---");
        whileCounter++;
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
