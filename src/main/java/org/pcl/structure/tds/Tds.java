package org.pcl.structure.tds;

import de.vandermeer.asciitable.AT_Cell;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciitable.CWC_LongestWordMin;
import de.vandermeer.asciithemes.a7.A7_Grids;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static org.pcl.structure.tds.SymbolType.*;

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
            return parent.getSymbol(SymbolName, type);
        }
        return null;
    }

    /** File all tds with deplacement. */
    public void fillAllDeplTds() {
        fillDeplTds();
        for (Tds tds : child) {
            tds.fillAllDeplTds();
        }
    }

    /** File tds with deplacement. */
    public void fillDeplTds() {
        int depl = 0;
        for (Symbol symbol : symbols) {
            switch (symbol.getType()) {
                case IDENTIFIER, OPERATION -> {
                    symbol.setDeplacement(null);
                }
                case FUNCTION, PROCEDURE -> {
                    symbol.setDeplacement(null);
                }
                case PARAM -> {
                    symbol.setDeplacement(depl);
                    String type = ((ParamSymbol) symbol).getType_variable();
                    if (type.equalsIgnoreCase("integer")) {
                        depl += 4;
                    } else  if (type.equalsIgnoreCase("char") || type.equalsIgnoreCase("character")) {
                        depl++;
                    } else {
                        symbol.setDeplacement(null);
                    }
                }
                case VARIABLE -> {
                    symbol.setDeplacement(depl);
                    String type = ((VariableSymbol) symbol).getType_variable();
                    if (type.equalsIgnoreCase("integer")) {
                        depl += 4;
                    } else  if (type.equalsIgnoreCase("char") || type.equalsIgnoreCase("character")) {
                        depl++;
                    } else {
                        symbol.setDeplacement(null);
                    }
                }
                case TYPE_RECORD -> {
                    symbol.setDeplacement(null);
                    // Not implemented
                }
                case TYPE_ACCESS -> {
                    symbol.setDeplacement(null);
                    // Not implemented
                }
            }
        }
    }


    public Symbol getSymbol(String SymbolName) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(SymbolName)) {
                return symbol;
            }
        }
        if (parent != null) {
            return parent.getSymbol(SymbolName);
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
        String depl = "";
        for (Symbol symbol : symbols) {
            depl = "";
            if (symbol.getDeplacement() != null) {
                depl = "<br>deplacement: " + symbol.getDeplacement();
            }
            AT_Cell cell = asciiTable.addRow(symbol.getName(), symbol + depl).getCells().get(1);
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
