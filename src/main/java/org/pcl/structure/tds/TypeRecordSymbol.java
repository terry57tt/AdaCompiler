package org.pcl.structure.tds;

import java.util.List;

public class TypeRecordSymbol extends Symbol {
    private String nom;
    private List<VariableSymbol> fields;

    public TypeRecordSymbol(SymbolType type, int deplacement, String nom, List<VariableSymbol> fields) {
        super(type, deplacement, nom);
        this.nom = nom;
        this.fields = fields;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<VariableSymbol> getFields() {
        return fields;
    }

    public void setFields(List<VariableSymbol> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "RECORD - Name: " + nom + " with fields: " + fields;
    }
}
