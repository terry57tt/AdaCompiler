package org.pcl.structure.tds;

/** Represent the Symbol in the TDS. */
public class Symbol {

    private SymbolType type;

    private String value;

    public Symbol(SymbolType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
}
