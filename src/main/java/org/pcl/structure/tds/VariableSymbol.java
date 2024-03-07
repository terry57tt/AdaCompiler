package org.pcl.structure.tds;

public class VariableSymbol extends Symbol {

        private String type_variable;
        private String value;

        public VariableSymbol(SymbolType type, int deplacement, String name, String type_variable, String value) {
            super(type, deplacement, name);
            this.type_variable = type_variable;
            this.value = value;
        }

        public VariableSymbol(SymbolType type, int deplacement, String name, String type_variable) {
            super(type, deplacement, name);
            this.type_variable = type_variable;
        }

        public String getType_variable() {
            return type_variable;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    @Override
    public String toString() {
        return "VARIABLE - Type: " + type_variable + " with value: " + value;
    }
}
