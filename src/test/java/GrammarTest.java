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

    @Test
    public void testBst() throws IOException {
        testAdaFile("bst.ada");
    }

    @Test
    public void testHello() throws IOException {
        testAdaFile("hello.ada");
    }

    @Test
    public void test1() throws IOException {
        testAdaFile("test1.ada");
    }

    @Test
    public void testF1() throws IOException {
        testAdaFile("function1.ada");
    }

    @Test
    public void testPrintInt() throws IOException {
        testAdaFile("print_int.ada");
    }

    @Test
    public void testRec2() throws IOException {
        testAdaFile("record2.ada");
    }

    @Test
    public void testPut() throws IOException {
        testAdaFile("put.ada");
    }

    @Test
    public void testRecord3() throws IOException {
        testAdaFile("record3.ada");
    }

    @Test
    public void testTestfileAssign2() throws IOException {
        testAdaFile("testfile-assign-2.ada");
    }

    @Test
    public void testTestfileComment1() throws IOException {
        testAdaFile("testfile-comment-1.ada");
    }

    @Test
    public void testTestfileParams3() throws IOException {
        testAdaFile("testfile-params-3.ada");
    }

    @Test
    public void testTestfileRecord2() throws IOException {
        testAdaFile("testfile-record-2.ada");
    }

    @Test
    public void testFact() throws IOException {
        testAdaFile("fact.ada");
    }

    @Test
    public void testJosephus() throws IOException {
        testAdaFile("josephus.ada");
    }

    @Test
    public void testQueens() throws IOException {
        testAdaFile("queens.ada");
    }

    @Test
    public void testRecord4() throws IOException {
        testAdaFile("record4.ada");
    }

    @Test
    public void testTestfileAssign3() throws IOException {
        testAdaFile("testfile-assign-3.ada");
    }

    @Test
    public void testTestfileEnd1() throws IOException {
        testAdaFile("testfile-end-1.ada");
    }

    @Test
    public void testTestfileParams4() throws IOException {
        testAdaFile("testfile-params-4.ada");
    }

    @Test
    public void testTestfileRecord3() throws IOException {
        testAdaFile("testfile-record-3.ada");
    }

    @Test
    public void testFib() throws IOException {
        testAdaFile("fib.ada");
    }

    @Test
    public void testMandelbrot() throws IOException {
        testAdaFile("mandelbrot.ada");
    }

    @Test
    public void testQuine() throws IOException {
        testAdaFile("quine.ada");
    }

    @Test
    public void testReturn1() throws IOException {
        testAdaFile("return1.ada");
    }

    @Test
    public void testTestfileAssign4() throws IOException {
        testAdaFile("testfile-assign-4.ada");
    }

    @Test
    public void testTestfileLexical1() throws IOException {
        testAdaFile("testfile-lexical-1.ada");
    }

    @Test
    public void testTestfileRec1() throws IOException {
        testAdaFile("testfile-rec-1.ada");
    }

    @Test
    public void testTestfileShadow1() throws IOException {
        testAdaFile("testfile-shadow-1.ada");
    }

    @Test
    public void testFor1() throws IOException {
        testAdaFile("for1.ada");
    }

    @Test
    public void testPascal() throws IOException {
        testAdaFile("pascal.ada");
    }

    @Test
    public void testRec1() throws IOException {
        testAdaFile("rec1.ada");
    }

    @Test
    public void testSyracuse() throws IOException {
        testAdaFile("syracuse.ada");
    }

    @Test
    public void testTestfileCase1() throws IOException {
        testAdaFile("testfile-case-1.ada");
    }

    @Test
    public void testTestfileLexical2() throws IOException {
        testAdaFile("testfile-lexical-2.ada");
    }

    @Test
    public void testTestfileRec2() throws IOException {
        testAdaFile("testfile-rec-2.ada");
    }

    @Test
    public void testTestfileShadow2() throws IOException {
        testAdaFile("testfile-shadow-2.ada");
    }

    @Test
    public void testFor2() throws IOException {
        testAdaFile("for2.ada");
    }

    @Test
    public void testPower() throws IOException {
        testAdaFile("power.ada");
    }

    @Test
    public void testRecord1() throws IOException {
        testAdaFile("record1.ada");
    }

    @Test
    public void testTestfileAssign1() throws IOException {
        testAdaFile("testfile-assign-1.ada");
    }

    @Test
    public void testTestCase3() throws IOException {
        testAdaFile("testfile-case-3.ada");
    }

    @Test
    public void testParam2() throws IOException {
        testAdaFile("testfile-params-2.ada");
    }

    @Test
    public void testFileRecord() throws IOException {
        testAdaFile("testfile-record-1.ada");
    }

    @Test
    public void testTestfileCase2() throws IOException {
        testAdaFile("testfile-case-2.ada");
    }

    @Test
    public void testTestfileParams1() throws IOException {
        testAdaFile("testfile-params-1.ada");
    }

    @Test
    public void testTestfileRec3() throws IOException {
        testAdaFile("testfile-rec-3.ada");
    }

    @Test
    public void testTestfileShadow3() throws IOException {
        testAdaFile("testfile-shadow-3.ada");
    }

    private void testAdaFile(String fileName) throws IOException {
        Lexeur lexeur = createLexeur("demo/AdditionnalTests/" + fileName);

        Grammar grammar = new Grammar(lexeur.tokenize());
        grammar.getSyntaxTree();

        assertFalse(grammar.error, "Syntax error should not be detected");
        assertTrue(grammar.getNumberErrors() == 0, "Number of errors should be 0");
    }


}
