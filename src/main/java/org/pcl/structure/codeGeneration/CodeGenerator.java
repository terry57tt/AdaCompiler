package org.pcl.structure.codeGeneration;

import org.pcl.OutputGenerator;
import org.pcl.structure.tds.*;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.NodeType;
import org.pcl.structure.tree.SyntaxTree;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.pcl.OutputGenerator.*;
import static org.pcl.structure.tree.NodeType.*;

public class CodeGenerator {
    SyntaxTree ast;
    Tds tds;
    Tds globalTds;

    int imbrication = 0;
    int region = 0;

    /* Valeur arbitraire pour représenter le null */
    private final String nullValue = "256";
    private final String whileLabel = "While";

    private final String ifLabel = "If";
    private final String endLabelWhile = "EndWhile";
    private final String endLabelIf = "End";

    private int whileCounter = 0;
    private int ifCounter = 0;
    private int forCounter = 0;
    private int nbChampCounter = 0;
    private int nonLocalAccessAffectationLoopCounter = 0;

    int nonLocalAccessLoopCounter = 0;

    int chainageStatiqueLoopCounter = 0;
    int charCounter = 0;
    int structureCounter = 0;

    public CodeGenerator(SyntaxTree ast, Tds tds) throws IOException {
        if (ast == null || tds == null) {
            throw new IllegalArgumentException("ast et tds ne doivent pas être null");
        }

        this.ast = ast;
        this.tds = tds;
        this.globalTds = tds;
        OutputGenerator.resetFile();
        OutputGenerator.resetTabulation();
        write("STR_OUT      FILL    0x1000");
        write("MOV r12, #STR_OUT");
        write("BL program2mainProcedure");
        generateDeclPrint();
        write("");
        write("");
        generateMultiplyFunction();
        write("");
        write("");
        generateDivideFunction();
        write("");
        write("");
        generateCode(ast.getRootNode());
    }

    public void generateCode(Node node) throws IOException {
        if (node == null) {
            return;
        }
        this.tds = globalTds;
        if (node.getType() != null) {
            switch (node.getType()) {
                case FILE:
                    if (node.getChildren() != null) {
                        for (Node child : node.getChildren()) {
                            generateCode(child);
                        }
                    }
                    break;
                case DECLARATION:
                    if (node.getParent().getType() == FILE) {
                        generateCodeDeclarationFuncOrProc(node);
                    }
                    break;
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
                    return;
                case WHILE:
                    generateWhile(node);
                    return;
                case AFFECTATION:
                    generateAffectationVar(node);
                    break;
                case DECL_VAR:
                    generateDeclVar(node);
                    break;
                case CALL:
                    generateCallFunctionProcedure(node);
                    break;
                case COMPARATOR:
                    generateBoolean(node);
                    return;
                case RETURN:
                    if (node.getParent().getType() != NodeType.DECL_PROC && node.getParent().getType() != NodeType.DECL_FUNC) {
                        generateReturn(node);
                    }
                    break;
                case BODY:
                    if (node.getParent().getType() == FILE) {
                        write("program2mainProcedure");
                        write("; ----- MAIN program -----");
                        write("MOV R11, R13");
                        for (Node child : node.getParent().getChildren()) {
                            if (child.getType() == DECLARATION) {
                                generateCodeDeclarationVariable(child);
                            }
                        }
                        if (node.getChildren() != null) {
                            for (Node child : node.getChildren()) {
                                generateCode(child);
                            }
                        }
                        write("END");
                    } else if (node.getChildren() != null) {
                        for (Node child : node.getChildren()) {
                            generateCode(child);
                        }
                    }
                case EXPRESSION, ELSIF, REVERSE, BEGIN, CHAR_VAL, NEW, NULL, FALSE, TRUE, CHARACTER, INTEGER, POINT,
                        NEGATIVE_SIGN, REM, DIVIDE, MULTIPLY, SUBSTRACTION, ADDITION, SUPERIOR_EQUAL, SUPERIOR, INFERIOR_EQUAL, INFERIOR, EQUAL, SLASH_EQUAL, NOT, THEN, AND, ELSE, OR, INOUT, IN, MODE, MULTIPLE_PARAM, PARAMETERS, INITIALIZATION, FIELD, RECORD, ACCESS, IS, TYPE, VIRGULE, IDENTIFIER:
                    // NO ACTION
                    break;
                default:
                    throw new IllegalArgumentException("NodeType inconnu : " + node.getType());
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
                if (children.get(0).getValue().equalsIgnoreCase("-")) {
                    write("MOV R0, #-" + children.get(0).getChildren().get(0).getValue());
                    write("STR R0, [R11, #4*3] ; Sauvegarder la valeur de retour");
                } else {
                    write("MOV R0, #" + children.get(0).getValue());
                    write("STR R0, [R11, #4*3] ; Sauvegarder la valeur de retour");
                }
            } else if (value_type.equalsIgnoreCase("character")) {
                write("MOV r0, #" + (int) children.get(0).getValue().charAt(0) + " ; '" + children.get(0).getValue() + "' en ASCII");
                write("STR r0, [R11, #4*3] ; Sauvegarder la valeur de retour");
            } else if (value_type.equalsIgnoreCase("boolean")) {
                write("MOV R0, #" + (children.get(0).getValue().equalsIgnoreCase("true") ? "1" : "0"));
                write("STR R0, [R11, #4*3] ; Sauvegarder la valeur de retour");
            } else if (value_type.equalsIgnoreCase("null")) {
                write("MOV R0, #0");
                write("STR R0, [R11, #4*3] ; Sauvegarder la valeur de retour");
            } else if (children.get(0).getType() == NodeType.ADDITION || children.get(0).getType() == NodeType.SUBSTRACTION || children.get(0).getType() == NodeType.MULTIPLY || children.get(0).getType() == NodeType.DIVIDE || children.get(0).getType() == NodeType.REM) {
                generateArithmetic(children.get(0));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [R11, #4*3] ; Sauvegarder la valeur de retour");
            } else if (children.get(0).getType() == NodeType.COMPARATOR) {
                generateBoolean(children.get(0));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [R11, #4*3] ; Sauvegarder la valeur de retour");
            } else if (children.get(0).getType() == NodeType.CALL) {
                generateCallFunctionProcedure(children.get(0));
                mettre_valeur_retour_en_registre_apres_appel("r0", children.get(0).getChildren().get(0).getValue(), children.get(0));
                write("SUB R13, R13, #4");
                write("STR R0, [R11, #4*3] ; Sauvegarder la valeur de retour");
            } else if (value_type.equalsIgnoreCase(" ")) {
                generateAccessVariable(children.get(0));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [R11, #4*3] ; Sauvegarder la valeur de retour");
            }
        }
        write("MOV r13, r11 ; Restaurer le pointeur de pile original");
        write("LDMFD r13!, {r10, r11, PC} ; Restaurer les registres et retourner");
    }

    private void generateBoolean(Node node) throws IOException {
        String value = node.getValue();

        // si c'est un opérateur arithmétique
        if (node.getValue().equalsIgnoreCase("-") || node.getValue().equalsIgnoreCase("+")
                || node.getValue().equalsIgnoreCase("*") || node.getValue().equalsIgnoreCase("/")
                || node.getValue().equalsIgnoreCase("REM")) {
            generateArithmetic(node);
            return;
        }

        // si c'est un appel de fonction
        if (node.getType() == NodeType.CALL) {
            generateCallFunctionProcedure(node);
            mettre_valeur_retour_en_registre_apres_appel("r0", node.getChildren().get(0).getValue(), node);
            write("SUB R13, R13, #4");
            write("STR R0, [R13]");
            return;
        }

        // si c'est un caractère, on met son code ASCII en sommet de pile
        try {
            if (node.getToken().getType().equals(TokenType.CHARACTER)) {
                int ascii = value.charAt(0);
                write("MOV R0, #" + ascii);
                write("SUB R13, R13, #4");
                write("STR R0, [R13] ; " + ascii + " en sommet de pile");
                return;
            }
        } catch (Exception ignored) {
        }

        // si c'est un nombre, on le met en sommet de pile
        try {
            Integer.parseInt(value);
            write("MOV R0, #" + value);
            write("SUB R13, R13, #4");
            write("STR R0, [R13] ; " + value + " en sommet de pile");
            return;
        } catch (Exception ignored) {
        }

        // si "not", on fait le not de la valeur en sommet de pile
        if (value.equalsIgnoreCase("not")) {
            generateBoolean(node.getChildren().get(0));
            write("LDR R0, [R13]");
            write("EOR R0, R0, #1 ; NOT logique");
            write("STR R0, [R13] ; NOT résultat en sommet de pile");
            return;
        }

        // si signe négatif, on prend l'opposé de la valeur en sommet de pile
        if (node.getType() == NodeType.NEGATIVE_SIGN) {
            generateBoolean(node.getChildren().get(0));
            write("LDR R0, [R13]");
            write("RSB R0, R0, #0 ; opposé");
            write("STR R0, [R13] ; opposé en sommet de pile");
            return;
        }

        // si "true" ou "false", on met 1 ou 0 en sommet de pile
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            write("MOV R0, #" + (value.equalsIgnoreCase("true") ? "1" : "0"));
            write("SUB R13, R13, #4");
            write("STR R0, [R13] ; " + value + " en sommet de pile");
            return;
        }

        // si "null", on met NULL en sommet de pile
        if (value.equalsIgnoreCase("null")) {
            write("MOV R0, #" + nullValue);
            write("SUB R13, R13, #4");
            write("STR R0, [R13] ; NULL en sommet de pile");
            return;
        }

        // si opérateur de comparaison
        if (value.equalsIgnoreCase("and") || value.equalsIgnoreCase("or") || value.equalsIgnoreCase("=") || value.equalsIgnoreCase("<") || value.equalsIgnoreCase("<=") || value.equalsIgnoreCase(">") || value.equalsIgnoreCase(">=")) {
            Node left = node.getChildren().get(0);
            Node right = node.getChildren().get(1);
            generateBoolean(left);
            generateBoolean(right);
            write("LDR R1, [R13, #4] ; recuperation premiere valeur SP"); // on récupère le résultat de la première opération
            write("LDR R2, [R13] ; recuperation deuxieme valeur SP+4"); // on récupère le résultat de la deuxième opération
            write("ADD R13, R13, #4"); // on décrémente le pointeur de pile

            switch (value) {
                case "=":
                    write("CMP R1, R2; comparaison \"" + value + "\"");
                    write("MOVEQ   R3, #1"); // on met 1 si retourne vrai
                    write("MOVNE   R3, #0"); // 0 sinon
                    break;
                case "<":
                    write("CMP R1, R2; comparaison \"" + value + "\"");
                    write("MOVLT   R3, #1");
                    write("MOVGE   R3, #0");
                    break;
                case "<=":
                    write("CMP R1, R2; comparaison \"" + value + "\"");
                    write("MOVLE   R3, #1");
                    write("MOVGT   R3, #0");
                    break;
                case ">":
                    write("CMP R1, R2; comparaison \"" + value + "\"");
                    write("MOVGT   R3, #1");
                    write("MOVLE   R3, #0");
                    break;
                case ">=":
                    write("CMP R1, R2; comparaison \"" + value + "\"");
                    write("MOVGE   R3, #1");
                    write("MOVLT   R3, #0");
                    break;
                case "and":
                    write("AND R3, R1, R2 ; ET logique");
                    break;
                case "or":
                    write("ORR R3, R1, R2 ; OU logique");
                    break;
                default:
                    System.out.println("Erreur : opérateur non reconnu");
                    break;
            }
            write("STR R3, [R13] ; resultat de la comparaison en sommet de pile"); // on remplace les deux résultats par le résultat de l'opération
            return;
        }

        // si c'est une variable (si la variable est un caractère, on considère que son code ASCII va être mis en sommet de pile)
        generateAccessVariable(node);
    }

    /**
     * Wrapper to print comment in ASM file
     */
    private void generateArithmetic(Node node) throws IOException {
        write("; ---  ARITHMETIC evaluation ---");
        generateArithmeticRecursif(node);
        write("; --- END ARITHMETIC evaluation ---");
    }

    private void generateArithmeticRecursif(Node node) throws IOException {

        if (node.getValue().equalsIgnoreCase("-") || node.getValue().equalsIgnoreCase("+")
                || node.getValue().equalsIgnoreCase("*") || node.getValue().equalsIgnoreCase("/")
                || node.getValue().equalsIgnoreCase("REM")) {

            if (node.getType() == NodeType.NEGATIVE_SIGN) {
                generateArithmeticRecursif(node.getChildren().get(0));
                write("LDR R0, [R13]");
                write("RSB R0, R0, #0 ; opposé");
                write("STR R0, [R13] ; opposé en sommet de pile");
                return;
            }

            generateArithmeticRecursif(node.getChildren().get(0));
            generateArithmeticRecursif(node.getChildren().get(1));

            write("; Right Operand");
            write("LDR R2, [R13] ; Get the value of right operand");
            write("ADD R13, R13, #4 ; increment the stack pointer");

            write("; Left Operand");
            write("LDR R1, [R13] ; Get the value of left operand");
            write("ADD R13, R13, #4 ; increment the stack pointer");


            switch (node.getValue().toUpperCase()) {
                case "+":
                    write("; Perform the addition");
                    write("ADD R0, R1, R2");
                    write("SUB R13, R13, #4"); // On décale le pointeur de pile
                    write("STR R0, [R13]"); // On stocke le résultat de la comparaison en pile
                    return;
                case "-":
                    write("; Perform the substraction");
                    write("SUB R0, R1, R2");
                    write("SUB R13, R13, #4"); // On décale le pointeur de pile
                    write("STR R0, [R13]"); // On stocke le résultat de la comparaison en pile
                    return;
                case "*":
                    write("SUB R13, R13, #4 ; leave value for return");
                    write("SUB R13, R13, #4; left operand"); // On décale le pointeur de pile
                    write("STR R1, [R13] ; left operand"); // On stocke l'op gauche de la comparaison en pile
                    write("SUB R13, R13, #4; right operand"); // On décale le pointeur de pile
                    write("STR R2, [R13] ; right operand"); // On stocke l'op droite de la comparaison en pile

                    write("; Perform the multiplication");
                    write("BL mul");
                    write("ADD R13, R13, #8 ; remove param function from stack");

                    return;
                case "/":
                    write("SUB R13, R13, #8 ; leave value for return");
                    write("SUB R13, R13, #4; left operand"); // On décale le pointeur de pile
                    write("STR R1, [R13] ; left operand"); // On stocke l'op gauche de la comparaison en pile
                    write("SUB R13, R13, #4; right operand"); // On décale le pointeur de pile
                    write("STR R2, [R13] ; right operand"); // On stocke l'op droite de la comparaison en pile


                    write("; Perform the division");
                    write("BL div");
                    write("ADD R13, R13, #8 ; Increment the stack pointer");
                    write("ADD R13, R13, #4 ; increment the stack pointer");
                    return;
                case "REM":
                    write("SUB R13, R13, #8 ; leave value for return");
                    write("SUB R13, R13, #4; left operand"); // On décale le pointeur de pile
                    write("STR R1, [R13] ; left operand"); // On stocke l'op gauche de la comparaison en pile
                    write("SUB R13, R13, #4; right operand"); // On décale le pointeur de pile
                    write("STR R2, [R13] ; right operand"); // On stocke l'op droite de la comparaison en pile


                    write("; Perform the modulo");
                    write("BL div");
                    write("ADD R13, R13, #8 ; Increment the stack pointer");
                    write("LDR R0, [R13] ; Get the value of resultat");
                    write("ADD R13, R13, #4 ; increment the stack pointer");
                    write("STR R0, [R13]"); // On stocke le résultat de la comparaison en pile
                    return;
                default:
            }
        }
        if (node.getType() == NodeType.CALL) {
            generateCallFunctionProcedure(node);
            mettre_valeur_retour_en_registre_apres_appel("r0", node.getChildren().get(0).getValue(), node);
            write("SUB R13, R13, #4");
            write("STR R0, [R13]");
        } else if (node.getToken().getType().equals(TokenType.NUMBER)) {
            write("MOV R0, #" + node.getValue() + " ; Load the value of the number: " + node.getValue());
            write("SUB R13, R13, #4");
            write("STR R0, [R13]");
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
        write("LDR R0, [R13] ; Get the boolean value");
        write("ADD R13, R13, #4 ; increment the stack pointer");
        write("CMP R0, #0");

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
        int if_counter_tmp = ifCounter;
        ifCounter++;

        List<Node> children = node.getChildren();
        Node condition = children.get(0); // 1 : condition
        Node body = children.get(1); // 2 : body

        List<Node> elseif = new ArrayList<>(); // 3 : elseif et le dernier body est le else
        Node elsenode = null;

        if (children.size() > 2) { // build list of elseif and else
            for (int i = 2; i < children.size(); i++) {
                if (children.get(i).getType() == NodeType.ELSIF) {
                    elseif.add(children.get(i));
                } else if (children.get(i).getType() == NodeType.BODY) {
                    elsenode = children.get(i);
                }
            }
        }

        write("IF" + if_counter_tmp); // first if
        incrementTabulation();
        generateBoolean(condition);

        // evaluate the condition
        write("LDMFD   r13!, {r0}");
        write("CMP r0, #0");
        if (!elseif.isEmpty()) {
            write("BEQ " + "ELSIF" + if_counter_tmp + "0");
            incrementTabulation();
            generateCode(body);
            decrementTabulation();
            write("B " + "EndIf" + if_counter_tmp);
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
            for (int i = 0; i < elseif.size() - 1; i++) {
                Node elseifnode = elseif.get(i);
                Node elseifcondition = elseifnode.getChildren().get(0);
                Node elseifbody = elseifnode.getChildren().get(1);
                write("ELSIF" + if_counter_tmp + i);
                incrementTabulation();
                generateBoolean(elseifcondition);
                write("LDMFD   r13!, {r0}");
                write("CMP r0, #0");
                write("BEQ " + "ELSIF" + if_counter_tmp + (i + 1));
                generateCode(elseifbody);
                write("B " + "EndIf" + if_counter_tmp);
                decrementTabulation();
            }
            Node elseifnode = elseif.get(elseif.size() - 1);
            Node elseifcondition = elseifnode.getChildren().get(0);
            Node elseifbody = elseifnode.getChildren().get(1);
            write("ELSIF" + if_counter_tmp + (elseif.size() - 1));
            generateBoolean(elseifcondition);
            write("LDMFD   r13!, {r0}");
            write("CMP r0, #0");
            if (elsenode != null) {
                write("BEQ " + "ELSE" + if_counter_tmp);
                generateCode(elseifbody);
                write("B " + "EndIf" + if_counter_tmp);
                write("ELSE" + if_counter_tmp);
                generateCode(elsenode);
                write("B " + "EndIf" + if_counter_tmp);
            } else {
                write("BEQ " + "EndIf" + if_counter_tmp);
                generateCode(elseifbody);
                write("B " + "EndIf" + if_counter_tmp);
            }
        } else {
            if (elsenode != null) {
                write("BEQ " + "Else" + if_counter_tmp);
                generateCode(body);
                write("B " + "EndIf" + if_counter_tmp);
                write("Else" + if_counter_tmp);
                generateCode(elsenode);
                write("B " + "EndIf" + if_counter_tmp);
            } else {
                write("BEQ " + "EndIf" + if_counter_tmp);
                generateCode(body);
                write("B " + "EndIf" + if_counter_tmp);
            }
        }
        write("EndIf" + if_counter_tmp);
    }

    private void generateFor(Node node) throws IOException {
        int forCounter_tmp = forCounter;
        forCounter++;
        //Empiler la borne inf, puis la borne sup, puis l'incrément
        List<Node> children = node.getChildren();
        String variable_compteur = children.get(0).getValue(); // Nom de la variable compteur
        String direction;
        Node borne_inf;
        Node borne_sup;
        Node body;
        if (children.get(2).getValue().equalsIgnoreCase("reverse")) {
            direction = "reverse";
            borne_inf = children.get(3);
            borne_sup = children.get(4);
            body = children.get(5);
        } else {
            direction = "in";
            borne_inf = children.get(2);
            borne_sup = children.get(3);
            body = children.get(4);
        }

        //aller chercher la valeur de la borne inf
        String type_borne_inf = type_valeur(borne_inf);
        if (borne_inf.getValue().equalsIgnoreCase(" ")) {
            //c'est une variable donc faut la chercher par la fonction accessVariable
            generateAccessVariable(borne_inf);
            write("LDMFD   r13!, {r0}");
            if (direction.equalsIgnoreCase("reverse")){
                write("SUB r0, r0, #1");
            }
            write("STMFD r13!, {r0} ; empiler la borne inf");
        } else if (type_borne_inf.equalsIgnoreCase("integer")) {
            write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
            write("MOV R0, #" + borne_inf.getValue());
            if (direction.equalsIgnoreCase("reverse")){
                write("SUB r0, r0, #1");
            }
            write("STR r0, [r13] ; Empiler la borne inf");
        }
        String type_borne_sup = type_valeur(borne_sup);
        if (type_borne_sup.equalsIgnoreCase(" ")) {
            //c'est une variable donc faut la chercher par la fonction accessVariable
            generateAccessVariable(borne_sup);
            write("LDMFD   r13!, {r0}");
            if (!direction.equalsIgnoreCase("reverse")) {
                write("ADD r0, r0, #1");
            }
            write("STMFD r13!, {r0} ; Empiler la borne sup");
        } else if (type_borne_sup.equalsIgnoreCase("integer")) {
            write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
            write("MOV R0, #" + borne_sup.getValue());
            if (!direction.equalsIgnoreCase("reverse")){
                write("ADD r0, r0, #1");
            }
            write("STR r0, [r13] ; Empiler la borne sup");
        }
        //initialisation : on met dans la pile : borne inf, borne sup, compteur initialisé à borne inf
        if (direction.equalsIgnoreCase("reverse")) {
            write("LDR r0, [r13] ; Récupérer la borne sup");
            write("STMFD r13!, {r0} ;empiler l'increment qui demarre à la borne sup (reverse)");
        } else {
            write("LDR r0, [r13, #4] ; Récupérer la borne inf");
            write("STMFD r13!, {r0} ;empiler l'incrément qui démarre à la borne inf");
        }
        //Pour le for
        write("FOR" + forCounter_tmp);
        write("LDR r0, [r13] ; Récupérer le compteur");
        if (direction.equalsIgnoreCase("reverse")) {
            write("LDR r1, [r13, #8] ; Récupérer borne inf");
        } else {
            write("LDR r1, [r13, #4] ; Récupérer borne sup");
        }
        write("CMP r0, r1");
        write("BEQ end_for" + forCounter_tmp);
        generateCode(body);
        write("LDR r0, [r13] ; Récupérer le compteur");
        if (direction.equalsIgnoreCase("reverse")) {
            write("SUB r0, r0, #1 ; Décrémenter le compteur");
        } else {
            write("ADD r0, r0, #1 ; Incrémenter le compteur");
        }
        write("STR r0, [r13] ; Sauvegarder le compteur");
        write("B FOR" + forCounter_tmp);
        write("end_for" + forCounter_tmp);
        write("ADD r13,r13,#12"); // Dépiler les bornes et le compteur
    }

    private void generateMultiplyFunction() throws IOException {
        // only multiply two integers
        write("; --- MULTIPLICATION function (to be add at the beginning of the file)" + " ---");
        write("; R0 = result , R1 = left operand, R2 = right operand");
        write("mul"); //multiplication function : to be called with "BL mul"
        incrementTabulation();
        write("STMFD SP!, {r11, r14}");
        write("MOV R11, R13");
        write("LDR R1, [R11, #4*3] ; get the left operand");
        write("LDR R2, [R11, #4*2] ; get the right operand");
        write("MOV R0, #0");
        decrementTabulation();
        write("mul_loop");
        incrementTabulation();
        write("LSRS R2, R2, #1");
        write("ADDCS   R0, R0, R1");
        write("LSL R1, R1, #1");
        write("TST R2, R2");
        write("BNE mul_loop");
        write("STR R0, [R11, #4*4] ; store the result in the stack");
        write("MOV R13, R11 ; restore the stack pointer at the end of the function");
        write("LDMFD SP!, {r11, PC}");
        decrementTabulation();
        write("; --- END MULTIPLICATION function ---");
    }

    private void generateDivideFunction() throws IOException {
        write("; --- DIVISION function (to be add at the beginning of the file)" + " ---");
        write("; R1 = left operand, R2 = right operand");
        write("; at the end : R0 = result, R1 = remainder");
        write("div"); //division function : to be called with "BL mul"
        incrementTabulation();
        write("STMFD SP!, {r11, r14}");
        write("MOV R11, R13 ; save the stack pointer");
        write("LDR R1, [R11, #4*3] ; get the left operand");
        write("LDR R2, [R11, #4*2] ; get the right operand");
        write("MOV R0, #0");
        write("MOV R3, #0");
        write("CMP R1, #0");
        write("RSBLT R1, R1, #0");
        write("EORLT R3, R3, #1");
        write("CMP R2, #0");
        write("RSBLT R2, R2, #0");
        write("EORLT R3, R3, #1");
        write("MOV R4, R2");
        write("MOV R5, #1");
        decrementTabulation();
        write("div_max");
        incrementTabulation();
        write("LSL R4, R4, #1");
        write("LSL R5, R5, #1");
        write("CMP R4, R1");
        write("BLE div_max");
        decrementTabulation();
        write("div_loop");
        incrementTabulation();
        write("LSR R4, R4, #1");
        write("LSR R5, R5, #1");
        write("CMP R4, R1");
        write("BGT div_loop");
        write("ADD R0, R0, R5");
        write("SUB R1, R1, R4");
        write("CMP R1, R2");
        write("BGE div_loop");
        write("CMP R3, #1");
        write("BNE div_exit");
        write("CMP R1, #0");
        write("ADDNE R0, R0, #1");
        write("RSB R0, R0, #0");
        write("RSB R1, R1, #0");
        write("ADDNE R1, R1, R2");
        decrementTabulation();
        write("div_exit");
        incrementTabulation();
        write("STR R0, [R11, #4*5] ; store the result in the stack");
        write("STR R1, [R11, #4*4] ; store the remainder in the stack");
        write("MOV R13, R11 ; restore the stack pointer at the end of the function");
        write("LDMFD SP!, {r11, PC}");
        decrementTabulation();
    }

    private void generateCodeDeclarationVariable(Node node) throws IOException {
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                if (child.getType() == DECL_VAR || child.getType() == AFFECTATION) {
                    generateCode(child);
                }
            }
        }
    }

    private void generateCodeDeclarationFuncOrProc(Node node) throws IOException {
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                if (child.getType() == DECL_PROC || child.getType() == DECL_FUNC) {
                    generateCode(child);
                }
            }
        }
    }

    private void generateDeclFunction(Node node) throws IOException {
        String nom_fonction = node.getChildren().get(0).getValue();
        List<Node> children = node.getChildren();
        if (children.get(2).getType() == NodeType.DECLARATION) {
            Node declaration = children.get(2);
            generateCodeDeclarationFuncOrProc(declaration);
        }
        write(nom_fonction.toUpperCase()); // Label de la fonction en majuscule
        incrementTabulation();
        write("STMFD r13!, {r10, r11, r14} ; Sauvegarde des registres FP et LR en pile");
        write("MOV r11, r13 ; Déplacer le pointeur de pile sur l'environnement de la fonction");
        Tds currentTds = tds;
        while (node.getParent().getType() != NodeType.FILE && node.getParent().getType() != NodeType.DECL_FUNC && node.getParent().getType() != NodeType.DECL_PROC) {
            node = node.getParent();
        }
        if (node.getParent().getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());

        } else if (node.getParent().getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
        }
        Symbol symbol = currentTds.getSymbol(nom_fonction);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol not found in tds : " + nom_fonction);
        }
        if (symbol instanceof FunctionSymbol) {
            int nb_params = ((FunctionSymbol) symbol).getNbParameters();
            for (int i = 0; i < nb_params; i++) {
                write("LDR r0, [r11, #4*" + (nb_params - i + 3) + "] ; Récupérer le paramètre " + (i + 1));
                write("STMFD r13!, {r0} ; Dépiler le paramètre " + (i + 1));
            }
        }
        if (children.get(2).getType() == NodeType.BODY) {
            Node body = children.get(2);
            generateCode(body);
        } else {
            if (children.get(2).getType() == NodeType.DECLARATION) {
                Node declaration = children.get(2);
                generateCodeDeclarationVariable(declaration);
                Node body = children.get(3);
                generateCode(body);
            } else {
                Node body = children.get(2);
                generateCode(body);
            }
        }
        decrementTabulation();
    }

    private void generateDeclProcedure(Node node) throws IOException {
        String nom_procedure = node.getChildren().get(0).getValue();
        List<Node> children = node.getChildren();
        if (children.get(1).getType() == NodeType.DECLARATION) {
            Node declaration = children.get(2);
            generateCodeDeclarationFuncOrProc(declaration);
        }
        write(nom_procedure.toUpperCase()); // Label de la procédure en majuscule
        incrementTabulation();
        write("STMFD r13!, {r10, r11, r14} ; Sauvegarde des registres FP et LR en pile");
        write("MOV r11, r13 ; Déplacer le pointeur de pile sur l'environnement de la procédure");
        Tds currentTds = tds;
        while (node.getParent().getType() != NodeType.FILE && node.getParent().getType() != NodeType.DECL_FUNC && node.getParent().getType() != NodeType.DECL_PROC) {
            node = node.getParent();
        }
        if (node.getParent().getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());

        } else if (node.getParent().getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
        }
        Symbol symbol = currentTds.getSymbol(nom_procedure);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol not found in tds : " + nom_procedure);
        }
        if (symbol instanceof ProcedureSymbol) {
            int nb_params = ((ProcedureSymbol) symbol).getNbParameters();
            for (int i = 0; i < nb_params; i++) {
                write("LDR r0, [r11, #4*" + (nb_params - i + 2) + "] ; Récupérer le paramètre " + (i + 1));
                write("STMFD r13!, {r0} ; Empiler le paramètre " + (i + 1));
            }
        }
        if (children.get(1).getType() == NodeType.BODY) {
            Node body = children.get(1);
            generateCode(body);
        } else {
            if (children.get(1).getType() == NodeType.DECLARATION) {
                Node declaration = children.get(1);
                generateCodeDeclarationVariable(declaration);
                Node body = children.get(2);
                generateCode(body);
            } else {
                Node body = children.get(1);
                generateCode(body);
            }
        }
        write("MOV r13, r11 ; Restaurer le pointeur de pile original");
        write("LDMFD r13!, {r10, r11, PC} ; Restaurer les registres et retourner");
        decrementTabulation();
    }

    /*avant d'appeler une fonction, on regarde le bloc dans lequel est Nx on est, le bloc dans lequel est déclaré la
     fonction Ny, et pour trouver le chainage statique actuel, on va remonter Nx - Ny + 1 chainages au dessus.

    Ensuite pour accéder à une variable qui n'est pas local, on fait pareil, on regarde l'endroit où elle est déclaré,
    on regarde où on est, on remonte Nx - Ny chainage statique de fonctions tel qu'ils ont été calculé au dessus pour
    y arriver.

    Attention pour pas tout casser sur le placement des valeurs de retour et les paramètres, ajouter 1 partout*/

    private void generateCallFunctionProcedure(Node node) throws IOException {
        /* Quand j'appelle une fonction ou une procédure, je dois garder une place pour la valeur de retour si c'est une fonction
        et également sauvegardé les paramètres puis le chainage statique, puis le chainage dynamique, puis l'adresse de retour
        * */
        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});
        List<NodeType> comparator = Arrays.asList(new NodeType[]{NodeType.EQUAL, NodeType.SLASH_EQUAL, NodeType.SUPERIOR, NodeType.SUPERIOR_EQUAL, NodeType.INFERIOR, NodeType.INFERIOR_EQUAL, NodeType.COMPARATOR, NodeType.AND, NodeType.OR});
        List<Node> children = node.getChildren();
        String nom_fonction = node.getChildren().get(0).getValue();
        if (nom_fonction.equalsIgnoreCase("PUT")) {
            generateCodePut(node);
            return;
        }
        for (int i = 1; i < children.size(); i++) {
            String value_type = type_valeur(children.get(i));
            if (value_type.equalsIgnoreCase("integer")) {
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                write("MOV R0, #" + children.get(i).getValue());
                write("STR r0, [r13] ; Empiler le paramètre " + i);
            } else if (value_type.equalsIgnoreCase("character")) {
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                write("MOV r0, #" + (int) children.get(i).getValue().charAt(0) + " ; '" + children.get(i).getValue() + "' en ASCII");
                write("STR r0, [r13] ; Empiler le paramètre " + i);
            } else if (operators.contains(children.get(i).getType())) {
                generateArithmetic(children.get(i));
                write("LDMFD   r13!, {r0}");
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                write("STR r0, [r13] ; Empiler le paramètre \" + i");
            } else if (comparator.contains(children.get(i).getType())) {
                generateBoolean(children.get(i));
                write("LDMFD   r13!, {r0}");
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                write("STR r0, [r13] ; Empiler le paramètre \" + i");
            } else if (children.get(i).getType() == NodeType.CALL) {
                generateCallFunctionProcedure(children.get(i));
                mettre_valeur_retour_en_registre_apres_appel("r0", children.get(i).getChildren().get(0).getValue(), node);
                write("SUB R13, R13, #4");
                write("STR R0, [R13] ; empiler le paramètre");
            } else if (value_type.equalsIgnoreCase(" ")) {
                //c'est une variable donc faut la chercher par la fonction accessVariable
                generateAccessVariable(children.get(i));
                write("LDMFD   r13!, {r0}");
                write("SUB R13, R13, #4 ; Décrémenter le pointeur de pile");
                write("STR r0, [r13] ; Empiler le paramètre " + i);
            }
        }
        Tds currentTds = tds;
        while (node.getParent().getType() != NodeType.FILE && node.getParent().getType() != NodeType.DECL_FUNC && node.getParent().getType() != NodeType.DECL_PROC) {
            node = node.getParent();
        }
        if (node.getParent().getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());

        } else if (node.getParent().getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
        }
        Symbol symbol = currentTds.getSymbol(nom_fonction);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol not found in tds : " + nom_fonction);
        }
        if (symbol instanceof FunctionSymbol) {
            write("SUB r13, r13, #4 ; laisser une place pour la valeur de retour");
        }
        write("; Chainage statique");
        generationChainageStatiqueFonction(node, nom_fonction);
        write("; fin de la mise en place du chainage statique");
        write("BL " + nom_fonction.toUpperCase());
    }

    /*avant d'appeler une fonction, on regarde le bloc dans lequel est Nx on est, le bloc dans lequel est déclaré la
     fonction Ny, et pour trouver le chainage statique actuel, on va remonter Nx - Ny + 1 chainages au dessus.

    Ensuite pour accéder à une variable qui n'est pas local, on fait pareil, on regarde l'endroit où elle est déclaré,
    on regarde où on est, on remonte Nx - Ny chainage statique de fonctions tel qu'ils ont été calculé au dessus pour
    y arriver.

    Attention pour pas tout casser sur le placement des valeurs de retour et les paramètres, ajouter 1 partout*/
    private void generationChainageStatiqueFonction(Node node, String nomFonction) throws IOException {
        int currentImbrication = 0;
        int fonctionImbrication = 0;
        Tds currentTds = tds;

        Node callNode = node;

        //searching for the tds (imbrication number) of the varToAffect
        while (callNode.getParent() != null && callNode.getType() != NodeType.FILE && callNode.getType() != NodeType.DECL_FUNC && callNode.getType() != NodeType.DECL_PROC) {
            if (callNode.getParent() != null) callNode = callNode.getParent();
            if (callNode.getParent() == null) break;
        }

        if (callNode.getType() != null && callNode.getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(callNode.firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());
            currentImbrication = currentTds.getImbrication();

        } else if (callNode.getType() != null && callNode.getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(callNode.firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
            currentImbrication = currentTds.getImbrication();
        }

        Symbol symbol = tds.getSymbolAbsolu(nomFonction);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol not found in tds : " + nomFonction);
        }
        Tds fonctionTds = currentTds.getTDSfromSymbol(nomFonction);
        fonctionImbrication = fonctionTds.getImbrication();
        int shift = 1;
        if (callNode.getType() == NodeType.FILE) {
            shift = 0;
        }
        //sachant que techniquement le chainage statique se trouve en r10
        write("MOV R0, #" + (currentImbrication - fonctionImbrication) + " ; nombre de chainage à remonter");
        write("MOV R10, R11 ;");
        write("chainageStatiqueLoop" + chainageStatiqueLoopCounter);
        write("CMP R0, #0 ;");
        write("BEQ chainageStatiqueEnd" + chainageStatiqueLoopCounter + " ;");
        write("LDR R10, [R10] ; Load the previous static chain");
        write("SUBS R0, R0, #1 ; Decrement the imbrication number");
        write("BNE chainageStatiqueLoop" + chainageStatiqueLoopCounter + " ; Continue until the imbrication number is reached");
        write("chainageStatiqueEnd" + chainageStatiqueLoopCounter);
        chainageStatiqueLoopCounter++;
    }


    private void generateDeclVar(Node node) throws IOException {
        String nom_variable = node.getChildren().get(0).getValue();
        Tds currentTds = tds;
        Node Decl = node;
        while (Decl.getParent() != null && Decl.getType() != NodeType.FILE && Decl.getType() != NodeType.DECL_FUNC && Decl.getType() != NodeType.DECL_PROC) {
            if (Decl.getParent() != null) Decl = Decl.getParent();
            if (Decl.getParent() == null) break;
        }

        if (Decl.getType() != null && Decl.getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(Decl.firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());

        } else if (Decl.getType() != null && Decl.getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(Decl.firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
        }
        Symbol symbol = currentTds.getSymbol(nom_variable);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol not found in tds :###8 " + nom_variable);
        }
        if (symbol instanceof StructureSymbol) {
            StructureSymbol structureSymbol = (StructureSymbol) symbol;
            int size = structureSymbol.getFields().size();
            write("; --- DECLARATION of variable " + nom_variable + " ---");
            write(nom_variable.toUpperCase() + structureCounter + " FILL 0x" + Integer.toHexString(size * 4) + " ; place pour réserver le record");
            declarer_structure(structureSymbol);
            write("SUB R13, R13, #4 ; place dans la pile pour la variable " + nom_variable);
            write("LDR r0, =" + nom_variable.toUpperCase() + structureCounter + " ; Load the address of the structure");
            write("STR r0, [r13]");
            write("; --- END DECLARATION of variable " + nom_variable + " ---");
            structureCounter++;
        } else {
            write("; --- DECLARATION of variable " + nom_variable + " ---");
            write("SUB R13, R13, #4 ; place dans la pile pour la variable " + nom_variable);
            write("; --- END DECLARATION of variable " + nom_variable + " ---");
        }

        if (node.getParent().getType() == NodeType.AFFECTATION) {
            System.out.println("On est pas censé passer par là sur le papier");
            // case declaration with affectation
            String valeur_affectation = node.getParent().getChild(1).getValue();
            int int_affectation = Integer.parseInt(valeur_affectation);
            write("; --- AFFECTATION of variable " + node.getChild(0).getValue() + " ---");
            write("LDR R7, =" + int_affectation + " ; LDR au lieu de MOV car MOV ne permet pas la gestion des nombres de plus de 8 bits");
            write("MOV R8, R13 ; R8 := @x");
            write("STR R7, [R8]" + " ; variable := " + int_affectation);
            write("; --- END AFFECTATION of variable " + node.getChild(0).getValue() + " ---");
        }
    }

    private void generateAffectationVar(Node node) throws IOException {
        // part non local variable to affect
        Node varToAffect = node.getChild(0);
        int currentImbrication = 0;
        int varImbrication = 0;
        Tds varTds = null;
        Tds currentTds = tds;

        //searching for the tds (imbrication number) of the varToAffect
        while (varToAffect.getParent() != null && varToAffect.getType() != NodeType.FILE && varToAffect.getType() != NodeType.DECL_FUNC && varToAffect.getType() != NodeType.DECL_PROC) {
            if (varToAffect.getParent() != null) varToAffect = varToAffect.getParent();
            if (varToAffect.getParent() == null) break;
        }

        if (varToAffect.getType() == null && varToAffect.getType() == NodeType.FILE) {
            varToAffect = varToAffect.getParent();
        }

        if (varToAffect.getType() != null && varToAffect.getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(varToAffect.firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());
            currentImbrication = currentTds.getImbrication();

        } else if (varToAffect.getType() != null && varToAffect.getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(varToAffect.firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
            currentImbrication = currentTds.getImbrication();
        }

        //searching for the imbrication number of the declaration of the variable to access

//        if(varToAffect.getType() == FILE){
//            varImbrication = 0;
//        } else {


        Symbol varSymbol;
        if (node.firstChild().getType() != DECL_VAR) {
            if (node.firstChild().getType() != POINT) {
                varSymbol = currentTds.getSymbol(node.firstChild().getValue());
                if (varSymbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds ###9 : " + node.firstChild().getValue() + " of type : " + node.firstChild().getType());
                }
                varTds = currentTds.getTDSfromSymbol(varSymbol.getName());
                varImbrication = varTds.getImbrication();
            } else {
                Node nodeRecord = node.firstChild();
                while (nodeRecord.getType() == POINT) {
                    nodeRecord = nodeRecord.firstChild();
                }
                String nomStructure = nodeRecord.getValue();
                varSymbol = currentTds.getSymbol(nomStructure);
                if (varSymbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###10 " + node.firstChild().getType());
                }
                varTds = currentTds.getTDSfromSymbol(varSymbol.getName());
                varImbrication = varTds.getImbrication();
            }
        } else {
            varSymbol = currentTds.getSymbol(node.firstChild().getChildren().get(0).getValue());
            if (varSymbol == null) {
                throw new IllegalArgumentException("Symbol not found in tds :###5 " + node.firstChild().getType());
            }
            varTds = currentTds.getTDSfromSymbol(varSymbol.getName());
            varImbrication = varTds.getImbrication();
        }
//        }

        // case : affectation of an integer
        if (node.firstChild().getType() != DECL_VAR) {
            // case : affectation of a local variable not in a declaration
            if (node.getChildren().get(0).getType() != POINT) {
                Symbol symbol = currentTds.getSymbol(node.getChild(0).getValue());
                if (symbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###6 " + node.getChild(0).getChild(0).getValue());
                } else {
                    if (varImbrication == currentImbrication) {
                        //local variable case
                        int depl = symbol.getDeplacement();
                        write("; --- AFFECTATION of variable " + symbol.getName() + " ---");
                        generateArithmetic(node.getChild(1));
                        write("LDR R7, [R13] ; Get the value of the result of generateArithmetic");
                        write("ADD R13, R13, #4 ; Increment the stack pointer for deletion of the result of generateArithmetic");
                        write("STR R7, [R11, #" + (-4 - depl) + "]" + " ; variable := " + node.getChild(1).getValue());
                        write("; --- END AFFECTATION of variable " + symbol.getName() + " ---");
                    } else {
                        //non local variable case
                        write("; --- NON LOCAL VARIABLE AFFECTATION ---");
                        incrementTabulation();
                        write("MOV R1, #" + (currentImbrication - varImbrication) + " ; Move to R1 the imbrication number of the variable to access");
                        write("MOV R10, R11 ; Save the current BP");
                        decrementTabulation();
                        write("nonLocalAccessAffectationLoop" + nonLocalAccessAffectationLoopCounter);
                        incrementTabulation();
                        
                        write("LDR R10, [R10] ; Load the previous static chain");
                        write("SUBS R1, R1, #1 ; Decrement the imbrication number");
                        write("BNE nonLocalAccessAffectationLoop" + nonLocalAccessAffectationLoopCounter + " ; Continue until the imbrication number is reached");
                        generateArithmetic(node.getChild(1));
                        write("LDR R7, [R13] ; Get the value of the result of generateArithmetic");
                        write("ADD R13, R13, #4 ; Increment the stack pointer for deletion of the result of generateArithmetic");
                        write("STR R7, [R10, #" + (varSymbol.getDeplacement() - 4) + "] ; variable := " + node.getChild(1).getValue());
                        write("; --- END NON LOCAL VARIABLE AFFECTATION ---");
                        nonLocalAccessAffectationLoopCounter++;
                    }
                }
            } else {
                Node nodeRecord = node.getChildren().get(0);
                String nomStructure = nodeRecord.getChildren().get(0).getValue();
                List<Integer> deplacements = getListeDeplacement(nodeRecord, currentTds);
                //Une fois que tout cela est fini, on a la liste des déplacements à faire pour accéder à la variable
                if (varImbrication == currentImbrication) {
                    //local variable case
                    int depl = varSymbol.getDeplacement();
                    write("; --- AFFECTATION of variable " + varSymbol.getName() + " ---");
                    generateArithmetic(node.getChild(1));
                    write("LDR R7, [R13] ; Get the value of the result of generateArithmetic");
                    write("ADD R13, R13, #4 ; Increment the stack pointer for deletion of the result of generateArithmetic");
                    write("LDR R0, [R11, #" + (-4 - depl) + "]" + " ; variable := " + node.getChild(1).getValue());
                    for (int i = 1; i < deplacements.size() - 1; i++) {
                        write("LDR R0, [R0, #" + (deplacements.get(i)) * 4 + "] ; Load the value of the field " + i);
                    }
                    write("STR R7, [R0, #" + (deplacements.get(deplacements.size() - 1)) * 4 + "] ; variable := " + node.getChild(1).getValue());
                    write("; --- END AFFECTATION of variable " + varSymbol.getName() + " ---");
                } else {
                    //non local variable case
                    int depl = varSymbol.getDeplacement();
                    write("; --- NON LOCAL VARIABLE AFFECTATION ---");
                    incrementTabulation();
                    write("MOV R1, #" + (currentImbrication - varImbrication) + " ; Move to R1 the imbrication number of the variable to access");
                    write("MOV R10, R11 ; Save the current BP");
                    decrementTabulation();
                    write("nonLocalAccessAffectationLoop" + nonLocalAccessAffectationLoopCounter);
                    incrementTabulation();
                    
                    write("LDR R10, [R10] ; Load the previous static chain");
                    write("SUBS R1, R1, #1 ; Decrement the imbrication number");
                    write("BNE nonLocalAccessAffectationLoop" + nonLocalAccessAffectationLoopCounter + " ; Continue until the imbrication number is reached");
                    generateArithmetic(node.getChild(1));
                    write("LDR R7, [R13] ; Get the value of the result of generateArithmetic");
                    write("ADD R13, R13, #4 ; Increment the stack pointer for deletion of the result of generateArithmetic");
                    write("LDR R0, [R10, #" + (-4 - depl) + "]" + " ; variable := " + node.getChild(1).getValue());
                    for (int i = 1; i < deplacements.size() - 1; i++) {
                        write("LDR R0, [R0, #" + (deplacements.get(i)) * 4 + "] ; Load the value of the field " + i);
                    }
                    write("STR R7, [R0, #" + (deplacements.get(deplacements.size() - 1)) * 4 + "] ; variable := " + node.getChild(1).getValue());
                    write("; --- END NON LOCAL VARIABLE AFFECTATION ---");
                    nonLocalAccessAffectationLoopCounter++;
                }

            }
        } else {
            // On est dans le cas d'une affectation et déclaration en même temps : on a même pas besoin de chercher la variable
            String nom_variable = node.getChildren().get(0).getChildren().get(0).getValue();
            Symbol symbol = currentTds.getSymbol(nom_variable);
            if (symbol == null) {
                throw new IllegalArgumentException("Symbol not found in tds :###8 " + nom_variable);
            }
            if (symbol instanceof StructureSymbol) {
                StructureSymbol structureSymbol = (StructureSymbol) symbol;
                int size = structureSymbol.getFields().size();
                write("; --- DECLARATION of variable " + nom_variable + " ---");
                write(nom_variable.toUpperCase() + structureCounter + " FILL 0x" + Integer.toHexString(size * 4) + " ; place pour réserver le record");
                declarer_structure(structureSymbol);
                write("SUB R13, R13, #4 ; place dans la pile pour la variable " + nom_variable);
                write("LDR r0, =" + nom_variable.toUpperCase() + structureCounter + " ; Load the address of the structure");
                write("STR r0, [r13]");
                write("; --- END DECLARATION of variable " + nom_variable + " ---");
                write("; --- AFFECTATION of variable " + nom_variable + " ---");
                //Il faut recopier tous les élements de la structure dans la structure que l'on vient de déclarer
                StructureSymbol structureSymbolToAffect = (StructureSymbol) varTds.getSymbol(nom_variable);
                if (structureSymbolToAffect == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###8 " + nom_variable);
                }
                TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbolAbsolu(structureSymbolToAffect.getType_variable());
                write("STMFD r13!, {r0}");
                generateAccessVariable(node.getChild(1));
                affecter_structure(typeRecordSymbol);
                structureCounter++;
            } else {
                write("; --- DECLARATION of variable " + nom_variable + " ---");
                write("SUB R13, R13, #4 ; place dans la pile pour la variable " + nom_variable);
                write("; --- END DECLARATION of variable " + nom_variable + " ---");
                write("; --- AFFECTATION of variable " + nom_variable + " ---");
                generateArithmetic(node.getChild(1));
                write("LDMFD   r13!, {r0}");
                write("STR r0, [r13]");
            };
        }
    }

    private void generateAccessVariable(Node nodeToAccess) throws IOException {
        //put at the bottom of the stack : the value of the variable to access
        // nodeToAccess = node of the variable to access
        Node node = nodeToAccess;
        int currentImbrication = 0;
        int varImbrication;
        Tds currentTds = tds;
        //searching for the tds (imbrication number) of the nodeToAccess
        while (node.getParent().getType() != NodeType.FILE && node.getParent().getType() != NodeType.DECL_FUNC && node.getParent().getType() != NodeType.DECL_PROC) {
            node = node.getParent();
        }
        if (node.getParent().getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());
            currentImbrication = currentTds.getImbrication();

        } else if (node.getParent().getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
            currentImbrication = currentTds.getImbrication();
        }

        //Pour les variables dans les boucles for, il faut savoir combien on en a croisé

        Node testFor = nodeToAccess;
        int nbFor_a_remonter = 0;
        int numero_For = 0;
        while (testFor.getType() != FILE) {
            testFor = testFor.getParent();
            if (testFor.getType() == FOR) {
                nbFor_a_remonter++;
                if (testFor.getChildren().get(0).getValue().equalsIgnoreCase(nodeToAccess.getValue())) {
                    numero_For = nbFor_a_remonter;
                }
            }
        }
        if (numero_For != 0) {
            int taille_TDS = currentTds.getNbVariables();
            write("LDR r0, [r11, #-" + (taille_TDS + (nbFor_a_remonter - numero_For + 1)*3)*4 + "]");
            write("SUB R13, R13, #4 ; Decrement the stack pointer");
            write("STR r0, [R13] ; Store the value in the stack");
            return;
        }

        if (nodeToAccess.getType() != NodeType.POINT) {
            //searching for the imbrication number of the declaration of the variable to access
            Symbol varSymbol = currentTds.getSymbol(nodeToAccess.getValue());
            if (varSymbol == null) {
                throw new RuntimeException("var symbol not found: " + nodeToAccess.getValue());
            }
            Tds varTds = currentTds.getTDSfromSymbol(varSymbol.getName());
            varImbrication = varTds.getImbrication();

            if (currentImbrication - varImbrication > 0) {
                write("; --- NON LOCAL VARIABLE ACCESS ---");
                incrementTabulation();
                write("MOV R1, #" + (currentImbrication - varImbrication) + " ; Move to R1 the imbrication number of the variable to access");
                write("MOV R10, R11 ; Save the current BP");
                decrementTabulation();
                write("nonLocalAccessLoop" + nonLocalAccessLoopCounter);
                incrementTabulation();
                
                write("LDR R10, [R10] ; Load the previous static chain");
                write("SUBS R1, R1, #1 ; Decrement the imbrication number");
                write("BNE nonLocalAccessLoop" + nonLocalAccessLoopCounter + " ; Continue until the imbrication number is reached");
                write("LDR R0, [R10, #" + (varSymbol.getDeplacement() - 4) + "] ; Load the value of the variable to access" + " " + varSymbol.getName());
                write("SUB R13, R13, #4 ; Decrement the stack pointer");
                write("STR R0, [R13] ; Store the value in the stack");
                decrementTabulation();
                write("; --- END NON LOCAL VARIABLE ACCESS ---");
                nonLocalAccessLoopCounter++;
            } else {
                write("; --- LOCAL VARIABLE ACCESS ---");
                incrementTabulation();
                write("LDR R0, [R11, #-" + (varSymbol.getDeplacement() + 4) + "] ; Load the value of the variable to access" + " " + varSymbol.getName());
                write("SUB R13, R13, #4 ; Decrement the stack pointer");
                write("STR R0, [R13] ; Store the value in the stack");
                decrementTabulation();
                write("; --- END LOCAL VARIABLE ACCESS ---");
            }
        } else {
            List<Integer> deplacements = getListeDeplacement(nodeToAccess, currentTds);
            Node nodeRecord = nodeToAccess;
            String nomStructure;
            int cas_particulier_fonction = 0;
            while (nodeRecord.getChildren().get(0).getType() == NodeType.POINT) {
                nodeRecord = nodeRecord.getChildren().get(0);
            }
            //Une fois qu'on a termniné, on est au dernier point tout en bas deux cas se présentent :  call ou nom de structure
            if (nodeRecord.getChildren().get(0).getType() == CALL) {
                varImbrication = currentImbrication;
                cas_particulier_fonction = 1;
            } else {
                nomStructure = nodeRecord.getChildren().get(0).getValue();
                Symbol symbol = currentTds.getSymbol(nomStructure);
                if (symbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###3 " + nomStructure);
                }
                StructureSymbol varSymbol = (StructureSymbol) symbol;
                Tds varTds = currentTds.getTDSfromSymbol(varSymbol.getName());
                varImbrication = varTds.getImbrication();
            }
            //Une fois que tout cela est fini, on a la liste des déplacements à faire pour accéder à la variable
            if (currentImbrication - varImbrication > 0) {
                write("; --- NON LOCAL VARIABLE ACCESS ---");
                incrementTabulation();
                write("MOV R1, #" + (currentImbrication - varImbrication) + " ; Move to R1 the imbrication number of the variable to access");
                write("MOV R10, R11 ; Save the current BP");
                decrementTabulation();
                write("nonLocalAccessLoop" + nonLocalAccessLoopCounter);
                incrementTabulation();
                write("LDR R10, [R10] ; Load the previous static chain");
                write("SUBS R1, R1, #1 ; Decrement the imbrication number");
                write("BNE nonLocalAccessLoop" + nonLocalAccessLoopCounter + " ; Continue until the imbrication number is reached");
                if (cas_particulier_fonction == 1) {
                    write("LDR R0, [R13] ; Load the value of the variable to access");
                    write("ADD R13, R13, #4 ; retirer la valeur de retour");
                } else {
                    write("LDR R0, [R10, #" + (deplacements.get(0) - 4) + "] ; Load the value of the variable to access");
                }
                for (int i = 1; i < deplacements.size(); i++) {
                    write("MOV r1, r0");
                    write("LDR R0, [R0, #" + (deplacements.get(i)) * 4 + "] ; Load the value of the field " + i);
                }
                write("SUB R13, R13, #4 ; Decrement the stack pointer");
                write("STR R0, [R13] ; Store the value in the stack");
                decrementTabulation();
                write("; --- END NON LOCAL VARIABLE ACCESS ---");
                nonLocalAccessLoopCounter++;
            } else {
                write("; --- LOCAL VARIABLE ACCESS ---");
                incrementTabulation();
                if (cas_particulier_fonction == 1) {
                    write("LDR R0, [R13] ; Load the value of the variable to access");
                    write("ADD R13, R13, #4 ; retirer la valeur de retour");
                } else {
                    write("LDR R0, [R11, #-" + (deplacements.get(0) + 4) + "] ; Load the value of the variable to access");
                }
                //C'est ici qu'il faut faire les déplacements, on sait que dans r0, il y a l'adresse de l'endroit où se trouve la structure
                for (int i = 1; i < deplacements.size(); i++) {
                    write("LDR R0, [R0, #" + (deplacements.get(i)) * 4 + "] ; Load the value of the field " + i);
                }
                write("SUB R13, R13, #4 ; Decrement the stack pointer");
                write("STR R0, [R13] ; Store the value in the stack");
                decrementTabulation();
                write("; --- END LOCAL VARIABLE ACCESS ---");
            }
        }
    }

    private void generateCodePut(Node node) throws IOException {
        Node value = node.getChild(1);
        String type_valeur = type_valeur(value);
        write("; --- PUT generation ---");
        if (value.getValue().equalsIgnoreCase("Character'Val")) {
            generateArithmetic(value.getChildren().get(0));
            write("LDMFD   r13!, {r0}");
            write("MOV R1, #1");
        } else if (value.getToken().getType().equals(TokenType.NUMBER)) {
            write("MOV R0, #" + value.getValue());
            write("MOV R1, #2");
        } else if (value.getToken().getType().equals(TokenType.CHARACTER)) {
            write("MOV R0, #" + (int) value.getValue().charAt(0));
            write("MOV R1, #1");
        } else if (value.getValue().equalsIgnoreCase("true") || value.getValue().equalsIgnoreCase("false")) {
            write("MOV R0, #" + (value.getValue().equalsIgnoreCase("true") ? 1 : 0));
            write("MOV R1, #3");
        } else {
            //c'est une variable donc faut la chercher par la fonction accessVariable
            generateAccessVariable(value);
            write("LDMFD   r13!, {r0}");
            Node testFor = node;
            int nbFor_a_remonter = 0;
            int numero_For = 0;
            while (testFor.getType() != FILE) {
                testFor = testFor.getParent();
                if (testFor.getType() == FOR) {
                    nbFor_a_remonter++;
                    if (testFor.getChildren().get(0).getValue().equalsIgnoreCase(value.getValue())) {
                        numero_For = nbFor_a_remonter;
                    }
                }
            }
            if (numero_For != 0) {
                write("MOV r1, #2");
            } else {
                Tds currentTds = tds;
                while (node.getParent().getType() != NodeType.FILE && node.getParent().getType() != NodeType.DECL_FUNC && node.getParent().getType() != NodeType.DECL_PROC) {
                    node = node.getParent();
                }
                if (node.getParent().getType() == NodeType.DECL_FUNC) {
                    FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
                    currentTds = tds.getTDSfonction(functionSymbol.getName());

                } else if (node.getParent().getType() == NodeType.DECL_PROC) {
                    ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
                    currentTds = tds.getTDSfonction(procedureSymbol.getName());
                }
                Symbol symbol = currentTds.getSymbol(value.getValue());
                if (symbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###2 " + value.getValue());
                }
                VariableSymbol variableSymbol = (VariableSymbol) symbol;
                if (variableSymbol.getType_variable().equalsIgnoreCase("integer")) {
                    write("MOV R1, #2");
                } else if (variableSymbol.getType_variable().equalsIgnoreCase("Character")) {
                    write("MOV R1, #1");
                } else if (variableSymbol.getType_variable().equalsIgnoreCase("boolean")) {
                    write("MOV R1, #3");
                }
            }
        }
        write("SUB r13, r13, #4");
        write("STR r1, [r13] ; Empiler le type de la valeur à afficher");
        write("SUB r13, r13, #4");
        write("STR r0, [r13] ; Empiler la valeur à afficher");
        write("BL PUT");
        write("ADD r13, r13, #8 ; Dépiler les paramètres de la fonction PUT");
        write("; --- END PUT generation ---");
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
            } else {
                if (valeur.getValue().equalsIgnoreCase("Character'Val")) {
                    return "Character";
                }
                if (valeur.getValue().equalsIgnoreCase("null")) {
                    return "null";
                }
                return " ";
            }
        }
    }

    private void generateDeclPrint() throws IOException {
        // La valeur pointeur vers la sortie standard est dans r12
        write("; --- PRINT function (to be add at the beginning of the file)" + " ---");
        write(" PUT");
        incrementTabulation();
        write("STMFD r13!, {r11, r14}");
        write("MOV r11, r13");
        write("LDR r0, [r11, #8] ; get the value to print");
        write("LDR r1, [r11, #12] ; get the type of the value to print (1 = character, 2 = integer, 3 = boolean)");
        write("STR r1, [r12] ; début de l'affichage du caractère");
        write("ADD r12, r12, #4");
        write("STR r0, [r12] ; caractère à afficher");
        write("ADD r12, r12, #4");
        write("MOV r13, r11 ; restore the stack pointer at the end of the function");
        write("LDMFD r13!, {r11, PC}");
        write("; --- END PRINT function (to be add at the beginning of the file)" + " ---");
    }

    private void mettre_valeur_retour_en_registre_apres_appel(String nom_registre, String nom_fonction, Node node) throws IOException {
        //mettre la valeur de retour dans un registre après un appel de fonction
        Tds currentTds = tds;
        while (node.getParent().getType() != NodeType.FILE && node.getParent().getType() != NodeType.DECL_FUNC && node.getParent().getType() != NodeType.DECL_PROC) {
            node = node.getParent();
        }
        if (node.getParent().getType() == NodeType.DECL_FUNC) {
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(functionSymbol.getName());

        } else if (node.getParent().getType() == NodeType.DECL_PROC) {
            ProcedureSymbol procedureSymbol = (ProcedureSymbol) tds.getSymbolAbsolu(node.getParent().firstChild().getValue());
            currentTds = tds.getTDSfonction(procedureSymbol.getName());
        }
        Symbol symbol = currentTds.getSymbol(nom_fonction);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol not found in tds : " + nom_fonction);
        }
        int nb_parametres = ((FunctionSymbol) symbol).getNbParameters();
        write("LDR " + nom_registre + ", [R13]");
        write("ADD R13, R13, #4 ; depiler la valeur de retour");
        write("ADD R13, R13, #" + (4 * nb_parametres) + " ; depiler les parametres de la fonction");
    }

    private List<Integer> getListeDeplacement(Node point, Tds currentTds) throws IOException {
        List<Integer> deplacements = new ArrayList<>();
        //Attention parce qu'on peut avoir un call aussi qui renvoit une structure : genre coucou().champ
        //mais ce cas ne oeut arriver que pour la récupération de la structure, cet à dire le premier déplacement
        //On commence par chercher la structure
        Node nodeRecord = point;
        String nomStructure;
        while (nodeRecord.getChildren().get(0).getType() == NodeType.POINT) {
            nodeRecord = nodeRecord.getChildren().get(0);
        }
        //Une fois qu'on a termniné, on est au dernier point tout en bas deux cas se présentent :  call ou nom de structure
        if (nodeRecord.getChildren().get(0).getType() == CALL) {
            String nomFonction = nodeRecord.getChildren().get(0).getChildren().get(0).getValue();
            Symbol symbol = currentTds.getSymbol(nomFonction);
            TypeRecordSymbol typeRecordSymbol = null;
            if (symbol == null) {
                throw new IllegalArgumentException("Symbol not found in tds :###7 " + nomFonction);
            }
            if (symbol instanceof FunctionSymbol) {
                FunctionSymbol functionSymbol = (FunctionSymbol) symbol;
                String typeRetour = functionSymbol.getReturnType();
                typeRecordSymbol = (TypeRecordSymbol) currentTds.getSymbol(typeRetour);
            }
            generateCallFunctionProcedure(nodeRecord.getChildren().get(0));
            mettre_valeur_retour_en_registre_apres_appel("r0", nodeRecord.getChildren().get(0).getChildren().get(0).getValue(), nodeRecord.getChildren().get(0));
            write("STMFD r13!, {r0}");
            if (typeRecordSymbol == null) {
                throw new IllegalArgumentException("Symbol not found in tds :###20 " + nodeRecord.getChildren().get(0).getValue());
            }
            deplacements.add(0);
            if (nodeRecord.getParent().getType() != POINT) {
                String nomChamp = nodeRecord.getChildren().get(1).getValue();
                int numeroChamp = 0;
                for (VariableSymbol field : typeRecordSymbol.getFields()) {
                    if (field.getName().equalsIgnoreCase(nomChamp)) {
                        break;
                    }
                    numeroChamp++;
                }
                if (numeroChamp == typeRecordSymbol.getFields().size()) {
                    throw new IllegalArgumentException("Field not found in record :####### " + typeRecordSymbol.getName());
                }
                deplacements.add(numeroChamp);
            } else {
                String prochainType = typeRecordSymbol.getName();
                while (nodeRecord.getType() == NodeType.POINT) {
                    String nomChamp = nodeRecord.getChildren().get(1).getValue();
                    TypeRecordSymbol nomType = (TypeRecordSymbol) currentTds.getSymbol(prochainType);
                    int numeroChamp = 0;
                    for (VariableSymbol field : nomType.getFields()) {
                        if (field.getName().equalsIgnoreCase(nodeRecord.getChildren().get(1).getValue())) {
                            prochainType = field.getType_variable();
                            break;
                        }
                        numeroChamp++;
                    }
                    if (numeroChamp == nomType.getFields().size()) {
                        throw new IllegalArgumentException("Field not found in record : ###" + nomType.getName());
                    }
                    deplacements.add(numeroChamp);
                    nodeRecord = nodeRecord.getParent();
                }
            }
        } else {
            nomStructure = nodeRecord.getChildren().get(0).getValue();
            Symbol varSymbol = currentTds.getSymbol(nomStructure);
            StructureSymbol typeRecordSymbol = (StructureSymbol) varSymbol;
            if (varSymbol == null) {
                throw new IllegalArgumentException("Symbol not found in tds :###14 " + nomStructure);
            }
            deplacements.add(varSymbol.getDeplacement());
            if (nodeRecord.getParent().getType() != POINT) {
                String nomChamp = nodeRecord.getChildren().get(1).getValue();
                int numeroChamp = 0;
                for (VariableSymbol field : typeRecordSymbol.getFields()) {
                    if (field.getName().equalsIgnoreCase(nomChamp)) {
                        break;
                    }
                    numeroChamp++;
                }
                if (numeroChamp == typeRecordSymbol.getFields().size()) {
                    throw new IllegalArgumentException("Field not found in record : ##2" + typeRecordSymbol.getName());
                }
                deplacements.add(numeroChamp);
            } else {
                String nomStructureEnglobante = nodeRecord.getChildren().get(0).getValue();
                StructureSymbol structureSymbol = (StructureSymbol) currentTds.getSymbol(nomStructureEnglobante);
                String prochainType = structureSymbol.getType_variable();
                while (nodeRecord.getType() == NodeType.POINT) {
                    String nomChamp = prochainType;
                    TypeRecordSymbol nomTypeRecord = (TypeRecordSymbol) currentTds.getSymbol(prochainType);
                    int numeroChamp = 0;
                    for (VariableSymbol field : nomTypeRecord.getFields()) {
                        if (field.getName().equalsIgnoreCase(nodeRecord.getChildren().get(1).getValue())) {
                            prochainType = field.getType_variable();
                            break;
                        }
                        numeroChamp++;
                    }
                    if (numeroChamp == structureSymbol.getFields().size()) {
                        throw new IllegalArgumentException("Field not found in record : " + structureSymbol.getName());
                    }
                    deplacements.add(numeroChamp);
                    nodeRecord = nodeRecord.getParent();
                }
            }
        }
        return deplacements;
    }

    private void declarer_structure(StructureSymbol structureSymbol) throws IOException {
        int numeroChamp = 0;
        for (VariableSymbol field : structureSymbol.getFields()) {
            numeroChamp++;
            String type_valeur = field.getType_variable();
            if (!(type_valeur.equalsIgnoreCase("integer") || type_valeur.equalsIgnoreCase("boolean") || type_valeur.equalsIgnoreCase("Character"))) {
                TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbolAbsolu(type_valeur);
                if (typeRecordSymbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###4 " + type_valeur);
                }
                write(typeRecordSymbol.getName().toUpperCase() + structureCounter + "_" + nbChampCounter + " FILL 0x" + Integer.toHexString(typeRecordSymbol.getFields().size() * 4) + " ; place pour réserver le record");
                write("LDR r0, =" + typeRecordSymbol.getName().toUpperCase() + structureCounter + "_" + nbChampCounter + " ; Load the address of the structure");
                write("LDR r1, =" + structureSymbol.getName().toUpperCase() + (structureCounter));
                write("STR r0, [r1, #" + (numeroChamp - 1) * 4 + "]");
                nbChampCounter++;
                declarer_structure(typeRecordSymbol);
            }
        }
    }

    private void declarer_structure(TypeRecordSymbol structureSymbol) throws IOException {
        int numeroChamp = 0;
        for (VariableSymbol field : structureSymbol.getFields()) {
            numeroChamp++;
            String type_valeur = field.getType_variable();
            if (!(type_valeur.equalsIgnoreCase("integer") || type_valeur.equalsIgnoreCase("boolean") || type_valeur.equalsIgnoreCase("Character"))) {
                TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbolAbsolu(type_valeur);
                if (typeRecordSymbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###4 " + type_valeur);
                }
                write(typeRecordSymbol.getName().toUpperCase() + structureCounter + "_" + nbChampCounter + " FILL 0x" + Integer.toHexString(typeRecordSymbol.getFields().size() * 4) + " ; place pour réserver le record");
                write("LDR r0, =" + typeRecordSymbol.getName().toUpperCase() + structureCounter + "_" + nbChampCounter + " ; Load the address of the structure");
                write("LDR r1, =" + structureSymbol.getName().toUpperCase() + (structureCounter));
                write("STR r0, [r1, #" + (numeroChamp - 1) * 4 + "]");
                nbChampCounter++;
                declarer_structure(typeRecordSymbol);
            }
        }
    }

    //ce qu'il faudrait c'est que à chaque fois qu'on appelle la fonction : r0 contient l'adresse de la structure declarée
    //et que r1, contienne l'adresse de la structure à affecter.
    private void affecter_structure(TypeRecordSymbol structureSymbol) throws IOException {
        int numeroChamp = 0;
        //On commence par récupérer l'adresse de la structure à affecter
        //On rappelle que dans r0 se trouve l'adresse de la structure déclaré
        //r1 est le truc qu'on veut affecter, on va recopier "chaque valeur de r1 dans r0"
        write("LDR r0, [r13, #4] ; Load the address of the structure declared");
        write("LDR r1, [r13] ; Load the address of the structure to affect");
        for (VariableSymbol field : structureSymbol.getFields()) {
            numeroChamp++;
            String type_valeur = field.getType_variable();
            if (!(type_valeur.equalsIgnoreCase("integer") || type_valeur.equalsIgnoreCase("boolean") || type_valeur.equalsIgnoreCase("Character"))) {
                TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbolAbsolu(type_valeur);
                if (typeRecordSymbol == null) {
                    throw new IllegalArgumentException("Symbol not found in tds :###4 " + type_valeur);
                }
                write("LDR r4, [r0, #" + (numeroChamp - 1) * 4 + "]");
                write("LDR r3, [r1, #" + (numeroChamp - 1) * 4 + "]");
                write("STMFD r13!, {r4}");
                write("STMFD r13!, {r3}");
                affecter_structure(typeRecordSymbol);
                write("ADD r13, r13, #8 ; depiler les valeurs de la structure");
                write("LDR r0, [r13, #4] ; Load the address of the structure declared");
                write("LDR r1, [r13] ; Load the address of the structure to affect");
            } else {
                write("LDR r3, [r1, #" + (numeroChamp - 1) * 4 + "]");
                write("STR r3, [r0, #" + (numeroChamp - 1) * 4 + "] ; affectation du champ " + numeroChamp);
            }
        }
    }
}




