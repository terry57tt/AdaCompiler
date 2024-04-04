package org.pcl.structure.tds;

import java.util.List;

public class StructureSymbol extends VariableSymbol {
    private List<VariableSymbol> fields;

    public StructureSymbol(SymbolType type, int deplacement, String name, String type_variable, List<VariableSymbol> fields) {
        super(type, deplacement, name, type_variable);
        this.fields = fields;
    }

    public List<VariableSymbol> getFields() {
        return fields;
    }

    public void setFields(List<VariableSymbol> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "Structure type " + getType_variable() + " with fields: " + fields;
    }
}
