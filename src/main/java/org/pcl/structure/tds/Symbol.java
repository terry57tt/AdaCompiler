package org.pcl.structure.tds;

/** Represent the Symbol in the TDS. */
public class Symbol {

    private SymbolType type;

    private int deplacement;

    public Symbol(SymbolType type, int deplacement) {
        this.type = type;
        this.deplacement = deplacement;
    }

    public int getDeplacement() {
        return deplacement;
    }
}
