package org.pcl.structure.tds;

public class VariableSymbol extends Symbol {

        private String nom;
        private String type_variable;
        private String value;

        public VariableSymbol(SymbolType type, int deplacement, String nom, String type_variable, String value) {
            super(type, deplacement);
            this.nom = nom;
            this.type_variable = type_variable;
            this.value = value;
        }

        public VariableSymbol(SymbolType type, int deplacement, String nom, String type_variable) {
            super(type, deplacement);
            this.nom = nom;
            this.type_variable = type_variable;
        }

        public String getNom() {
            return nom;
        }

        public String getType_variable() {
            return type_variable;
        }

        public String getValue() {
            return value;
        }
    
}
