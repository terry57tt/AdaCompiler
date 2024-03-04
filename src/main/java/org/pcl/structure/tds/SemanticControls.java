package org.pcl.structure.tds;

import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

/** List all the semantic controls. */
public class SemanticControls {

    private final  static List<String> errors = new ArrayList<>();




    public static void controleSemantiqueFile(Node file){
        test_egalite_nom_debut_fin(file);
    }

    public static void controleSemantiqueDeclVariable(Node decl_var, Tds tds){
        test_double_declaration(decl_var, tds);
    }


    public static void controleSemantiqueFor(Node for_node){
        List<Node> children = for_node.getChildren();
        String variable_compteur = children.get(0).getValue();
        String direction = children.get(1).getValue();
        String borne_inf = children.get(2).getValue();
        String borne_sup = children.get(3).getValue();

        test_borne_suf_inf(borne_inf, borne_sup);

    }

    public static void controleSemantiqueIf(Node if_node, Tds globalTds){
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

        test_condition_booleene(condition, globalTds);

    }


    public static void controleSemantiqueDeclFonction(Node decl_func, Tds tds){
        List<Node> children = decl_func.getChildren();
        Node valeur_retour = children.get(1);
        Node body = children.get(2);

        test_return_present(body);
        test_existence_type(valeur_retour.getValue(), tds);
        test_double_declaration(decl_func, tds);
    }

    public static void controleSemantiqueDeclProcedure(Node decl_proc, Tds tds){
        List<Node> children = decl_proc.getChildren();
        Node body = children.get(1);

        test_double_declaration(decl_proc, tds);
    }

    public static void controleSemantiqueAffectation(Node affectation, Tds tds){
        List<Node> children = affectation.getChildren();
        Node variable = children.get(0);
        Node valeur = children.get(1);
        Symbol symbol = tds.getSymbol(variable.getValue(), SymbolType.VARIABLE);
        if (symbol == null){
            System.out.println("La variable " + variable.getValue() + " n'a pas été déclaré");
        }
        else {
            String type_valeur = type_valeur(valeur, tds);
            if (!((VariableSymbol) symbol).getType_variable().equalsIgnoreCase(type_valeur)){
                System.out.println("La valeur affecté à " + variable.getValue() + " n'est pas du bon type");
            }
        }
    }





    // Utility function

    private static void test_egalite_nom_debut_fin(Node file){
        List<Node> children = file.getChildren();
        int nombre_enfants = children.size();
        String nom_debut = children.get(0).getValue();
        String nom_fin = children.get(nombre_enfants - 1).getValue();
        if (!nom_debut.equals(nom_fin)){
            System.out.println("Les noms donnée au début et à la fin du programme ne sont pas les mêmes : " + nom_debut + " et " + nom_fin);
        }
    }

    private static void test_double_declaration(Node node, Tds tds){
        boolean a = tds.containsSymbol(node.getValue());
        if (a){
            System.out.println("Double déclaration de " + node.getValue());
        }
    }

    private static void test_return_present(Node node){
        List<Node> children = node.getChildren();
        for (Node child : children) {
            if (child.getType() == NodeType.RETURN){
                return;
            }
        }

        System.out.println("Il n'y a pas de return dans le body de la fonction");
    }

    private static void test_borne_suf_inf(String borne_inf, String borne_sup){
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

    private static void test_existence_type(String type, Tds tds){
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

    private static void test_condition_booleene(Node condition, Tds tds){
        //On a une condition booléenne si on voit un opérateur de comparaison ou un opérateur logique (mais à ce moment là, on a déjà vérifié que les deux opérandes étaient des booléens)

        if (condition.getValue().equalsIgnoreCase("True") || condition.getValue().equalsIgnoreCase("False")){
            return;
        }

        if (condition.getValue().equalsIgnoreCase("AND") || condition.getValue().equalsIgnoreCase("OR")){
            List<Node> children = condition.getChildren();
            for (Node child : children) {
                test_condition_booleene(child, tds);
            }
        }
        else if (condition.getValue().equalsIgnoreCase("NOT")){
            List<Node> children = condition.getChildren();
            for (Node child : children) {
                test_condition_booleene(child, tds);
            }
        }
        else if (condition.getValue().equalsIgnoreCase("<=") || condition.getValue().equalsIgnoreCase(">=") || condition.getValue().equalsIgnoreCase("=") || condition.getValue().equalsIgnoreCase("<") || condition.getValue().equalsIgnoreCase(">") || condition.getValue().equalsIgnoreCase("!=") || condition.getValue().equalsIgnoreCase("/=")){
            List<Node> children = condition.getChildren();
            Node left = children.get(0);
            Node right = children.get(1);
            if (type_valeur(left, tds) == "integer"){
                return;
            }
            else if (test_expression_arithmetique(right, tds)) {
                return;
            }
            else {
                System.out.println("La condition n'est pas une condition booléenne car les opérandes des deux cotés du comparateur ne sont pas des entiers");
            }
        }
        else {
            System.out.println("La condition n'est pas une condition booléenne");
        }
    }

    private static boolean test_expression_arithmetique(Node node, Tds tds){
        if (node.getType() == NodeType.ADDITION || node.getType() == NodeType.SUBSTRACTION || node.getType() == NodeType.MULTIPLY || node.getType() == NodeType.DIVIDE || node.getType() == NodeType.REM){
            List<Node> children = node.getChildren();
            Node left = children.get(0);
            Node right = children.get(1);
            if (left.getType() == NodeType.ADDITION || left.getType() == NodeType.SUBSTRACTION || left.getType() == NodeType.MULTIPLY || left.getType() == NodeType.DIVIDE || left.getType() == NodeType.REM){
                boolean a = test_expression_arithmetique(left, tds);
                boolean b = test_expression_arithmetique(right, tds);
                if (a==false || b==false){
                    return false;
                }
            }
            else if (right.getType() == NodeType.ADDITION || right.getType() == NodeType.SUBSTRACTION || right.getType() == NodeType.MULTIPLY || right.getType() == NodeType.DIVIDE || right.getType() == NodeType.REM){
                boolean a = test_expression_arithmetique(left, tds);
                boolean b = test_expression_arithmetique(right, tds);
                if (a==false || b==false){
                    return false;
                }
            }
            else {
                String type_left = type_valeur(left, tds);
                String type_right = type_valeur(right, tds);
                if (type_left.equalsIgnoreCase("integer") && type_right.equalsIgnoreCase("integer")){
                    return true;
                }
                else {
                    System.out.println("L'opération n'est pas valide car les opérandes ne sont pas des entiers");
                    return false;
                }
            }
        }
        return false;
    }

    private static String type_valeur(Node valeur, Tds tds){
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



    public static List<String> getErrors() {
        return errors;
    }

    public static void clearErrors() {
        errors.clear();
    }

    private static void printError(String error) {
        System.out.println(error);
        errors.add(error);
    }

}
