import org.junit.jupiter.api.Test;
import org.pcl.FileHandler;
import org.pcl.Lexeur;
import org.pcl.Token;
import org.pcl.grammaire.Grammar;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.tds.Semantic;
import org.pcl.structure.tds.SemanticControls;
import org.pcl.structure.tree.SyntaxTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueFile"));
        errors.clear();
    }

    @Test
    public void testFileErrorControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/error_file_naming.ada");
        SemanticControls.setName_file(FileHandler.getFileName("error_file_naming.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().anyMatch(s -> s.contains("controleSemantiqueFile"));
        errors.clear();
    }

    @Test
    public void testDeclVarControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueDeclVariable"));
        errors.clear();
    }

    @Test
    public void testForControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueFor"));
        errors.clear();
    }

    @Test
    public void testIfControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueIf"));
        errors.clear();
    }

    @Test
    public void testDeclFunction() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueDeclFonction"));
        errors.clear();
    }

    @Test
    public void testAppelFunction() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueAppelFonction"));
        errors.clear();
    }

    @Test
    public void testDeclProcedure() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueDeclProcedure"));
        errors.clear();
    }

    @Test
    public void testAppelProcedure() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueAppelProcedure"));
        errors.clear();
    }

    @Test
    public void testAffectation() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueAffectation"));
        errors.clear();
    }

    @Test
    public void testDeclAffectation() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueAffectationDecl"));
        errors.clear();
    }

    @Test
    public void testOperationControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueOperateur"));
        errors.clear();
    }

    @Test
    public void testWhileControl() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueWhile"));
        errors.clear();
    }

    @Test
    public void testAccessVariable() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.stream().noneMatch(s -> s.contains("controleSemantiqueAccessVariable"));
        errors.clear();
    }

    @Test
    public void testPoint() throws IOException {
        SyntaxTree ast = createAST("example/SemanticDemo/demo_1.ada");
        SemanticControls.setName_file(FileHandler.getFileName("demo_1.ada"));
        Semantic semantic = new Semantic(ast);
        List<String> errors = SemanticControls.getErrors();
        assert errors.isEmpty() || errors.stream().noneMatch(s -> s.contains("controleSemantiquePoint"));
        errors.clear();
    }
}
