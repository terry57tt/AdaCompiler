package org.pcl.structure.codeGeneration;

import org.pcl.OutputGenerator;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tds.Tds;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.NodeType;
import org.pcl.structure.tree.SyntaxTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.pcl.OutputGenerator.*;

public class CodeGenerator {
    SyntaxTree ast;
    Tds tds;

    private final String whileLabel = "While";

    private final String ifLabel = "If";
    private final String endLabelWhile = "EndWhile";
    private final String endLabelIf = "End";

    private int whileCounter = 0;
    private int ifCounter = 0;
    private int forCounter = 0;
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
                    return;
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
                case RETURN:
                    generateReturn(node);
                    break;
                case EXPRESSION, ELSIF, REVERSE, BEGIN, CHAR_VAL, NEW, NULL, FALSE, TRUE, CHARACTER, INTEGER, POINT, BODY,
                     NEGATIVE_SIGN, REM, DIVIDE, MULTIPLY, SUBSTRACTION, ADDITION, SUPERIOR_EQUAL, SUPERIOR, INFERIOR_EQUAL, INFERIOR, EQUAL, SLASH_EQUAL, NOT, THEN, AND, ELSE, OR, INOUT, IN, MODE, MULTIPLE_PARAM, PARAMETERS, INITIALIZATION, FIELD, DECL_VAR, RECORD, ACCESS, IS, TYPE, VIRGULE, FILE, IDENTIFIER, PROGRAM:
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

    private void generateReturn(Node node) throws IOException {
        //Quand on voit un return, soit on return un char, un int, un bool, une expression arithmétique, une expression booléenne,
        //une variable, un appel de fonction fonction ou de procédure.
        //La pile : valeur de retour, chainage statique, chainage dynamique, adresse de retour donc on doit sauvegarder la valeur de retour à r11
        List<Node> children = node.getChildren();
        if (children.size() == 1) {
            String value_type = type_valeur(children.get(0));
            if (value_type.equalsIgnoreCase("integer")) {
                write("MOV R0, #" + children.get(0).getValue());
                write("STR R0, [R11, #4*4] ; Sauvegarder la valeur de retour");
            }
            else if (value_type.equalsIgnoreCase("character")) {
                write("Char" + children.get(0).getValue().toUpperCase() + "  DCD  " + (int)children.get(0).getValue().charAt(0) + " ; '" + children.get(0).getValue() + "' en ASCII");
                write("LDR R0, =Char" + children.get(0).getValue().toUpperCase());
                write("LDR r0, [r0]");
                write("STR r0, [R11, #4*4] ; Sauvegarder la valeur de retour");
            }
            else if (value_type.equalsIgnoreCase("boolean")) {
                write("MOV R0, #" + children.get(0).getValue());
                write("STR R0, [R11, #4*4] ; Sauvegarder la valeur de retour");
            }
            else if (value_type.equalsIgnoreCase("null")) {
                write("MOV R0, #0");
                write("STR R0, [R11, #4*4] ; Sauvegarder la valeur de retour");
            }
            else if (value_type.equalsIgnoreCase(" ")) {
                generateAccessVariable(children.get(0));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [R11, #4] ; Sauvegarder la valeur de retour");
            }
            else {
                write("BL " + children.get(0).getValue().toUpperCase());
            }
        }
        else {
            if (children.get(0).getType() == NodeType.ADDITION || children.get(0).getType() == NodeType.SUBSTRACTION || children.get(0).getType() == NodeType.MULTIPLY || children.get(0).getType() == NodeType.DIVIDE || children.get(0).getType() == NodeType.REM) {
                generateArithmetic(children.get(0));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [R11, #4*4] ; Sauvegarder la valeur de retour");
            }
            else if (children.get(0).getType() == NodeType.COMPARATOR) {
                generateBoolean(children.get(0));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [R11, #4*4] ; Sauvegarder la valeur de retour");
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

    /** Wrapper to print comment in ASM file*/
    private void generateArithmetic(Node node) throws IOException {
        write("; ---  ARITHMETIC evaluation ---");
        generateArithmeticRecursif(node);
        write("; --- END ARITHMETIC evaluation ---");
    }

    private void generateArithmeticRecursif(Node node) throws IOException {

        if (node.getValue().equalsIgnoreCase("-") || node.getValue().equalsIgnoreCase("+")
                || node.getValue().equalsIgnoreCase("*") || node.getValue().equalsIgnoreCase("/")
                || node.getValue().equalsIgnoreCase("REM"))
        {

            generateArithmeticRecursif( node.getChildren().get(0));
            generateArithmeticRecursif(node.getChildren().get(1));

            write("; Right Operand");
            write("LDR R2, [R13] ; Get the value of right operand");
            write("SUB R13, R13, #4 ; Decrement the stack pointer");

            write("; Left Operand");
            write("LDR R1, [R13] ; Get the value of left operand");
            write("SUB R13, R13, #4 ; Decrement the stack pointer");




            switch (node.getValue().toUpperCase()) {
                case "+":
                    write("; Perform the addition");
                    write("ADD R0, R1, R2");
                    write("STR R0, [R13, #4]"); // On stocke le résultat de la comparaison en pile
                    write("ADD R13, R13, #4"); // On décale le pointeur de pile
                    return;
                case "-":
                    write("; Perform the substraction");
                    write("SUB R0, R1, R2");
                    write("STR R0, [R13, #4]"); // On stocke le résultat de la comparaison en pile
                    write("ADD R13, R13, #4"); // On décale le pointeur de pile;
                    return;
                case "*":
                    //TODO
                    write("; Perform the multiplication");
                    write("MUL R0, R1, R2");
                    write("STR R0, [R13, #4]"); // On stocke le résultat de la comparaison en pile
                    write("ADD R13, R13, #4"); // On décale le pointeur de pile
                    return;
                case "/":
                    //TODO
                    write("; Perform the division");
                    write("SDIV R0, R1, R2");
                    write("STR R0, [R13, #4]"); // On stocke le résultat de la comparaison en pile
                    write("SUB R13, R13, #4"); // On décale le pointeur de pile;
                    return;
                case "REM":
                    //TODO
                    write("; Perform the modulo");
                    write("STR R0, [R13, #4]"); // On stocke le résultat de la comparaison en pile
                    write("ADD R13, R13, #4"); // On décale le pointeur de pile;
                    return;
                default:
            }
        }

        if (node.getToken().getType().equals(TokenType.NUMBER)) {
            write("MOV R0, #" + node.getValue() + " ; Load the value of the number: " + node.getValue());
            write("STR R0, [R13, #4]");
            write("ADD R13, R13, #4");
        } else {
            generateAccessVariable(node);
        }
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

        write("BEQ " + whileLabel + endLabelWhile + number + " ; exit while if condition is false");
        write("");
        write("; body of while");

        generateCode(body);

        write("BL " + whileLabel + number + " ; continue iteration in while");
        decrementTabulation();
        write(whileLabel + endLabelWhile + number);
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
            write("BEQ " + "ELSIF" + ifCounter + "0");
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
                write("ELSIF" + ifCounter + i);
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
            write("ELSIF" + ifCounter + (elseif.size() - 1));
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
            }
            else {
                write("BEQ " + "EndIf" + ifCounter);
                generateCode(body);
                write("B " + "EndIf" + ifCounter);
            }
        }
        write ("EndIf" + ifCounter);
        ifCounter++;
        // Node comparator = node.getChildren().get(0); // noeud de comparaison
        // Node trueCase = node.getChildren().get(1); // noeud du cas vrai
        // Node falseCase = node.getChildren().get(2); // noeud du cas faux

        // int number = ifCounter;
        // ifCounter++;

        // write("; ---  IF generation ---");
        // generateBoolean(comparator);
        // write("LDR R0, [R11, #-4]"); // on récupère le résultat de la comparaison (base de la pile)
        // write("CMP R0, #1"); // on compare le résultat avec 1
        // write("BEQ " + ifLabel + number); // si vrai, on va au cas vrai
        // if (falseCase != null) {
        //     generateCode(falseCase);
        // }
        // write("B " + endLabelIf + number); // on saute le cas vrai
        // write(ifLabel + number);
        // generateCode(trueCase);
        // write(endLabelIf + number);
        // write("; --- END IF generation ---");
    }

    private void generateFor(Node node) throws IOException {
        //Empiler la borne inf, puis la borne sup, puis l'incrément
        List<Node> children = node.getChildren();
        String variable_compteur = children.get(0).getValue();
        String direction = children.get(1).getValue();
        Node borne_inf = children.get(2);
        Node borne_sup = children.get(3);
        Node body = children.get(4);

        //aller chercher la valeur de la borne inf
        String type_borne_inf = type_valeur(borne_inf);
        if (borne_inf.getValue().equalsIgnoreCase(" ")) {
            //c'est une variable donc faut la chercher par la fonction accessVariable
            generateAccessVariable(borne_inf);
            write("LDMFD   r13!, {r0}");
            write("STMFD r13!, {r0}");
        }
        else if (type_borne_inf.equalsIgnoreCase("integer")) {
            write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
            write("MOV R0, #" + borne_inf.getValue());
            write("STR r0, [r13] ; Empiler la borne inf");
        }
        String type_borne_sup = type_valeur(borne_sup);
        if (borne_sup.getValue().equalsIgnoreCase(" ")) {
            //c'est une variable donc faut la chercher par la fonction accessVariable
            generateAccessVariable(borne_sup);
            write("LDMFD   r13!, {r0}");
            write("STMFD r13!, {r0}");
        }
        else if (type_borne_sup.equalsIgnoreCase("integer")) {
            write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
            write("MOV R0, #" + borne_sup.getValue());
            write("STR r0, [r13] ; Empiler la borne sup");
        }
        //initialisation : on met dans la pile : borne inf, borne sup, compteur initialisé à borne inf
        if (direction.equalsIgnoreCase("reverse")) {
            write("LDR r0, [r13, #4] ; Récupérer la borne sup");
            write("STMFD r13!, {r0} ;empiler l'increment qui demarre à la borne sup (reverse)");
        } else {
            write("LDR r0, [r13, #8] ; Récupérer la borne inf");
            write("STMFD r13!, {r0} ;empiler l'incrément qui démarre à la borne inf");
        }
        //Pour le for
        write("FOR" + forCounter);
        write("LDR r0, [r13] ; Récupérer le compteur");
        write("LDR r1, [r13, #4] ; Récupérer la borne sup");
        write("CMP r0, r1");
        write("BEQ end_for" + forCounter);
        generateCode(body);
        write("LDR r0, [r13] ; Récupérer le compteur");
        if (direction.equalsIgnoreCase("reverse")) {
            write("ADD r0, r0, #1 ; Incrémenter le compteur");
        } else {
            write("SUB r0, r0, #1 ; Décrémenter le compteur");
        }
        write("STR r0, [r13] ; Sauvegarder le compteur");
        write("B FOR" + forCounter);
        write("end_for" + forCounter);
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
        generateArithmetic(node.getChild(1));
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

