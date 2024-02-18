package org.pcl.structure.tds;
import org.pcl.ColorAnsiCode;
import org.pcl.Token;
//import org.pcl.ig.PCLWindows;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class CreateTDS {

    private Tds tds = new Tds("root");



    public void buildTds(SyntaxTree ast) {
        Node root = ast.getRootNode();
        AnalyseChildren(root, tds);
    }

    //On parcourt l'arbre abstrait pour constuire la table des symboles : selon le nom du noeud, on ajoute un symbole à la table des symboles
    //Si on rencontre un noeud de type boucle for, while, if, on crée une nouvelle table des symboles.
    private void AnalyseChildren(Node node, Tds tds) {
    }
}