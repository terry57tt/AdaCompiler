package org.pcl.structure.tds;

import java.util.List;


public class FunctionSymbol extends Symbol {

    private String returnType;
    private List<ParamSymbol> parameters;

    public FunctionSymbol(SymbolType type, int deplacement, String name, String returnType, List<ParamSymbol> parameters) {
        super(type, deplacement, name);
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public FunctionSymbol(SymbolType type, int deplacement, String name, String returnType) {
        super(type, deplacement, name);
        this.returnType = returnType;
    }


    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<ParamSymbol> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParamSymbol> parameters) {
        this.parameters = parameters;
    }

    public String toString() {
        return  "FUNCTION - return type: " + returnType + "<br>Parameters: " + parameters;
    }

}
