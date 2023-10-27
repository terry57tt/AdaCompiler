package org.pcl;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.ArrayList;

import org.pcl.structure.automaton.Graph;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.TokenType;

public class Lexeur {
    //Attributs 
    String path;

    //Lecture du fichier à partir des fonctions dans fileHandler

    Stream<Character> characterStream;

    public Lexeur(String path){
        this.path = path;
    }

    /*Fonction qui renvoit une liste de token : la fonction va parcourir le tableau 
     * de stream<caracter> va parcourir les transitions du graphes et si il voit 
     * un espace ou un separateur il va creer un token et le rajouter dans la liste de token
     * qui sera crée progressivement. Le token sera un triplet (type, valeur, ligne)
    */
    public ArrayList<Token> getTokens() throws IOException {
        // reste à traiter : les numéros de lignes, les symboles non reconnu, les erreurs dans l'automate (il n'y a pas de liaison),
        //les commentaires, les différents cas de séparateurs/opérateurs

        //nouvelle idée : avancer dans l'automate, si plus possible, on regarde si c'est un état final. Si oui, créer le token.
        //Si non, ???. Continuer avec le prochain caractère. + cas \n + cas commentaire

        ArrayList<Token> tokens = new ArrayList<>();
        String valueToken = "";
        Automaton automaton = Graph.create();

        Stream<Character> characterStream = FileHandler.getCharacters(path);
        Iterator<Character> iterator = characterStream.iterator();
        long line = 1;

        while (iterator.hasNext()) {
            Character current_character = iterator.next();
            StringBuilder value = new StringBuilder();
            //in the first loop iterator.next() is the first character in the stream

            //while the current character is not a separator or an operator, add the character to value
            while (!current_character.equals(TokenType.SEPARATOR) && !current_character.equals(TokenType.OPERATOR)){
                automaton.advance(current_character);
                value.append(current_character);
                current_character = iterator.next();
            } // at the end of the loop, the current character is a separator or a
            //create a new token with "value"

            Token token = new Token(value.toString());
            //update the line number of the token
            //change type of the token if possible
            if (automaton.isFinal()) {
                token.setType(automaton.getCurrentState().getTokenType());
            }
            //add token to the list of tokens
            tokens.add(token);
            automaton.reset();

            String separator = "" + current_character;
            //different cases of separator





        }

        return tokens;
        }
}


