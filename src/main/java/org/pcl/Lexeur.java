package org.pcl;
import java.util.stream.Stream;
import java.util.ArrayList;

import org.pcl.structure.automaton.Graph;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.TokenType;

public class Lexeur {
    //Attributs 
    String path;

    //Lecture du fichier à partir des fonctions dans fileHandler

    FileHandler fileHandler = new FileHandler();
    Stream<Character> characterStream;

    /*Fonction qui renvoit une liste de token : la fonction va parcourir le tableau 
     * de stream<caracter> va parcourir les transitions du graphes et si il voit 
     * un espace ou un separateur il va creer un token et le rajouter dans la liste de token
     * qui sera crée progressivement. Le token sera un triplet (type, valeur, ligne)
    */
    public ArrayList<Token> getTokens() {
        ArrayList<Token> tokens = new ArrayList<>();
        Graph graph = new Graph();
        Automaton automaton = graph.create();
        //Tant que le prochain caractère de la stream n'est pas null
        }
}


