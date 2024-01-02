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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

public class GrammarTest {
    
    public Lexeur createLexeur(String file) throws IOException {
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        return new Lexeur(automaton, stream, file);
    }

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

    @Test
    public void testExtraTokens() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/SyntaxError/extra_tokens.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertTrue(grammar.error,"Syntax error should be detected");
        assertTrue(grammar.getNumberErrors() > 0,"Number of errors should be greater than 0");
    }

    @Test
    public void testMismatchedParentheses() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/SyntaxError/mismatched_parentheses.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertTrue(grammar.error,"Syntax error should be detected");
        assertTrue(grammar.getNumberErrors() > 0,"Number of errors should be greater than 0");
    }

    @Test
    public void testMissingSemicolon() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/SyntaxError/missing_semicolon.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertTrue(grammar.error,"Syntax error should be detected");
        assertTrue(grammar.getNumberErrors() > 0,"Number of errors should be greater than 0");
    }

    @Test
    public void testUnexpectedToken() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/SyntaxError/unexpected_token.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertTrue(grammar.error,"Syntax error should be detected");
        assertTrue(grammar.getNumberErrors() > 0,"Number of errors should be greater than 0");
    }

    @Test
    public void testWrongKeyword() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/SyntaxError/wrong_keyword.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertTrue(grammar.error,"Syntax error should be detected");
        assertTrue(grammar.getNumberErrors() > 0,"Number of errors should be greater than 0");
    }

    @Test
    public void testWrongOperation() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/SyntaxError/wrong_operation.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertTrue(grammar.error,"Syntax error should be detected");
        assertTrue(grammar.getNumberErrors() > 0,"Number of errors should be greater than 0");
    }

    @Test
    public void testMixedOperations() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/mixed_operations_test.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testRecord() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/record_test.ada");
        
        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testLoop() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/test_loop.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();
        
        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific1() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_1.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific2() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_2.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific3() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_3.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific4() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_4.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific5() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_5.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific6() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_6.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific7() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_7.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }

    @Test
    public void testSpecific8() throws IOException {
      
        Lexeur lexeur = createLexeur("demo/CorrectSyntax/specific_test_8.ada");

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();
        
        assertFalse(grammar.error,"Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0,"Number of errors should be 0");
    }
}
