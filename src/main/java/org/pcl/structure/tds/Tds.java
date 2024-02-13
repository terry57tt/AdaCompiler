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

    public Tds(List<Symbol> symbols, Tds parent, int imbrication) {
        this.symbols = symbols;
        this.parent = parent;
        region = REGION_COUNTER;
        REGION_COUNTER++;
    }


    public Tds(Tds parent, int imbrication) {
        this(new ArrayList<>(), parent, imbrication);
    }

    public Tds(List<Symbol> symbols, int imbrication) {
        this(symbols, null, imbrication);
    }

    public Tds(int imbrication) {
        this(new ArrayList<>(), null, imbrication);
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
        asciiTable.addRow("TDS - Région:" + region + " Imbrication:" + imbrication);
        asciiTable.addRule();

        for (Symbol symbol : symbols) {
            asciiTable.addRow(symbol);
            //asciiTable.addRule();
        }
        asciiTable.addRule();
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        return asciiTable.render();
    }
}
