package org.pcl.structure.tds;

import de.vandermeer.asciitable.AT_Cell;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciitable.CWC_LongestWordMin;
import de.vandermeer.asciithemes.a7.A7_Grids;
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

    public Tds(List<Symbol> symbols, String name) {
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

    public Tds(String name) {
        this(new ArrayList<>(), name);
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

    public boolean containsSymbol(String SymbolName, SymbolType type) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(SymbolName) && symbol.getType() == type) {
                return true;
            }
        }
        if (parent == null) {
            return false;
        }
        boolean a = parent.containsSymbol(SymbolName, type);
        if (a) {
            return true;
        }
        return false;
    }

    public Symbol getSymbol(String SymbolName, SymbolType type) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(SymbolName) && symbol.getType() == type) {
                return symbol;
            }
        }
        if (parent != null) {
            System.out.println(parent.getName());
            return parent.getSymbol(SymbolName, type);
        }
        return null;
    }

    @Override
    public String toString() {
        System.out.println("\n");
        AsciiTable asciiTable = new AsciiTable();
        asciiTable.addRule();
        System.out.println(" TDS - Région:" + region + " Imbrication:" + imbrication + " Nom : " + name);
        asciiTable.addRow("Nom", "Contenu");
        asciiTable.addRule();

        for (Symbol symbol : symbols) {
            AT_Cell cell = asciiTable.addRow(symbol.getName(), symbol).getCells().get(1);
            cell.getContext().setPadding(1);
            cell.getContext().setTextAlignment(TextAlignment.LEFT);
            asciiTable.addRule();
        }
        asciiTable.getRenderer().setCWC(new CWC_LongestWordMin(new int[]{-1,90}));
        return asciiTable.render();
    }

    public void displayWithChild() {
        System.out.println(this);
        for (Tds tds : child) {
            tds.displayWithChild();
        }
    }
}
