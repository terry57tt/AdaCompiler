package org.pcl.structure.tds;

import java.util.List;

public class ProcedureSymbol extends Symbol {

        private String nom_apres_end;
        private List<ParamSymbol> parameters;
    
        public ProcedureSymbol(SymbolType type, int deplacement, String name) {
            super(type, deplacement, name);
        }

        public ProcedureSymbol(SymbolType type, int deplacement, String name, String nom_apres_end) {
            super(type, deplacement, name);
            this.nom_apres_end = nom_apres_end;
        }

        public ProcedureSymbol(SymbolType type, int deplacement, String name, List<ParamSymbol> parameters) {
            super(type, deplacement, name);
            this.parameters = parameters;
        }

        public ProcedureSymbol(SymbolType type, int deplacement, String name, String nom_apres_end, List<ParamSymbol> parameters) {
            super(type, deplacement, name);
            this.nom_apres_end = nom_apres_end;
            this.parameters = parameters;
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

        public int getNbParameters() {
            if (parameters == null) {
                return 0;
            }
            return parameters.size();
        }

        public void setParameters(List<ParamSymbol> parameters) {
            this.parameters = parameters;
        }

    @Override
    public String toString() {
        return "PROCEDURE - closing name " + nom_apres_end + "<br>Parameters: " + parameters ;
        }
}
