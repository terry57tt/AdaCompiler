package org.pcl.structure.tds;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.pcl.ColorAnsiCode;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.NodeType;
import org.stringtemplate.v4.ST;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.pcl.structure.tds.Semantic.typeEnCoursDeDeclaration;

/** List all the semantic controls. */
public class SemanticControls {

    private static String name_file;

    private final static List<String> errors = new ArrayList<>();

    private static String currentSemanticControl;

    public static void printError(String error, Node node) {
        String numberLine;
        if (node.getToken() != null)
            numberLine = node.getToken().getLineNumber() + ": ";
        else
            numberLine = " ";
        System.out.println(name_file + ":" + numberLine + ColorAnsiCode.ANSI_RED + "error: " + ColorAnsiCode.ANSI_RESET + error + "\n");
        if (!errors.contains(currentSemanticControl)) {
            errors.add(currentSemanticControl);
        }
    }

    public static void controleSemantiqueFile(Node file) {
        currentSemanticControl = "controleSemantiqueFile";
        test_egalite_nom_debut_fin(file);
    }

    /**
     * nom
     * Type
     * Controles sémantiques :
     * Vérifier que le nom n’est pas déjà pris (double déclaration)
     * Vérifier que le type est bien défini
     */
    public static void controleSemantiqueDeclVariable(Node decl_var, Tds tds, VariableSymbol variableSymbol) {
        currentSemanticControl = "controleSemantiqueDeclVariable";
        test_double_declaration(decl_var, tds, variableSymbol);
        List<Node> children = decl_var.getChildren();
        test_existence_type(variableSymbol.getType_variable(), tds, children.get(1));
    }

    public static void controleSemantiqueTypeAccess(String nom, String type_pointe, Tds tds) {
        currentSemanticControl = "controleSemantiqueTypeAccess";
        //Test la double déclaration
        Node node = new Node();
        boolean a = tds.containsSymbol(nom, SymbolType.TYPE_ACCESS);
        if (a) {
            printError(nom + " has already been declared in the current scope", node);
        }

        boolean b = tds.containsSymbol(nom, SymbolType.TYPE_RECORD);
        if (b) {
            printError(node.getValue() + " has already been declared in the current scope", node);
        }

        //Test l'existence du type pointé
        test_existence_type(type_pointe, tds, node);
    }

    public static void controleSemantiqueTypeRecord(String nom, List<VariableSymbol> fields, Tds tds) {
        currentSemanticControl = "controleSemantiqueTypeRecord";
        //Test la double déclaration
        Node node = new Node();
        boolean a = tds.containsSymbol(nom, SymbolType.TYPE_ACCESS);
        if (a) {
            printError(nom + " has already been declared in the current scope", node);
        }

        boolean b = tds.containsSymbol(nom, SymbolType.TYPE_RECORD);
        if (b) {
            printError(node.getValue() + " has already been declared in the current scope", node);
        }

        //Test l'existence des types des champs
        List<VariableSymbol> fields_declare = new ArrayList<>();
        for (VariableSymbol field1 : fields) {
            for (VariableSymbol field_declare : fields_declare) {
                if (field1.getName().equalsIgnoreCase(field_declare.getName())) {
                    printError("The field " + field1.getName() + " has already been declared", node);
                }
            }
            test_existence_type(field1.getType_variable(), tds, node);
            fields_declare.add(field1);
        }
    }

    public static void controleSemantiquePoint(Node point, Tds tds) {
        currentSemanticControl = "controleSemantiquePoint";
        if (point.getChildren().get(0).getType() == NodeType.POINT) {
            String typeNoeudPoint = getTypeNoeudPointAvant(point, tds);
            if (typeNoeudPoint.equals(" ")) return;
            else {
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(typeNoeudPoint, SymbolType.TYPE_RECORD);
                if (symbolStructure == null) {
                    printError(typeNoeudPoint + " is not a declared structure ###", point);
                    return;
                }
                List<VariableSymbol> fields = ((TypeRecordSymbol) symbolStructure).getFields();
                for (VariableSymbol field1 : fields) {
                    if (field1.getName().equalsIgnoreCase(field.getValue())) {
                        return;
                    }
                }
                printError("The field " + field.getValue() + " doesn't exist for " + typeNoeudPoint, field);
            }
        }
        else {
            if (point.getChildren().get(0).getType() == NodeType.CALL) {
                controleSemantiqueAppelFonction(point.getChildren().get(0), tds);
                String returnType = ((FunctionSymbol) tds.getSymbol(point.getChildren().get(0).getChildren().get(0).getValue(), SymbolType.FUNCTION)).getReturnType();
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(returnType, SymbolType.TYPE_RECORD);
                if (symbolStructure == null) {
                    if (returnType.equals(" ")){
                        return;
                    }
                    printError(returnType + " is not a declared structure", point);
                    return;
                }
                List<VariableSymbol> fields = ((TypeRecordSymbol) symbolStructure).getFields();
                for (VariableSymbol field1 : fields) {
                    if (field1.getName().equalsIgnoreCase(field.getValue())) {
                        return;
                    }
                }
                printError("The field " + field.getValue() + " doesn't exist for " + returnType + " which is the result of the function " + point.getChildren().get(0).getChildren().get(0).getValue(), field);
                return;
            } else {
                Node structure = point.getChildren().get(0);
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(structure.getValue(), SymbolType.VARIABLE);
                if (symbolStructure == null) {
                    if (structure.getValue().equals(" ")){
                        return;
                    }
                    printError(structure.getValue() + " is not a declared structure", structure);
                    return;
                }
                try {
                    List<VariableSymbol> fields = ((StructureSymbol) symbolStructure).getFields();
                    for (VariableSymbol field1 : fields) {
                        if (field1.getName().equalsIgnoreCase(field.getValue())) {
                            return;
                        }
                    }
                    printError("The field " + field.getValue() + " doesn't exist for " + structure.getValue(), field);
                } catch (Exception e) {
                    printError("The variable " + structure.getValue() + " is not a declared structure", structure);
                }
            }
        }
    }

    public static String getTypeNoeudPoint(Node point, Tds tds){
        if (point.getChildren().get(0).getType() != NodeType.POINT){
            if (point.getChildren().get(0).getType() == NodeType.CALL){
                controleSemantiqueAppelFonction(point.getChildren().get(0), tds);
                FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbol(point.getChildren().get(0).getChildren().get(0).getValue(), SymbolType.FUNCTION);
                //String returnType = ((FunctionSymbol) tds.getSymbol(point.getChildren().get(0).getValue(), SymbolType.FUNCTION)).getReturnType();
                if (functionSymbol == null) {
                    printError("Function is not declared", point);
                    return " ";
                }
                String returnType = functionSymbol.getReturnType();
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(returnType, SymbolType.TYPE_RECORD);
                if (symbolStructure == null) {
                    printError(returnType + " is not a declared structure", point);
                    return " ";
                }
                List<VariableSymbol> fields = ((TypeRecordSymbol) symbolStructure).getFields();
                for (VariableSymbol field1 : fields) {
                    if (field1.getName().equalsIgnoreCase(field.getValue())) {
                        return field1.getType_variable();
                    }
                }
                printError("The field " + field.getValue() + " doesn't exist for " + returnType + " which is the result of the function " + point.getChildren().get(0).getValue(), field);
                return " ";
            }
            else {
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(point.getChildren().get(0).getValue(), SymbolType.STRUCTURE);
                if (symbolStructure == null) {
                    return " ";
                }
                List<VariableSymbol> fields = ((StructureSymbol) symbolStructure).getFields();
                for (VariableSymbol field1 : fields) {
                    if (field1.getName().equalsIgnoreCase(field.getValue())) {
                        return field1.getType_variable();
                    }
                }
                printError("The field " + field.getValue() + " doesn't exist for " + point.getChildren().get(0).getValue(), field);
                return " ";
            }

        }
        else {
            String typeNoeudPoint = getTypeNoeudPointAvant(point, tds);
            if (typeNoeudPoint.equals(" ")) return " ";
            else {
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(typeNoeudPoint, SymbolType.TYPE_RECORD);
                if (symbolStructure == null) {
                    return " ";
                }
                List<VariableSymbol> fields = ((TypeRecordSymbol) symbolStructure).getFields();
                for (VariableSymbol field1 : fields) {
                    if (field1.getName().equalsIgnoreCase(field.getValue())) {
                        return field1.getType_variable();
                    }
                }
                printError("The field " + field.getValue() + " doesn't exist for " + typeNoeudPoint, field);
                return " ";
            }
        }
    }

    public static String getTypeNoeudPointAvant(Node point, Tds tds){
        if (point.getChildren().get(0).getType() != NodeType.POINT){
            if (point.getChildren().get(0).getType() == NodeType.CALL){
                controleSemantiqueAppelFonction(point.getChildren().get(0), tds);
                String returnType = ((FunctionSymbol) tds.getSymbol(point.getChildren().get(0).getChildren().get(0).getValue(), SymbolType.FUNCTION)).getReturnType();
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(returnType, SymbolType.TYPE_RECORD);
                if (symbolStructure == null) {
                    printError(returnType + " is not a declared structure", point);
                    return " ";
                }
                List<VariableSymbol> fields = ((TypeRecordSymbol) symbolStructure).getFields();
                for (VariableSymbol field1 : fields) {
                    if (field1.getName().equalsIgnoreCase(field.getValue())) {
                        return field1.getType_variable();
                    }
                }
                printError("The field " + field.getValue() + " doesn't exist for " + returnType + " which is the result of the function " + point.getChildren().get(0).getChildren().get(0).getValue(), field);
                return " ";
            }
            else {
                Node structure = point.getChildren().get(0);
                Node field = point.getChildren().get(1);
                Symbol symbolStructure = tds.getSymbol(structure.getValue(), SymbolType.STRUCTURE);
                if (symbolStructure == null) {
                    printError(structure.getValue() + " is not a declared structure", structure);
                    return " ";
                }
                List<VariableSymbol> fields = ((StructureSymbol) symbolStructure).getFields();
                for (VariableSymbol field1 : fields) {
                    if (field1.getName().equalsIgnoreCase(field.getValue())) {
                        System.out.println("field1.getType_variable() : " + field1.getType_variable());
                        return field1.getType_variable();
                    }
                }
                printError("The field " + field.getValue() + " doesn't exist for " + structure.getValue(), field);
                return " ";
            }
        }
        else {
            String typeNoeudPointAvant = getTypeNoeudPointAvant(point.getChildren().get(0), tds);
            Node field = point.getChildren().get(1);
            Symbol symbolStructure = tds.getSymbol(typeNoeudPointAvant, SymbolType.TYPE_RECORD);
            if (symbolStructure == null) {
                if (typeNoeudPointAvant.equals(" ")){
                    return " ";
                }
                printError(typeNoeudPointAvant + " is not a declared structure", point);
                return " ";
            }
            List<VariableSymbol> fields = ((TypeRecordSymbol) symbolStructure).getFields();
            for (VariableSymbol field1 : fields) {
                if (field1.getName().equalsIgnoreCase(field.getValue())) {
                    return field1.getType_variable();
                }
            }
            printError("The field " + field.getValue() + " doesn't exist for " + typeNoeudPointAvant, field);
            return " ";
        } 
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
    public static void controleSemantiqueFor(Node for_node, Tds tds) {
        currentSemanticControl = "controleSemantiqueFor";

        List<Node> children = for_node.getChildren();
        String variable_compteur = children.get(0).getValue();
        String direction = children.get(1).getValue();


        test_borne_suf_inf(children.get(2), children.get(3), for_node, tds);

        Node body = children.get(4);
        for (Node child : body.getChildren()) {
            if (child.getType() == NodeType.AFFECTATION) {
                if (child.getChildren().get(0).getValue().equalsIgnoreCase(variable_compteur)) {
                    printError("The counter variable " + variable_compteur + " cannot be modified in the loop body", child.getChildren().get(0));
                }
            }
        }
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
    public static void controleSemantiqueIf(Node if_node, Tds globalTds) {
        currentSemanticControl = "controleSemantiqueIf";
        List<Node> children = if_node.getChildren();
        Node condition = children.get(0);
        Node then = children.get(1);
        List<Node> elsif = new ArrayList<>();
        Node else_node = null;
        for (int i = 2; i < children.size(); i++) {
            if (children.get(i).getType() == NodeType.ELSIF) {
                elsif.add(children.get(i));
            } else {
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
    public static void controleSemantiqueDeclFonction(Node decl_func, Tds tds) {
        currentSemanticControl = "controleSemantiqueDeclFonction";

        List<Node> children = decl_func.getChildren();
        Node valeur_retour = children.get(1);
        Node body = children.get(2);
        if (children.get(2).getType() == NodeType.DECLARATION)
            body = children.get(3);
        valeur_retour = valeur_retour.getChild(0);
        test_return_present(body);
        test_existence_type(valeur_retour.getValue(), tds, valeur_retour);
        test_bon_type_retour(body, valeur_retour.getValue(), tds);
    }

    /**
     * Appel de fonction :
     * Vérifier que le nombre de paramètre et le type corresponde à celui qu’on a déclaré dans la TDS
     */
    public static void controleSemantiqueAppelFonction(Node call_func, Tds tds) {
        currentSemanticControl = "controleSemantiqueAppelFonction";
        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});
        List<Node> children = call_func.getChildren();
        Node call_name = children.get(0);

        int nb_params = children.size() - 1;

        // call do not precise if it's a function or a procedure

        Symbol function_symbol = tds.getSymbol(call_name.getValue(), SymbolType.FUNCTION);
        Symbol procedure_symbol = tds.getSymbol(call_name.getValue(), SymbolType.PROCEDURE);
        boolean is_function = function_symbol != null;
        if (function_symbol == null && procedure_symbol == null) {
            printError("The function or procedure " + call_name.getValue() + " has not been declared", call_name);
            return;
        }
        if (!is_function) {
            controleSemantiqueAppelProcedure(call_func, tds);
            return;
        }

        // number of parameters match
        if (nb_params != ((FunctionSymbol) function_symbol).getNbParameters()) {
            printError("The number of parameters in the function \"" + call_name.getValue() + "\" doesn't match the number of parameters in the function declaration. Expected " + ((FunctionSymbol) function_symbol).getNbParameters() + " but got " + nb_params, call_name);
        } else {
            for (int i = 1; i < children.size(); i++) {
                String mode = ((FunctionSymbol) function_symbol).getParameters().get(i - 1).getMode();
                if(mode.equals("in out")){
                    Symbol childSymbol = tds.getSymbol(children.get(i).getValue(), SymbolType.VARIABLE);
                    if(childSymbol == null) childSymbol = tds.getSymbol(children.get(i).getValue(), SymbolType.PARAM);
                    if(!children.get(i).getValue().equals(".") && childSymbol == null){
                        printError("The mode of the parameter "+ i +" \"" + children.get(i).getValue() + "\" in \"" + call_name.getValue() + "\" is \" in out \". Expected a variable or an x.f expression.", children.get(i));
                    }
                    if(childSymbol != null && childSymbol.getType() == SymbolType.VARIABLE){
                        VariableSymbol variableSymbol = (VariableSymbol) childSymbol;
                        if(variableSymbol.isForVariable()){
                            printError("The mode of the parameter "+ i +" \"" + children.get(i).getValue() + "\" in\"" + call_name.getValue() + "\" is \" in out \". Expected a variable or an x.f expression, but got a for loop counter", children.get(i));
                        }
                    }
                }

                String value_type = type_valeur(children.get(i), tds);
                if (value_type.equals(" ")) continue;
                String expected_type;
                expected_type = ((FunctionSymbol) function_symbol).getParameters().get(i - 1).getType_variable();
                if (!value_type.equalsIgnoreCase(expected_type) && !value_type.equals("operator")) {
                    printError("The type of the parameter number "+ i +" \"" + children.get(i).getValue() + "\" in\"" + call_name.getValue() + "\" doesn't match the type of the parameter in the declaration. Expected " + expected_type + " but got " + value_type, call_name);
                }

            }
        }

    }


    /**
     * Déclaration de procédure : pareil mais sans la valeur de retour
     * Attention à vérifier que si il y a un enfant en plus, ce soit bien le nom de la procédure
     * vérifier pas de return
     */
    public static void controleSemantiqueDeclProcedure(Node decl_proc, Tds tds) {
        currentSemanticControl = "controleSemantiqueDeclProcedure";
        List<Node> children = decl_proc.getChildren();
        Node body = children.get(1);

    }

    /**
     * Appel de procédure : pareil que pour la fonction
     * nombre param match et type (pas de return)
     */
    public static void controleSemantiqueAppelProcedure(Node call_proc, Tds tds) {
        currentSemanticControl = "controleSemantiqueAppelProcedure";
        int nb_params = call_proc.getChildren().size() - 1;
        List<Node> children = call_proc.getChildren();
        // number of parameters match
        Symbol procedure_symbol = tds.getSymbol(call_proc.getChildren().get(0).getValue(), SymbolType.PROCEDURE);
        if (nb_params != ((ProcedureSymbol) procedure_symbol).getNbParameters()) {
            printError("The number of parameters in the function \"" + call_proc.getChildren().get(0).getValue() + "\" doesn't match the number of parameters in the function declaration. Expected "
                    + ((ProcedureSymbol) procedure_symbol).getNbParameters() + " but got " + nb_params, call_proc.getChildren().get(0));
        } else {
            for (int i = 1; i < call_proc.getChildren().size(); i++) {

                String mode = ((ProcedureSymbol) procedure_symbol).getParameters().get(i - 1).getMode();
                if(mode.equals("in out")){
                    Symbol childSymbol = tds.getSymbol(children.get(i).getValue(), SymbolType.VARIABLE);
                    if(childSymbol == null) childSymbol = tds.getSymbol(children.get(i).getValue(), SymbolType.PARAM);
                    if(!children.get(i).getValue().equals(".") && childSymbol == null){
                        printError("The mode of the parameter "+ i +" \"" + children.get(i).getValue() + "\" in \"" + call_proc.getChildren().get(0).getValue() + "\" is \" in out \". Expected a variable or an x.f expression.", children.get(i));
                    }
                    if(childSymbol != null && childSymbol.getType() == SymbolType.VARIABLE){
                        VariableSymbol variableSymbol = (VariableSymbol) childSymbol;
                        if(variableSymbol.isForVariable()){
                            printError("The mode of the parameter  "+ i +" \"" + children.get(i).getValue() + "\" in \"" + call_proc.getChildren().get(0).getValue() + "\" is \" in out \". Expected a variable or an x.f expression, but got a for loop counter", children.get(i));
                        }
                    }
                }
                String value_type = type_valeur(call_proc.getChildren().get(i), tds);
                if (value_type.equals(" ")) continue;
                String expected_type;
                expected_type = ((ProcedureSymbol) procedure_symbol).getParameters().get(i - 1).getType_variable();
                if (!value_type.equalsIgnoreCase(expected_type) && !value_type.equals("operator")) {
                    printError("The type of the parameter number "+ i +" \"" + call_proc.getChildren().get(i).getValue() + "\" in the call \""
                            + call_proc.getChildren().get(0).getValue() + "\" doesn't match the type of the parameter in the declaration. Expected " + expected_type + " but got " + value_type, call_proc.getChildren().get(0));
                }
            }
        }

    }

    public static void controleSemantiqueAffectation(Node affectation, Tds tds) {
        currentSemanticControl = "controleSemantiqueAffectation";
        List<Node> children = affectation.getChildren();
        Node variable = children.get(0);
        Node valeur = children.get(1);

        //search if the variable is a variable symbol in the tds
        Symbol symbol = tds.getSymbol(variable.getValue(), SymbolType.VARIABLE);
        //if not, search if the variable is a param symbol in the tds
        if(symbol == null) symbol = tds.getSymbol(variable.getValue(), SymbolType.PARAM);

        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});

        // if variable is neither a variable nor a parameter
        if (symbol == null){
            if(variable.getType() == NodeType.POINT){
                controleSemantiquePoint(variable, tds);
                Symbol symbolVar = tds.getSymbol(variable.firstChild().getValue(), SymbolType.PARAM);
                if(symbolVar != null) {
                    test_in_out(variable.firstChild(), tds);
                }
                ParamSymbol symbolParam = (ParamSymbol) symbolVar;
                String typeNoeudPoint = getTypeNoeudPoint(variable, tds);
                String type_valeur = type_valeur(valeur, tds);
                if (!typeNoeudPoint.equalsIgnoreCase(type_valeur)) {
                    if(symbolParam != null && symbolParam.getMode().equals("in")) return;
                    printError("Mismatch type : " + typeNoeudPoint + " and " +  type_valeur , variable);
                }
            }
            else {
                printError("The variable " + variable.getValue() + " has not been declared", variable);
            }
            return;
        }
        // if the symbol is a param
        if(symbol.getType() == SymbolType.PARAM){
            test_in_out(variable, tds);
            ParamSymbol paramSymbol = (ParamSymbol) symbol;
            if(paramSymbol.getMode().equals("in out")) {
                String type_valeur = type_valeur(valeur, tds);
                if (!type_valeur.equalsIgnoreCase(type_valeur(variable, tds))){
                    if(!operators.contains(valeur.getType())){
                        printError("Mismatch type for parameter " + variable.getValue() + " : " + (paramSymbol).getType_variable() + " and " + valeur.getValue(), variable);
                    }
                }
            }
            return;
        }
        //if the symbol is a variable
        if(symbol.getType() == SymbolType.VARIABLE || symbol.getType() == SymbolType.STRUCTURE){
            VariableSymbol variableSymbol = (VariableSymbol) symbol;
            if (valeur.getType() == NodeType.CALL){
                if (tds.getSymbol(valeur.getChildren().get(0).getValue(), SymbolType.PROCEDURE) != null){
                    printError("The procedure " + valeur.getChildren().get(0).getValue() + " can't be used in an affectation", variable);
                    return;
                }
                controleSemantiqueAppelFonction(valeur, tds);
                FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbol(valeur.getChildren().get(0).getValue());
                if (functionSymbol == null){
                    return;
                }
                if (!variableSymbol.getType_variable().equalsIgnoreCase(functionSymbol.getReturnType())) {
                    printError("Mismatch type for variable " + symbol.getName() + " : " + variableSymbol.getType_variable() + " and " +  functionSymbol.getReturnType() , variable);
                }
                return;
            }

            else if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")
                    && operators.contains(valeur.getType())) {
                test_expression_arithmetique(valeur, tds);
            }
            else if (valeur.getType() == NodeType.POINT){
                String typeNoeudPoint = getTypeNoeudPoint(valeur, tds);
                if (!variableSymbol.getType_variable().equalsIgnoreCase(typeNoeudPoint)){
                    if (typeNoeudPoint.equals(" ")) return;
                    printError("Mismatch type for variable " + symbol.getName() + " : " + variableSymbol.getType_variable() + " and " +  typeNoeudPoint, variable);
                }
            }else if(valeur.getValue().equalsIgnoreCase("null")){ // variable doit etre un pointeur
                if(((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")||((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("boolean")||((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("character")){
                    printError("The variable " + variable.getValue() + " is not a pointer", variable);
                } else {
                    return;
                }
            }
            else {
                String type_valeur = type_valeur(valeur, tds);
                if (!((VariableSymbol) symbol).getType_variable().equalsIgnoreCase(type_valeur)) {
                    if (type_valeur.equals(" ")) return;
                    printError("Mismatch type for variable " + variable.getValue() + " : " + ((VariableSymbol) symbol).getType_variable() + " and " + type_valeur, variable);
                }
            }
        }
    }

    public static void controleSemantiqueAffectationDecl(Node affectation, Tds tds) {
        // currentSemanticControl = "controleSemantiqueAffectationDecl";
        // List<Node> children = affectation.getChildren();
        // Node variable = children.get(0);
        // Node valeur = children.get(1);
        // Symbol symbol = tds.getSymbol(variable.getChildren().get(0).getValue(), SymbolType.VARIABLE);
        // if (symbol == null) {
        //     printError("The variable " + variable.getValue() + " has not been declared", variable);
        // } else if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")
        //         && type_valeur(valeur, tds).equalsIgnoreCase("operator")) {
        //     test_expression_arithmetique(valeur, tds);
        // } else {
        //     String type_valeur = type_valeur(valeur, tds);
        //     if (!((VariableSymbol) symbol).getType_variable().equalsIgnoreCase(type_valeur)) {
        //         printError("Mismatch type for variable " + variable.getValue() + " : " + ((VariableSymbol) symbol).getType_variable() + " and " + type_valeur, variable);
        //     }
        // }

        List<Node> children = affectation.getChildren();
        Node variable = children.get(0).getChildren().get(0);
        Node valeur = children.get(1);

        //search if the variable is a variable symbol in the td
        Symbol symbol = tds.getSymbol(variable.getValue(), SymbolType.VARIABLE);
        //if not, search if the variable is a param symbol in the tds
        if(symbol == null) symbol = tds.getSymbol(variable.getValue(), SymbolType.PARAM);

        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});

        // if variable is neither a variable nor a parameter
        if (symbol == null){
            if(variable.getType() == NodeType.POINT){
                controleSemantiquePoint(variable, tds);
                String typeNoeudPoint = getTypeNoeudPoint(variable, tds);
                String type_valeur = type_valeur(valeur, tds);
                if (!typeNoeudPoint.equalsIgnoreCase(type_valeur)) {
                    printError("Mismatch type : " + typeNoeudPoint + " and " +  type_valeur , variable);
                }
            }
            else {
                printError("The variable " + variable.getValue() + " has not been declared", variable);
            }
            return;
        }
        // if the symbol is a param
        if(symbol.getType() == SymbolType.PARAM){
            test_in_out(variable, tds);
            ParamSymbol paramSymbol = (ParamSymbol) symbol;
            if(paramSymbol.getMode().equals("in out")) {
                String type_valeur = type_valeur(valeur, tds);
                if (!type_valeur.equalsIgnoreCase(type_valeur(variable, tds))){
                    printError("Mismatch type for parameter " + variable.getValue() + " : " + (paramSymbol).getType_variable() + " and " + valeur, variable);
                }
            }
            return;
        }
        //if the symbol is a variable
        if(symbol.getType() == SymbolType.VARIABLE || symbol.getType() == SymbolType.STRUCTURE){
            VariableSymbol variableSymbol = (VariableSymbol) symbol;
            if (valeur.getType() == NodeType.CALL){
                if (tds.getSymbol(valeur.getChildren().get(0).getValue(), SymbolType.PROCEDURE) != null){
                    printError("The procedure " + valeur.getChildren().get(0).getValue() + " can't be used in an affectation", variable);
                    return;
                }
                controleSemantiqueAppelFonction(valeur, tds);
                FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbol(valeur.getChildren().get(0).getValue());
                if (functionSymbol == null){
                    return;
                }
                if (!variableSymbol.getType_variable().equalsIgnoreCase(functionSymbol.getReturnType())) {
                    printError("Mismatch type for variable " + symbol.getName() + " : " + variableSymbol.getType_variable() + " and " +  functionSymbol.getReturnType() , variable);
                }
                return;
            }
            else if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")
                    && operators.contains(valeur.getType())) {
                test_expression_arithmetique(valeur, tds);
            }
            else if (valeur.getType() == NodeType.POINT){
                String typeNoeudPoint = getTypeNoeudPoint(valeur, tds);
                if (!variableSymbol.getType_variable().equalsIgnoreCase(typeNoeudPoint)){
                    if (typeNoeudPoint.equals(" ")) return;
                    printError("Mismatch type for variable " + symbol.getName() + " : " + variableSymbol.getType_variable() + " and " +  typeNoeudPoint, variable);
                }
            }
            else {
                String type_valeur = type_valeur(valeur, tds);
                if (!((VariableSymbol) symbol).getType_variable().equalsIgnoreCase(type_valeur)) {
                    if (type_valeur.equals(" ")) return;
                    printError("Mismatch type for variable " + variable.getValue() + " : " + ((VariableSymbol) symbol).getType_variable() + " and " + type_valeur, variable);
                }
            }
        }
    }

    /**
     * Opérateur +, *, / :
     * ca peut etre n’importe quel entier ou opérateur en dehors du =, du AND bien entendu, on ne peut pas avoir 2+3=5=6
     * <p>
     * Opérateur - :
     * peut avoir un ou deux paramètres selon si c’est le moins normal ou le moins unaire mais dans tout les cas, il ne doit y avoir que des entiers
     * <p>
     * Opérateur de comparaison de booleen : AND, OR :
     * expression booleene à gauche et à droite
     * <p>
     * Opérateur de comparaison d’entier (<=, =) :
     * Entier à gauche et droite
     */
    public static void controleSemantiqueOperateur(Node operateur, Tds tds) {
        currentSemanticControl = "controleSemantiqueOperateur";
        switch (operateur.getType()) {
            case ADDITION, SUBSTRACTION, MULTIPLY, REM, EQUAL, DIVIDE, SLASH_EQUAL, SUPERIOR, SUPERIOR_EQUAL, INFERIOR_EQUAL, INFERIOR:
                if (!test_expression_arithmetique(operateur, tds)) {
                    errors.add("OPERATOR");
                    printError("The operator " + operateur.getValue() + " is not a valid arithmetic expression", operateur);
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
    public static void controleSemantiqueWhile(Node while_node, Tds tds) {
        currentSemanticControl = "controleSemantiqueWhile";
        Node condition = while_node.getChild(0);
        test_condition_booleene(condition, tds);
    }

    /**
     * Vérifier que la variable a bien été déclaré qu'on y a accès
     */
    public static void controleSemantiqueAccessVariable(Node access_var, Tds tds) {
        currentSemanticControl = "controleSemantiqueAccessVariable";
        Symbol symbol = tds.getSymbol(access_var.getValue(), SymbolType.VARIABLE);
        if (symbol == null) {
            printError("The variable " + access_var.getValue() + " has not been declared", access_var);
        }
    }

    public static void controleSemantiqueFonctionStatement(Node call_node, Tds tds){
        currentSemanticControl = "controleSemantiqueFonctionStatement";
        Symbol function_symbol = tds.getSymbol(call_node.getChildren().get(0).getValue(), SymbolType.FUNCTION);
        if(call_node.getParent().getType() == NodeType.BODY && function_symbol != null){
            printError("Cannot use call to function \""+ call_node.firstChild() +"\" as a statement", call_node);
        }
        Symbol proc_symbol = tds.getSymbol(call_node.getChildren().get(0).getValue(), SymbolType.PROCEDURE);
        if(proc_symbol != null) controleSemantiqueAppelProcedure(call_node, tds);
    }

    public static void controleSemantiqueBodyStatement(Node body_node, Tds tds){
        currentSemanticControl = "controleSemantiqueBodyStatement";
        for (Node child : body_node.getChildren()){
            Symbol symbol = tds.getSymbol(child.getValue(), SymbolType.VARIABLE);
            if(symbol == null) symbol = tds.getSymbol(child.getValue(), SymbolType.PARAM);
            if(symbol == null) symbol = tds.getSymbol(child.getValue(), SymbolType.FUNCTION);
            if(symbol != null && child.getToken() != null && child.getToken().getType() == TokenType.IDENTIFIER && symbol.getType() != SymbolType.FUNCTION) {
                printError("The variable " + child.getValue() + " do not do anything.", child);
            } else if (symbol == null && child.getToken() != null && child.getToken().getType() == TokenType.IDENTIFIER){
                printError("The variable " + child.getValue() + " has not been declared", child);
            } else if (symbol != null && symbol.getType() == SymbolType.FUNCTION){
                printError("Cannot use call to function \""+ child.getValue() +"\" as a statement", child);
            }
        }
    }

    /**
     * Vérifier que le noeud à gauche existe, si existe récupérer tous les champs, regarder si celui de droite est dedans
     */
    // public static void controleSemantiquePoint(Node point, Tds tds){
    //    currentSemanticControl = "controleSemantiquePoint";
    //     Node structure = point.firstChild();
    //     Node field = point.getChild(1);
    //     Symbol symbolStructure = tds.getSymbol(structure.getValue(), SymbolType.VARIABLE);
    //     if (symbolStructure == null) {
    //         printError(structure.getValue() + " is not a declared structure", structure);
    //         return;
    //     }
    //     Symbol symbolField = tds.getSymbol(field.getValue(), SymbolType.TYPE_ACCESS);
    //     if(symbolField == null) symbolField = tds.getSymbol(field.getValue(), SymbolType.TYPE_RECORD);
    //     if(symbolField == null) printError("The field " + field.getValue() + " doesn't exist for " + structure.getValue() , field);
    // }

    // Utility function
    private static void test_egalite_nom_debut_fin(Node file) {
        List<Node> children = file.getChildren();
        int nombre_enfants = children.size();
        String nom_debut = children.get(0).getValue();
        String nom_fin = children.get(nombre_enfants - 1).getValue();
        if (!nom_debut.equalsIgnoreCase(nom_fin) && !nom_fin.equalsIgnoreCase("body")) {
            printError("The file name at the beginning and at the end of the program don't match : " + nom_debut + " != " + nom_fin, children.get(nombre_enfants - 1));
        }
    }

    private static void test_double_declaration(Node node, Tds tds, VariableSymbol variableSymbol) {
        SymbolType type = SymbolType.VARIABLE;
        boolean a = tds.containsSymbol(variableSymbol.getName(), type);
        if (a) {
            printError(node.getValue() + " has already been declared in the current scope", node);
        }
    }

    private static void test_return_present(Node node) {
        List<Node> children = node.getChildren();
        for (Node child : children) {
            if (child.getType() == NodeType.RETURN) {
                return;
            }
        }
        printError("Missing return statement in the function", node);
    }

    private static void test_borne_suf_inf(Node borne_inf, Node borne_sup, Node node, Tds tds) {
        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});

        if (borne_inf.getToken() != null && borne_inf.getToken().getType() == TokenType.NUMBER) {
            if (borne_sup.getToken() != null && borne_sup.getToken().getType() == TokenType.NUMBER) {
                return;
            }
            if (operators.contains(borne_sup.getType())) {
                test_expression_arithmetique(borne_sup, tds);
                return;
            }
        }
        if (borne_sup.getToken() != null && borne_sup.getToken().getType() == TokenType.NUMBER) {
            if (operators.contains(borne_inf.getType())) {
                if (test_expression_arithmetique(borne_inf, tds)) return;
            }
        }
        if (operators.contains(borne_inf.getType()) && operators.contains(borne_sup.getType())) {
            test_expression_arithmetique(borne_inf, tds);
            test_expression_arithmetique(borne_sup, tds);
        }
    }

    private static void test_existence_type(String type, Tds tds, Node node) {
        List<String> typesValide = new ArrayList<>();
        typesValide.add("integer");
        typesValide.add("boolean");
        typesValide.add("Character");
        typesValide.add("access");

        //Symbol createdType = tds.getSymbol(type,SymbolType.TYPE);
        //if (createdType != null){
        //    return;
        // }

        Symbol symbol = tds.getSymbol(type, SymbolType.TYPE_ACCESS);
        Symbol symbol2 = tds.getSymbol(type, SymbolType.TYPE_RECORD);
        if (symbol != null || symbol2 != null) {
            return;
        }


        for (String t : typesValide) {
            if (type.equalsIgnoreCase(t)) {
                return;
            }
        }

        for (String t : typeEnCoursDeDeclaration){
            if (type.equalsIgnoreCase(t)){
                return;
            }
        }

        printError("The type " + type + " is not a valid type", node);
    }

    private static void test_condition_booleene(Node condition, Tds tds) {
        //On a une condition booléenne si on voit un opérateur de comparaison ou un opérateur logique (mais à ce moment là, on a déjà vérifié que les deux opérandes étaient des booléens)

        if (condition.getValue().equalsIgnoreCase("True") || condition.getValue().equalsIgnoreCase("False")) {
            return;
        }

        if (condition.getValue().equalsIgnoreCase("AND") || condition.getValue().equalsIgnoreCase("OR")) {
            List<Node> children = condition.getChildren();
            for (Node child : children) {
                test_condition_booleene(child, tds);
            }
        }
        else if(condition.getValue().equalsIgnoreCase("AND THEN") || condition.getValue().equalsIgnoreCase("OR ELSE")){
            List<Node> children = condition.getChildren();
            for (Node child : children) {
                test_condition_booleene(child, tds);
            }
        }
        else if (condition.getValue().equalsIgnoreCase("NOT")) {
            List<Node> children = condition.getChildren();
            for (Node child : children) {
                test_condition_booleene(child, tds);
            }
        } else if (condition.getValue().equalsIgnoreCase("<=") || condition.getValue().equalsIgnoreCase(">=") || condition.getValue().equalsIgnoreCase("=") || condition.getValue().equalsIgnoreCase("<") || condition.getValue().equalsIgnoreCase(">") || condition.getValue().equalsIgnoreCase("/=")) {
            List<Node> children = condition.getChildren();
            Node left = children.get(0);
            Node right = children.get(1);
            // in a while or if condition
            if (condition.getValue().equalsIgnoreCase("=") || condition.getValue().equalsIgnoreCase("/=")) {

                // special cases for null
                if(return_type_node(left,tds).equalsIgnoreCase("null")){
                    if(return_type_node(right,tds).equalsIgnoreCase("null")) return; // case null = null
                    if(return_type_node(right,tds).equalsIgnoreCase("integer") || return_type_node(right,tds).equalsIgnoreCase("boolean") || return_type_node(right,tds).equalsIgnoreCase("Character")){
                        printError("The condition is not a valid boolean expression because "+ return_type_node(right,tds) + " cannot be compared to null", right);
                    } else{
                        return;
                    }
                };
                if(return_type_node(right,tds).equalsIgnoreCase("null")){
                    if(return_type_node(left,tds).equalsIgnoreCase("integer") || return_type_node(left,tds).equalsIgnoreCase("boolean") || return_type_node(left,tds).equalsIgnoreCase("Character")){
                        printError("The condition is not a valid boolean expression because "+ return_type_node(left,tds) + " cannot be compared to null", left);
                    } else {
                        return;
                    }
                };

                if(return_type_node(left,tds).equalsIgnoreCase("point") || return_type_node(right,tds).equalsIgnoreCase("point")) return;

                // cas ou les types ne correspondent pas
                if (!return_type_node(left,tds).equalsIgnoreCase(return_type_node(right,tds))) {
                    printError("The condition is not a valid boolean expression because the operands are not of the same type: " + left.getValue() + " " + right.getValue(), left);
                } // cas ou on a un type liste
                else if(return_type_node(left,tds).equalsIgnoreCase("none") || return_type_node(right,tds).equalsIgnoreCase("none")){
                    printError("The condition is not a valid boolean expression because the operands are not of the same type: " + left.getValue() + " " + right.getValue(), left);
                }
                return;
            }
            if (type_valeur(left, tds).equalsIgnoreCase("integer")) {
                return;
            } else if (left.getType() == NodeType.CALL) {
                List<Node> children_call = left.getChildren();
                Node name_function = children_call.get(0);

                if (tds.getSymbol(name_function.getValue(), SymbolType.FUNCTION) != null) {
                    Symbol symbol = tds.getSymbol(name_function.getValue(), SymbolType.FUNCTION);
                    if (((FunctionSymbol) symbol).getReturnType().equalsIgnoreCase("integer")) {
                        return;
                    } else {
                        printError("The condition is not a valid boolean expression because the function return type is not an integer: " + left.getValue(), left);
                    }
                } else {
                    printError("The condition is not a valid boolean expression because " + name_function.getValue() + " is a procedure", left);
                }
            } else if (test_expression_arithmetique(right, tds)) {
                return;
            } else if (tds.getSymbol(left.getValue(), SymbolType.VARIABLE) != null) {
                Symbol symbol = tds.getSymbol(left.getValue(), SymbolType.VARIABLE);
                if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")) {
                    return;
                } else {
                    printError("The condition is not a valid boolean expression because the variable is not a integer: " + left.getValue(), left);
                }
            } else if (tds.getSymbol(right.getValue(), SymbolType.VARIABLE) != null) {
                Symbol symbol = tds.getSymbol(right.getValue(), SymbolType.VARIABLE);
                if (((VariableSymbol) symbol).getType_variable().equalsIgnoreCase("integer")) {
                    return;
                } else {
                    printError("The condition is not a valid boolean expression because the variable is not a integer: " + right.getValue(), right);
                }
            } else {

                printError("The condition is not a valid boolean expression because the operands are not integers: " + left.getValue() + " " + right.getValue(), left);
            }
        } else if(condition.getType() != null){
            if(condition.getType().equals(NodeType.CALL)){
                FunctionSymbol functionSymbol = (FunctionSymbol) tds.getSymbol(condition.getChildren().get(0).getValue(), SymbolType.FUNCTION);
                if(!functionSymbol.getReturnType().equalsIgnoreCase("boolean")){
                    printError("The condition is not a valid boolean expression because the function return type is not a boolean: " + functionSymbol.getReturnType(), condition);
                } else {
                    return;
                }
            }
        } else if(tds.getSymbol(condition.getValue()).getType() == SymbolType.PARAM){
            if(((ParamSymbol) tds.getSymbol(condition.getValue())).getType_variable().equalsIgnoreCase("boolean")){
                return;
            } else {
                printError("The condition is not a valid boolean expression because the parameter is not a boolean: " + condition.getValue(), condition);
            }
        }

        else {
            printError("The condition is not a valid boolean expression", condition);
        }
    }

    private static boolean test_expression_arithmetique(Node node, Tds tds) {
        if (node.getType() == NodeType.DIVIDE){
            if (node.getChild(1).getValue().equals("0")){
                printError("Division by zero", node.getChild(1));
            }
        }
        if (node.getType() == NodeType.ADDITION || node.getType() == NodeType.SUBSTRACTION || node.getType() == NodeType.MULTIPLY || node.getType() == NodeType.DIVIDE || node.getType() == NodeType.REM) {
            List<Node> children = node.getChildren();
            Node left = children.get(0);
            Node right = children.get(1);
            if (left.getType() == NodeType.ADDITION || left.getType() == NodeType.SUBSTRACTION || left.getType() == NodeType.MULTIPLY || left.getType() == NodeType.DIVIDE || left.getType() == NodeType.REM) {
                boolean a = test_expression_arithmetique(left, tds);
                if (!a) {
                    return false;
                } else {
                    String type_right = type_valeur(right, tds);
                    if (!type_right.equalsIgnoreCase("integer")) {
                        printError("Operation '" + node.getValue() + "' between two different types : integer" + " and " + type_right, node);
                        return false;
                    }
                    return true;
                }
            }
            if (right.getType() == NodeType.ADDITION || right.getType() == NodeType.SUBSTRACTION || right.getType() == NodeType.MULTIPLY || right.getType() == NodeType.DIVIDE || right.getType() == NodeType.REM) {

                boolean b = test_expression_arithmetique(right, tds);
                if (!b) {
                    return false;
                } else {
                    String type_left = type_valeur(left, tds);
                    if (!type_left.equalsIgnoreCase("integer")) {
                        printError("Operation '" + node.getValue() + "' between two different types : " + type_left + " and integer", node);
                        return false;
                    }
                    return true;
                }
            } else {

                if (left.getType() == NodeType.NEGATIVE_SIGN) {
                    if (!left.getChild(0).getChildren().isEmpty()) {
                        printError("The negative sign can only be applied to a single value", left);
                        return false;
                    }
                    left = left.getChild(0);
                }
                if (right.getType() == NodeType.NEGATIVE_SIGN) {
                    if (!right.getChild(0).getChildren().isEmpty()) {
                        printError("The negative sign can only be applied to a single value", right);
                        return false;
                    }
                    right = right.getChild(0);
                }


                String type_left = type_valeur(left, tds);
                String type_right = type_valeur(right, tds);

                if (type_left.equalsIgnoreCase("integer") && type_right.equalsIgnoreCase("integer")) {
                    return true;
                } else {
                    if (!type_left.equals(" ") && !type_right.equals(" ")) {
                        printError("Operation \'" + node.getValue() + "\' between two different types : " + type_left + " and " + type_right, node);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private static String type_valeur(Node valeur, Tds tds) {
        List<NodeType> operators = Arrays.asList(new NodeType[]{NodeType.ADDITION, NodeType.SUBSTRACTION, NodeType.MULTIPLY, NodeType.DIVIDE, NodeType.REM});
        List<NodeType> comparator = Arrays.asList(new NodeType[]{NodeType.EQUAL, NodeType.SLASH_EQUAL, NodeType.SUPERIOR, NodeType.SUPERIOR_EQUAL, NodeType.INFERIOR, NodeType.INFERIOR_EQUAL, NodeType.COMPARATOR, NodeType.AND, NodeType.OR});
        if (valeur.getType() == NodeType.POINT){
            return getTypeNoeudPoint(valeur, tds);
        }
        try {
            ;
            // Essaie de parser la valeur en entier
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
            } else if (operators.contains(valeur.getType())) {
                if (test_expression_arithmetique(valeur, tds)) return "integer";
                return "operator";
            } else if (comparator.contains(valeur.getType())) {
                test_condition_booleene(valeur, tds);
                return "boolean";
            }
            else {
                Symbol symbol = tds.getSymbol(valeur.getValue(), SymbolType.TYPE_ACCESS);
                Symbol symbol2 = tds.getSymbol(valeur.getValue(), SymbolType.TYPE_RECORD);
                if (symbol != null) {
                    return ((TypeAccessSymbol) symbol).getNom();
                } else if (symbol2 != null) {
                    return ((TypeRecordSymbol) symbol2).getNom();
                }
                if (tds.getSymbol(valeur.getValue()) != null) {
                    return ((VariableSymbol) tds.getSymbol(valeur.getValue())).getType_variable();
                } else {
                    if (valeur.getType() == NodeType.CALL){
                        if (valeur.getChildren().size() == 1){
                            if (tds.getSymbol(valeur.getChildren().get(0).getValue()) != null) {
                                return ((VariableSymbol) tds.getSymbol(valeur.getValue())).getType_variable();
                            }
                        }
                        if (valeur.getParent().getType() == NodeType.CALL){
                            controleSemantiqueAppelFonction(valeur, tds);
                            FunctionSymbol function = ((FunctionSymbol) tds.getSymbol(valeur.getChildren().get(0).getValue(), SymbolType.FUNCTION));
                            if (function == null) {
                                printError("le symbol " + valeur.getChildren().get(0).getValue() + " n'existe pas", valeur);
                                return " ";
                            }
                            else return function.getReturnType();
                        }
                        FunctionSymbol function = ((FunctionSymbol) tds.getSymbol(valeur.getChildren().get(0).getValue(), SymbolType.FUNCTION));
                        if (function == null) {
                            printError("le symbol " + valeur.getChildren().get(0).getValue() + " n'existe pas", valeur);
                            return " ";
                        }
                        else return function.getReturnType();
                    }
                    if (valeur.getValue().equalsIgnoreCase("Character'Val")){
                        try {
                            Integer.parseInt(valeur.getChildren().get(0).getValue());
                            if (valeur.getChildren().get(0).getToken() != null && valeur.getChildren().get(0).getToken().getType() == TokenType.CHARACTER) {
                                printError("The function Character'Val can only be applied to an integer", valeur.getChildren().get(0));
                                return "Character";
                            }
                            return "Character";
                        } catch (NumberFormatException e1) {
                            if (!test_expression_arithmetique(valeur.getChildren().get(0), tds)) {
                                printError("The function Character'Val can only be applied to an integer", valeur.getChildren().get(0));
                            }
                            return "Character";
                        }
                    }
                    if (valeur.getValue().equalsIgnoreCase("null")){ return "null"; }
                    controleSemantiqueAccessVariable(valeur, tds);
                    return " ";
                }
            }
        }
    }

    private static boolean test_param(Node node, Tds tds){
        Symbol symbol = tds.getSymbol(node.getValue());
        if(symbol.getType() == null) return false;
        return symbol.getType() == SymbolType.PARAM;
    }

    private static void test_in_out(Node node, Tds tds){
        if(!test_param(node, tds)) return;
        ParamSymbol param = (ParamSymbol) tds.getSymbol(node.getValue());
        if(param.getMode().equals("in")){
            printError("The mode of the parameter " + param.getName() + " is \"in\", it can not be overwrite", node);
        }
}

    private static String return_type_node(Node currentNode, Tds tds) {
        try{
            return switch (currentNode.getType()) {
                case AND, OR, EQUAL, SLASH_EQUAL, SUPERIOR, SUPERIOR_EQUAL, INFERIOR, INFERIOR_EQUAL -> "boolean";
                case ADDITION, SUBSTRACTION, MULTIPLY, DIVIDE, REM, NEGATIVE_SIGN -> "integer";
                case CALL -> {
                    FunctionSymbol function = (FunctionSymbol) tds.getSymbol(currentNode.getChildren().get(0).getValue(), SymbolType.FUNCTION);
                    if(function!= null){
                        yield function.getReturnType();
                    }
                    else{
                        yield "none";
                    }
                }
                case POINT -> "point";
                default -> type_valeur(currentNode, tds);
            };
        }catch (Exception e){
            return type_valeur(currentNode, tds);
        }
    }

    public static void controlesSemantiquesPut(Node node, Tds tds){
        Node param1 = node.getChild(1);
        if (node.getChildren().size() >= 3){
            printError("Too many parameters for the function Put expected : 1 parameter and got " + (node.getChildren().size() - 1), node);
        }
        String type_valeur = type_valeur(param1, tds);
        if (!type_valeur.equalsIgnoreCase("integer") && !type_valeur.equalsIgnoreCase("Character")){
            printError("The parameter of the function Put must be an integer or a Character", param1);
        }
    }

    private static void test_bon_type_retour(Node function_decl, String type_retour, Tds tds) {
        for (Node child : function_decl.getChildren()) {
            if (child.getType() == NodeType.RETURN) {
                //si on trouve un return, on vérifie que le type de retour est bien celui attendu
                if (child.getChildren().get(0).getType() == NodeType.CALL){
                    controleSemantiqueAppelFonction(child.getChildren().get(0), tds);
                    FunctionSymbol function = (FunctionSymbol) tds.getSymbol(child.getChildren().get(0).getValue(), SymbolType.FUNCTION);
                    if(function!= null){
                        if (!type_retour.equalsIgnoreCase(function.getReturnType())) {
                            printError("The return type of the function is not the same as the declared return type : expected " + type_retour + " and got " + function.getReturnType(), child);
                        }
                    }
                }
                else if (child.getChildren().get(0).getType() == NodeType.POINT){
                    String typeNoeudPoint = getTypeNoeudPoint(child.getChildren().get(0), tds);
                    if (!type_retour.equalsIgnoreCase(typeNoeudPoint)){
                        printError("The return type of the function is not the same as the declared return type : expected " + type_retour + " and got " + typeNoeudPoint, child);
                    }
                }
                else {
                    String type_valeur = type_valeur(child.getChildren().get(0), tds);
                    TypeRecordSymbol structureSymbol = ((TypeRecordSymbol) tds.getSymbol(type_retour, SymbolType.TYPE_RECORD));
                    if (structureSymbol != null){
                        String test_structure = structureSymbol.getNom();
                        if (type_valeur.equalsIgnoreCase("null") && !test_structure.equalsIgnoreCase("null")) return;
                    }
                    if (!type_retour.equalsIgnoreCase(type_valeur)) {
                    printError("The return type of the function is not the same as the declared return type : expected " + type_retour + " and got " + type_valeur, child);
                    }
                }
            }
            test_bon_type_retour(child, type_retour, tds);
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
