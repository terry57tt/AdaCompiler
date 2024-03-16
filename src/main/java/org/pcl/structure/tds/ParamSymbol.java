package org.pcl.structure.tds;

public class ParamSymbol extends VariableSymbol {

    private String mode;

    public ParamSymbol(SymbolType type, int deplacement, String nom, String type_variable, String value) {
        super(type, deplacement, nom, type_variable, value);
        this.mode = value;
    }

    public ParamSymbol(SymbolType type, int deplacement, String nom, String type_variable) {
        super(type, deplacement, nom, type_variable);
        this.mode = "in";
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
       return "PARAM - (" + getType_variable() + " " + getName() + " " + mode + ")";
    }
}
