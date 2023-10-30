import org.junit.jupiter.api.Test;
import org.pcl.Lexeur;
import org.pcl.Token;
import org.pcl.structure.automaton.TokenType;
import org.pcl.FileHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;

public class LexeurTest {

    @Test
    public void testGetCharacters() throws IOException {
        String testFilePath = "data/keywords.ada";

        Stream<Character> characterStream = FileHandler.getCharacters(testFilePath);
        assertNotNull(characterStream);
        
        String result = characterStream.map(String::valueOf).collect(Collectors.joining());
        // check that the content is correct
        String expectedContent = "access and begin else elsif end if false true\n";

        // check that the file is read correctly
        assertEquals(expectedContent.length(), result.length());

        assertEquals(expectedContent, result);

        // check that spaces are not ignored
        assertEquals(' ', result.charAt(6));

        // check that new lines are not ignored
        assertEquals('\n', result.charAt(45));
    }

    @Test
    public void testGetCharactersWithNonExistentFile() {
        String nonExistentFilePath = "non_existent_file.ada";

        assertThrows(IOException.class, () -> FileHandler.getCharacters(nonExistentFilePath));
    }

    @Test
    public void testAdaProgram0() throws Exception {
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters("data/keywords.ada");

        Lexeur lexeur = new Lexeur(automaton, stream);
        
        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 9;

        for (Token token : tokens) {
            assert token.getType() == TokenType.KEYWORD;
            assert token.getLineNumber() == 1;
        }
    }

    @Test
    public void testAdaProgram1() throws Exception {
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters("data/program1.ada");

        Lexeur lexeur = new Lexeur(automaton, stream);
        
        ArrayList<Token> tokens = lexeur.tokenize();
        
        assert tokens.size() == 26;
        
        assert tokens.get(0).getType() == TokenType.KEYWORD;
        assert tokens.get(0).getValue().equals("with");
        assert tokens.get(0).getLineNumber() == 1;

        assert tokens.get(1).getType() == TokenType.IDENTIFIER;
        assert tokens.get(1).getValue().equals("Ada");
        assert tokens.get(1).getLineNumber() == 1;

        assert tokens.get(2).getType() == TokenType.OPERATOR;
        assert tokens.get(2).getValue().equals(".");
        assert tokens.get(2).getLineNumber() == 1;

        assert tokens.get(6).getType() == TokenType.IDENTIFIER;
        assert tokens.get(6).getValue().equals("Hello_World");
        assert tokens.get(6).getLineNumber() == 2;

        assert tokens.get(14).getType() == TokenType.OPERATOR;
        assert tokens.get(14).getValue().equals("(");
        assert tokens.get(14).getLineNumber() == 4;
    }

    @Test
    public void testInvalidCharacter() throws Exception {
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters("data/invalid_character.ada");
        Lexeur lexeur = new Lexeur(automaton, stream);
        
        try {
            ArrayList<Token> tokens = lexeur.tokenize();
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    public void testSpecificCharacters() throws Exception {
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters("data/specificCharacters.ada");
        Lexeur lexeur = new Lexeur(automaton, stream);

        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 22;

        assert ":="==tokens.get(1).getValue();
        assert "<="==tokens.get(10).getValue();
        assert ">="==tokens.get(15).getValue();
        assert "/="==tokens.get(20).getValue();
    }

    @Test
    public void testRemoveComments() throws Exception {
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters("data/remove_comments.ada");
        Lexeur lexeur = new Lexeur(automaton, stream);

        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 8;

        assert "access"==tokens.get(0).getValue();
        assert "program"==tokens.get(1).getValue();
        assert "with"==tokens.get(2).getValue();
        assert "hello_Word"==tokens.get(3).getValue();
        assert ";"==tokens.get(4).getValue();
        assert "procedure"==tokens.get(5).getValue();
        assert "hello_Word"==tokens.get(6).getValue();
        assert "is"==tokens.get(7).getValue();
    }
}
