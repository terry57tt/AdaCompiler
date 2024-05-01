package org.pcl.structure.codeGeneration;

import org.pcl.OutputGenerator;
import org.pcl.structure.tds.FunctionSymbol;
import org.pcl.structure.tds.ParamSymbol;
import org.pcl.structure.tds.Symbol;
import org.pcl.structure.tds.Tds;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.NodeType;
import org.pcl.structure.tree.SyntaxTree;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.pcl.OutputGenerator.*;
import static org.pcl.structure.tds.SemanticControls.type_valeur;
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
                    generateCallFunctionProcedure(node, tds);
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
            case "=":
            case "<":
            case "<=":
            case ">":
            case ">=":
                write("MOV R1, #" + left.getValue()); // Valeur de gauche
                write("MOV R2, #" + right.getValue()); // Valeur de droite
                write("CMP R1, R2"); // Comparaison
                write("SUB R13, R13, #4"); // Décrémenter le pointeur de pile (on va mettre flag en pile)
                switch (node.getValue()){
                    case "=":
                        write("MOVEQ   R3, #1"); // on met 1 si retourne vrai
                        write("MOVNE   R3, #0"); // 0 sinon
                        break;
                    case "<":
                        write("MOVLT   R3, #1");
                        write("MOVGE   R3, #0");
                        break;
                    case "<=":
                        write("MOVLE   R3, #1");
                        write("MOVGT   R3, #0");
                        break;
                    case ">":
                        write("MOVGT   R3, #1");
                        write("MOVLE   R3, #0");
                        break;
                    case ">=":
                        write("MOVGE   R3, #1");
                        write("MOVLT   R3, #0");
                        break;
                }
                shift = shift-4; // déplacement par rapport à R11
                write("STR   R3, [R11, #"+ shift +"]"); // on met le résultat en pile
                return;
            case "and":
            case "or":
                generateBoolean(left);
                generateBoolean(right);
                write("LDR R1, [R11, #"+shift+"]"); // on récupère le résultat de la première opération
                shift = shift+4; // (en fait on récupère les deux derniers résultats de la pile)
                write("LDR R2, [R11, #"+shift+"]"); // on récupère le résultat de la deuxième opération
                if (node.getValue().equals("and")) {
                    write("AND R3, R1, R2");
                } else {
                    write("ORR R3, R1, R2");
                }
                write("STR R3, [R11, #" + shift + "]"); // on remplace les deux résultats par le résultat de l'opération
                write("ADD R13, R13, #4"); // on décrémente le pointeur de pile
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
        String nom_fonction = node.getChildren().get(0).getValue();
        write(nom_fonction.toUpperCase()); // Label de la fonction en majuscule
        incrementTabulation();
        write("STMFD r13!, {r11, r14} ; Sauvegarde des registres FP et LR en pile");
        write("MOV r11, r13 ; Déplacer le pointeur de pile sur l'environnement de la fonction");
        List<Node> children = node.getChildren();
        if (children.get(2).getType() == NodeType.BODY) {
            Node body = children.get(2);
            generateCode(body);
        }
        else {
            if (children.get(2).getType() == NodeType.DECLARATION) {
                Node declaration = children.get(2);
                generateCode(declaration);
                Node body = children.get(3);
                generateCode(body);
            } else {
                Node body = children.get(2);
                generateCode(body);
            }
        }
        write("MOV r13, r11 ; Restaurer le pointeur de pile original");
        write("LDMFD r13!, {r11, PC} ; Restaurer les registres et retourner");
        decrementTabulation();
    }

    private void generateDeclProcedure(Node node) throws IOException {
        String nom_procedure = node.getChildren().get(0).getValue();
        write(nom_procedure.toUpperCase()); // Label de la procédure en majuscule
        incrementTabulation();
        write("STMFD r13!, {r11, r14} ; Sauvegarde des registres FP et LR en pile");
        write("MOV r11, r13 ; Déplacer le pointeur de pile sur l'environnement de la procédure");
        List<Node> children = node.getChildren();
        if (children.get(2).getType() == NodeType.BODY) {
            Node body = children.get(2);
            generateCode(body);
        }
        else {
            if (children.get(2).getType() == NodeType.DECLARATION) {
                Node declaration = children.get(2);
                generateCode(declaration);
                Node body = children.get(3);
                generateCode(body);
            } else {
                Node body = children.get(2);
                generateCode(body);
            }
        }
        write("MOV r13, r11 ; Restaurer le pointeur de pile original");
        write("LDMFD r13!, {r11, PC} ; Restaurer les registres et retourner");
        decrementTabulation();
    }

    private void generateCallFunctionProcedure(Node node, Tds tds) throws IOException {
        /* Quand j'appelle une fonction ou une procédure, je dois garder une place pour la valeur de retour si c'est une fonction
        et également sauvegardé les paramètres puis le chainage statique, puis le chainage dynamique, puis l'adresse de retour
        * */
        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});
        List<NodeType> comparator = Arrays.asList(new NodeType[]{NodeType.EQUAL, NodeType.SLASH_EQUAL, NodeType.SUPERIOR, NodeType.SUPERIOR_EQUAL, NodeType.INFERIOR, NodeType.INFERIOR_EQUAL, NodeType.COMPARATOR, NodeType.AND, NodeType.OR});
        List<Node> children = node.getChildren();
        String nom_fonction = node.getChildren().get(0).getValue();
        int shift = 0;
        for (int i = 1; i < children.size(); i++) {
            String value_type = type_valeur(children.get(i), tds);
            if (value_type.equalsIgnoreCase("integer")) {
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                write("MOV R0, #" + children.get(i).getValue());
                write("STR r0, [r13] ; Empiler le paramètre " + i);
            }
            else if (value_type.equalsIgnoreCase("character")){
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                write("Char" + children.get(i).getValue().toUpperCase() + "  DCD  " + (int)children.get(i).getValue().charAt(0) + " ; '" + children.get(i).getValue() + "' en ASCII");
                write("LDR R0, =Char" + children.get(i).getValue().toUpperCase());
                write("LDR r0, [r0]");
                write("STR r0, [r13] ; Empiler le paramètre " + i);
            }
            else if (operators.contains(children.get(i).getType())){
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                generateArithmetic(children.get(i));
                write("STR r0, [r13] ; Empiler le paramètre \" + i");
            }
            else if (comparator.contains(children.get(i).getType())){
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                generateBoolean(children.get(i));
                write("STR r0, [r13] ; Empiler le paramètre \" + i");
            }
            else if (value_type.equalsIgnoreCase(" ")){
                //c'est une variable donc faut la chercher par la fonction accessVariable
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                generateAccessVariable(children.get(i));
                write("STR r0, [r13] ; Empiler le paramètre \" + i");
            }
            write("; TODO : Chainage statique");
            write("BL " + nom_fonction.toUpperCase());
        }
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
