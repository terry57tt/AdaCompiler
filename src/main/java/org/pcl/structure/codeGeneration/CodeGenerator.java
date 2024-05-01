package org.pcl.structure.codeGeneration;

import org.pcl.OutputGenerator;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tds.*;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.NodeType;
import org.pcl.structure.tree.SyntaxTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.pcl.OutputGenerator.*;
import static org.pcl.structure.tree.NodeType.SUBSTRACTION;
import static org.pcl.structure.tree.NodeType.SUPERIOR;

public class CodeGenerator {
    SyntaxTree ast;
    Tds tds;

    private final String whileLabel = "While";
    private final String endLabel = "End";
    private int whileCounter = 0;

    private int ifCounter = 0;
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
        List<Node> children = node.getChildren();
        Node condition = children.get(0);
        Node body = children.get(1);
        List<Node> elseif = new ArrayList<>();
        Node elsenode = null;
        if (children.size() > 2){
            for (int i = 2 ; i < children.size(); i++){
                if (children.get(i).getType() == NodeType.ELSIF) {
                    elseif.add(children.get(i));
                }
                else if (children.get(i).getType() == NodeType.BODY){
                    elsenode = children.get(i);
                }
            }
        }
        write("IF" + ifCounter);
        generateBoolean(condition);
        write("LDMFD   r13!, {r0}");
        write("CMP r0, #0");
        if (elseif.size() > 0){
            write("BEQ " + "ElSIF" + ifCounter + "0");
            generateCode(body);
            write("B " + "EndIf" + ifCounter);
            /*
            * IF CMP r0, #0
            * BEQ ElSIF0
            * body du IF
            * B EndIf0
            * Calcul de la condition de elsif0
            * ElSIF0 CMP r0, #0
            * BEQ ElSIF1
            * body du ElSIF0
            * B EndIf0
            * Calcul de la condition de elsif1
            * ElSIF1 CMP r0, #0
            * BEQ ELSE0
            * body du ElSIF1
            * B EndIf0
            * ELSE0 body du ELSE0
            * EndIf0
            * */
            for (int i = 0; i < elseif.size() - 1; i++){
                Node elseifnode = elseif.get(i);
                Node elseifcondition = elseifnode.getChildren().get(0);
                Node elseifbody = elseifnode.getChildren().get(1);
                write("ElSIF" + ifCounter + i);
                generateBoolean(elseifcondition);
                write("LDMFD   r13!, {r0}");
                write("CMP r0, #0");
                write("BEQ " + "ELSIF" + ifCounter + (i+1));
                generateCode(elseifbody);
                write("B " + "EndIf" + ifCounter);
            }
            Node elseifnode = elseif.get(elseif.size() - 1);
            Node elseifcondition = elseifnode.getChildren().get(0);
            Node elseifbody = elseifnode.getChildren().get(1);
            write("ElSIF" + ifCounter + (elseif.size() - 1));
            generateBoolean(elseifcondition);
            write("LDMFD   r13!, {r0}");
            write("CMP r0, #0");
            if (elsenode != null){
                write("BEQ " + "ELSE" + ifCounter);
                generateCode(elseifbody);
                write("B " + "EndIf" + ifCounter);
                write("ELSE" + ifCounter);
                generateCode(elsenode);
                write("B " + "EndIf" + ifCounter);
            }
            else {
                write("BEQ " + "EndIf" + ifCounter);
                generateCode(elseifbody);
                write("B " + "EndIf" + ifCounter);
            }
        }
        else {
            if (elsenode != null) {
                write("BEQ " + "Else" + ifCounter);
                generateCode(body);
                write("B " + "EndIf" + ifCounter);
                generateCode(elsenode);
                ifCounter++;
            }
            else {
                write("BEQ " + "EndIf" + ifCounter);
                generateCode(body);
                write("B " + "EndIf" + ifCounter);
                ifCounter++;
            }
        }
        write ("EndIf" + whileCounter);
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
            String value_type = type_valeur(children.get(i));
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
                write("LDMFD   r13!, {r0}");
                write("STR r0, [r13] ; Empiler le paramètre \" + i");
            }
            else if (comparator.contains(children.get(i).getType())){
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                generateBoolean(children.get(i));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [r13] ; Empiler le paramètre \" + i");
            }
            else if (value_type.equalsIgnoreCase(" ")){
                //c'est une variable donc faut la chercher par la fonction accessVariable
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                generateAccessVariable(children.get(i));
                write("LDMFD   r13!, {r0}");
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

    public static String type_valeur(Node valeur) {
        try {
            if (valeur.getValue().equalsIgnoreCase("-") && valeur.getChildren().size() == 1) {
                Integer.parseInt(valeur.getChildren().get(0).getValue());
                return "integer";
            }
            Integer.parseInt(valeur.getValue());
            if (valeur.getToken() != null && valeur.getToken().getType() == TokenType.CHARACTER) {
                return "Character";
            }
            return "integer";
        } catch (NumberFormatException e) {
            String valueStr = valeur.getValue().toLowerCase();
            if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
                return "boolean";
            } else if (valeur.getToken() != null && valeur.getToken().getType() == TokenType.CHARACTER) {
                return "Character";
            }
            else {
                if (valeur.getValue().equalsIgnoreCase("Character'Val")){
                        return "Character";
                }
                if (valeur.getValue().equalsIgnoreCase("null")){ return "null"; }
                return " ";
            }
            }
        }
}

