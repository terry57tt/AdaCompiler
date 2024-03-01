import org.junit.jupiter.api.Test;
import org.pcl.FileHandler;
import org.pcl.Lexeur;
import org.pcl.Token;
import org.pcl.grammaire.Grammar;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.tds.Semantic;
import org.pcl.structure.tree.SyntaxTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class SemanticTest {

    public SyntaxTree createAST(String file) throws FileNotFoundException {
        Lexeur lexeur = new Lexeur(Graph.create(), FileHandler.getCharacters(file), FileHandler.getFileName(file));
        ArrayList<Token> tokens = lexeur.getTokens();

        Grammar grammar = new Grammar(tokens, FileHandler.getFileName(file));
        grammar.getSyntaxTree();
        grammar.createAST();
        grammar.nameNodes();

        return grammar.getSyntaxTree();
    }

    @Test
    public void testFileControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        Semantic semantic = new Semantic(ast);
    }

    @Test
    public void testForControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        Semantic semantic = new Semantic(ast);
    }

    @Test
    public void testIfControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        Semantic semantic = new Semantic(ast);
    }

    @Test
    public void testDeclVariable() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        Semantic semantic = new Semantic(ast);
    }

    @Test
    public void testDeclFunction() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        Semantic semantic = new Semantic(ast);
    }

    @Test
    public void testDeclProcedure() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        Semantic semantic = new Semantic(ast);
    }

    @Test
    public void testDeclAffection() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        Semantic semantic = new Semantic(ast);
    }
}
