package org.pcl.structure.tds;

import java.util.List;


public class FunctionSymbol extends Symbol {
    private String nom;
    private String returnType;
    private List<ParamSymbol> parameters;

    public FunctionSymbol(SymbolType type, int deplacement, String nom, String returnType, List<ParamSymbol> parameters) {
        super(type, deplacement);
        this.nom = nom;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public FunctionSymbol(SymbolType type, int deplacement, String nom, String returnType) {
        super(type, deplacement);
        this.nom = nom;
        this.returnType = returnType;
    }

    public String getNom() {
        return nom;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

}
