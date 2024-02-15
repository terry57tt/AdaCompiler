package org.pcl.structure.tds;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/** Represent the TDS. */
public class Tds {

    private static int REGION_COUNTER = 0;

    private List<Symbol> symbols;

    private Tds parent;

    private int imbrication;

    private int region;

    public Tds(List<Symbol> symbols, Tds parent) {
        this.symbols = symbols;
        this.parent = parent;
        region = REGION_COUNTER;
        REGION_COUNTER++;
        if (parent != null) {
            imbrication = parent.imbrication + 1;
        } else {
            imbrication = 0;
        }
    }


    public Tds(Tds parent) {
        this(new ArrayList<>(), parent);
    }

    public Tds(List<Symbol> symbols) {
        this(symbols, null);
    }

    public Tds() {
        this(new ArrayList<>(), null);
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Tds getParent() {
        return parent;
    }

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    @Override
    public String toString() {
        AsciiTable asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("TDS - Région:" + region + " Imbrication:" + imbrication, " Déplacement");
        asciiTable.addRule();

        for (Symbol symbol : symbols) {
            asciiTable.addRow(symbol, symbol.getDeplacement());
            //asciiTable.addRule();
        }
        asciiTable.addRule();
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        return asciiTable.render();
    }
}
