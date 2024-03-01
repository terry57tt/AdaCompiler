package org.pcl.structure.tds;

import java.util.List;

/** Represent the Symbol in the TDS. */
public abstract class Symbol {

    private SymbolType type;

    private int deplacement;

    private String name;

    public Symbol(SymbolType type, int deplacement) {
        this.type = type;
        this.deplacement = deplacement;
    }

    public int getDeplacement() {
        return deplacement;
    }

    public SymbolType getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
