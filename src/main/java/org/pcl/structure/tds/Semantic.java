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

import static org.pcl.structure.tds.SemanticControls.controleSemantiqueFile;


public class Semantic {

    private final Tds GlobalTds = new Tds("root");

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

Déclaration de fonction : 
nom de fonction qui a comme enfants des noeuds de type param qui ont chacun comme enfant (un ou plusieurs nom de variable et un type) 
valeur de retour 
body de la fonction
Controles sémantiques : 
Si la valeur de retour n’est pas void, vérifier que le body contient bien un noeud return. En fait il doit toujours y avoir un return. 
Vérifier qu’il n’y a aucune double déclaration
Vérifier que les paramètres ont des types bien défini
Vérifier que la valeur de retour est un type bien défini 

Déclaration de procédure : pareil mais sans la valeur de retour
Attention à vérifier que si il y a un enfant en plus, ce soit bien le nom de la procédure

Body : 
Suite d’instructions 
Controles sémantiques : aucun de spécifique 

For : 
variable compteur 
noeud in (dans le bon sens) ou noeud reverse (va dans l’autre sens)
borne inf
borne sup 
un noeud body qui s’appelle loop
Controles sémantiques : 
Vérifier que la borne sup et la borne inf sont bien défini et que ce sont des entiers (surtout si ce sont des variables)
Normalement la variable compteur n’a pas besoin d’avoir été défini et on sait déjà qu’il s’agit d’un ident donc pas besoin de le vérifier

If : 
Condition booleene
bloc then servant de body 
eventuellement une succession indéfini de elsif 
un else à la fin
Controles sémantiques : 
Vérifier qu’il n’y rien après le else, pas d’autres elsif, et qu’il n’y pas rien d’autres sinon il y a un probleme
Vérifier que le premier paramètre est bien une condition booleene 

Déclaration de variable : 
nom
Type 
Controles sémantiques : 
Vérifier que le nom n’est pas déjà pris (double déclaration)
Vérifier que le type est bien défini

Affectation de variable : 
nom de la variable 
Valeur affecté
Controles sémantiques : 
Attention à ce que la déclaration se fasse bien de gauche à droite et pas le contraire.
vérifier que la valeur affecté correspond au type de déclaration

Opérateur +, *, / :
ca peut etre n’importe quel entier ou opérateur en dehors du =, du AND bien entendu, on ne peut pas avoir 2+3=5=6

Opérateur - : 
peut avoir un ou deux paramètres selon si c’est le moins normal ou le moins unaire mais dans tout les cas, il ne doit y avoir que des entiers

Opérateur de comparaison de booleen : AND, OR : 
expression booleene à gauche et à droite

Opérateur de comparaison d’entier (<=, =) : 
Entier à gauche et droite

Appel de fonction :
nom fonction ou procédure
Succession de param
Controles sémantiques : 
Si la fonction a un type de retour, vérifier que le parent est bien un noeud d’affectation
Vérifier que le nombre de paramètre et le type corresponde à celui qu’on a déclaré dans la TDS 

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

        System.out.println(node.getType() + " " +  node.getValue());
        if (node.getChildren() == null) return;
        if (node.getType() == null) return;

        switch (node.getType()) {
            case FILE, DECLARATION, AFFECTATION, BODY, COMPARATOR, IDENTIFIER, INTEGER, CHARACTER, NEW, CHAR_VAL, RETURN, BEGIN,
                    AND, OR, NOT, IN, INOUT, MODE, ADDITION, SUBSTRACTION, MULTIPLY, REM, DIVIDE,
                    RECORD, ACCESS, VIRGULE, PARAMETERS, MULTIPLE_PARAM, TRUE, FALSE, NULL, INITIALIZATION, FIELD, IS,
                    EQUAL, SLASH_EQUAL, INFERIOR, INFERIOR_EQUAL, SUPERIOR, SUPERIOR_EQUAL -> fillTDsChild(node, tds);

            case PROGRAM -> {
            }

            case TYPE -> {
            }
            case DECL_VAR -> {
                List<Node> children = node.getChildren();
                String nom = children.get(0).getValue();
                String type = children.get(1).getValue();
                VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, nom, type);
                tds.addSymbol(variableSymbol);
            }
            case DECL_PROC -> {
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
                        if (p.getChildren().get(children_number -2).getValue().equalsIgnoreCase("out")) {
                            for (int i = 0; i < children_number - 2; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                String mode = "in out";
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type, mode);
                                paramSymbols.add(paramSymbol);
                            }
                        } else {
                            for (int i = 0; i < children_number; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type);
                                paramSymbols.add(paramSymbol);
                            }
                        }
                        ProcedureSymbol procedureSymbol = new ProcedureSymbol(SymbolType.PROCEDURE, 0, nom_procedure, paramSymbols);
                        tds.addSymbol(procedureSymbol);
                    }
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
                List<Node> children = node.getChildren();
                String nom_fonction = children.get(0).getValue();
                Node valeur_retour = children.get(1);
                Node body = children.get(2);
                if (children.get(0).getChildren().size() != 0) {
                    List<Node> param = new ArrayList<>();
                    for (Node child : children.get(0).getChildren()) {
                        param.add(child);
                    }

                    List<ParamSymbol> paramSymbols = new ArrayList<>();
                    for (Node p : param) {
                        int children_number = p.getChildren().size();
                        if (p.getChildren().get(children_number -2).getValue().equalsIgnoreCase("out")) {
                            for (int i = 0; i < children_number - 2; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                String mode = "in out";
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type, mode);
                                paramSymbols.add(paramSymbol);
                            }
                        } else {
                            for (int i = 0; i < children_number; i++) {
                                String nom = p.getChildren().get(i).getValue();
                                String type = p.getChildren().get(children_number - 1).getValue();
                                ParamSymbol paramSymbol = new ParamSymbol(SymbolType.PARAM, 0, nom, type);
                                paramSymbols.add(paramSymbol);
                            }
                        }
                    }
                    FunctionSymbol functionSymbol = new FunctionSymbol(SymbolType.FUNCTION, 0, nom_fonction, valeur_retour.getValue(), paramSymbols);
                    tds.addSymbol(functionSymbol);
                }
                else {
                    FunctionSymbol functionSymbol = new FunctionSymbol(SymbolType.FUNCTION, 0, nom_fonction, valeur_retour.getValue());
                    tds.addSymbol(functionSymbol);
                }

                Tds tds_function = new Tds(nom_fonction);

                tds.addChild(tds_function);

                constructorTDS(body, tds_function);
            }

            case ELSE -> {
            }

            case THEN -> {
            }
            case NEGATIVE_SIGN -> {
            }
            case POINT -> {
            }
            case IF -> {
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
                List<Node> children = node.getChildren();
                String variable_compteur = children.get(0).getValue();
                String direction = children.get(1).getValue();
                String borne_inf = children.get(2).getValue();
                String borne_sup = children.get(3).getValue();
                Node loop = children.get(4);
                constructorTDS(loop, tds);
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
            }
            case EXPRESSION -> {
            }

        }
    }


    /** fill the tds of the child of the node */
    public void fillTDsChild(Node node, Tds tds){
        List<Node> children = node.getChildren();
        for (Node child : children) {
            constructorTDS(child, tds);
        }
    }


}