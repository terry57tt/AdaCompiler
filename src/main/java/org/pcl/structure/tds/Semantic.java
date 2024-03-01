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


public class Semantic {

    private Tds GlobalTds = new Tds("root");

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

        if (node.getChildren() == null) {
            return;
        }


        if (node.getType() == NodeType.FILE) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child,  tds);
            }
        }

        if (node.getType() == NodeType.DECLARATION) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child,  tds);
            }
        }

        if (node.getType() == NodeType.DECL_FUNC) {
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

        if (node.getType() == NodeType.DECL_PROC) {
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

        if (node.getType() == NodeType.BODY) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.FOR) {
            List<Node> children = node.getChildren();
            String variable_compteur = children.get(0).getValue();
            String direction = children.get(1).getValue();
            String borne_inf = children.get(2).getValue();
            String borne_sup = children.get(3).getValue();
            Node loop = children.get(4);
            constructorTDS(loop, tds);
        }

        if (node.getType() == NodeType.IF) {
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

        if (node.getType() == NodeType.WHILE) {
            List<Node> children = node.getChildren();
            Node condition = children.get(0);
            Node loop = children.get(1);
            constructorTDS(loop, tds);
        }

        if (node.getType() == NodeType.DECL_VAR) {
            List<Node> children = node.getChildren();
            String nom = children.get(0).getValue();
            String type = children.get(1).getValue();
            VariableSymbol variableSymbol = new VariableSymbol(SymbolType.VARIABLE, 0, nom, type);
            tds.addSymbol(variableSymbol);
        }

        if (node.getType() == NodeType.AFFECTATION) { 
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.ADDITION || node.getType() == NodeType.SUBSTRACTION || node.getType() == NodeType.MULTIPLY || node.getType() == NodeType.DIVIDE || node.getType() == NodeType.REM) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.COMPARATOR) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.IDENTIFIER) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.INTEGER) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.CHARACTER) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.TRUE || node.getType() == NodeType.FALSE || node.getType() == NodeType.NULL) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.NEW) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.CHAR_VAL) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.RETURN) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.BEGIN) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.AND || node.getType() == NodeType.OR) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.NOT) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.EQUAL || node.getType() == NodeType.SLASH_EQUAL || node.getType() == NodeType.INFERIOR || node.getType() == NodeType.INFERIOR_EQUAL || node.getType() == NodeType.SUPERIOR || node.getType() == NodeType.SUPERIOR_EQUAL) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.IN) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.INOUT) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.MODE) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.PARAMETERS) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.MULTIPLE_PARAM) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.INITIALIZATION) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.FIELD) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.TYPE) {
        }

        if (node.getType() == NodeType.IS) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.ACCESS) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.RECORD) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }

        if (node.getType() == NodeType.VIRGULE) {
            List<Node> children = node.getChildren();
            for (Node child : children) {
                constructorTDS(child, tds);
            }
        }
    }

    public void controleSemantiqueFile(Node file){
        test_egalite_nom_debut_fin(file);
    }

    public void controleSemantiqueFor(Node for_node){
        List<Node> children = for_node.getChildren();
        String variable_compteur = children.get(0).getValue();
        String direction = children.get(1).getValue();
        String borne_inf = children.get(2).getValue();
        String borne_sup = children.get(3).getValue();

        test_borne_suf_inf(borne_inf, borne_sup);
        
    }

    public void controleSemantiqueIf(Node if_node){
        List<Node> children = if_node.getChildren();
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
                if (i != children.size() - 1) {
                    System.out.println("Il y a quelque chose après le else");
                }
            }
        }

        test_condition_booleene(condition);

    }

    //Controle semantique declaration de variable, fonction, procédure

    public void controleSemantiqueDeclVariable(Node decl_var, Tds tds){
        test_double_declaration(decl_var, tds);
    }

    public void controleSemantiqueDeclFonction(Node decl_func, Tds tds){
        List<Node> children = decl_func.getChildren();
        Node valeur_retour = children.get(1);
        Node body = children.get(2);

        test_return_present(body);
        test_existence_type(valeur_retour.getValue(), tds);
        test_double_declaration(decl_func, tds);
    }

    public void controleSemantiqueDeclProcedure(Node decl_proc, Tds tds){
        List<Node> children = decl_proc.getChildren();
        Node body = children.get(1);

        test_double_declaration(decl_proc, tds);
    }

    public void controleSemantiqueAffectation(Node affectation, Tds tds){
        List<Node> children = affectation.getChildren();
        Node variable = children.get(0);
        Node valeur = children.get(1);
        Symbol symbol = tds.getSymbol(variable.getValue());
        if (symbol == null){
            System.out.println("La variable " + variable.getValue() + " n'a pas été déclaré");
        }
        else {
            String type_valeur = type_valeur(valeur, tds);

        }
    }

    public void test_return_present(Node node){
        List<Node> children = node.getChildren();
        for (Node child : children) {
            if (child.getType() == NodeType.RETURN){
                return;
            }
        }

        System.out.println("Il n'y a pas de return dans le body de la fonction");
    }





    public void test_egalite_nom_debut_fin(Node file){
        List<Node> children = file.getChildren();
        int nombre_enfants = children.size();
        String nom_debut = children.get(0).getValue();
        String nom_fin = children.get(nombre_enfants - 1).getValue();
        if (!nom_debut.equals(nom_fin)){
            System.out.println("Les noms donnée au début et à la fin du programme ne sont pas les mêmes : " + nom_debut + " et " + nom_fin);
        }
    }

    public void test_double_declaration(Node node, Tds tds){
        boolean a = tds.containsSymbol(node.getValue());
        if (a){
            System.out.println("Double déclaration de " + node.getValue());
        }
    }

    public void test_borne_suf_inf(String borne_inf, String borne_sup){
        try {
            int inf = Integer.parseInt(borne_inf);
            int sup = Integer.parseInt(borne_sup);

            if (sup < inf) {
                System.out.println("La borne sup est inférieur à la borne inf");
            }


        } catch (NumberFormatException e) {
            System.out.println("La borne inf ou sup n'est pas un entier");
        }
    }

    public void test_existence_type(String type, Tds tds){
        List<String> typesValide = new ArrayList<>();
        typesValide.add("integer");
        typesValide.add("boolean");
        typesValide.add("char");
        
        for (String t : typesValide) {
            if (type.equalsIgnoreCase(t)){
                return;
            }
        }

        System.out.println("Le type " + type + " n'est pas un type valide");
    }

    public void test_condition_booleene(Node condition){
        //On a une condition booléenne si on voit un opérateur de comparaison ou un opérateur logique (mais à ce moment là, on a déjà vérifié que les deux opérandes étaient des booléens)
        
        if (condition.getValue().equalsIgnoreCase("True") || condition.getValue().equalsIgnoreCase("False")){
            return;
        }

        if (condition.getValue().equalsIgnoreCase("AND") || condition.getValue().equalsIgnoreCase("OR")){
            List<Node> children = condition.getChildren();
            for (Node child : children) {
                test_condition_booleene(child);
            }
        }
        else if (condition.getValue().equalsIgnoreCase("NOT")){
            List<Node> children = condition.getChildren();
            for (Node child : children) {
                test_condition_booleene(child);
            }
        }
        else if (condition.getValue().equalsIgnoreCase("<=") || condition.getValue().equalsIgnoreCase(">=") || condition.getValue().equalsIgnoreCase("=") || condition.getValue().equalsIgnoreCase("<") || condition.getValue().equalsIgnoreCase(">") || condition.getValue().equalsIgnoreCase("!=")){
            List<Node> children = condition.getChildren();
            Node left = children.get(0);
            Node right = children.get(1);
            if (left.getType() == NodeType.INTEGER || right.getType() == NodeType.INTEGER){
                return;
            }
            else {
                System.out.println("La condition n'est pas une condition booléenne car les opérandes ne sont pas des entiers");
            }
        }
        
        else {
            System.out.println("La condition n'est pas une condition booléenne");
        }
    }

    public String type_valeur(Node valeur, Tds tds){
        //Vérifier si valeur.getValue() 
        try {
            int a = Integer.parseInt(valeur.getValue());
            return "integer";
        } catch (Exception e) {
            try {
                boolean b = Boolean.parseBoolean(valeur.getValue());
                return "boolean";
            } catch (Exception e2) {
                if (valeur.getValue().length() == 1){
                    return "char";
                }
                else {
                    // Symbol symbol = tds.getSymbol(valeur.getValue());
                    // if (symbol != null){
                    //     return symbol.getType();
                    // }
                    System.out.println("Le type de " + valeur.getValue() + " n'existe pas");
                    return null;
                }
            }
        }
    }




}