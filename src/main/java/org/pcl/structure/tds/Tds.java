package org.pcl.structure.tds;

import java.util.ArrayList;
import java.util.List;

/** Represent the TDS. */
public class Tds {

    private List<Symbol> symbols;

    private Tds parent;

    public Tds(List<Symbol> symbols, Tds parent) {
        this.symbols = symbols;
        this.parent = parent;
    }


    public Tds(Tds parent) {
        this.symbols = new ArrayList<>();
        this.parent = parent;
    }

    public Tds(List<Symbol> symbols) {
        this(symbols, null);
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Tds getParent() {
        return parent;
    }

}
