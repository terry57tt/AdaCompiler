package org.pcl.structure.tds;

import java.util.List;

/** Represent the Symbol in the TDS. */
public abstract class Symbol {

    private SymbolType type;

    private Integer deplacement;

    private String name;

    public Symbol(SymbolType type, int deplacement, String name) {
        this.type = type;
        this.name = name;
        this.deplacement = null;
    }

    public Integer getDeplacement() {
        return deplacement;
    }

    public void setDeplacement(Integer deplacement) {
        this.deplacement = deplacement;
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
