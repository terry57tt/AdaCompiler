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

    private String name;

    private String chainage; 

    public Tds(List<Symbol> symbols, String name, String chainage) {
        this.symbols = symbols;
        this.parent = null;
        this.child = new ArrayList<>();
        imbrication = 0;
        region = REGION_COUNTER;
        REGION_COUNTER++;
        this.name = name;
    }

    public void addChild(Tds tds) {
        tds.parent = this;
        tds.imbrication = tds.parent.imbrication + 1;
        child.add(tds);
    }

    public Tds(String name, String chainage) {
        this(new ArrayList<>(), name, chainage);
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

    public String getName() {
        return name;
    }

    public List<Tds> getChild() {
        return child;
    }

    public void setChild(List<Tds> child) {
        this.child = child;
    }

    public void setParent(Tds parent) {
        this.parent = parent;
    }

    public void setImbrication(int imbrication) {
        this.imbrication = imbrication;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public void addSymbols(List<Symbol> symbols) {
        this.symbols.addAll(symbols);
    }

    public void addSymbols(Tds tds) {
        this.symbols.addAll(tds.getSymbols());
    }

    public boolean containsSymbol(String SymbolName) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(SymbolName)) {
                return true;
            }
        }
        if (chainage.equals("true") || parent.getName().equals("root")) {
            boolean a = parent.containsSymbol(SymbolName);
            if (a) {
                return true;
            }
        }
        return false;
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
