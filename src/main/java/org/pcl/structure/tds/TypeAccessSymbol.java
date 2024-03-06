package org.pcl.structure.tds;

public class TypeAccessSymbol extends Symbol {

    private String nom;

    private String typePointe;

    public TypeAccessSymbol(SymbolType type, int deplacement, String nom, String typePointe) {
        super(type, deplacement, nom);
        this.nom = nom;
        this.typePointe = typePointe;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTypePointe() {
        return typePointe;
    }

    public void setTypePointe(String typePointe) {
        this.typePointe = typePointe;
    }

    @Override
    public String toString() {
        return "ACCESS - Name: " + nom + " toward type: " + typePointe;
    }
}
