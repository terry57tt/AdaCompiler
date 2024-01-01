import java.io.IOException;
import java.util.ArrayList;

import org.pcl.FileHandler;
import org.pcl.Lexeur;
import org.pcl.Token;

import org.pcl.grammaire.Grammar;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.junit.jupiter.api.Test;


import org.pcl.structure.automaton.TokenType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

public class GrammarTest {
    @Test
    public void createGrammar() throws IOException {
        String file = "demo/SyntaxError/syntaxError.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);

        Lexeur lexeur = new Lexeur(automaton, stream, file);
        
        ArrayList<Token> tokens = lexeur.tokenize();

        Grammar grammar = new Grammar(tokens);
        SyntaxTree tree = grammar.getSyntaxTree();
        System.out.println("Printrds");
        System.out.println(tree);
    }
    
}
