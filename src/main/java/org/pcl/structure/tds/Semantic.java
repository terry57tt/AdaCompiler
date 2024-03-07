package org.pcl.structure.tds;
import org.pcl.ColorAnsiCode;
import org.pcl.Token;
//import org.pcl.ig.PCLWindows;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;
import org.pcl.structure.tree.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.pcl.structure.tds.SemanticControls.*;


public class Semantic {

    private final Tds GlobalTds = new Tds("root");

    private List<String> typeEnCoursDeDeclaration = new ArrayList<>();

    /*
     * 
     * TDS : une TDS pour chaque bloc : bloc appel de fonction, bloc IF, bloc FOR, bloc BODY

Fichier (FILE) : 
nom_programme 
déclaration
Body
(nom_programme)
Controles sémantiques : 
Vérifier que le nom donné au début du programme est le même qu’à la fin du programme

Declaration (DECLARATION) : 
nombre d’enfant indéterminée : ce sont successivement des blocs pouvant être de type declaration fonction ou déclaration de procédure, ou déclaration de type 
Controles sémantiques : il y en a pas sur ce noeud

Body : 
Suite d’instructions 
Controles sémantiques : aucun de spécifique 


Affectation de variable : 
nom de la variable 
Valeur affecté
Controles sémantiques : 
Attention à ce que la déclaration se fasse bien de gauche à droite et pas le contraire.
vérifier que la valeur affecté correspond au type de déclaration

* Déclaration de type :
*
*/
    public Semantic(SyntaxTree ast) {
        buildTds(ast);
        controleSemantiqueFile(ast.getRootNode());

    }

    public Tds getGlobalTds() {
        return GlobalTds;
    }

    public void buildTds(SyntaxTree ast) {
        Node root = ast.getRootNode();
        constructorTDS(root, GlobalTds);
    }

    public void constructorTDS(Node node, Tds tds) {
        //Il doit y avoir un if pour chaque type de l'enum NodeType


        if (node.getChildren() == null) return;
        if (node.getType() == null) return;

        switch (node.getType()) {
            case FILE, DECLARATION, BODY, COMPARATOR, IDENTIFIER, INTEGER, CHARACTER, NEW, CHAR_VAL, RETURN, BEGIN,
                    NOT, IN, INOUT, MODE, RECORD, ACCESS, VIRGULE, PARAMETERS, MULTIPLE_PARAM, TRUE, FALSE,
                    NULL, INITIALIZATION, FIELD, IS-> fillTDsChild(node, tds);

            case ADDITION, SUBSTRACTION, MULTIPLY, DIVIDE, REM, OR, AND,
                    EQUAL, SLASH_EQUAL, SUPERIOR, SUPERIOR_EQUAL, INFERIOR_EQUAL, INFERIOR -> {
                fillTDsChild(node, tds);
                controleSemantiqueOperateur(node, tds);
            }

            case PROGRAM -> {
            }

            case AFFECTATION -> {
                controleSemantiqueAffectation(node, tds);
            }

            //Déclaration de type

            case TYPE -> {
                List<Node> children = node.getChildren();
                String nom = children.get(0).getValue();
                if (children.size() == 1){
                    typeEnCoursDeDeclaration.add(nom);
                    return;
                }
                else {
                    String type = children.get(1).getChildren().get(0).getValue();
                    Node type_node = children.get(1).getChildren().get(0);
                    if (type.equalsIgnoreCase("RECORD")) {
                        List<VariableSymbol> fields = new ArrayList<>();
                        for (Node field : type_node.getChildren()) {
                            String nom_field = field.getChildren().get(0).getValue();
                            String type_field = field.getChildren().get(1).getValue();
                            VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, nom_field, type_field);
                            fields.add(variableSymbol);
                        }
                        TypeRecordSymbol typeRecordSymbol = new TypeRecordSymbol(SymbolType.TYPE_RECORD, 0, nom, fields);
                        tds.addSymbol(typeRecordSymbol);
                    }
                    else if (type.equalsIgnoreCase("ACCESS")) {
                        String type_pointe = children.get(1).getChildren().get(0).getChildren().get(0).getValue();
                        TypeAccessSymbol typeAccessSymbol = new TypeAccessSymbol(SymbolType.TYPE_ACCESS, 0, nom, type_pointe);
                        tds.addSymbol(typeAccessSymbol);
                    }
                }
            }
            case DECL_VAR -> {
                controleSemantiqueDeclVariable(node, tds);
                List<Node> children = node.getChildren();
                String nom = children.get(0).getValue();
                String type = children.get(1).getValue();
                VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, nom, type);
                tds.addSymbol(variableSymbol);
            }
            case DECL_PROC -> {
                controleSemantiqueDeclProcedure(node, tds);
                List<Node> children = node.getChildren();
                String nom_procedure = children.get(0).getValue();
                Node body = children.get(1);
                if (children.get(0).getChildren().size() != 0) {
                    List<Node> param = new ArrayList<>();
                    for (Node child : children.get(0).getChildren()) {
                        param.add(child);
                    }

                    List<ParamSymbol> paramSymbols = new ArrayList<>();
                    for (Node p : param) {
                        int children_number = p.getChildren().size();
                        if (p.getChildren().get(children_number - 2).getValue().equalsIgnoreCase("out")) {
                            for (int i = 0; i < children_number - 3; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                String mode = "in out";
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type, mode);
                                paramSymbols.add(paramSymbol);
                            }
                        } else if (p.getChildren().get(children_number - 2).getValue().equalsIgnoreCase("in")) {
                            for (int i = 0; i < children_number - 2; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type);
                                paramSymbols.add(paramSymbol);
                            }
                        }
                        else {
                            for (int i = 0; i < children_number - 1; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number -1).getValue();
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type);
                                paramSymbols.add(paramSymbol);
                            }
                        }
                        }
                        ProcedureSymbol procedureSymbol = new ProcedureSymbol(SymbolType.PROCEDURE, 0, nom_procedure, paramSymbols);
                        tds.addSymbol(procedureSymbol);
                    }
                else {
                    ProcedureSymbol procedureSymbol = new ProcedureSymbol(SymbolType.PROCEDURE, 0, nom_procedure);
                    tds.addSymbol(procedureSymbol);
                }
                Tds tds_procedure = new Tds(nom_procedure);
                tds.addChild(tds_procedure);
                constructorTDS(body, tds_procedure);
            }
            case DECL_FUNC -> {
                controleSemantiqueDeclFonction(node, tds);
                List<Node> children = node.getChildren();
                String nom_fonction = children.get(0).getValue();
                String valeur_retour = children.get(1).getChildren().get(0).getValue();
                if (children.get(0).getChildren().size() != 0) {
                    List<Node> param = new ArrayList<>();
                    for (Node child : children.get(0).getChildren()) {
                        param.add(child);
                    }

                    List<ParamSymbol> paramSymbols = new ArrayList<>();
                    for (Node p : param) {
                        int children_number = p.getChildren().size();
                        if (p.getChildren().get(children_number - 2).getValue().equalsIgnoreCase("out")) {
                            for (int i = 0; i < children_number - 3; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                String mode = "in out";
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type, mode);
                                paramSymbols.add(paramSymbol);
                            }
                        } else if (p.getChildren().get(children_number - 2).getValue().equalsIgnoreCase("in")) {
                            for (int i = 0; i < children_number - 2; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type);
                                paramSymbols.add(paramSymbol);
                            }
                        }
                        else {
                            for (int i = 0; i < children_number - 1; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number -1).getValue();
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type);
                                paramSymbols.add(paramSymbol);
                            }
                        }
                    }
                    FunctionSymbol functionSymbol = new FunctionSymbol(SymbolType.FUNCTION, 0, nom_fonction, valeur_retour, paramSymbols);
                    tds.addSymbol(functionSymbol);
                }
                else {
                    FunctionSymbol functionSymbol = new FunctionSymbol(SymbolType.FUNCTION, 0, nom_fonction, valeur_retour);
                    tds.addSymbol(functionSymbol);
                }

                Tds tds_function = new Tds(nom_fonction);

                tds.addChild(tds_function);

                if (children.get(2).getType() == NodeType.BODY) {
                    Node body = children.get(2);
                    constructorTDS(body, tds_function);
                }
                else {
                    if (children.get(2).getType() == NodeType.DECLARATION) {
                        Node body = children.get(3);
                        for (Node declaration : children.get(2).getChildren()) {
                            constructorTDS(declaration, tds_function);
                        }
                    } else {
                        Node declaration = children.get(2);
                        constructorTDS(declaration, tds_function);
                        Node body = children.get(3);
                        constructorTDS(body, tds_function);
                    }
                }
            }

            case ELSE -> {
                fillTDsChild(node, tds);
            }

            case THEN -> {
                fillTDsChild(node, tds);

            }
            case NEGATIVE_SIGN -> {
                fillTDsChild(node, tds);
            }
            case POINT -> {
                fillTDsChild(node, tds);
            }
            case IF -> {
                controleSemantiqueIf(node, tds);
                List<Node> children = node.getChildren();
                Node condition = children.get(0);
                Node then = children.get(1);
                List<Node> elsif = new ArrayList<>();
                Node else_node = null;
                for (int i = 2; i < children.size(); i++) {
                    if (children.get(i).getType() == NodeType.ELSIF) {
                        elsif.add(children.get(i));
                    }
                    else {
                        else_node = children.get(i);
                    }
                }
                constructorTDS(then, tds);
                for (Node n : elsif) {
                    constructorTDS(n, tds);
                }
                if (else_node != null) {
                    constructorTDS(else_node, tds);
                }
            }
            case FOR -> {
                controleSemantiqueFor(node);
                List<Node> children = node.getChildren();
                String variable_compteur = children.get(0).getValue();
                String direction = children.get(1).getValue();
                String borne_inf = children.get(2).getValue();
                String borne_sup = children.get(3).getValue();
                Node loop = children.get(4);
                Tds tds_for = new Tds("for");
                tds.addChild(tds_for);
                VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, variable_compteur, "INTEGER");
                tds_for.addSymbol(variableSymbol);
                constructorTDS(loop, tds_for);
            }
            case WHILE -> {
                List<Node> children = node.getChildren();
                Node condition = children.get(0);
                Node loop = children.get(1);
                constructorTDS(loop, tds);
            }
            case REVERSE -> {
            }
            case ELSIF -> {
                List<Node> children = node.getChildren();
                Node condition = children.get(0);
                Node then = children.get(1);
                constructorTDS(then, tds);
            }
            case EXPRESSION -> {
            }
            case CALL -> {
                SemanticControls.controleSemantiqueAppelFonction(node, tds);
            }

        }
    }


    /** fill the tds of the child of the node */
    public void fillTDsChild(Node node, Tds tds){
        List<Node> children = node.getChildren();
        if (children == null) return;
        for (Node child : children) {
            constructorTDS(child, tds);
        }
    }


}