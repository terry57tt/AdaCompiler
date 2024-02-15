package org.pcl.structure.tds;

/** Represent the Symbol in the TDS. */
public class Symbol {

    private SymbolType type;

    private String value;

    private int deplacement;

    public Symbol(SymbolType type, String value, int deplacement) {
        this.type = type;
        this.value = value;
        this.deplacement = deplacement;
    }

    public int getDeplacement() {
        return deplacement;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
}
