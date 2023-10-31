import org.junit.jupiter.api.Test;
import org.pcl.FileHandler;
import org.pcl.Lexeur;
import org.pcl.Token;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.automaton.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
        String file = "data/keywords.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);

        Lexeur lexeur = new Lexeur(automaton, stream, file);
        
        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 9;

        for (Token token : tokens) {
            assert token.getType() == TokenType.KEYWORD;
            assert token.getLineNumber() == 1;
        }
    }

    @Test
    public void testAdaProgram1() throws Exception {
        String file = "data/program1.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);

        Lexeur lexeur = new Lexeur(automaton, stream, file);
        
        ArrayList<Token> tokens = lexeur.tokenize();
        
        assert tokens.size() == 25;
        
        assert tokens.get(0).getType() == TokenType.KEYWORD;
        assert tokens.get(0).getValue().equals("with");
        assert tokens.get(0).getLineNumber() == 1;

        assert tokens.get(1).getType() == TokenType.IDENTIFIER;
        assert tokens.get(1).getValue().equals("Ada");
        assert tokens.get(1).getLineNumber() == 1;

        assert tokens.get(2).getType() == TokenType.SEPARATOR;
        assert tokens.get(2).getValue().equals(".");
        assert tokens.get(2).getLineNumber() == 1;

        assert tokens.get(6).getType() == TokenType.IDENTIFIER;
        assert tokens.get(6).getValue().equals("Hello_World");
        assert tokens.get(6).getLineNumber() == 3;

        assert tokens.get(14).getType() == TokenType.SEPARATOR;
        assert tokens.get(14).getValue().equals("(");
        assert tokens.get(14).getLineNumber() == 5;
    }

    @Test
    public void testInvalidCharacter() throws Exception {
        String file = "data/invalid_character.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);
        ArrayList<Token> tokens = lexeur.tokenize();

        for (Token token: tokens) {
            assert !token.getValue().equals("#");
            assert !token.getValue().equals("!");
        }
    }

    @Test
    public void testSpecificCharacters() throws Exception {
        String file = "data/specific_characters.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);

        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 34;

        assert tokens.get(1).getValue().equals(":=");
        assert tokens.get(10).getValue().equals("<=");
        assert tokens.get(15).getValue().equals(">=");
        assert tokens.get(20).getValue().equals("/=");
    }

    @Test
    public void testRemoveComments() throws Exception {
        String file = "data/remove_comments.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);

        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 8;

        assert tokens.get(0).getValue().equals("access");
        assert tokens.get(0).getLineNumber() == 2;

        assert tokens.get(1).getValue().equals("program");
        assert tokens.get(1).getLineNumber() == 2;

        assert tokens.get(2).getValue().equals("with");
        assert tokens.get(2).getLineNumber() == 2;

        assert tokens.get(3).getValue().equals("hello_Word");
        assert tokens.get(3).getLineNumber() == 2;

        assert tokens.get(4).getValue().equals(";");
        assert tokens.get(4).getLineNumber() == 2;

        assert tokens.get(5).getValue().equals("procedure");
        assert tokens.get(5).getLineNumber() == 4;

        assert tokens.get(6).getValue().equals("hello_Word");
        assert tokens.get(6).getLineNumber() == 4;

        assert tokens.get(7).getValue().equals("is");
        assert tokens.get(7).getLineNumber() == 4;
    }
}
