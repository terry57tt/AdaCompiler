package org.pcl.structure.tds;

public class ParamSymbol extends VariableSymbol {

    private String mode = "in";

    public ParamSymbol(SymbolType type, int deplacement, String nom, String type_variable, String value) {
        super(type, deplacement, nom, type_variable, value);
    }

    public ParamSymbol(SymbolType type, int deplacement, String nom, String type_variable) {
        super(type, deplacement, nom, type_variable);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
       return "(" + getType_variable() + " " + getValue() + " " + mode + ")";
    }
}
