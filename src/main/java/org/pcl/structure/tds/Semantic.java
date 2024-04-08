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

    public static List<String> typeEnCoursDeDeclaration = new ArrayList<>();

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
        FunctionSymbol functionSymbol = new FunctionSymbol(SymbolType.FUNCTION, 0, "Put", "NONE");
        GlobalTds.addSymbol(functionSymbol);
        constructorTDS(root, GlobalTds);
    }

    public void constructorTDS(Node node, Tds tds) {
        //Il doit y avoir un if pour chaque type de l'enum NodeType

        if (node.getChildren() == null) return;
        if (node.getType() == null) return;

        switch (node.getType()) {
            case FILE, DECLARATION, BODY, COMPARATOR, IDENTIFIER, INTEGER, CHARACTER, NEW, CHAR_VAL, RETURN, BEGIN,
                    NOT, IN, INOUT, MODE, VIRGULE, PARAMETERS, MULTIPLE_PARAM, TRUE, FALSE,
                    NULL, INITIALIZATION, FIELD, IS-> fillTDsChild(node, tds);

            case ADDITION, SUBSTRACTION, MULTIPLY, DIVIDE, REM, OR, AND,
                    EQUAL, SLASH_EQUAL, SUPERIOR, SUPERIOR_EQUAL, INFERIOR_EQUAL, INFERIOR -> {
                controleSemantiqueOperateur(node, tds);
                fillTDsChild(node, tds);
            }

            case PROGRAM -> {
            }

            case RECORD -> {
            }

            case ACCESS -> {
            }

            case AFFECTATION -> {
                if (node.getChildren().get(0).getType() == NodeType.DECL_VAR) {
                    List<Node> children = node.getChildren();
                    String nom = children.get(0).getChildren().get(0).getValue();
                    VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, nom , children.get(0).getChildren().get(1).getValue());

                    controleSemantiqueDeclVariable(node.getChildren().get(0), tds);
                    tds.addSymbol(variableSymbol);
                    controleSemantiqueAffectationDecl(node, tds);
                    return;
                }
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
                            for (int i = 0; i < field.getChildren().size() - 1; i++) {
                                String nom_field = field.getChildren().get(i).getValue();
                                String type_field = field.getChildren().get(field.getChildren().size() - 1).getValue();
                                VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, nom_field, type_field);
                                fields.add(variableSymbol);
                            }
                        }
                        controleSemantiqueTypeRecord(nom, fields, tds);
                        TypeRecordSymbol typeRecordSymbol = new TypeRecordSymbol(SymbolType.TYPE_RECORD, 0, nom, fields);
                        tds.addSymbol(typeRecordSymbol);
                    }
                    else if (type.equalsIgnoreCase("ACCESS")) {
                        String type_pointe = children.get(1).getChildren().get(0).getChildren().get(0).getValue();
                        controleSemantiqueTypeAccess(nom, type_pointe, tds);
                        TypeAccessSymbol typeAccessSymbol = new TypeAccessSymbol(SymbolType.TYPE_ACCESS, 0, nom, type_pointe);
                        tds.addSymbol(typeAccessSymbol);

                    }
                }
            }
            case DECL_VAR -> {
                List<Node> children = node.getChildren();
                List<String> noms_variables = new ArrayList<>();
                for (int i = 0; i < children.size() - 1; i++) {
                    noms_variables.add(children.get(i).getValue());
                }
                String type = children.get(children.size() - 1).getValue();
                if (tds.containsSymbol(type, SymbolType.TYPE_RECORD)) {
                    for (String nom : noms_variables) {
                        TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbol(type, SymbolType.TYPE_RECORD);
                        StructureSymbol structureSymbol = new StructureSymbol(SymbolType.STRUCTURE, 0, nom, type, typeRecordSymbol.getFields());
                        tds.addSymbol(structureSymbol);
                    }
                }
                else {
                    for (String nom : noms_variables) {
                        VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, nom, type);
                        controleSemantiqueDeclVariable(node, tds);
                        tds.addSymbol(variableSymbol);
                    }
                }
            }

            case DECL_PROC -> {

                List<Node> children = node.getChildren();
                String nom_procedure = children.get(0).getValue();
                if (tds.containsSymbol(nom_procedure, SymbolType.PROCEDURE)) {
                    SemanticControls.printError("The procedure " + nom_procedure + " is already declared", node);
                    return;
                }
                List<ParamSymbol> paramSymbols = new ArrayList<>();
                if (!children.get(0).getChildren().isEmpty()) {
                    List<Node> param = new ArrayList<>();
                    for (Node child : children.get(0).getChildren()) {
                        param.add(child);
                    }


                    for (Node p : param) {
                        int children_number = p.getChildren().size();
                        if (p.getChildren().get(children_number - 2).getValue().equalsIgnoreCase("in out")) {
                            for (int i = 0; i <= children_number - 3; i++) {
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
                                String type = p.getChildren().get(children_number - 1).getValue();
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
                for (ParamSymbol paramSymbol: paramSymbols) {
                    tds_procedure.addSymbol(paramSymbol);
                    String type_variable = paramSymbol.getType_variable();
                    if (tds.containsSymbol(type_variable, SymbolType.TYPE_RECORD)) {
                        TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbol(type_variable, SymbolType.TYPE_RECORD);
                        StructureSymbol structureSymbol = new StructureSymbol(SymbolType.STRUCTURE, 0, paramSymbol.getName(), type_variable, typeRecordSymbol.getFields());
                        tds_procedure.addSymbol(structureSymbol);
                    }
                    if (tds.containsSymbol(type_variable, SymbolType.TYPE_ACCESS)) {
                        TypeAccessSymbol typeAccessSymbol = (TypeAccessSymbol) tds.getSymbol(type_variable, SymbolType.TYPE_ACCESS);
                        String type_pointe = typeAccessSymbol.getTypePointe();
                        if (tds.containsSymbol(type_pointe, SymbolType.TYPE_RECORD)) {
                            TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbol(type_pointe, SymbolType.TYPE_RECORD);
                            StructureSymbol structureSymbol = new StructureSymbol(SymbolType.STRUCTURE, 0, paramSymbol.getName(), type_pointe, typeRecordSymbol.getFields());
                            tds_procedure.addSymbol(structureSymbol);
                        }
                    }
                }

                if (children.get(1).getType() == NodeType.BODY) {
                    Node body = children.get(1);
                    constructorTDS(body, tds_procedure);
                }
                else {
                    if (children.get(1).getType() == NodeType.DECLARATION) {
                        Node body = children.get(2);
                        for (Node declaration : children.get(1).getChildren()) {
                            constructorTDS(declaration, tds_procedure);
                        }
                        constructorTDS(body, tds_procedure);
                    } else {
                        Node body = children.get(1);
                        constructorTDS(body, tds_procedure);
                    }
                }

                controleSemantiqueDeclProcedure(node, tds_procedure);
            }

            case DECL_FUNC -> {
                List<Node> children = node.getChildren();

                String nom_fonction = children.get(0).getValue();
                if (tds.containsSymbol(nom_fonction, SymbolType.FUNCTION)) {
                    SemanticControls.printError("The function " + nom_fonction + " is already declared", node);
                    return;
                }
                String valeur_retour = children.get(1).getChildren().get(0).getValue();
                if (valeur_retour.equalsIgnoreCase("access")) {
                    valeur_retour = children.get(1).getChildren().get(0)
                            .getChildren().get(0).getValue();
                }
                List<ParamSymbol> paramSymbols = new ArrayList<>();
                if (!children.get(0).getChildren().isEmpty()) {
                    List<Node> param = new ArrayList<>();
                    for (Node child : children.get(0).getChildren()) {
                        param.add(child);
                    }

                    for (Node p : param) {
                        int children_number = p.getChildren().size();
                        if (p.getChildren().get(children_number - 2).getValue().equalsIgnoreCase("in out")) {
                            for (int i = 0; i <= children_number - 3; i++) {
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
                for (ParamSymbol paramSymbol: paramSymbols) {
                    tds_function.addSymbol(paramSymbol);
                    String type_variable = paramSymbol.getType_variable();
                    if (tds.containsSymbol(type_variable, SymbolType.TYPE_RECORD)) {
                        TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbol(type_variable, SymbolType.TYPE_RECORD);
                        StructureSymbol structureSymbol = new StructureSymbol(SymbolType.STRUCTURE, 0, paramSymbol.getName(), type_variable, typeRecordSymbol.getFields());
                        tds_function.addSymbol(structureSymbol);
                    }
                    if (tds.containsSymbol(type_variable, SymbolType.TYPE_ACCESS)) {
                        TypeAccessSymbol typeAccessSymbol = (TypeAccessSymbol) tds.getSymbol(type_variable, SymbolType.TYPE_ACCESS);
                        String type_pointe = typeAccessSymbol.getTypePointe();
                        if (tds.containsSymbol(type_pointe, SymbolType.TYPE_RECORD)) {
                            TypeRecordSymbol typeRecordSymbol = (TypeRecordSymbol) tds.getSymbol(type_pointe, SymbolType.TYPE_RECORD);
                            StructureSymbol structureSymbol = new StructureSymbol(SymbolType.STRUCTURE, 0, paramSymbol.getName(), type_pointe, typeRecordSymbol.getFields());
                            tds_function.addSymbol(structureSymbol);
                        }
                    }
                }

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
                        constructorTDS(body, tds_function);
                    } else {
                        Node body = children.get(2);
                        constructorTDS(body, tds_function);
                    }
                }


                controleSemantiqueDeclFonction(node, tds_function);
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
                controleSemantiquePoint(node, tds);
                fillTDsChild(node, tds);
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
                controleSemantiqueIf(node, tds);
            }
            case FOR -> {
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
                controleSemantiqueFor(node, tds_for);
            }
            case WHILE -> {
                List<Node> children = node.getChildren();
                Node condition = children.get(0);
                Node loop = children.get(1);
                constructorTDS(loop, tds);
                controleSemantiqueWhile(node, tds);
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
                if (node.getChildren().get(0).getValue().equalsIgnoreCase("Put")) {
                    return;
                }
                controleSemantiqueAppelFonction(node, tds);
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