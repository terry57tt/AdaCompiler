package org.pcl.structure.tds;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/** Represent the TDS. */
public class Tds {

    private static int REGION_COUNTER = 0;

    private List<Symbol> symbols;

    private List<Tds> child;

    private Tds parent;

    private int imbrication;

    private int region;

    public Tds(List<Symbol> symbols) {
        this.symbols = symbols;
        this.parent = null;
        this.child = new ArrayList<>();
        imbrication = 0;
        region = REGION_COUNTER;
        REGION_COUNTER++;
    }

    public void addChild(Tds tds) {
        tds.parent = this;
        tds.imbrication = tds.parent.imbrication + 1;
        child.add(tds);
    }

    public Tds() {
        this(new ArrayList<>());
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

    public int getImbrication() {
        return imbrication;
    }

    public int getRegion() {
        return region;
    }

    @Override
    public String toString() {
        AsciiTable asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("TDS - Région:" + region + " Imbrication:" + imbrication, " Déplacement");
        asciiTable.addRule();

        for (Symbol symbol : symbols) {
            asciiTable.addRow(symbol, symbol.getDeplacement());
        }
        if (!symbols.isEmpty())
            asciiTable.addRule();

        asciiTable.setTextAlignment(TextAlignment.CENTER);
        return asciiTable.render();
    }
}
