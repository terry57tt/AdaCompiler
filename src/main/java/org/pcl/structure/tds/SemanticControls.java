package org.pcl.structure.tds;

import org.pcl.ColorAnsiCode;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.NodeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** List all the semantic controls. */
public class SemanticControls {

    private static String name_file;

    private final  static List<String> errors = new ArrayList<>();

    public static void printError(String error, Node node) {
        String numberLine;
        if (node.getToken() != null)
             numberLine = node.getToken().getLineNumber() + ": ";
        else
            numberLine = " ";
        System.out.println(name_file + ":" + numberLine + ColorAnsiCode.ANSI_RED + "error: " + ColorAnsiCode.ANSI_RESET + error + "\n");
        errors.add(error);
    }





    public static void controleSemantiqueFile(Node file){
        test_egalite_nom_debut_fin(file);
    }

    /**
     * nom
     * Type
     * Controles sémantiques :
     * Vérifier que le nom n’est pas déjà pris (double déclaration)
     * Vérifier que le type est bien défini
     */
    public static void controleSemantiqueDeclVariable(Node decl_var, Tds tds){
        test_double_declaration(decl_var, tds);
        List<Node> children = decl_var.getChildren();
        test_existence_type(children.get(1).getValue(), tds, children.get(1));
    }


    /**
     * variable compteur
     * noeud in (dans le bon sens) ou noeud reverse (va dans l’autre sens)
     * borne inf
     * borne sup
     * un noeud body qui s’appelle loop
     * Controles sémantiques :
     * Vérifier que la borne sup et la borne inf sont bien défini et que ce sont des entiers (surtout si ce sont des variables)
     * Normalement la variable compteur n’a pas besoin d’avoir été défini et on sait déjà qu’il s’agit d’un ident donc pas besoin de le vérifier
     */
    public static void controleSemantiqueFor(Node for_node, Tds tds){
        List<Node> children = for_node.getChildren();
        String variable_compteur = children.get(0).getValue();
        String direction = children.get(1).getValue();


        test_borne_suf_inf(children.get(2), children.get(3), for_node, tds);

    }

    /**
     * If :
     * Condition booleene
     * bloc then servant de body
     * eventuellement une succession indéfini de elsif
     * un else à la fin
     * Controles sémantiques :
     * Vérifier qu’il n’y rien après le else, pas d’autres elsif, et qu’il n’y pas rien d’autres sinon il y a un probleme
     * Vérifier que le premier paramètre est bien une condition booleene
     */
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
            }
        }

        test_condition_booleene(condition, globalTds);

    }


    /**
     * Teste la sémantique d'une déclaration de fonction
     * nom de fonction qui a comme enfants des noeuds de type param qui ont chacun comme enfant (un ou plusieurs nom de variable et un type)
     * valeur de retour
     * body de la fonction présence d'un return
     * Controles sémantiques :
     * Vérifier qu’il n’y a aucune double déclaration
     * Vérifier que les paramètres ont des types bien défini
     * Vérifier que la valeur de retour est un type bien défini
     */
    public static void controleSemantiqueDeclFonction(Node decl_func, Tds tds){
        List<Node> children = decl_func.getChildren();
        Node valeur_retour = children.get(1);
        Node body = children.get(2);
        if (children.get(2).getType() == NodeType.DECLARATION)
            body = children.get(3);
        valeur_retour = valeur_retour.getChild(0);
        test_return_present(body);
        test_existence_type(valeur_retour.getValue(), tds, valeur_retour);
    }

    /**
     * Appel de fonction :
     * Vérifier que le nombre de paramètre et le type corresponde à celui qu’on a déclaré dans la TDS
     * TODO : TDS renvoie le mauvais nombre de paramètres et ajouter Put() dans la TDS par défaut et ajouter log d'erreur pour tests
     */
    public static void controleSemantiqueAppelFonction(Node call_func, Tds tds) {
        List<Node> children = call_func.getChildren();
        Node call_name = children.get(0);

        int nb_params = children.size() - 1;

        // call do not precise if it's a function or a procedure

        Symbol function_symbol = tds.getSymbol(call_name.getValue(), SymbolType.FUNCTION);
        Symbol procedure_symbol = tds.getSymbol(call_name.getValue(), SymbolType.PROCEDURE);
        boolean is_function = function_symbol != null;
        if (function_symbol == null && procedure_symbol == null){
            printError("The call name " + call_name.getValue() + " has not been declared", call_name);
            return;
        }
        if (!is_function) {
            controleSemantiqueAppelProcedure(call_func, tds);
            return;
        }

        // number of parameters match
        if( nb_params != ((FunctionSymbol) function_symbol).getNbParameters()){
            printError("The number of parameters in the function \""+ call_name.getValue() +"\" doesn't match the number of parameters in the function declaration. Expected " + ((FunctionSymbol) function_symbol).getNbParameters() + " but got " + nb_params, call_name);
        } else {
            for (int i = 1; i < children.size(); i++) {

                String value_type = type_valeur(children.get(i), tds);
                if (value_type.equals(" ")) continue;
                String expected_type;
                expected_type = ((FunctionSymbol) function_symbol).getParameters().get(i - 1).getType_variable();
                if (!value_type.equalsIgnoreCase(expected_type)) {
                    printError("The type of the parameter \"" + children.get(i) + "\" in the call \"" + call_name.getValue() +"\" doesn't match the type of the parameter in the declaration. Expected " + expected_type + " but got " + value_type, call_name);
                }
            }
        }

    }


    /**
     * Déclaration de procédure : pareil mais sans la valeur de retour
     * Attention à vérifier que si il y a un enfant en plus, ce soit bien le nom de la procédure
     * vérifier pas de return
     */
    public static void controleSemantiqueDeclProcedure(Node decl_proc, Tds tds){
        List<Node> children = decl_proc.getChildren();
        Node body = children.get(1);

    }

    /**
     * Appel de procédure : pareil que pour la fonction
     * nombre param match et type (pas de return)
     */
    public static void controleSemantiqueAppelProcedure(Node call_proc, Tds tds){
        int nb_params = call_proc.getChildren().size() - 1;
        // number of parameters match
        Symbol procedure_symbol = tds.getSymbol(call_proc.getChildren().get(0).getValue(), SymbolType.PROCEDURE);
        if( nb_params != ((ProcedureSymbol) procedure_symbol).getNbParameters()){
            printError("The number of parameters in the function \""+ call_proc.getChildren().get(0).getValue() +"\" doesn't match the number of parameters in the function declaration. Expected "
                    + ((ProcedureSymbol) procedure_symbol).getNbParameters() + " but got " + nb_params, call_proc.getChildren().get(0));
        } else {
            for (int i = 1; i < call_proc.getChildren().size(); i++) {

                String value_type = type_valeur(call_proc.getChildren().get(i), tds);
                if (value_type.equals(" ")) continue;
                String expected_type;
                expected_type = ((ProcedureSymbol) procedure_symbol).getParameters().get(i - 1).getType_variable();
                if (!value_type.equalsIgnoreCase(expected_type)) {
                    printError("The type of the parameter \"" + call_proc.getChildren().get(i) + "\" in the call \""
                            + call_proc.getChildren().get(0).getValue() +"\" doesn't match the type of the parameter in the declaration. Expected " + expected_type + " but got " + value_type, call_proc.getChildren().get(0));
                }
            }
        }

    }

    public static void controleSemantiqueAffectation(Node affectation, Tds tds){
        List<Node> children = affectation.getChildren();
        Node variable = children.get(0);
        Node valeur = children.get(1);
        VariableSymbol symbol = (VariableSymbol) tds.getSymbol(variable.getValue(), SymbolType.VARIABLE);

        if (symbol == null){
            printError("The variable " + variable.getValue() + " has not been declared", variable);
        }

        else if (valeur.getType() == NodeType.CALL){
            if (tds.getSymbol(valeur.getChildren().get(0).getValue(), SymbolType.PROCEDURE) != null){
                printError("The procedure " + valeur.getChildren().get(0).getValue() + " can't be used in an affectation", variable);
                return;
            }
            controleSemantiqueAppelFonction(valeur, tds);
            FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbol(valeur.getChildren().get(0).getValue());
            if (!symbol.getType_variable().equalsIgnoreCase(functionSymbol.getReturnType())) {
                printError("Mismatch type for variable " + symbol.getName() + " : " + symbol.getType_variable() + " and " +  functionSymbol.getReturnType() , variable);
            }
            return;
        }

        else if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")
                && type_valeur(valeur, tds).equalsIgnoreCase("operator")) {
            test_expression_arithmetique(valeur, tds);
        } else {
            String type_valeur = type_valeur(valeur, tds);
            if (!((VariableSymbol) symbol).getType_variable().equalsIgnoreCase(type_valeur)){
                    printError("Mismatch type for variable " + variable.getValue() + " : " + ((VariableSymbol) symbol).getType_variable() + " and " + valeur, variable);
            }
        }
    }

    public static void controleSemantiqueAffectationDecl(Node affectation, Tds tds){
        List<Node> children = affectation.getChildren();
        Node variable = children.get(0);
        Node valeur = children.get(1);
        Symbol symbol = tds.getSymbol(variable.getChildren().get(0).getValue(), SymbolType.VARIABLE);
        if (symbol == null){
            printError("The variable " + variable.getValue() + " has not been declared", variable);
        } else if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")
                && type_valeur(valeur, tds).equalsIgnoreCase("operator")) {
            test_expression_arithmetique(valeur, tds);
        } else {
            String type_valeur = type_valeur(valeur, tds);
            if (!((VariableSymbol) symbol).getType_variable().equalsIgnoreCase(type_valeur)){
                printError("Mismatch type for variable " + variable.getValue() + " : " + ((VariableSymbol) symbol).getType_variable() + " and " + valeur, variable);
            }
        }
    }

    /**
     *Opérateur +, *, / :
     * ca peut etre n’importe quel entier ou opérateur en dehors du =, du AND bien entendu, on ne peut pas avoir 2+3=5=6
     *
     * Opérateur - :
     * peut avoir un ou deux paramètres selon si c’est le moins normal ou le moins unaire mais dans tout les cas, il ne doit y avoir que des entiers
     *
     * Opérateur de comparaison de booleen : AND, OR :
     * expression booleene à gauche et à droite
     *
     * Opérateur de comparaison d’entier (<=, =) :
     * Entier à gauche et droite
     */
    public static void controleSemantiqueOperateur(Node operateur, Tds tds){
        switch (operateur.getType()){
            case ADDITION, SUBSTRACTION, MULTIPLY, DIVIDE, REM, EQUAL, SLASH_EQUAL, SUPERIOR, SUPERIOR_EQUAL, INFERIOR_EQUAL, INFERIOR:
                if(!test_expression_arithmetique(operateur, tds)){
                    printError("The operation " + operateur.getValue() + " is not a valid arithmetic expression", operateur);
                }
                break;
            case AND, OR:
                test_condition_booleene(operateur, tds);
                break;
        }
    }

    /**
     * Vérifier condition bien booléenne
     */
    public static void controleSemantiqueWhile(Node while_node, Tds tds){
        Node condition = while_node.getChild(0);
        test_condition_booleene(condition, tds);
    }

    /**
     * Vérifier que la variable a bien été déclaré qu'on y a accès
     */
    public static void controleSemantiqueAccessVariable(Node access_var, Tds tds){
        Symbol symbol = tds.getSymbol(access_var.getValue(), SymbolType.VARIABLE);
        if (symbol == null){
            printError("The variable " + access_var.getValue() + " has not been declared", access_var);
        }
    }

    // Utility function

    private static void test_egalite_nom_debut_fin(Node file){
        List<Node> children = file.getChildren();
        int nombre_enfants = children.size();
        String nom_debut = children.get(0).getValue();
        String nom_fin = children.get(nombre_enfants - 1).getValue();
        if (!nom_debut.equalsIgnoreCase(nom_fin) && !nom_fin.equalsIgnoreCase("body")){
            printError("The file name at the beginning and at the end of the program don't match : " + nom_debut + " != " + nom_fin, children.get(nombre_enfants - 1));
         }
    }

    private static void test_double_declaration(Node node, Tds tds){
        SymbolType type;
        if (node.getType() == NodeType.DECL_VAR){
            type = SymbolType.VARIABLE;
        }
        else if (node.getType() == NodeType.DECL_FUNC){
            type = SymbolType.FUNCTION;
        }
        else if (node.getType() == NodeType.DECL_PROC){
            type = SymbolType.PROCEDURE;
        }
        else {
            return;
        }

        boolean a = tds.containsSymbol(node.getChildren().get(0).getValue(), type);
        if (a){
            printError(node.getValue() + " has already been declared in the current scope", node);
        }
    }

    private static void test_return_present(Node node){
        List<Node> children = node.getChildren();
        for (Node child : children) {
            if (child.getType() == NodeType.RETURN){
                return;
            }
        }
        printError("Missing return statement in the function", node);
    }

    private static void test_borne_suf_inf(Node borne_inf, Node borne_sup, Node node, Tds tds){
        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});


        if (borne_inf.getToken()!=null && borne_inf.getToken().getType() == TokenType.NUMBER){
            if (borne_sup.getToken()!=null && borne_sup.getToken().getType() == TokenType.NUMBER){
                return;
            }
            if(operators.contains(borne_sup.getType())){
                test_expression_arithmetique(borne_sup,tds);
                return;
            }
        }
        if (borne_sup.getToken()!=null && borne_sup.getToken().getType() == TokenType.NUMBER){
            if(operators.contains(borne_inf.getType())){
                if(test_expression_arithmetique(borne_inf,tds)) return;
            }
        }
        if(operators.contains(borne_inf.getType()) && operators.contains(borne_sup.getType())){
            test_expression_arithmetique(borne_inf, tds);
            test_expression_arithmetique(borne_sup, tds);
        }
    }

    private static void test_existence_type(String type, Tds tds, Node node){
        List<String> typesValide = new ArrayList<>();
        typesValide.add("integer");
        typesValide.add("boolean");
        typesValide.add("char");
        typesValide.add("access");

        //Symbol createdType = tds.getSymbol(type,SymbolType.TYPE);
        //if (createdType != null){
        //    return;
        // }

        Symbol symbol = tds.getSymbol(type, SymbolType.TYPE_ACCESS);
        Symbol symbol2 = tds.getSymbol(type, SymbolType.TYPE_RECORD);
        if (symbol != null || symbol2 != null){
            return;
        }


        for (String t : typesValide) {
            if (type.equalsIgnoreCase(t)){
                return;
            }
        }

        printError("The type " + type + " is not a valid type", node);
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
            if (type_valeur(left, tds).equalsIgnoreCase("integer")){
                return;
            }
            else if (test_expression_arithmetique(right, tds)) {
                return;
            }
            else if (tds.getSymbol(left.getValue(), SymbolType.VARIABLE) != null) {
                Symbol symbol = tds.getSymbol(left.getValue(), SymbolType.VARIABLE);
                if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")){
                    return;
                }
                else {
                    printError("The condition is not a valid boolean expression because the variable is not a integer: " + left.getValue(), left);
                }
            }
            else if (tds.getSymbol(right.getValue(), SymbolType.VARIABLE) != null) {
                Symbol symbol = tds.getSymbol(right.getValue(), SymbolType.VARIABLE);
                if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")){
                    return;
                }
                else {
                    printError("The condition is not a valid boolean expression because the variable is not a integer: " + right.getValue(), right);
                }
            }
            else {

                printError("The condition is not a valid boolean expression because the operands are not integers: " + left.getValue() + " " + right.getValue(), left);
            }
        }
        else {
            printError("The condition is not a valid boolean expression", condition);
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
            if (right.getType() == NodeType.ADDITION || right.getType() == NodeType.SUBSTRACTION || right.getType() == NodeType.MULTIPLY || right.getType() == NodeType.DIVIDE || right.getType() == NodeType.REM){
                boolean a = test_expression_arithmetique(left, tds);
                boolean b = test_expression_arithmetique(right, tds);
                if (a==false || b==false){
                    return false;
                }
            }
            else {
                if(left.getType() == NodeType.NEGATIVE_SIGN){
                    if(!left.getChild(0).getChildren().isEmpty()){
                        printError("The negative sign can only be applied to a single value", left);
                        return false;
                    }
                    left = left.getChild(0);
                }
                if(right.getType() == NodeType.NEGATIVE_SIGN){
                    if(!right.getChild(0).getChildren().isEmpty()){
                        printError("The negative sign can only be applied to a single value", right);
                        return false;
                    }
                    right = right.getChild(0);
                }


                String type_left = type_valeur(left, tds);
                String type_right = type_valeur(right, tds);

                if (type_left.equalsIgnoreCase("integer") && type_right.equalsIgnoreCase("integer")){
                    return true;
                }
                else {
                    if(!type_left.equals(" ") && !type_right.equals(" ")){
                        printError("Operation \'" + node.getValue() + "\' between two different types : " + type_left + " and " + type_right, node);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private static String type_valeur(Node valeur, Tds tds){
        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});
        try {
            ;
            // Essaie de parser la valeur en entier
            Integer.parseInt(valeur.getValue());
            return "integer";
        } catch (NumberFormatException e) {
            String valueStr = valeur.getValue().toLowerCase();
            if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
                return "boolean";
            } else if (valeur.getToken() != null && valeur.getToken().getType() == TokenType.CHARACTER) {
                return "char";
            } else if (operators.contains(valeur.getType())) {
                if(test_expression_arithmetique(valeur, tds)) return "integer";
                return "operator";
            }  else {
                Symbol symbol = tds.getSymbol(valeur.getValue(), SymbolType.TYPE_ACCESS);
                Symbol symbol2 = tds.getSymbol(valeur.getValue(), SymbolType.TYPE_RECORD);
                if (symbol != null) {
                    return ((TypeAccessSymbol) symbol).getNom();
                } else if (symbol2 != null) {
                    return ((TypeRecordSymbol) symbol2).getNom();
                }
                if (tds.getSymbol(valeur.getValue()) != null){
                    return ((VariableSymbol) tds.getSymbol(valeur.getValue())).getType_variable();
                }
                else {
                    controleSemantiqueAccessVariable(valeur, tds);
                    return " ";
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

    public static String getName_file() {
        return name_file;
    }

    public static void setName_file(String name_file) {
        SemanticControls.name_file = name_file;
    }
}
