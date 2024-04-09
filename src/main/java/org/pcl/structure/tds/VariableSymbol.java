package org.pcl.structure.tds;

public class VariableSymbol extends Symbol {

        private String type_variable;
        private String value;
        private boolean forVariable = false; //is true if the variable is a for counter

        public VariableSymbol(SymbolType type, int deplacement, String name, String type_variable, String value) {
            super(type, deplacement, name);
            this.type_variable = type_variable;
            this.value = value;
        }

        public VariableSymbol(SymbolType type, int deplacement, String name, String type_variable) {
            super(type, deplacement, name);
            this.type_variable = type_variable;
        }
        public VariableSymbol(SymbolType type, int deplacement, String name, String type_variable, boolean isForCounter) {
            super(type, deplacement, name);
            this.type_variable = type_variable;
            this.forVariable = isForCounter;
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
        public boolean isForVariable(){
            return this.forVariable;
        }
        public void setForVariable(boolean bool){
            this.forVariable = bool;
        }

    @Override
    public String toString() {
        return "VARIABLE - Nom " + getName() + " Type: " + type_variable;
    }
}
