package org.pcl.structure.tds;

import java.util.List;

public class ProcedureSymbol extends Symbol {
    
        private String nom;
        private String nom_apres_end;
        private List<ParamSymbol> parameters;
    
        public ProcedureSymbol(SymbolType type, int deplacement, String nom) {
            super(type, deplacement);
            this.nom = nom;
        }

        public ProcedureSymbol(SymbolType type, int deplacement, String nom, String nom_apres_end) {
            super(type, deplacement);
            this.nom = nom;
            this.nom_apres_end = nom_apres_end;
        }

        public ProcedureSymbol(SymbolType type, int deplacement, String nom, List<ParamSymbol> parameters) {
            super(type, deplacement);
            this.nom = nom;
            this.parameters = parameters;
        }

        public ProcedureSymbol(SymbolType type, int deplacement, String nom, String nom_apres_end, List<ParamSymbol> parameters) {
            super(type, deplacement);
            this.nom = nom;
            this.nom_apres_end = nom_apres_end;
            this.parameters = parameters;
        }
    
        public String getNom() {
            return nom;
        }
    
        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getNom_apres_end() {
            return nom_apres_end;
        }

        public void setNom_apres_end(String nom_apres_end) {
            this.nom_apres_end = nom_apres_end;
        }

        public List<ParamSymbol> getParameters() {
            return parameters;
        }

        public void setParameters(List<ParamSymbol> parameters) {
            this.parameters = parameters;
        }
}
